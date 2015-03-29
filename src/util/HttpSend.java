package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
 
public class HttpSend 
{
	public static final String SEND_METHOD_GET  = "GET";
	public static final String SEND_METHOD_POST = "POST";
	
	private static int m_ConnectTimeout = 5000;
	private static int m_ReadTimeout = 5000;
	
	public static void sendHttpRequest(final String strUrl,final String strData,final String strMethod,final HttpCallbackListener listener)
	{
		Thread thrd = new Thread( new Runnable() 
		{
			@Override
			public void run() 
			{
				HttpURLConnection conn = null;
				try
				{
					URL url = new URL(strUrl);
					conn = (HttpURLConnection)url.openConnection();
					conn.setRequestMethod(strMethod);
					conn.setConnectTimeout(m_ConnectTimeout);
					conn.setReadTimeout(m_ReadTimeout);
					
					InputStream in = conn.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					
					String strLine;
					StringBuilder strResult = new StringBuilder();
					while((strLine = reader.readLine()) != null)
						strResult.append(strLine);
					
					if(listener != null)
						listener.onFinish(strResult.toString());
				}
				catch(Exception e)
				{
					if(listener != null)
						listener.onError(e);
				}
				finally
				{
					if(conn != null)
						conn.disconnect();
				}
			}
		});
		
		thrd.start();
	
		// listener为空表示为同步模式，等线程退出再返回
		if(listener == null)
		{
			while(true)
			{
				if(thrd.isAlive() == false)
					break;
				
				 try 
				 {
					 Thread.sleep(1000);
			     }
				 catch (InterruptedException e) 
				 {
					 e.printStackTrace(); 
			     }
			}	
		}
	}
	
	public static void setConnectionTimeout(int nTimeout)
	{
		m_ConnectTimeout = nTimeout;
	}
	
	public static void setReadTimeout(int nTimeout)
	{
		m_ReadTimeout = nTimeout;
	}
}
 














