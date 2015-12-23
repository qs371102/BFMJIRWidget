package com.bfmj.network;

import com.fishjord.irwidget.ir.codes.ControlCommand;
import com.fishjord.irwidget.ir.codes.IRCommand;
import com.fishjord.irwidget.ir.codes.LearnedCommand;

public interface INetworkService {
	public void sendCommand(IRCommand command);
	public void sendControlCommand(ControlCommand command);
	public void sendLearnedCommand(LearnedCommand command);
}
