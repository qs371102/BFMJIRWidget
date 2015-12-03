package com.fishjord.irwidget.ir.codes;

import java.util.Arrays;

import android.util.Log;


public class ProntoParser {

	public static IRCommand parsePronto(String pronto) {
		// ‘\\s’表示 空格,回车,换行等空白符,‘+’号表示一个或多个的意思
		Log.d("pronto", pronto);
		final String[] lexemes = pronto.split("\\s+");
		int[] codes = new int[lexemes.length];
		for(int index = 0;index < lexemes.length;index++) {
			codes[index] = Integer.parseInt(lexemes[index], 16);
		}
		//断言assert <boolean表达式> 如果<boolean表达式>为true，则程序继续执行。如果为false，则程序抛出AssertionError，并终止执行。
		assert(codes[0] == 0);
		return new IRCommand((int)(1000000 / (codes[1] * 0.241246)), Arrays.copyOfRange(codes, 4, codes.length));		
	}
	
	public static void main(String[] args) {
		System.out.println(parsePronto("0000 006d 0000 0022 00ac 00ac 0015 0040 0015 0040 0015 0040 0015 0015 0015 0015 0015 0015 "));
	}
}
