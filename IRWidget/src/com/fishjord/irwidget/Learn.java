package com.fishjord.irwidget;

import com.bfmj.byteandstringtools.ByteAndStringTools;
import com.bfmj.handledb.HandleSqlDB;
import com.bfmj.handledevices.HandleDevices;
import com.bfmj.network.INetworkCallback;
import com.bfmj.network.NetworkService;
import com.fishjord.irwidget.ir.codes.ControlCommand;
import com.fishjord.irwidget.ir.codes.LearnedButton;
import com.fishjord.irwidget.ir.codes.LearnedCommand;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;


public class Learn extends Activity implements INetworkCallback {

	private NetworkService service;
	private int[] cmdAddress=new int[]{0x00};
	private String cmdData="";
	String TAG = "IRWidget";
	private String selectedIcon;


	private String selectedGroup;

	private String[] icons;


	private HandleSqlDB hddb;

	private Button btLearn;
	private Button btSave;
	private Button btFinish;
	
	private TextView tvTitle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();

		if(intent.hasExtra("current_remoter_group"))
		{
			selectedGroup=intent.getStringExtra("current_remoter_group");
		}
		else
		{
			String extra_select_remoter_type = intent.getStringExtra(getResources().getString(R.string.EXTRE_SELECT_REMOTER_TYPE));
			String extra_remoter_name=intent.getStringExtra(getResources().getString(R.string.EXTRA_REMOTER_NAME));
			selectedGroup=extra_remoter_name+" "+extra_select_remoter_type;
		}
		
		setContentView(R.layout.learn_advance);
		service=new NetworkService(this);
		service.delegate=this;
		btLearn=(Button)findViewById(R.id.btLearn);
		Log.d(TAG, btLearn.getText().toString());
		btLearn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btLearn.setEnabled(false);
				btSave.setEnabled(false);
				int cmd=0x88;
				int[] datas=cmdAddress;
				ControlCommand command=new ControlCommand(cmd, datas,true,true,true);
				service.sendControlCommand(command);

				new android.os.Handler().postDelayed(
						new Runnable() {
							public void run() {
								Log.d(TAG,"Delay");
								btLearn.setEnabled(true);
							}
						}, 
						20000);
			}
		});

		icons=new String[]{"Vol+","Vol-","^","v","Power","Menu","Mute","Ok"};

		selectedIcon=icons[0];

		//图标 Spinner
		Spinner spinner = (Spinner) findViewById(R.id.spIcon);
		//Log.d(this.getClass().getCanonicalName(), "Spinner: " + spinner);

		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, icons);
		spinnerArrayAdapter
		.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
		spinner.setAdapter(spinnerArrayAdapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				selectedIcon=icons[pos];
				Log.i(this.getClass().getCanonicalName(), "Item selected: "
						+ selectedIcon);
			}

			public void onNothingSelected(AdapterView<?> parent) {
				selectedIcon=icons[0];
				Log.i(this.getClass().getCanonicalName(), "No selected: "
						+ selectedIcon);
			}

		});
		btFinish=(Button)findViewById(R.id.btFinish);
		btFinish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(Learn.this,MainActivity.class);
				//intent.putExtra(getResources().getString(R.string.EXTRA_REMOTER_NAME),mEtRemoterName.getText());
				startActivity(intent);
				Learn.this.finish();
			}
		});



		btSave=(Button)findViewById(R.id.btSave);
		btSave.setEnabled(false);
		btSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EditText et=(EditText)findViewById(R.id.etNote);
				if(et.getText().equals(""))
				{
					Toast.makeText(Learn.this, "备注不能为空", Toast.LENGTH_LONG).show();
					return;
				}
				if(hddb==null)
					hddb=HandleSqlDB.getInstant(Learn.this);

				cmdAddress[0]=(byte)hddb.getAddressToLearnCommand();
				LearnedCommand lc=new LearnedCommand(cmdAddress[0],cmdData.trim());
				btLearn.setEnabled(true);
				btSave.setEnabled(false);
				LearnedButton lb = new LearnedButton( et.getText().toString(),selectedIcon,selectedGroup,lc,HandleDevices.getRobotID());
				hddb.insert(lb);
				hddb.close();
			}
		});
		
		tvTitle=(TextView)findViewById(R.id.tvRT);
		tvTitle.setText(selectedGroup);
	}

	void updateSelectedIcon(int pos)
	{
		selectedIcon=icons[pos];
	}



	@Override
	public void receiveData(String data) {
		// TODO Auto-generated method stub
		Log.d(TAG, "receive command");
		String datas=ByteAndStringTools.parseStringToData(data);

		if(datas==null)
			return;

		if(datas.equals("e0"))
		{
			Toast.makeText(Learn.this, "超时！未能成功学习", Toast.LENGTH_LONG).show();
			btLearn.setEnabled(true);
			btSave.setEnabled(false);
		}
		else
		{
			Toast.makeText(this, datas, Toast.LENGTH_LONG).show();
			cmdData=datas;
			btSave.setEnabled(true);
		}
	}
}
