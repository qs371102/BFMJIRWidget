package com.bfmj.network;

import com.fishjord.irwidget.ir.codes.ControlCommand;
import com.fishjord.irwidget.ir.codes.IRCommand;

import android.util.Log;

public class DummyNetworkService implements INetworkService{

	@Override
	public void sendCommand(IRCommand command) {
		// TODO Auto-generated method stub
		Log.d(this.getClass().getCanonicalName(), command.toString());
	}

	@Override
	public void sendControlCommand(ControlCommand command) {
		// TODO Auto-generated method stub
		Log.d(this.getClass().getCanonicalName(), command.toString());
	}

}
