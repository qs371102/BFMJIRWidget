package com.bfmj.handledb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.bfmj.handledevices.HandleDevices;
import com.fishjord.irwidget.R;
import com.fishjord.irwidget.ir.codes.LearnedButton;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class HandleSqlDB {

	private static final String DATABASE_PATH = "/data/data/com.fishjord.irwidget/databases";
	private static final int DATABASE_VERSION = 2000000;

	private static final String DATABASE_NAME = "mj.db";

	private static String outFileName = DATABASE_PATH + "/" + DATABASE_NAME;

	private static final String TABLE_CONTACTS="LearnedCommands";
	
	private static int MIN_ADDRESS=1;
	private static int MAX_ADDRESS=63;


	private static final String KEY_ID="id";
	private static final String KEY_NAME="name";
	private static final String KEY_DISPLAY="display";
	private static final String KEY_GROUP="buttongroup";
	private static final String KEY_COMMAND="command";
	private static final String KEY_ADDRESS="address";
	private static final String KEY_ROBOTID="robotid";
	private static final String KEY_PRESERVE="preserve";

	private static Context context;

	private SQLiteDatabase database;
	
	
	private static int robotID;
	
	public static HandleSqlDB instance;

	
	public static HandleSqlDB getInstant(Context ctext)
	{
		context=ctext;
		robotID=HandleDevices.getRobotID();
		if(instance==null)
		{
			instance=new HandleSqlDB(ctext);
		}
		return  instance;
			
	}
	
	private HandleSqlDB(Context context) {
		HandleSqlDB.context = context;

		File file = new File(outFileName);
		if (file.exists()) {
			database = SQLiteDatabase.openOrCreateDatabase(outFileName, null);
			
			Log.d("IRWidget",database.getVersion()+"====version======"+DATABASE_VERSION );
			
			if (database.getVersion() != DATABASE_VERSION) {
				database.close();
				file.delete();	
			}
		}
		try {
			buildDatabase();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void buildDatabase() throws Exception{
		InputStream myInput = context.getResources().openRawResource(R.raw.mj);
		File file = new File(outFileName);

		File dir = new File(DATABASE_PATH);
		if (!dir.exists()) {
			if (!dir.mkdir()) {
				throw new Exception("创建失败");
			}
		}

		if (!file.exists()) {	
			Log.w("IRWidget", "Not================================ Exists");
			try {
				OutputStream myOutput = new FileOutputStream(outFileName);

				byte[] buffer = new byte[1024];
				int length;
				while ((length = myInput.read(buffer))>0){
					myOutput.write(buffer, 0, length);
				}
				myOutput.close();
				myInput.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 查找
	 * @return
	 */
	public Cursor select() {
		database = SQLiteDatabase.openOrCreateDatabase(outFileName, null);
		String sql = "select * from "+TABLE_CONTACTS +" where "+KEY_ROBOTID+"="+robotID;

		Cursor cursor = database.rawQuery(sql, null);
		return cursor;
	}
	//
//	public Cursor getMaxAddress()
//	{
//		database = SQLiteDatabase.openOrCreateDatabase(outFileName, null);
//		String sql = "select max("+KEY_ADDRESS+")+1 from "+TABLE_CONTACTS+"where "+KEY_ADDRESS+"+1 between 1 and 63";
//		Cursor cursor = database.rawQuery(sql, null);
//		return cursor;
//	}
	
	public int getMaxAddress()
	{
		database = SQLiteDatabase.openOrCreateDatabase(outFileName, null);
		String sql = "select max("+KEY_ADDRESS+") from "+TABLE_CONTACTS +" where "+KEY_ROBOTID+"="+robotID;
		Cursor cursor = database.rawQuery(sql, null);
		if(cursor.moveToFirst())
		{
			return cursor.getInt(0);
		}
		else
			return -1;
	}
	
	
	
	//
	public Cursor selectMaxAddress() {
		database = SQLiteDatabase.openOrCreateDatabase(outFileName, null);
		String sql = "select max("+KEY_ADDRESS+")+1 from "+TABLE_CONTACTS+" where "+KEY_ADDRESS+"+1 between 1 and 63 and "+KEY_ROBOTID+"="+robotID;
		Cursor cursor = database.rawQuery(sql, null);
		return cursor;
	}
	
	/*
	 * 插入
	 */
	
	public long insert(LearnedButton lb) {
		database = SQLiteDatabase.openOrCreateDatabase(outFileName, null);
		ContentValues cv = new ContentValues();
		//cv.put(KEY_ID, lb.id);
		cv.put(KEY_NAME, lb.getName());
		cv.put(KEY_DISPLAY, lb.getDisplay());	
		cv.put(KEY_GROUP,lb.getGroup());
		cv.put(KEY_COMMAND, lb.getLearnCommand().getOnAndOffs());
		cv.put(KEY_ADDRESS, lb.getLearnCommand().getAddress());
		cv.put(KEY_ROBOTID, lb.getRobotId());
		Log.d("IRWidget", lb.getName()+"  "+ lb.getDisplay()+" OnAndOffs: "+lb.getLearnCommand().getOnAndOffs()+" "+lb.getLearnCommand().getAddress()+" "+lb.getRobotId());
		long result = database.insert(TABLE_CONTACTS, null, cv);	
		return result;
	}


	//	/**
	//	 * 更新
	//	 * @param word
	//	 * @param note
	//	 * @return
	//	*/
	//	
	//		private int update(String word, String note) {
	//			database = SQLiteDatabase.openOrCreateDatabase(outFileName, null);
	//			
	//			ContentValues cv = new ContentValues();
	//			cv.put("note", note);
	//			
	//			int result = database.update(TABLE_CONTACTS, cv, "word=?", new String[]{word});
	//			
	//			return result;
	//		}
	/**
	 * 删除
	 * @param id
	 */
	public int delete(String id) {
		database = SQLiteDatabase.openOrCreateDatabase(outFileName, null);
		int result = database.delete(TABLE_CONTACTS, "id=?", new String[]{id});
		return result;
	}
	
	
	
	
	public int getMinUnusedAddress()
	{
		database=SQLiteDatabase.openOrCreateDatabase(outFileName, null);
		String sql="select address-1 as lad  from LearnedCommands where lad between 1 and 63  and where "+KEY_ROBOTID+"="+robotID+"and lad not in (select address  from LearnedCommands where "+KEY_ROBOTID+"="+robotID+") order by lad asc limit 1";
		Cursor cursor = database.rawQuery(sql,null);
		if(cursor.moveToFirst())
			return cursor.getInt(0);
		else
			return -1;
	}

	public int getAddressToLearnCommand()
	{
		int c=getContactsCount();
		int address;
		if(c==0)
		{
			return 1;
		}
		if(c<63)
		{
			address=getMaxAddress();
			if(address>=MIN_ADDRESS&&address<=MAX_ADDRESS)
				return address;
			else
			{
				address=getMinUnusedAddress(); 
				return address;
			}
		}
		else
			return -1;
	}
	
	public void close() {
		database.close();
	}
	
	public Cursor getGroups()
	{
		database = SQLiteDatabase.openOrCreateDatabase(outFileName, null);
		String sql = "select distinct buttonGroup from "+TABLE_CONTACTS+" where "+KEY_ROBOTID+"="+robotID;
		Log.d("IRWidget", "========"+sql);
		Cursor cursor = database.rawQuery(sql, null);
		return cursor;
	}
	
	public int getContactsCount() {
		
		database = SQLiteDatabase.openOrCreateDatabase(outFileName, null);
		String sql = "select * from "+TABLE_CONTACTS +" where "+KEY_ROBOTID+"="+robotID;
		Cursor cursor = database.rawQuery(sql, null);
		return cursor.getCount();
	}
	
	public boolean ifExistCustomerRemoter()
	{
		return false;
	}
}
