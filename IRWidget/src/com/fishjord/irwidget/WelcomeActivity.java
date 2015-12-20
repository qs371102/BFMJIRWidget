package com.fishjord.irwidget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WelcomeActivity extends Activity {
	private Button mBtBeginMatch;
	private Button mBtBeginLearn;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		
		mBtBeginLearn=(Button)findViewById(R.id.btBeginLearn);
		mBtBeginLearn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent();
				mIntent.setClass(WelcomeActivity.this, SetLearnInfoActivity.class);
				WelcomeActivity.this.startActivity(mIntent);
			}
		});
		mBtBeginMatch=(Button)findViewById(R.id.btBeginMatch);
		mBtBeginMatch.setEnabled(false);
		
	}
}
