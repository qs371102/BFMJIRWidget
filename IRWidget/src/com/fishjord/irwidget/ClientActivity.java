package com.fishjord.irwidget;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bfmj.network.*;


import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Spinner;
import android.widget.Toast;
import tw.com.prolific.driver.pl2303.PL2303Driver;

public class ClientActivity extends Activity implements INetworkCallback {

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
	private NetworkService mService;

	private static final boolean SHOW_DEBUG = true;

	// Defines of Display Settings
	private static final int DISP_CHAR = 0;

	// Linefeed Code Settings
	//    private static final int LINEFEED_CODE_CR = 0;
	private static final int LINEFEED_CODE_CRLF = 1;
	private static final int LINEFEED_CODE_LF = 2;

	PL2303Driver mSerial;
	//TODO
	private Handler mHandler=new Handler(){
		public void handleMessage(Message msg) {
			Toast.makeText(ClientActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
		}
	};
	//    private ScrollView mSvText;
	//   private StringBuilder mText = new StringBuilder();

	String TAG = "com.fishjord.irwidget";

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

	public void onCreate(Bundle savedInstanceState) {

		Log.d(DT, "=========Enter onCreate===========");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_client);

		mService=new NetworkService(this);
		mService.delegate=this;

		Log.d(TAG, "network Service!");
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
		Toast.makeText(this, "======:"+String.valueOf(mSerial.isConnected()), Toast.LENGTH_SHORT).show();
		new android.os.Handler().postDelayed(
				new Runnable() {
					public void run() {
						Log.d(DT, "This'll run 500 milliseconds later");
						openUsbSerial();
					}
				}, 
				500);
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
		else
			Toast.makeText(this, "attached", Toast.LENGTH_SHORT).show();

		Log.d(TAG, "Leave onResume"); 
	}        

	private void openUsbSerial() {
		Log.d(TAG, "Enter  openUsbSerial");

		Toast.makeText(this, "open======:"+String.valueOf(mSerial.isConnected()), Toast.LENGTH_SHORT).show();
		if(null==mSerial)
			return;   	 

		if (mSerial.isConnected()) {
			if (SHOW_DEBUG) {
				Log.d(TAG, "openUsbSerial : isConnected ");
			}
			String str = PL2303HXD_BaudRate_spinner.getSelectedItem().toString();
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

	@Override
	public void receiveData(String data) {
		// TODO Auto-generated method stub
		Log.d(DT, "receive command:"+data);
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
		//		try{
		//			Thread.sleep(5000);
		//		}catch(Exception ex)
		//		{
		//			Log.w(DT, "exception:"+ex.toString());
		//		}
		if( res<0 ) {
			Log.d(DT, "setup2: fail to controlTransfer: "+ res);
			return;
		}else
		{
			Toast.makeText(this, "Write length: "+datas.length+" bytes", Toast.LENGTH_SHORT).show();
			if(!needCallback)
				return;

			pool.execute(tReadCallback);
		}
	}



	//	Handler networkMessageHandler = new Handler() {
	//		public void handleMessage(Message msg) {
	//			mService.sendCommand(msg.obj.toString());
	//			super.handleMessage(msg);
	//		}//handleMessage
	//	};
	//	
	//	private void Send_Network_Message(String mmsg) {
	//		Message m= new Message();
	//		m.obj = mmsg;
	//		networkMessageHandler.sendMessage(m);
	//		Log.d(TAG, String.format("Msg index: %04x", mmsg));
	//		try {
	//			Thread.sleep(1000);
	//		} catch (InterruptedException e) {
	//			e.printStackTrace();
	//		}
	//	}

	private Runnable tReadCallback = new Runnable() {
		public void run() {	
			finalCallBackData=new ArrayList<Byte>();

			startTime=System.currentTimeMillis();

			while(false||(System.currentTimeMillis()-startTime)<interval)
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
		final String[] datas = data.split(":");

		if(datas.length==4)
		{

			mService.SetTargetID(datas[0]);
//TODO
			if(!NetworkService.isMine(datas[1])&&false)
			{
				return null;
			}
			byte[] finalBytes=hexStr2Bytes(datas[2]);
			needCallback= Boolean.valueOf(datas[3]);
			return finalBytes;
		}
		else
		{
			Log.d(TAG, "not mine");
			return null;
		}
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
			//Log.w(DT,"+++++"+message+"+++++");
			if(message.length()!=0)
			{
				//mService.sendCommand(message);
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