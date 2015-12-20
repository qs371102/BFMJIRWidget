package com.fishjord.irwidget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


public class SetLearnInfoActivity extends Activity
{
//	private static String EXTRE_SELECT_REMOTER_TYPE;
//	private static String EXTRA_REMOTER_NAME;
	private Button mBtNext;
	private Spinner mSpRemoterType;
	private EditText mEtRemoterName;
	private String mSelectType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome_setlearninfo);
		mBtNext=(Button)findViewById(R.id.btNext);
		mBtNext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// TODO Auto-generated method stub
				if(validate())
				{
					Log.d("IRWidget", "=============oooooooo============");
					Intent intent=new Intent(SetLearnInfoActivity.this,Learn.class);
					intent.putExtra(getResources().getString(R.string.EXTRE_SELECT_REMOTER_TYPE),mEtRemoterName.getText());
					intent.putExtra(getResources().getString(R.string.EXTRA_REMOTER_NAME),mEtRemoterName.getText());
					startActivity(intent);
				}
				
			}
		});
		final String[] types=getResources().getStringArray(R.array.remoter_types);
		mSpRemoterType=(Spinner)findViewById(R.id.spRemoterType);
		ArrayAdapter<String> adp=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,types);
		mSpRemoterType.setAdapter(adp);
		
		mSpRemoterType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				mSelectType=types[arg2];
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				mSelectType=types[0];
			}
		});
		
		mEtRemoterName=(EditText)findViewById(R.id.etRemoterName);
		
	}
	private boolean validate()
	{
		if(mEtRemoterName.getText().equals(""))
			return false;
		return true;
	}
}