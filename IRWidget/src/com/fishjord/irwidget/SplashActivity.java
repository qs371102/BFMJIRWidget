package com.fishjord.irwidget;

import com.bfmj.handledb.HandleSqlDB;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SplashActivity extends Activity {

	private boolean isFirstLoad=true;
	//跳页延时
	private long mDelay=50;
	private HandleSqlDB mHsdb=HandleSqlDB.getInstant(this);
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		HandleSqlDB.getInstant(this);
		isFirstLoad = isFirstEnter(SplashActivity.this,SplashActivity.this.getClass().getName());
		if(isFirstLoad)
			mHandler.sendEmptyMessageDelayed(SWITCH_GUIDACTIVITY,mDelay);
		else
			mHandler.sendEmptyMessageDelayed(SWITCH_MAINACTIVITY,mDelay);
	}   

	//****************************************************************
	// 判断应用是否初次加载，读取SharedPreferences中的guide_activity字段
	//****************************************************************
	private static final String SHAREDPREFERENCES_NAME = "my_pref";
	private static final String KEY_GUIDE_ACTIVITY = "guide_activity";

	private boolean isFirstEnter(Context context,String className){
		if(context==null || className==null||"".equalsIgnoreCase(className))return false;
		String mResultStr = context.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_WORLD_READABLE)
				.getString(KEY_GUIDE_ACTIVITY, "");//取得所有类名 如 com.my.MainActivity
		if(mResultStr.equalsIgnoreCase("false"))
			return false;
		else
			return true;
	}

	//*************************************************
	// Handler:跳转至不同页面
	//*************************************************
	private final static int SWITCH_MAINACTIVITY = 1000;
	private final static int SWITCH_GUIDACTIVITY = 1001;
	public Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
			case SWITCH_MAINACTIVITY:
				Intent mIntent = new Intent();
				if(mHsdb.ifExistCustomerRemoter())
					mIntent.setClass(SplashActivity.this, MainActivity.class);
				else
					mIntent.setClass(SplashActivity.this, WelcomeActivity.class);
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
