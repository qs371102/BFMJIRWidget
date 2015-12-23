package com.fishjord.irwidget.ir.codes;

public class LearnedCommand {
	
	private static String preCmd="89 ";
	private static boolean needCallback=false;
	private String onAndOffs;
	private int address;
	
	public LearnedCommand(int address,String onAndOffs)
	{
		this.address=address;
		this.onAndOffs=onAndOffs;
	}
	
	public String getOnAndOffs()
	{
		return onAndOffs;
	}
	
	public int getAddress()
	{
		return address;
	}
	@Override
	public String toString()
	{
		return preCmd+onAndOffs+":"+String.valueOf(needCallback);
	}
}
