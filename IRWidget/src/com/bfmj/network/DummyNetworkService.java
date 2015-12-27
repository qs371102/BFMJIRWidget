package com.bfmj.network;

import com.fishjord.irwidget.ir.codes.ControlCommand;
import com.fishjord.irwidget.ir.codes.IRCommand;
import com.fishjord.irwidget.ir.codes.LearnedCommand;

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

	@Override
	public void sendLearnedCommand(LearnedCommand command) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendCallback(String msg) {
		// TODO Auto-generated method stub
		
	}

}
