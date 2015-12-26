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
import android.util.Log;
import android.view.View;


import com.bfmj.handledb.HandleSqlDB;
import com.bfmj.handledevices.HandleDevices;
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

private static String stringToSplite="\\s+";
	
	private ExecutorService pool = Executors.newFixedThreadPool(2); 
	//---------------------------------------------------
	private int formerCallbackCount;
	private List<Byte> finalCallBackData;

	private boolean needCallback;
	
	private long startTime;
	//20s
	private final long interval= (long) 20000;
	// debug settings
	// private static final boolean SHOW_DEBUG = false;
	//private NetworkService mService;

	private static final boolean SHOW_DEBUG = true;

	// Defines of Display Settings
	private static final int DISP_CHAR = 0;

	// Linefeed Code Settings
	//    private static final int LINEFEED_CODE_CR = 0;
	private static final int LINEFEED_CODE_CRLF = 1;
	private static final int LINEFEED_CODE_LF = 2;

	PL2303Driver mSerial;

	//    private ScrollView mSvText;
	//   private StringBuilder mText = new StringBuilder();

	String DT="Debug";
	
	private int mDisplayType = DISP_CHAR;
	private int mReadLinefeedCode = LINEFEED_CODE_LF;
	private int mWriteLinefeedCode = LINEFEED_CODE_LF;

	//BaudRate.B4800, DataBits.D8, StopBits.S1, Parity.NONE, FlowControl.RTSCTS
	private PL2303Driver.BaudRate mBaudrate = PL2303Driver.BaudRate.B9600;
	private PL2303Driver.DataBits mDataBits = PL2303Driver.DataBits.D8;
	private PL2303Driver.Parity mParity = PL2303Driver.Parity.NONE;
	private PL2303Driver.StopBits mStopBits = PL2303Driver.StopBits.S1;
	private PL2303Driver.FlowControl mFlowControl = PL2303Driver.FlowControl.OFF;


	private static final String ACTION_USB_PERMISSION = "com.fishjord.irwidget.USB_PERMISSION";

	private static final String NULL = null;   

	// Linefeed
	//    private final static String BR = System.getProperty("line.separator");

	public Spinner PL2303HXD_BaudRate_spinner;
	public int PL2303HXD_BaudRate;
	public String PL2303HXD_BaudRate_str="B4800";

	private String strStr;
	
	//============================================================
	//private NetworkService service;
	private int[] cmdAddress=new int[]{0x00};
	private String cmdData="";
	String TAG = "IRWidget";
	private String selectedIcon;
	//
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
		
		Log.d(DT, "=========Enter onCreate===========");


		//mService=new NetworkService(this);
		//mService.delegate=this;
		//Log.d(TAG, "network Service!");
		
		// get service
		mSerial = new PL2303Driver((UsbManager) getSystemService(Context.USB_SERVICE),
				this, ACTION_USB_PERMISSION); 

		// check USB host function.
		if (!mSerial.PL2303USBFeatureSupported()) {

			Toast.makeText(this, "No Support USB host API", Toast.LENGTH_SHORT)
			.show();

			Log.d(TAG, "No Support USB host API");

			mSerial = null;

		}

		Log.d(TAG, "Leave onCreate");

		new android.os.Handler().postDelayed(
				new Runnable() {
					public void run() {
						Log.d(DT, "This'll run 500 milliseconds later");
						openUsbSerial();
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

		if(mSerial!=null) {
			mSerial.end();
			mSerial = null;
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
		if(!mSerial.isConnected()) {
			if (SHOW_DEBUG) {
				Log.d(TAG, "New instance : " + mSerial);
			}
			
			if( !mSerial.enumerate() ) {

				Toast.makeText(this, "no more devices found", Toast.LENGTH_SHORT).show();     
				return;
			} else {
				Log.d(TAG, "onResume:enumerate succeeded!");
			}    		 
		}//if isConnected  
		Toast.makeText(this, "attached", Toast.LENGTH_SHORT).show();

		Log.d(TAG, "Leave onResume"); 
	}        

	private void openUsbSerial() {
		Log.d(TAG, "Enter  openUsbSerial");


		if(null==mSerial)
			return;   	 

		if (mSerial.isConnected()) {
			if (SHOW_DEBUG) {
				Log.d(TAG, "openUsbSerial : isConnected ");
			}
			String str = "9600";
			int baudRate= Integer.parseInt(str);
			switch (baudRate) {
			case 9600:
				mBaudrate = PL2303Driver.BaudRate.B9600;
				break;
			case 19200:
				mBaudrate =PL2303Driver.BaudRate.B19200;
				break;
			case 115200:
				mBaudrate =PL2303Driver.BaudRate.B115200;
				break;
			default:
				mBaudrate =PL2303Driver.BaudRate.B9600;
				break;
			}   		            
			Log.d(TAG, "baudRate:"+baudRate);
			// if (!mSerial.InitByBaudRate(mBaudrate)) {
			if (!mSerial.InitByBaudRate(mBaudrate,700)) {
				if(!mSerial.PL2303Device_IsHasPermission()) {
					Toast.makeText(this, "cannot open, maybe no permission", Toast.LENGTH_SHORT).show();		
				}

				if(mSerial.PL2303Device_IsHasPermission() && (!mSerial.PL2303Device_IsSupportChip())) {
					Toast.makeText(this, "cannot open, maybe this chip has no support, please use PL2303HXD / RA / EA chip.", Toast.LENGTH_SHORT).show();
				}
			} else {        	

				Toast.makeText(this, "connected : " , Toast.LENGTH_SHORT).show(); 	

			}
		}//isConnected

		Log.d(TAG, "Leave openUsbSerial");


		//----------------------------------

	}//openUsbSerial
	
	
	//FIXME 
	public void receiveData(String data) {
		// TODO Auto-generated method stub
		data="88 00 00 00 88";
		Log.d(DT, "-----receive command----------:"+data);
		String strWrite = data;

		byte[] datas=parseStringToData(data);
		if(datas==null)
			return;

		if(null==mSerial)
			return;

		if(!mSerial.isConnected()) 
			return;
		
		int res = mSerial.write(datas, datas.length);
		
		Log.d(DT, "mSerial.write  res:"+res);
		if( res<0 ) {
			Log.d(DT, "setup2: fail to controlTransfer: "+ res);
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
					Log.w(DT, "exception:"+ex.toString());
				}
			}
		}
	};
	
	//TODO tmp

	public byte[] parseStringToData(String data)
	{
		byte[] finalBytes=hexStr2Bytes(data);
		return finalBytes;
	}


	public static byte[] hexStr2Bytes(String src){  

		String[] datas = src.trim().split(stringToSplite);  

		byte[] finalData=new byte[datas.length];
		Log.d("PL2303HXD_APLog", "ex:"+datas.length);
		for (int i=0;i<datas.length;i++) {

			int tmp=Integer.parseInt(datas[i],16);
			//Log.d("Debug", i+"==="+tmp);
			finalData[i]=(byte)tmp;
		}
		return finalData;  
	}  

	public boolean ifContinueWaitToReadCallBackFromSerial()
	{
		int len;
		byte[] rbuf = new byte[1024];

		if(null==mSerial)
			return false;        

		if(!mSerial.isConnected()) 
			return false;

		Log.d(TAG, "Connected");

		len = mSerial.read(rbuf);

		if(len<0) {
			Log.d(DT, "Fail to bulkTransfer(read data)");
			return true;
		}
		//符合条件读取结束
		if(formerCallbackCount>0&&len==0)
		{
			//置0
			formerCallbackCount=len;
			
			//Log.w(DT, "finalCallbackData:"+formerCallbackCount);

			String message="";
			for (int i=0;i<finalCallBackData.size();i++) 
			{   
				Byte b=finalCallBackData.get(i);
				String temp=Integer.toHexString(b&0x000000FF);
				if(i+1<finalCallBackData.size())
					message+=temp+" ";
				else
					message+=temp;
				//Log.d(DT, " "+temp+" ");
			}
			Log.w(DT,"+++++"+message+"+++++");
			if(message.length()!=0)
			{
				//TODO FIXME
				//mService.sendCommand(message);
				//Toast.makeText(this.getApplicationContext(),message,Toast.LENGTH_LONG).show();
			}
			else
				return true;
			//Log.d(DT,"fcbd"+ finalCallBackData.toString());
			return false;
		}

		formerCallbackCount=len;
		

		if (len > 0) {        	
			if (SHOW_DEBUG) {
				Log.d(DT, "read len >0 : " + len);
			}                

			Log.d(DT, "=================start=================");
			for (int j = 0; j < len; j++) {            	   
				String temp=Integer.toHexString(rbuf[j]&0x000000FF);
				//Log.w(DT, "str_rbuf["+j+"]="+temp);
				finalCallBackData.add(rbuf[j]);
			}
			Log.d(DT, "=================end===================");
		}
		else {     	
			if (SHOW_DEBUG&&false) {
				Log.d(DT, "read len : 0 ");
			}
			return true;
		}

		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Log.d(DT, "Leave readDataFromSerial");	
		return true;
	}

}
