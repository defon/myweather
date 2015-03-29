package com.example.myweather;

public class CityData 
{
	private String m_strName;
	private String m_strCode;
	private String m_strProvinceCode;
	
	public String getName() { return m_strName;}
	public String getCode() { return m_strCode;}	
	public String getProvinceCode() { return m_strProvinceCode;}
	
	public void setName(String strName) { m_strName = strName;}
	public void setCode(String strCode) { m_strCode = strCode;}
	public void setProvinceCode(String strCode) { m_strProvinceCode = strCode;}
}
