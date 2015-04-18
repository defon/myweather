package com.example.myweather;

import java.util.ArrayList;
import java.util.List;

import db.MyWeatherDBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;
import util.WeatherHttpDataHelper;

public class MyWeatherDatas 
{
	private static final String LOGTAG = "MyWeatherDatas";
	
	private static final String DB_NAME = "myWeather.db";

	private static final int DB_VERSION = 1;

	private static MyWeatherDatas s_Instance;

	private SQLiteDatabase m_db;

	private Context m_SavedContext;

	private MyWeatherDatas(Context context) {
		assert(context != null);
		m_SavedContext = context;
		MyWeatherDBHelper helper = new MyWeatherDBHelper(context, DB_NAME,null, DB_VERSION);
		m_db = helper.getWritableDatabase();
	}

	public synchronized static MyWeatherDatas getInstance(Context context) {
		if (s_Instance == null)
			s_Instance = new MyWeatherDatas(context);

		return s_Instance;
	}

	public boolean initData(boolean bGetFromServer) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(m_SavedContext);
		String strLastGetTime = pref.getString("lastUpdateTime", ""); // 如果已经获取过了，跳过
		if (!strLastGetTime.isEmpty())
		{
			//todo
//			Log.v(LOGTAG,"lastUpdateTime %s,跳过从server获取数据",strLastGetTime);
			return true;
		}

		List<ProvinceData> lstProvince = getProvinceData();
		if (lstProvince.isEmpty() && bGetFromServer)
			lstProvince = WeatherHttpDataHelper.getProvinceDataFromServer();

		if(lstProvince.isEmpty())
			return false;
		
		for (ProvinceData prov : lstProvince) {
			saveProvinceData(prov);
			List<CityData> lstCity = WeatherHttpDataHelper
					.getCityDataFromServer(prov.getCode());
			
			if(lstCity.isEmpty())	return false;
			
			for (CityData city : lstCity) {
				saveCityData(city);
				List<CountyData> lstCounty = WeatherHttpDataHelper
						.getCountyDataFromServer(city.getCode());
				
				if(lstCounty.isEmpty())	return false;
				
				for (CountyData county : lstCounty)
					saveCountyData(county);
			}
		}

		// 用sharedPrefrence来做历史记录,应该不用重新获取了，全国省市县数据一般不会变的
		SharedPreferences.Editor prefEditor = pref.edit();
		prefEditor.putString("lastUpdateTime", "1");
		prefEditor.commit();
		return true;
	}

	public List<ProvinceData> getProvinceData() {
		List<ProvinceData> lstResult = new ArrayList<ProvinceData>();

		Cursor cursor = m_db.query("t_province", null, null, null, null, null,
				"id");
		if (cursor.moveToFirst()) {
			do {
				ProvinceData prov = new ProvinceData();
				prov.setName(cursor.getString(cursor.getColumnIndex("name")));
				prov.setCode(cursor.getString(cursor.getColumnIndex("code")));
				lstResult.add(prov);

			} while (cursor.moveToNext());
		}

		return lstResult;
	}

	public List<CityData> getCityData(String strProvinceCode) {
		List<CityData> lstResult = new ArrayList<CityData>();

		Cursor cursor;
		if(strProvinceCode.isEmpty())
			cursor = m_db.query("t_city", null, null, null, null, null, "id");
		else
			cursor = m_db.rawQuery("select * from t_city where province_code = ?",new String[] {strProvinceCode});
			
		if (cursor.moveToFirst()) {
			do {
				CityData city = new CityData();
				city.setName(cursor.getString(cursor.getColumnIndex("name")));
				city.setCode(cursor.getString(cursor.getColumnIndex("code")));
				city.setProvinceCode(cursor.getString(cursor
						.getColumnIndex("province_code")));
				lstResult.add(city);

			} while (cursor.moveToNext());
		}

		return lstResult;
	}

	public List<CountyData> getCountyData(String strCityCode) {
		List<CountyData> lstResult = new ArrayList<CountyData>();

		Cursor cursor;
		if(strCityCode.isEmpty())
			cursor = m_db.query("t_county", null, null, null, null, null,"id");
		else
			cursor = m_db.rawQuery("select * from t_county where city_code = ?",new String[] {strCityCode});
		
		if (cursor.moveToFirst()) {
			do {
				CountyData county = new CountyData();
				county.setName(cursor.getString(cursor.getColumnIndex("name")));
				county.setCode(cursor.getString(cursor.getColumnIndex("code")));
				county.setCityCode(cursor.getString(cursor
						.getColumnIndex("city_code")));
				lstResult.add(county);

			} while (cursor.moveToNext());
		}

		return lstResult;
	}

	void saveProvinceData(ProvinceData data) {
		if (data != null) {
			ContentValues values = new ContentValues();
			values.put("name", data.getName());
			values.put("code", data.getCode());
			m_db.insert("t_province", null, values);
		}
	}

	void saveCityData(CityData data) {
		if (data != null) {
			ContentValues values = new ContentValues();
			values.put("name", data.getName());
			values.put("code", data.getCode());
			values.put("province_code", data.getProvinceCode());
			m_db.insert("t_city", null, values);
		}
	}

	void saveCountyData(CountyData data) {
		if (data != null) {
			ContentValues values = new ContentValues();
			values.put("name", data.getName());
			values.put("code", data.getCode());
			values.put("city_code", data.getCityCode());
			m_db.insert("t_county", null, values);
		}
	}
}
