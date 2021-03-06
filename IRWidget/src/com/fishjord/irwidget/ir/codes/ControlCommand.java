package com.fishjord.irwidget.ir.codes;

import android.util.Log;

public class ControlCommand {
	
	private String TAG="BFMJ";
	
	private int cmd;
	
	private int[] reservedField=new int[]{0,0};
	
	private int[] address;
	//----------------------------
	private int[] onOffs;

	private boolean useXor=true;
	private boolean useRF=true;
	private int xor;
	
	private boolean needCallBack=false;
	private static String stringToAppend=" ";
	//TODO needCallback
	public ControlCommand(int cmd,int[] datas,boolean useXor,boolean useRF,boolean needCallback)
	{
		this.useXor=useXor;
		this.useRF=useRF;
		this.cmd=cmd;
		this.needCallBack=needCallback;
		if(this.useXor)
		{
			this.address=datas;
			formXor();
		}
		else
			this.onOffs=datas;
	}


	public int[] getOnOffs()
	{
		return this.onOffs;
	}

	public int[] getAddress()
	{
		return this.address;
	}

	public int getCmd()
	{
		return this.cmd;
	}
	//寮傛垨
	private void formXor()
	{
		if(this.useXor)
		{
			int cmd = this.cmd;
			int xor=cmd;
			for (int b : address) {
				int i=(int)b;
				xor^=i;
			}
			// convert back to byte
			this.xor = (int)(0xff & xor);
		}
		else
			return;
	}
	@Override
	public String toString()
	{
		StringBuilder ret = new StringBuilder();
		ret.append(String.format("%02x",cmd)).append(stringToAppend);
		if(useXor)
		{
			for(int index = 0;index < address.length;index++) {
				ret.append(String.format("%02x", address[index]));
				ret.append(stringToAppend);
			}
			if(useRF)
			for(int index = 0;index < reservedField.length;index++) {
				ret.append(String.format("%02x",reservedField[index]));
				ret.append(stringToAppend);
			}
			ret.append(String.format("%02x",xor));
		}
		else
		{
			for(int index = 0;index < onOffs.length;index++) {
				ret.append(String.format("%02x",onOffs[index]));
				if(index + 1 != onOffs.length) {
					ret.append(stringToAppend);
				}
			}
		}
		ret.append(":"+String.valueOf(needCallBack));
		Log.d(TAG, ret.toString());
		return ret.toString();
	}
}
