package com.example.myweather;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity 
{
	private static final int PROVINCE_LEVEL = 1;
	private static final int CITY_LEVEL = 2;
	private static final int COUNTY_LEVEL = 3;
	
	private MyWeatherDatas m_dbDatas;
	private TextView m_titleText;
	private ProgressDialog m_progressDialog;
	
	// listview 数据	
	private ListView m_areaListView;
	private ArrayAdapter<String> m_dataAdapter;
	private List<String> m_dataLst = new ArrayList<String>();
	
	private int m_nCurLevel;
	private ProvinceData 	m_selectedProvince;
	private CityData 		m_selectedCity;
	private CountyData 		m_selectedCounty;
	
	private List<ProvinceData> 	m_provDataLst;
	private List<CityData> 		m_cityDataLst;
	private List<CountyData> 	m_countyDataLst;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        
        m_titleText 	= (TextView)findViewById(R.id.title_text);
        m_areaListView 	= (ListView)findViewById(R.id.area_list_view);
        m_dataAdapter 	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,m_dataLst);
        m_areaListView.setAdapter(m_dataAdapter);
        
        m_dbDatas = MyWeatherDatas.getInstance(this);
        m_nCurLevel = PROVINCE_LEVEL;
        
        init();
    }
    
    private void setProvinceDatas()
    {
        m_provDataLst = m_dbDatas.getProvinceData();
        if(m_provDataLst.size() > 0)
        {
        	m_dataLst.clear();
        	for(ProvinceData data : m_provDataLst)
        		m_dataLst.add(data.getName());
        	
        	m_dataAdapter.notifyDataSetChanged();
        	m_areaListView.setSelection(0);
        	m_selectedProvince = m_provDataLst.get(0);
        	m_titleText.setText("中国");
        	m_nCurLevel = PROVINCE_LEVEL;
        }    	
    }
    
    private void setCityDatas()
    {
        m_cityDataLst = m_dbDatas.getCityData();
        if(m_cityDataLst.size() > 0)
        {
        	m_dataLst.clear();
        	for(CityData data : m_cityDataLst)
        		m_dataLst.add(data.getName());
        	
        	m_dataAdapter.notifyDataSetChanged();
        	m_areaListView.setSelection(0);
        	m_selectedCity = m_cityDataLst.get(0);
        	m_titleText.setText(m_selectedProvince.getName());
        	m_nCurLevel = CITY_LEVEL;
        }    	
    }
    
    private void setCountyDatas()
    {
        m_countyDataLst = m_dbDatas.getCountyData();
        if(m_countyDataLst.size() > 0)
        {
        	m_dataLst.clear();
        	for(CountyData data : m_countyDataLst)
        		m_dataLst.add(data.getName());
        	
        	m_dataAdapter.notifyDataSetChanged();
        	m_areaListView.setSelection(0);
        	m_selectedCounty = m_countyDataLst.get(0);
        	m_titleText.setText(m_selectedCity.getName());
        	m_nCurLevel = COUNTY_LEVEL;
        }    	
    }
    
    private void init()
    {
    	showProgressDialog();
    	Thread thrd = new Thread( new Runnable() 
		{
			@Override
			public void run() 
			{
				if(m_dbDatas.initData(true) == true)
				{
					runOnUiThread(new Runnable(){
						public void run(){
							closeProgressDialog();
					        setProvinceDatas();							
						}
					});
				}
				else
				{
					runOnUiThread(new Runnable(){
						public void run(){
							Toast.makeText(ChooseAreaActivity.this,"Load data failed!",Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		});
		
		thrd.start();
    }
    
    private void showProgressDialog()
    {
    	if(m_progressDialog == null)
    	{
    		m_progressDialog = new ProgressDialog(this);
    		m_progressDialog.setMessage("城市信息加载中，请稍候...");
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











