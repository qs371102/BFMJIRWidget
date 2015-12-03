package com.fishjord.irwidget;

import com.bfmj.network.NetworkService;
import com.fishjord.irwidget.ir.codes.ControlCommand;
import com.fishjord.irwidget.ir.codes.IRCommand;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class Learn extends Activity {
    
	private NetworkService service;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.learn);
		service=new NetworkService(this);
		Button btnLearn = (Button)findViewById(R.id.btnLearn);
		btnLearn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
//				IRCommand irCommand = new IRCommand(9600, new int[]{0x88,0x00,0x00,0x00,0x88});
//				Log.d(this.getClass().getCanonicalName(),
//						"btnLearn" + " pushed, sending "
//								+ irCommand);
				//service.sendCommand(irCommand);
				int cmd=0x88;
				int[] datas=new int[]{0x02};
				ControlCommand command=new ControlCommand(cmd, datas,true);
				service.sendControlCommand(command);
			}
		}); 
		
		Button btnRepeat = (Button)findViewById(R.id.btnEmmitLearned);
		btnRepeat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				IRCommand irCommand = new IRCommand(9600, new int[]{0x86,0x00,0x00,0x00,0x86});
				Log.d(this.getClass().getCanonicalName(),
						"btnLearn" + " pushed, sending "
								+ irCommand);
				service.sendCommand(irCommand);
			}
		}); 
	}

}
