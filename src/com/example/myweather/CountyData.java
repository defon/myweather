package com.example.myweather;

public class CountyData
{
	private String m_strName;
	private String m_strCode;
	private String m_strCityCode;
	
	public String getName() { return m_strName;}
	public String getCode() { return m_strCode;}	
	public String getCityCode() { return m_strCityCode;}
	
	public void setName(String strName) { m_strName = strName;}
	public void setCode(String strCode) { m_strCode = strCode;}
	public void setCityCode(String strCode) { m_strCityCode = strCode;}
}
