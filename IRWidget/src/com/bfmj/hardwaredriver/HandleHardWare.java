package com.bfmj.hardwaredriver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class HandleHardWare extends Service{

	private static String TAG="IRService";
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.v(TAG, "ServiceDemo onBind");
		return null;
	}

	@Override    
    public void onCreate() {    
        Log.v(TAG, "ServiceDemo onCreate");    
        super.onCreate();    
    }    
        
    @Override    
    public void onStart(Intent intent, int startId) {    
        Log.v(TAG, "ServiceDemo onStart");    
        super.onStart(intent, startId);    
    }    
        
    @Override    
    public int onStartCommand(Intent intent, int flags, int startId) {    
        Log.v(TAG, "ServiceDemo onStartCommand");    
        return super.onStartCommand(intent, flags, startId);    
    }    
}
