package util;

public interface HttpCallbackListener 
{
	void onFinish(String strResponse);
	
	void onError(Exception e);
}
