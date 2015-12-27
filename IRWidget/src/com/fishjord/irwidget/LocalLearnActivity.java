package com.fishjord.irwidget;

import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tw.com.prolific.driver.pl2303.PL2303Driver;
import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.bfmj.byteandstringtools.ByteAndStringTools;
import com.bfmj.handledb.HandleSqlDB;
import com.bfmj.handledevices.HandleDevices;
import com.bfmj.network.NetworkService;
import com.fishjord.irwidget.ir.codes.ControlCommand;
import com.fishjord.irwidget.ir.codes.LearnedButton;
import com.fishjord.irwidget.ir.codes.LearnedCommand;
import android.content.Intent;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class LocalLearnActivity extends Activity {

	
	private ExecutorService pool = Executors.newFixedThreadPool(2); 
	//---------------------------------------------------
	private int formerCallbackCount;
	private List<Byte> finalCallBackData;

	private boolean needCallback;
	
	private long startTime;
	//20s
	private final long interval= (long) 20000;
	// debug settings


	private static final String NULL = null;   

	// Linefeed
	//    private final static String BR = System.getProperty("line.separator");

	private String strStr;
	
	//============================================================
	//private NetworkService service;
	private int[] cmdAddress=new int[]{0x00};
	private String cmdData="";
	
	static String TAG = "IRWidget";
	
	private String selectedIcon;
	//
	private String selectedGroup;

	private String[] icons;


	private HandleSqlDB hddb;

	private Button btLearn;
	private Button btSave;
	private Button btFinish;
	
	private TextView tvTitle;

	private Handler mHandler=new Handler()
			{
				public void handleMessage(Message msg)
				{
					String data=msg.obj.toString();
					Log.d(TAG, "++++++++++++++++++++++++receive command");
					String datas=data;
					Log.d(TAG,"-------------"+ data);
					if(datas==null)
						return;

					if(datas.equals("e0"))
					{
						Toast.makeText(LocalLearnActivity.this, "超时！未能成功学习", Toast.LENGTH_LONG).show();
						btLearn.setEnabled(true);
						btSave.setEnabled(false);
					}
					else
					{
						Toast.makeText(LocalLearnActivity.this, datas, Toast.LENGTH_LONG).show();
						cmdData=datas;
						btSave.setEnabled(true);
					}
				}
			};
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.learn_advance);
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
		
		
//		service=new NetworkService(this);
//		service.delegate=this;
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
				//service.sendControlCommand(command);
				receiveData(command.toString());
				new android.os.Handler().postDelayed(
						new Runnable() {
							public void run() {
								Log.d(TAG,"Delay");
								btLearn.setEnabled(true);
								//receiveData("88 00 00 00 88");
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
				Intent intent=new Intent(LocalLearnActivity.this,MainActivity.class);
				//intent.putExtra(getResources().getString(R.string.EXTRA_REMOTER_NAME),mEtRemoterName.getText());
				startActivity(intent);
				LocalLearnActivity.this.finish();
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
					Toast.makeText(LocalLearnActivity.this, "备注不能为空", Toast.LENGTH_LONG).show();
					return;
				}
				if(hddb==null)
					hddb=HandleSqlDB.getInstant(LocalLearnActivity.this);

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
		
		Log.d(TAG, "=========Enter onCreate===========");


		//mService=new NetworkService(this);
		//mService.delegate=this;
		//Log.d(TAG, "network Service!");
		
		// get service

		HandleDevices.initSerial(this);
		
		new android.os.Handler().postDelayed(
				new Runnable() {
					public void run() {
						Log.d(TAG, "This'll run 500 milliseconds later");
						HandleDevices.openUsbSerial();
						//receiveData("88 00 00 00 88");
					}
				}, 
				500);


		
	}

	void updateSelectedIcon(int pos)
	{
		selectedIcon=icons[pos];
	}
	
	protected void onStop() {
		Log.d(TAG, "Enter onStop");
		super.onStop();        
		Log.d(TAG, "Leave onStop");
	}    

	@Override
	protected void onDestroy() {
		Log.d(TAG, "Enter onDestroy");   

		if(HandleDevices.mSerial!=null) {
			HandleDevices.mSerial.end();
			HandleDevices.mSerial = null;
		}    	

		super.onDestroy();        
		Log.d(TAG, "Leave onDestroy");
	}    

	public void onStart() {
		Log.d(TAG, "Enter onStart");
		super.onStart();
		Log.d(TAG, "Leave onStart");
	}

	public void onResume() {
		Log.d(TAG, "Enter onResume"); 
		super.onResume();
		String action =  getIntent().getAction();
		Log.d(TAG, "onResume:"+action);

		//if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action))        
		if(!HandleDevices.mSerial.isConnected()) {
			
			if( !HandleDevices.mSerial.enumerate() ) {

				Toast.makeText(this, "no more devices found", Toast.LENGTH_SHORT).show();     
				return;
			} else {
				Log.d(TAG, "onResume:enumerate succeeded!");
			}    		 
		}//if isConnected  
		Toast.makeText(this, "attached", Toast.LENGTH_SHORT).show();

		Log.d(TAG, "Leave onResume"); 
	}        

	
	
	
	//FIXME 
	public void receiveData(String data) {
		// TODO Auto-generated method stub
		String strWrite = data;
		Log.d(TAG, "HandleDevices.mSerial.write  res:"+data);
		byte[] datas=parseStringToData(data);
		if(datas==null)
			return;

		if(null==HandleDevices.mSerial)
			return;

		if(!HandleDevices.mSerial.isConnected()) 
			return;
		
		int res = HandleDevices.mSerial.write(datas, datas.length);
		
		Log.d(TAG, "HandleDevices.mSerial.write  res:"+res);
		if( res<0 ) {
			Log.d(TAG, "setup2: fail to controlTransfer: "+ res);
			return;
		}else
		{
			Toast.makeText(this, "Write length: "+datas.length+" bytes", Toast.LENGTH_SHORT).show();
			pool.execute(tReadCallback);
		}
	}
	
	private Runnable tReadCallback = new Runnable() {
		public void run() {	
			finalCallBackData=new ArrayList<Byte>();

			startTime=System.currentTimeMillis();
			
			//while(false||(System.currentTimeMillis()-startTime)<interval)
			while(true)
			{
				if(!ifContinueWaitToReadCallBackFromSerial())
				{
					break;
				}
				try{
					Thread.sleep(1000);
				}catch(Exception ex)
				{
					Log.w(TAG, "exception:"+ex.toString());
				}
			}
		}
	};
	
	//TODO tmp
	public byte[] parseStringToData(String data)
	{
		Log.d(TAG, "mSerial is connected:"+HandleDevices.mSerial.isConnected());
		final String[] datas = data.split(":");

		if(datas.length==2)
		{
			Log.d(TAG, datas[0]);
			byte[] finalBytes=HandleDevices.hexStr2Bytes(datas[0]);
			
			return finalBytes;
		}
		else
		{
			Log.d(TAG, "not mine");
			return null;
		}
	}
	
//	public byte[] parseStringToData(String data)
//	{
//		byte[] finalBytes=HandleDevices.hexStr2Bytes(data);
//		return finalBytes;
//	}


	public boolean ifContinueWaitToReadCallBackFromSerial()
	{
		int len;
		byte[] rbuf = new byte[1024];

		if(null==HandleDevices.mSerial)
			return false;        

		if(!HandleDevices.mSerial.isConnected()) 
			return false;

		Log.d(TAG, "Connected");

		len = HandleDevices.mSerial.read(rbuf);

		if(len<0) {
			Log.d(TAG, "Fail to bulkTransfer(read data)");
			return true;
		}
		//符合条件读取结束
		if(formerCallbackCount>0&&len==0)
		{
			//置0
			formerCallbackCount=len;
			
			//Log.w(TAG, "finalCallbackData:"+formerCallbackCount);

			String message="";
			for (int i=0;i<finalCallBackData.size();i++) 
			{   
				Byte b=finalCallBackData.get(i);
				String temp=Integer.toHexString(b&0x000000FF);
				if(i+1<finalCallBackData.size())
					message+=temp+" ";
				else
					message+=temp;
				//Log.d(TAG, " "+temp+" ");
			}
			Log.w(TAG,"+++++"+message+"+++++");
			if(message.length()!=0)
			{
				//TODO FIXME
				Message msg=new Message();
				msg.obj=message;
				mHandler.sendMessage(msg);
				//cmdData=message;
				//btSave.setEnabled(true);
				//mService.sendCommand(message);
				//Toast.makeText(this.getApplicationContext(),message,Toast.LENGTH_LONG).show();
			}
			else
				return true;
			//Log.d(TAG,"fcbd"+ finalCallBackData.toString());
			return false;
		}

		formerCallbackCount=len;
		

		if (len > 0) {        	
			
			Log.d(TAG, "=================start=================");
			for (int j = 0; j < len; j++) {            	   
				String temp=Integer.toHexString(rbuf[j]&0x000000FF);
				//Log.w(TAG, "str_rbuf["+j+"]="+temp);
				finalCallBackData.add(rbuf[j]);
			}
			Log.d(TAG, "=================end===================");
		}
		else {     	
			return true;
		}

		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Log.d(TAG, "Leave readDataFroHandleDevices.mSerial");	
		return true;
	}

}
