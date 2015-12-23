package com.bfmj.byteandstringtools;

import com.bfmj.network.NetworkService;

import android.util.Log;

public class ByteAndStringTools {

	private static String TAG="IRWidget";
	
	public static String byteArrayToHex(byte[] a) {
		StringBuilder sb = new StringBuilder(a.length * 2);
		for(byte b: a)
			sb.append(String.format("%02x", b&0xff));
		return sb.toString();
	}

	public static String parseStringToData(String data)
	{
		Log.d(TAG, data);
		final String[] datas = data.split(":");
		if(datas.length==3)
		{
			if(!NetworkService.isMine(datas[1]))
			{
				Log.d(TAG, "is not Mine");
				return null;
			}
			return datas[2];
		}
		else
			return null;
	}


	public static byte[] hexStr2Bytes(String src){  

		String[] datas = src.trim().split(",");  

		byte[] finalData=new byte[datas.length];
		for (int i=0;i<datas.length;i++) {

			int tmp=Integer.parseInt(datas[i]);
			finalData[i]=(byte)tmp;
		}
		return finalData;  
	}
}
