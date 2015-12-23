package com.fishjord.irwidget.ir.codes;

public class IRCommand {
	private final int frequency;
	private final int[] onOffs;

	private static boolean needCallback=false;
	private static String stringToAppend=" ";

	public IRCommand(int frequency, int[] onOffs) {
		this.frequency = frequency;
		this.onOffs = onOffs;
	}

	public int getFrequency() {
		return frequency;
	}

	public int[] getOnOffs() {
		return onOffs;
	}

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		if(needCallback)
		{
			ret.append(frequency).append(stringToAppend);

			for(int index = 0;index < onOffs.length;index++) {
				ret.append(onOffs[index]);
				if(index + 1 != onOffs.length) {
					ret.append(stringToAppend);
				}
			}
		}
		else
		{
			//TODO need modify
			ret.append("89"+stringToAppend);
			for(int index = 0;index < onOffs.length;index++) {
				ret.append(onOffs[index]);
				if(index + 1 != onOffs.length) {
					ret.append(stringToAppend);
				}
			}
			ret.append(":"+String.valueOf(needCallback));
		}
		return ret.toString();
	}
}
