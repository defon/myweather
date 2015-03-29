package util;

public class WeatherHttpDataHelper 
{	
	public static void getProvinceDataFromServer()
	{
		String strUrl = "http://www.weather.com.cn/data/list3/city.xml";
		HttpSend.sendHttpRequest(strUrl,"",HttpSend.SEND_METHOD_GET, listener)
	}
}
