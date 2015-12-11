package com.fishjord.irwidget;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import com.bfmj.network.INetworkCallback;
import com.bfmj.network.NetworkService;
import com.fishjord.irwidget.ir.codes.CodeManager;
import com.fishjord.irwidget.ir.codes.ControlCommand;
import com.fishjord.irwidget.ir.codes.IRCommand;
import com.fishjord.irwidget.ir.codes.Manufacturer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;


public class Learn extends Activity implements INetworkCallback {

	private NetworkService service;
	private int[] cmdAddress=new int[]{0x00};

	String TAG = "IRWidget";
	private static final boolean SHOW_DEBUG = true; 
	private String selectedIcon;
	
	private String[] icons;

	private CodeManager codeManager;
	Manufacturer Mine;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			codeManager=CodeManager.getInstance(this.getApplicationContext());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		selectedIcon=icons[0];
		
		Spinner spinner = (Spinner) findViewById(R.id.spIcon);
		Log.d(this.getClass().getCanonicalName(), "Spinner: " + spinner);

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
				Mine= codeManager.getManufacturer("Mine");
				
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
		String datas=parseStringToData(data);

		if(datas==null)
			return;
		
		if(datas.equals("e0"))
		{
			Log.d(TAG, "Failed!");
		}
		else
		{
			Log.d(TAG, "Succeed!");
		}
		Log.w(TAG,"+++++"+datas+"+++++");
	}

	public static String byteArrayToHex(byte[] a) {
		   StringBuilder sb = new StringBuilder(a.length * 2);
		   for(byte b: a)
		      sb.append(String.format("%02x", b & 0xff));
		   return sb.toString();
	}
	
	public String parseStringToData(String data)
	{
		Log.d(TAG, data);
		final String[] datas = data.split(":");
		if(datas.length==3)
		{
			if(!service.isMine(datas[1]))
			{
				//service.SetTargetID(datas[0]);
				Log.d(TAG, "is not Mine");
				return null;
			}
			Log.d(TAG,datas[2]+"=======");
			Toast.makeText(this, datas[2], Toast.LENGTH_LONG).show();
			return datas[2];
		}
		else
			return null;
	}


	public static byte[] hexStr2Bytes(String src){  

		String[] datas = src.trim().split(",");  

		byte[] finalData=new byte[datas.length];
		for (int i=0;i<datas.length;i++) {

			int tmp=Integer.parseInt(datas[i]);
			finalData[i]=(byte)tmp;
		}
		return finalData;  
	}

}
