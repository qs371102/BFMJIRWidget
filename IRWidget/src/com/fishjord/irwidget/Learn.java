package com.fishjord.irwidget;

import com.bfmj.network.NetworkService;
import com.fishjord.irwidget.ir.codes.ControlCommand;
import com.fishjord.irwidget.ir.codes.IRCommand;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class Learn extends Activity {
    
	private NetworkService service;
	private int[] cmdAddress=new int[]{0x00};;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.learn);
		service=new NetworkService(this);
		Button btnLearn = (Button)findViewById(R.id.btnLearn);
		btnLearn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				int cmd=0x88;
				int[] datas=cmdAddress;
				ControlCommand command=new ControlCommand(cmd, datas,true,true,true);
				service.sendControlCommand(command);
			}
		}); 
		
		Button btnRepeat = (Button)findViewById(R.id.btnEmmitLearned);
		btnRepeat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				int cmd=0x86;
				int[] datas=cmdAddress;
				ControlCommand command=new ControlCommand(cmd, datas,true,true,false);
				service.sendControlCommand(command);
			}
		}); 
		
		Button btnExport=(Button)findViewById(R.id.btnExport);
		btnExport.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				int cmd=0x8A;
				int[] datas=cmdAddress;
				ControlCommand command=new ControlCommand(cmd, datas,true,false,true);
				service.sendControlCommand(command);
			}
		});
		
		Button btnDownload=(Button)findViewById(R.id.btnDownload);
		btnDownload.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int cmd=0x89;
				int[] datas=new int[]{0x00};
				ControlCommand command=new ControlCommand(cmd, datas,false,false,false);
				service.sendControlCommand(command);
			}
		});
	}

}
