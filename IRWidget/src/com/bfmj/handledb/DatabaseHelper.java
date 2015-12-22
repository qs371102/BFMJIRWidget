package com.bfmj.handledb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "mydata.db"; //数据库名称
	private static final int version = 1; //数据库版本
	
	private static DatabaseHelper instance;
	private SQLiteDatabase database;
	
	public DatabaseHelper GetInstance(Context context)
	{
		if(instance==null)
			instance=new DatabaseHelper(context);
		return instance;
	}
	
	private DatabaseHelper(Context context) {
		super(context, DB_NAME, null, version);
		database=instance.getWritableDatabase();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "CREATE TABLE LearnedCommands (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, display TEXT, command TEXT, buttonGroup TEXT, address INTEGER);";          
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	
}
