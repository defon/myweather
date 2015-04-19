package com.example.myweather;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import util.HttpCallbackListener;
import util.HttpSend;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class WeatherInfo extends Activity implements HttpCallbackListener,OnClickListener
{
	private Button	 m_btnSwitchCity;
	private Button	 m_btnRefresh;	
	private TextView m_countyNameText;
	private TextView m_publishTimeText;
	private TextView m_curDateText;
	private TextView m_weatherInfoText;
	private TextView m_tempInfoText;
	private ProgressDialog m_progressDialog;
	
	private String m_strCountyCode;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_display);
        
        m_countyNameText 	= (TextView)findViewById(R.id.county_name);
        m_publishTimeText 	= (TextView)findViewById(R.id.publish_time);
        m_curDateText 		= (TextView)findViewById(R.id.cur_date);
        m_weatherInfoText 	= (TextView)findViewById(R.id.weather_info);
        m_tempInfoText 		= (TextView)findViewById(R.id.temp_info);
        m_btnSwitchCity		= (Button)findViewById(R.id.switch_city);
        m_btnRefresh		= (Button)findViewById(R.id.refresh);
        
        m_btnSwitchCity.setOnClickListener(this);
        m_btnRefresh.setOnClickListener(this);
        
        m_strCountyCode = getIntent().getStringExtra("county_code");
        if(TextUtils.isEmpty(m_strCountyCode))
        	finish();
        
        showWeather(true);
    }
    
    @Override
    public void onBackPressed()
    {
    	Intent intent = new Intent(this,ChooseAreaActivity.class);
		intent.putExtra("fromWeatherActivity",true);
		startActivity(intent);
		finish();
    }
    
    @Override
    public void onClick(View v)
    {
    	switch(v.getId())
    	{
    	case R.id.switch_city:
    		Intent intent = new Intent(this,ChooseAreaActivity.class);
    		intent.putExtra("fromWeatherActivity",true);
    		startActivity(intent);
    		finish();
    		break;
    	case R.id.refresh:
    		m_publishTimeText.setText("同步天气数据中，请稍候...");
    		showWeather(false);
    		break;
    	default:
    		break;
    	}
    }
    
    private void showWeather(Boolean bUseCache)
    {
    	if(!bUseCache || !showWeatherFromCache())
    		showWeatherFromServer();
    }
    
    private Boolean showWeatherFromCache()
    {  
    	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
    	if(pref.getInt("county_"+m_strCountyCode+"_Saved",0) == 1)
    	{
    		m_countyNameText.setText(pref.getString("county_"+m_strCountyCode+"_Name",""));
    		m_publishTimeText.setText(pref.getString("county_"+m_strCountyCode+"_PublishTime",""));
    		m_curDateText.setText(pref.getString("county_"+m_strCountyCode+"_PublishDate",""));
    		m_weatherInfoText.setText(pref.getString("county_"+m_strCountyCode+"_WeatherInfo",""));
    		m_tempInfoText.setText(pref.getString("county_"+m_strCountyCode+"_TempInfo",""));
    		return true;
    	}
    	
    	return false;
    }
    
    private void showWeatherFromServer()
    {
    	showProgressDialog();
 
		String strUrl = "http://www.weather.com.cn/data/list3/city" + m_strCountyCode + ".xml";
		HttpSend.sendHttpRequestAsynchronous(strUrl,"",HttpSend.SEND_METHOD_GET,new HttpCallbackListener()
		{
			@Override
		    public void onFinish(String strResponse)
		    {
				// strResponse: city_code|weather_code
		    	if(!TextUtils.isEmpty(strResponse))
		    	{
		    		String strArr[] = strResponse.split("\\|");
		    		if(strArr != null && strArr.length == 2)
		    		{
		    			String strUrl = "http://www.weather.com.cn/data/cityinfo/" + strArr[1] + ".html";
		    			HttpSend.sendHttpRequestAsynchronous(strUrl,"",HttpSend.SEND_METHOD_GET,WeatherInfo.this);
		    			return;
		    		}
		    	}
		    	
		    	runOnUiThread(new Runnable()
				{
					public void run()
					{
						m_publishTimeText.setText("获取数据失败！请检查您的网络连接情况");
						closeProgressDialog();
					}
				});
		    }
			
			@Override
		    public void onError(Exception e)
			{
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						m_publishTimeText.setText("获取数据失败！请检查您的网络连接情况");
						closeProgressDialog();
					}
				});
			}
		});
    }
    
    @Override
    public void onFinish(String strResponse)
    {
    	Boolean bRlt = false;
    	if(!TextUtils.isEmpty(strResponse))
    	{
        	// {"weatherinfo":{"city":"通州","cityid":"101010600","temp1":"14℃","temp2":"5℃","weather":"多云","img1":"d1.gif","img2":"n1.gif","ptime":"08:00"}}
        	try
        	{
            	JSONObject jvRoot = new JSONObject(strResponse);
            	JSONObject jvWeather = jvRoot.getJSONObject("weatherinfo");
            	String strCityName = jvWeather.getString("city");
            	String strTemp1 = jvWeather.getString("temp1");
            	String strTemp2 = jvWeather.getString("temp2");
            	String strWeather = jvWeather.getString("weather");
            	String strPublishTime = jvWeather.getString("ptime");
            	
            	SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
            	SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            	editor.putString("county_"+m_strCountyCode+"_Name", strCityName);
            	editor.putString("county_"+m_strCountyCode+"_PublishTime", sdf.format(new Date()) + strPublishTime + "发布");
            	editor.putString("county_"+m_strCountyCode+"_PublishDate", sdf.format(new Date()));
            	editor.putString("county_"+m_strCountyCode+"_WeatherInfo", strWeather);
            	editor.putString("county_"+m_strCountyCode+"_TempInfo", strTemp2+"~"+strTemp1);
            	editor.putString("lastSelect",m_strCountyCode);
            	editor.putInt("county_"+m_strCountyCode+"_Saved",1);
            	editor.commit();
            	bRlt = true;
        	}
        	catch(JSONException e)
        	{
        		e.printStackTrace();
        	}
    	}
    	
    	if(bRlt)
    	{
        	runOnUiThread(new Runnable()
        	{
        		@Override
        		public void run()
        		{
        			showWeatherFromCache();
        			closeProgressDialog();
        		}
        	});    		
    	}
    	else
    	{
    		runOnUiThread(new Runnable()
			{
				public void run()
				{
					m_publishTimeText.setText("获取数据失败！请检查您的网络连接情况");
					closeProgressDialog();
				}
			});
    	}
    }
	
    @Override
    public void onError(Exception e)
	{
    	runOnUiThread(new Runnable()
    	{
    		@Override
    		public void run()
    		{
    			m_publishTimeText.setText("获取数据失败！请检查您的网络连接情况");
    			closeProgressDialog();
     		}
    	});
	}
	
    private void showProgressDialog()
    {
    	if(m_progressDialog == null)
    	{
    		m_progressDialog = new ProgressDialog(this);
    		m_progressDialog.setMessage("天气信息加载中，请稍候...");
    		m_progressDialog.setCancelable(false);
    		m_progressDialog.setCanceledOnTouchOutside(false);
    	}
    	
    	m_progressDialog.show();
    }
    
    private void closeProgressDialog()
    {
    	if(m_progressDialog != null)
    		m_progressDialog.dismiss();
    }
}














