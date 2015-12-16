package com.fishjord.irwidget;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.bfmj.byteandstringtools.ByteAndStringTools;
import com.bfmj.handledb.HandleSqlDB;
import com.bfmj.network.INetworkCallback;
import com.bfmj.network.NetworkService;
import com.fishjord.irwidget.ir.codes.IRButton;
import com.fishjord.irwidget.ir.codes.IRCommand;
import com.fishjord.irwidget.ir.codes.LearnedButton;
import com.fishjord.irwidget.ir.codes.LearnedCommand;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class ShowLearned extends Activity implements INetworkCallback {
	
	private NetworkService service;
	
	private Map<String,List<LearnedButton>> groupsAndButtons=new LinkedHashMap<String, List<LearnedButton>>();;
	private List<LearnedButton> lbs=new ArrayList<LearnedButton>();  
	private List<String> groups=new ArrayList<String>();
	private String selectedGroup;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showlearned);
		service=new NetworkService(this);
		service.delegate=this;
		
		HandleSqlDB hddb=HandleSqlDB.getInstant(this);
		Cursor cursor=hddb.getGroups();
		Log.d("IRWidget", "get group");
		while(cursor.moveToNext())
		{
			String group=cursor.getString(cursor.getColumnIndex("buttonGroup"));
			groups.add(group);
		}
		hddb.close();
		Log.d("IRWidget", "get All");
		cursor= hddb.select();
		while(cursor.moveToNext())
		{
			int id=cursor.getInt(cursor.getColumnIndex("id"));
			String name=cursor.getString(cursor.getColumnIndex("name"));
			String display=cursor.getString(cursor.getColumnIndex("display"));
			String group=cursor.getString(cursor.getColumnIndex("buttonGroup"));
			int address=cursor.getInt(cursor.getColumnIndex("address"));
			String onAndOffs=cursor.getString(cursor.getColumnIndex("command"));
			LearnedCommand lc=new LearnedCommand(address, onAndOffs);
			LearnedButton lb=new LearnedButton(name, display, group, lc);
			lb.id=id;
			lbs.add(lb);
		}
		hddb.close();
		Log.d("IRWidget","size:"+lbs.size());
		
		Spinner spGroup=(Spinner)findViewById(R.id.spMyGroup);
		ArrayAdapter<String> spGroupArrayAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,groups);
		spGroupArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spGroup.setAdapter(spGroupArrayAdapter);
		spGroup.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				// TODO Auto-generated method stub
				Log.d("IRWidget", "onItemSelected");
				selectedGroup=groups.get(pos);
				updateSelectedGroup((String) parent
						.getItemAtPosition(pos));
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				selectedGroup=groups.get(0);
			}
		});
		
		
		
	}
	
	public void updateSelectedGroup(String manny) 
	{
		Log.d("IRWidget","======="+manny);
		final RelativeLayout layout = (RelativeLayout) findViewById(R.id.lbLayout);
		layout.removeAllViewsInLayout();

		int id = 0;
		int numButtons = 0;

		for (final LearnedButton button : lbs) {
			final LearnedButton thisButton = button;
			// Button newButton = new Button(this);
			// hm, ignores the colors?
			if(!thisButton.getGroup().equals(manny))
				continue;
			Button newButton = new Button(new ContextThemeWrapper(
					ShowLearned.this, R.style.btnStyleOrange));
			newButton.setText(thisButton.getDisplay());

			newButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					LearnedCommand learnedCommand = thisButton.getLearnCommand();
					Log.d(this.getClass().getCanonicalName(),
							thisButton.getName() + " pushed, sending "
									+ learnedCommand);
					service.sendLearnedCommand(learnedCommand);
				}
			});
			
			newButton.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					 final View view=v;
					 new AlertDialog.Builder(ShowLearned.this)  
		                .setIcon(R.drawable.icon)  
		                .setTitle("删除此按钮")  
		                .setPositiveButton("确定",  
		                        new DialogInterface.OnClickListener() {  
		                            @Override  
		                            public void onClick(DialogInterface dialog,  
		                                    int which) {  
		                                // TODO Auto-generated method stub  
		                            	HandleSqlDB.getInstant(ShowLearned.this).delete(String.valueOf(button.id));
		            					layout.removeView(view);
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

			layout.addView(newButton);
		}
	}

	@Override
	public void receiveData(String data) {
		// TODO Auto-generated method stub
		Log.d("IRWidget", "receive command:");
		String datas=ByteAndStringTools.parseStringToData(data);

		if(datas==null)
			return;
		Log.d("IRWidget", datas);
	}

}
