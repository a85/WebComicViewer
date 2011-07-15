package com.rickreation.webcomicviewer;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.app.Application;
import android.util.Log;

public class WebComicViewerApp extends Application {
	public static final String TAG = "WebComicViewerApp";
	
	private HttpClient httpClient;
	
	@Override
	public void onCreate() {
		httpClient = createHttpClient();
	}
	
	@Override
	public void onLowMemory()
	{		
		shutdownHttpClient();
		super.onLowMemory();		
	}
	
	@Override
	public void onTerminate()
	{		
		shutdownHttpClient();
		super.onTerminate();		
	}

	private HttpClient createHttpClient()
	{
		
		try {
			Log.d(TAG, "createHttpClient()...");
			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
			HttpProtocolParams.setUseExpectContinue(params, true);

			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
			ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);

			HttpClient httpClient = new DefaultHttpClient();
			return httpClient;
		}
		catch(Exception e) {
			Log.d(TAG, "Could not create httpClient");
			return null;
		}
		
	}
	
	public HttpClient getHttpClient() {
		return httpClient;
	}
	
	private void shutdownHttpClient()
	{
		Log.d(TAG, "Shutting down http client");
		if(httpClient != null && httpClient.getConnectionManager() != null)
		{
			httpClient.getConnectionManager().shutdown();
		}
	}
}
