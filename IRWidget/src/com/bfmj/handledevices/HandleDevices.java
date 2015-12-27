package com.bfmj.handledevices;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;
import tw.com.prolific.driver.pl2303.PL2303Driver;

public class HandleDevices {
	private static String TAG="IRWidget";
	public static PL2303Driver mSerial;
	private static String stringToSplite="\\s+";
	private static PL2303Driver.BaudRate mBaudrate = PL2303Driver.BaudRate.B9600;
	
	//private NetworkService mService;

	private static final boolean SHOW_DEBUG = true;


	//BaudRate.B4800, DataBits.D8, StopBits.S1, Parity.NONE, FlowControl.RTSCTS
	private PL2303Driver.DataBits mDataBits = PL2303Driver.DataBits.D8;
	private PL2303Driver.Parity mParity = PL2303Driver.Parity.NONE;
	private PL2303Driver.StopBits mStopBits = PL2303Driver.StopBits.S1;
	private PL2303Driver.FlowControl mFlowControl = PL2303Driver.FlowControl.OFF;


	public static final String ACTION_USB_PERMISSION = "com.fishjord.irwidget.USB_PERMISSION";
	
	public static void initSerial(Context context)
	{
		HandleDevices.mSerial = new PL2303Driver((UsbManager) context.getSystemService(Context.USB_SERVICE),
				context,HandleDevices.ACTION_USB_PERMISSION); 

		// check USB host function.
		if (!HandleDevices.mSerial.PL2303USBFeatureSupported()) {

			Toast.makeText(context, "No Support USB host API", Toast.LENGTH_SHORT)
			.show();

			Log.d(TAG, "No Support USB host API");

			HandleDevices.mSerial = null;

		}

		Log.d(TAG, "Leave onCreate");
	}
	
	public static void writeToSerial(String data)
	{
		byte[] datas=hexStr2Bytes(data);
		if(datas==null)
			return;

		if(null==HandleDevices.mSerial)
			return;

		if(!HandleDevices.mSerial.isConnected()) 
			return;
		
		HandleDevices.mSerial.write(datas, datas.length);
	}
	
	public static String getSelfID()
	{
		return "MJRobot";
	}
	
	public static String getTargetID()
	{
		return "Master";
	}
	
	public static int getRobotID()
	{
		return 1;
	}
	
	public static byte[] hexStr2Bytes(String src){  

		String[] datas = src.trim().split(stringToSplite);  

		byte[] finalData=new byte[datas.length];
		Log.d("IRWidget", "ex:"+datas.length);
		for (int i=0;i<datas.length;i++) {

			int tmp=Integer.parseInt(datas[i],16);
			//Log.d("Debug", i+"==="+tmp);
			finalData[i]=(byte)tmp;
		}
		return finalData;  
	} 
	
	public static void openUsbSerial() {
		Log.d(TAG, "Enter  openUsbSerial");


		if(null==HandleDevices.mSerial)
			return;   	 

		if (HandleDevices.mSerial.isConnected()) {
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
			// if (!HandleDevices.mSerial.InitByBaudRate(mBaudrate)) {
			if (!HandleDevices.mSerial.InitByBaudRate(mBaudrate,700)) {
				if(!HandleDevices.mSerial.PL2303Device_IsHasPermission()) {
					//Toast.makeText(this, "cannot open, maybe no permission", Toast.LENGTH_SHORT).show();		
				}

				if(HandleDevices.mSerial.PL2303Device_IsHasPermission() && (!HandleDevices.mSerial.PL2303Device_IsSupportChip())) {
					//Toast.makeText(this, "cannot open, maybe this chip has no support, please use PL2303HXD / RA / EA chip.", Toast.LENGTH_SHORT).show();
				}
			} else {        	

				//Toast.makeText(this, "connected : " , Toast.LENGTH_SHORT).show(); 	

			}
		}//isConnected

		Log.d(TAG, "Leave openUsbSerial");


		//----------------------------------

	}//openUsbSerial
}
