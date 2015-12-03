package com.fishjord.irwidget.ir.codes;

import android.util.Log;

public class ControlCommand {
	
	private String TAG="BFMJ";
	
	private int cmd;

	//UseXor=false 时 为下载模式 
	private int[] reservedField=new int[]{0,0};
	// 地址长度为一个字节
	private int[] address;
	//----------------------------
	private int[] onOffs;

	private boolean useXor=true;
	private int xor;

	public ControlCommand(int cmd2,int[] datas,boolean useXor)
	{
		this.useXor=useXor;
		this.cmd=cmd2;
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
	//异或
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
		ret.append(cmd).append(",");
		if(useXor)
		{
			for(int index = 0;index < address.length;index++) {
				ret.append(address[index]);
				ret.append(",");
			}
			for(int index = 0;index < reservedField.length;index++) {
				ret.append(reservedField[index]);
				ret.append(",");
			}
			ret.append(xor);
		}
		else
		{
			for(int index = 0;index < onOffs.length;index++) {
				ret.append(onOffs[index]);
				if(index + 1 != onOffs.length) {
					ret.append(",");
				}
			}
		}
		
		Log.d(TAG, ret.toString());
		return ret.toString();
	}
}
