package com.fishjord.irwidget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SplashActivity extends Activity {

	private boolean isFirstLoad=true;
	//��ҳ��ʱ
	private long delay=50;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		isFirstLoad = isFirstEnter(SplashActivity.this,SplashActivity.this.getClass().getName());
		if(isFirstLoad)
			mHandler.sendEmptyMessageDelayed(SWITCH_GUIDACTIVITY,delay);
		else
			mHandler.sendEmptyMessageDelayed(SWITCH_MAINACTIVITY,delay);
	}   

	//****************************************************************
	// �ж�Ӧ���Ƿ���μ��أ���ȡSharedPreferences�е�guide_activity�ֶ�
	//****************************************************************
	private static final String SHAREDPREFERENCES_NAME = "my_pref";
	private static final String KEY_GUIDE_ACTIVITY = "guide_activity";

	private boolean isFirstEnter(Context context,String className){
		if(context==null || className==null||"".equalsIgnoreCase(className))return false;
		String mResultStr = context.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_WORLD_READABLE)
				.getString(KEY_GUIDE_ACTIVITY, "");//ȡ���������� �� com.my.MainActivity
		if(mResultStr.equalsIgnoreCase("false"))
			return false;
		else
			return true;
	}

	//*************************************************
	// Handler:��ת����ͬҳ��
	//*************************************************
	private final static int SWITCH_MAINACTIVITY = 1000;
	private final static int SWITCH_GUIDACTIVITY = 1001;
	public Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
			case SWITCH_MAINACTIVITY:
				Intent mIntent = new Intent();
				mIntent.setClass(SplashActivity.this, MainActivity.class);
				SplashActivity.this.startActivity(mIntent);
				SplashActivity.this.finish();
				break;
			case SWITCH_GUIDACTIVITY:
				mIntent = new Intent();
				mIntent.setClass(SplashActivity.this, GuideActivity.class);
				SplashActivity.this.startActivity(mIntent);
				SplashActivity.this.finish();
				break;
			}
			super.handleMessage(msg);
		}
	};
}
