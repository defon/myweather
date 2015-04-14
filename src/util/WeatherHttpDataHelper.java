package util;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import android.util.Log;

import com.example.myweather.CityData;
import com.example.myweather.CountyData;
import com.example.myweather.ProvinceData;

public class WeatherHttpDataHelper 
{	
	public static List<ProvinceData> getProvinceDataFromServer()
	{
		List<ProvinceData> lstData = new ArrayList<ProvinceData>();
		
		String strUrl = "http://www.weather.com.cn/data/list3/city.xml";
		String strResult = HttpSend.sendHttpRequestSynchronous(strUrl,"",HttpSend.SEND_METHOD_GET);
		do
		{
			if(TextUtils.isEmpty(strResult))
				break;
			
			String[] arrProvinces = strResult.split(",");
			if(arrProvinces == null || arrProvinces.length <= 0)
				break;
			
			for(String p : arrProvinces)
			{
				String[] arr = p.split("\\|");
				if(arr == null || arr.length < 2 || arr[0].isEmpty() || arr[1].isEmpty())
					continue;
				
				ProvinceData data = new ProvinceData();
				data.setCode(arr[0]);
				data.setName(arr[1]);
				lstData.add(data);
			}
		}while(false);
		
		return lstData;
	}
	
	public static List<CityData> getCityDataFromServer(String strProvinceCode)
	{
		List<CityData> lstData = new ArrayList<CityData>();
		
		String strUrl = "http://www.weather.com.cn/data/list3/city" + strProvinceCode + ".xml";
		String strResult = HttpSend.sendHttpRequestSynchronous(strUrl,"",HttpSend.SEND_METHOD_GET);

		do
		{
			if(TextUtils.isEmpty(strResult))
				break;
			
			String[] arrCities = strResult.split(",");
			if(arrCities == null || arrCities.length <= 0)
				break;
			
			for(String p : arrCities)
			{
				String[] arr = p.split("\\|");
				if(arr == null || arr.length < 2 || arr[0].isEmpty() || arr[1].isEmpty())
					continue;
				
				CityData data = new CityData();
				data.setCode(arr[0]);
				data.setName(arr[1]);
				data.setProvinceCode(strProvinceCode);
				lstData.add(data);
			}
		}while(false);
		
		return lstData;
	}
	
	public static List<CountyData> getCountyDataFromServer(String strCityCode)
	{
		List<CountyData> lstData = new ArrayList<CountyData>();
		
		String strUrl = "http://www.weather.com.cn/data/list3/city" + strCityCode + ".xml";
		String strResult = HttpSend.sendHttpRequestSynchronous(strUrl,"",HttpSend.SEND_METHOD_GET);
		
		do
		{
			if(TextUtils.isEmpty(strResult))
				break;
			
			String[] arrCounties = strResult.split(",");
			if(arrCounties == null || arrCounties.length <= 0)
				break;
			
			for(String p : arrCounties)
			{
				String[] arr = p.split("\\|");
				if(arr == null || arr.length < 2 || arr[0].isEmpty() || arr[1].isEmpty())
					continue;
				
				CountyData data = new CountyData();
				data.setCode(arr[0]);
				data.setName(arr[1]);
				data.setCityCode(strCityCode);
				lstData.add(data);
			}
		}while(false);
		
		return lstData;
	}
}

















