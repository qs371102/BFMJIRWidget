package com.fishjord.irwidget;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import com.bfmj.byteandstringtools.ByteAndStringTools;
import com.bfmj.handledb.HandleSqlDB;
import com.bfmj.network.INetworkCallback;
import com.bfmj.network.NetworkService;
import com.fishjord.irwidget.ir.codes.CodeManager;
import com.fishjord.irwidget.ir.codes.CommandButton;
import com.fishjord.irwidget.ir.codes.ControlCommand;
import com.fishjord.irwidget.ir.codes.IRButton;
import com.fishjord.irwidget.ir.codes.IRCommand;
import com.fishjord.irwidget.ir.codes.LearnedButton;
import com.fishjord.irwidget.ir.codes.LearnedCommand;
import com.fishjord.irwidget.ir.codes.Manufacturer;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;


public class Learn extends Activity implements INetworkCallback {

	private NetworkService service;
	private int[] cmdAddress=new int[]{0x00};
	private String sendCmd="89 ";
	private boolean learnStatus=false;
	private String cmdData="";

	String TAG = "IRWidget";
	private static final boolean SHOW_DEBUG = true; 
	private String selectedIcon;
	private String selectedGroup;

	private String[] icons;

	private String[] groups;

	private CodeManager codeManager;

	private HandleSqlDB hddb;

	Manufacturer Mine;
	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//初始化数据库
		context=this;
		hddb=HandleSqlDB.getInstant(this);
		Log.d(TAG, "========count:======="+hddb.getContactsCount());
		hddb.close();
		cmdAddress[0]=(byte)hddb.getAddressToLearnCommand();
		Log.d(TAG, "=======Address:======"+cmdAddress[0]);
		hddb.close();

		setContentView(R.layout.learn_advance);
		service=new NetworkService(this);
		service.delegate=this;
		Button btLearn=(Button)findViewById(R.id.btLearn);
		btLearn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int cmd=0x88;
				int[] datas=cmdAddress;
				ControlCommand command=new ControlCommand(cmd, datas,true,true,true);
				service.sendControlCommand(command);
			}
		});

		icons=new String[]{"Vol+","Vol-","^","v","Power","Menu","Mute","Ok"};

		groups=new String[]{"TV","Air Conditioning","Refrigerator","Other"};
		
		selectedIcon=icons[0];
		//分组 Spinner

		Spinner spGroup=(Spinner)findViewById(R.id.spGroup);
		ArrayAdapter<String> spGroupArrayAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,groups);
		spGroupArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spGroup.setAdapter(spGroupArrayAdapter);
		spGroup.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				// TODO Auto-generated method stub
				selectedGroup=groups[pos];
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				selectedGroup=groups[0];
			}
		});




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

		Button btSave=(Button)findViewById(R.id.btSave);
		btSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cmdAddress[0]=(byte)hddb.getAddressToLearnCommand();
				if(cmdData=="")
					return;
				LearnedCommand lc=new LearnedCommand(cmdAddress[0], sendCmd+cmdData);
				EditText et=(EditText)findViewById(R.id.etNote);
				LearnedButton lb = new LearnedButton( et.getText().toString(),selectedIcon,selectedGroup,lc);
				Log.d(TAG, "----insert:---======----"+hddb.insert(lb));
				hddb.close();
			}
		});
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
			Log.d(TAG, "Failed!");
			learnStatus=false;
			Button btSave=(Button)findViewById(R.id.btSave);
			btSave.setClickable(true);
			//btSave.setEnabled(true);
		}
		else
		{
			Toast.makeText(this, datas, Toast.LENGTH_LONG).show();
			cmdData=datas;
			learnStatus=true;
			Log.d(TAG, "Succeed!");
			//EditText etNote=(EditText)findViewById(R.id.etNote);
		}
	}

	public void OnPause()
	{

	}

	public void OnStop()
	{

	}

	public void OnDestroy()
	{

	}
}
