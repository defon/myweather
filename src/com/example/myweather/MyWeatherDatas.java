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
import util.WeatherHttpDataHelper;

public class MyWeatherDatas 
{
	private static final String DB_NAME = "MyWeatherDB";

	private static final int DB_VERSION = 1;

	private static MyWeatherDatas s_Instance;

	private SQLiteDatabase m_db;

	private Context m_SavedContext;

	private MyWeatherDatas(Context context) {
		MyWeatherDBHelper helper = new MyWeatherDBHelper(context, DB_NAME,
				null, DB_VERSION);
		m_db = helper.getWritableDatabase();
	}

	public synchronized static MyWeatherDatas getInstance(Context context) {
		if (s_Instance != null)
			s_Instance = new MyWeatherDatas(context);

		return s_Instance;
	}

	public void initData() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(m_SavedContext);
		String strLastGetTime = pref.getString("lastUpdateTime", ""); // ����Ѿ���ȡ���ˣ�����
		if (!strLastGetTime.isEmpty())
			return;

		List<ProvinceData> lstProvince = getProvinceData();
		if (lstProvince.isEmpty())
			lstProvince = WeatherHttpDataHelper.getProvinceDataFromServer();

		for (ProvinceData prov : lstProvince) {
			saveProvinceData(prov);
			List<CityData> lstCity = WeatherHttpDataHelper
					.getCityDataFromServer(prov.getCode());
			for (CityData city : lstCity) {
				saveCityData(city);
				List<CountyData> lstCounty = WeatherHttpDataHelper
						.getCountyDataFromServer(city.getCode());
				for (CountyData county : lstCounty)
					saveCountyData(county);
			}
		}

		// ��sharedPrefrence������ʷ��¼,Ӧ�ò������»�ȡ�ˣ�ȫ��ʡ��������һ�㲻����
		SharedPreferences.Editor prefEditor = pref.edit();
		prefEditor.putString("lastUpdateTime", "1");
		prefEditor.commit();
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

	public List<CityData> getCityData() {
		List<CityData> lstResult = new ArrayList<CityData>();

		Cursor cursor = m_db
				.query("t_city", null, null, null, null, null, "id");
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

	public List<CountyData> getCountyData() {
		List<CountyData> lstResult = new ArrayList<CountyData>();

		Cursor cursor = m_db.query("t_county", null, null, null, null, null,
				"id");
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