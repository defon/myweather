package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class MyWeatherDBHelper extends SQLiteOpenHelper 
{
	private static final String CREATE_T_PROVINCE = "create table t_province (" +
			"id integer primary key autoincrement," +
			"name text not NULL," + 
			"code text not NULL)";
	
	private static final String CREATE_T_CITY = "create table t_city (" +
			"id integer primary key autoincrement," +
			"name text not NULL," + 
			"code text not NULL," + 
			"province_code text not NULL)";
	
	private static final String CREATE_T_COUNTY = "create table t_county (" +
			"id integer primary key autoincrement," +
			"name text not NULL," + 
			"code text not NULL," + 
			"city_code text not NULL)";
	
	public MyWeatherDBHelper(Context context,String strName,CursorFactory factory,int nVersion)
	{
		super(context,strName,factory,nVersion);
	}
	 
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(CREATE_T_PROVINCE);
		db.execSQL(CREATE_T_CITY);
		db.execSQL(CREATE_T_COUNTY);	
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db,int nOldVersion,int nNewVersion)
	{	
	}
}
