package com.fishjord.irwidget;

import java.util.ArrayList;

import com.bfmj.network.INetworkCallback;
import com.bfmj.network.NetworkService;
import com.fishjord.irwidget.ir.codes.ControlCommand;
import com.fishjord.irwidget.ir.codes.IRCommand;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class Learn extends Activity implements INetworkCallback {

	private NetworkService service;
	private int[] cmdAddress=new int[]{0x00};

	String TAG = "IRWidget";
	private static final boolean SHOW_DEBUG = true; 


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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

		Button btSave=(Button)findViewById(R.id.btSave);
		btSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//save

			}
		});



	}
	@Override
	public void receiveData(String data) {
		// TODO Auto-generated method stub
		Log.d(TAG, "receive command");
		byte[] datas=parseStringToData(data);

		if(datas==null)
			return;
		for (Byte b : datas) 
		{            	   
			String temp=Integer.toHexString(b&0x000000FF);
			Log.w(TAG, temp+" ");
		}
	}

	public byte[] parseStringToData(String data)
	{
		final String[] datas = data.split(":");
		if(datas.length==3)
		{
			if(service.isMine(datas[1]))
			{
				//service.SetTargetID(datas[0]);
				return null;
			}
			byte[] finalBytes=hexStr2Bytes(datas[2]);
			return finalBytes;
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
