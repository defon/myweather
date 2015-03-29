package com.example.myweather;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ChooseAreaActivity extends Activity 
{
	private TextView m_titleText;
	private ListView m_areaListView;
	
	// listview Êý¾Ý
	private ArrayAdapter<String> m_dataAdapter;
	private List<String> m_dataLst = new ArrayList<String>();
	
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
    }
}
