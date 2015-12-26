package com.fishjord.irwidget;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.bfmj.handledb.HandleSqlDB;
import com.bfmj.network.INetworkCallback;
import com.bfmj.network.NetworkService;
import com.fishjord.irwidget.ir.codes.LearnedButton;
import com.fishjord.irwidget.ir.codes.LearnedCommand;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity implements INetworkCallback  {




	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	private ArrayList<String> mRemoterTitles=new ArrayList<String>();
	
	
	//private final NetworkService mService=new NetworkService(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTitle = mDrawerTitle = getTitle();

		//mService.delegate=this;

		//mPlanetTitles = getResources().getStringArray(R.array.remoter_types);
		Cursor c= HandleSqlDB.instance.getGroups();
		while(c.moveToNext())
		{
			mRemoterTitles.add(c.getString(c.getColumnIndex("buttongroup")));
		}
		mRemoterTitles.add("添加遥控器+");


		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mRemoterTitles));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(
				this,                  /* host Activity */
				mDrawerLayout,         /* DrawerLayout object */
				R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
				R.string.drawer_open,  /* "open drawer" description for accessibility */
				R.string.drawer_close  /* "close drawer" description for accessibility */
				) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			selectItem(0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Intents must be placed in the AndroidManifest.xml file ...
		int id = item.getItemId();
		if(id == R.id.action_client)
			startActivity(new Intent(this, ClientActivity.class));
		else if (id == R.id.action_settings)
			startActivity(new Intent(this, Settings.class));
		else if (id == R.id.action_about)
			startActivity(new Intent(this, About.class));
		else if (id == R.id.action_license)
			startActivity(new Intent(this, License.class));
		else
			return false;
		return true;
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		// update the main content by replacing fragments
		String selectItem=mRemoterTitles.get(position);
		if(selectItem.equals("添加遥控器+"))
		{
			Intent mIntent=new Intent();
			mIntent.setClass(MainActivity.this, SetLearnInfoActivity.class);
			MainActivity.this.startActivity(mIntent);
			return;
		}
		Fragment fragment = new RemoterFragment();
		Bundle args = new Bundle();
		args.putInt(RemoterFragment.FRAGMENT_NUMBER, position);
		args.putString(RemoterFragment.FRAGMENT_TITLE, mRemoterTitles.get(position));
		fragment.setArguments(args);

		FragmentManager fragmentManager = getFragmentManager();
		Bundle nBundle = new Bundle();   

		fragment.setArguments(args);
		fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(mRemoterTitles.get(position));
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * Fragment that appears in the "content_frame", shows a planet
	 */
	public static class RemoterFragment extends Fragment  {
		public static final String FRAGMENT_NUMBER = "fragment_number";
		public static final String FRAGMENT_TITLE="fragment_title";
		
		private Context mContext;
		private RelativeLayout mlayout ;
		private View mRootView;
		
		private List<LearnedButton> mLearnButtons=new ArrayList<LearnedButton>(); 
		private String mSelectedGroup;
		
		private Button btAddMore;
		public RemoterFragment() {
			// Empty constructor required for fragment subclasses
		}

		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			mRootView = inflater.inflate(R.layout.fragment_remoter, container, false);
			 Log.d("IRWidget", "frag on Create");
			
			int i = getArguments().getInt(FRAGMENT_NUMBER);
			
			mSelectedGroup=getArguments().getString(FRAGMENT_TITLE);
			mContext=container.getContext();
			mlayout = (RelativeLayout) mRootView.findViewById(R.id.BtnLayout);
			
			
			btAddMore=(Button)mRootView.findViewById(R.id.BtnAddMore);
			btAddMore.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent mIntent=new Intent();
					mIntent.setClass(mContext, Learn.class);
					//TODO 
					mIntent.putExtra("current_remoter_group",mSelectedGroup);
					startActivity(mIntent);
				}
			});
			
			
			
			
			getActivity().setTitle(mSelectedGroup);
			return mRootView;
		}
		
		 public void onStart() {
		        super.onStart();
		        Log.d("IRWidget", "frag on Start");
		        //清空布局 和数据
		        mlayout.removeAllViewsInLayout();
		        mLearnButtons.clear();
		        // During startup, check if there are arguments passed to the fragment.
		        // onStart is a good place to do this because the layout has already been
		        // applied to the fragment at this point so we can safely call the method
		        // below that sets the article text.
		        Log.d("IRWidget", "onStart");
		        Bundle args = getArguments();
		        if (args != null) {
		        	
		        	Cursor cursor= HandleSqlDB.instance.select();
		    		while(cursor.moveToNext())
		    		{
		    			//Log.d("IRWidget", "move next");
		    			int id=cursor.getInt(cursor.getColumnIndex("id"));
		    			String name=cursor.getString(cursor.getColumnIndex("name"));
		    			String display=cursor.getString(cursor.getColumnIndex("display"));
		    			String group=cursor.getString(cursor.getColumnIndex("buttongroup"));
		    			int address=cursor.getInt(cursor.getColumnIndex("address"));
		    			String onAndOffs=cursor.getString(cursor.getColumnIndex("command"));
		    			LearnedCommand lc=new LearnedCommand(address, onAndOffs);
		    			int robotId=cursor.getInt(cursor.getColumnIndex("robotid"));
		    			LearnedButton lb=new LearnedButton(name, display, group, lc,robotId);
		    			lb.id=id;
		    			mLearnButtons.add(lb);
		    		}
		    		HandleSqlDB.instance.close();
		            // Set article based on argument passed in
					String planet = getResources().getString(R.string.green_dot);
					int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
							"drawable", getActivity().getPackageName());
					((ImageView) mRootView.findViewById(R.id.ivBg)).setImageResource(imageId);
					
					int id = 0;
					int numButtons = 0;

					for (final LearnedButton button : mLearnButtons) {
						final LearnedButton thisButton = button;
						// Button newButton = new Button(this);
						// hm, ignores the colors?
						if(!thisButton.getGroup().equals(mSelectedGroup))
							continue;
						Button newButton = new Button(new ContextThemeWrapper(
								mContext, R.style.btnStyleOrange));
						newButton.setText(thisButton.getDisplay());

						newButton.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View view) {
								LearnedCommand learnedCommand = thisButton.getLearnCommand();
								Log.d(this.getClass().getCanonicalName(),
										thisButton.getName() + " pushed, sending "
												+ learnedCommand);
								//mContext.mService.sendLearnedCommand(learnedCommand);
							}
						});
						
						newButton.setOnLongClickListener(new OnLongClickListener() {
							
							@Override
							public boolean onLongClick(View v) {
								// TODO Auto-generated method stub
								 final View view=v;
								 new AlertDialog.Builder(mContext)  
					                .setIcon(R.drawable.icon)  
					                .setTitle("删除此按钮")  
					                .setPositiveButton("确定",  
					                        new DialogInterface.OnClickListener() {  
					                            @Override  
					                            public void onClick(DialogInterface dialog,  
					                                    int which) {  
					                                // TODO Auto-generated method stub  
					                            	HandleSqlDB.getInstant(mContext).delete(String.valueOf(button.id));
					            					mlayout.removeView(view);
					                            }  
					                        }).setNegativeButton("取消", null).create()  
					                .show();  
								return false;
							}
						});

						RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
								RelativeLayout.LayoutParams.WRAP_CONTENT,
								RelativeLayout.LayoutParams.WRAP_CONTENT);

						if (numButtons == 0) {
							relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
							relativeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
						} else {
							if (numButtons % 4 == 0) {
								relativeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
								relativeParams.addRule(RelativeLayout.BELOW, id);
							} else {
								relativeParams.addRule(RelativeLayout.RIGHT_OF, id);
								relativeParams.addRule(RelativeLayout.ALIGN_BOTTOM, id);
							}
						}
						numButtons++;
						id++;
						newButton.setId(id);

						// http://www.mindfreakerstuff.com/2012/09/50-useful-android-custom-button-style-set-1/
						/*
						 * int style = (Integer) null; if(numButtons % 2 == 0)
						 * style=R.style.btnStyleOrange; else
						 * style=R.style.btnStyleBlackpearl;
						 */
						newButton.setLayoutParams(relativeParams);

						mlayout.addView(newButton);
					}
		        }else
		        {
		        	

		        }
		    }
	}

	@Override
	public void receiveData(String data) {
		// TODO Auto-generated method stub
		
	}
}
