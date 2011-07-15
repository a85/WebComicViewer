package com.rickreation.webcomicviewer.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rickreation.webcomicviewer.models.Comic;
import com.rickreation.webcomicviewer.models.Strip;

public class WebComicViewerApi {
	public static final String TAG = "WebComicViewerApi";
	
	//API resource to be accessed
	private String resource;
	private String apiUrl;
	private HttpClient httpClient;
	
	public WebComicViewerApi(HttpClient mHttpClient, String mApiUrl) {
		httpClient = mHttpClient;
		apiUrl = mApiUrl;
	}
	
	public String get(LinkedHashMap<String, String> params, String format) throws Exception {		
		try {			
			BufferedReader in = null;
			
			StringBuilder urlString = new StringBuilder(apiUrl);			
					
			if(params.size() > 0) {
				urlString.append("?");			
				Set<String> st = params.keySet();
				
				for(Iterator<String> it = st.iterator(); it.hasNext();) {
					String key = it.next();
					String val = params.get(key);
					
					String encKey = URLEncoder.encode(key);
					String encVal = URLEncoder.encode(val);
					
					urlString.append(encKey + "=" + encVal + "&");				
				}
				
				urlString.deleteCharAt(urlString.length() - 1);
			}						
			
			HttpGet request = new HttpGet();						
			request.setURI(new URI(urlString.toString()));		

			HttpResponse response = httpClient.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			String page = sb.toString();
			
			Log.d(TAG, "Data obtained from " + urlString.toString());
			return page;
		}
		catch(Exception e) {
			throw new Exception("Could not connect to the server.");			
		}		
	}
	
	public List<Comic> getComicList() {
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		params.put("r", "comics");
		
		try {
			String data = this.get(params, "json");
			
			Gson gson = new Gson();
			Type collectionType = new TypeToken<List<Comic>>() {}.getType();
			List<Comic> comicsList = gson.fromJson(data, collectionType);
						
			Log.d(TAG, data);
			return comicsList;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public ArrayList<Strip> getStrips(int comic_id, int from, int count) {
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		params.put("r", "strips");
		params.put("comic_id", Integer.toString(comic_id));
		params.put("from", Integer.toString(from));
		params.put("to", Integer.toString(count));
		
		try {
			String data = this.get(params, "json");
			
			Log.d(TAG, data);
			
			Gson gson = new Gson();
			Type collectionType = new TypeToken<List<Strip>>() {}.getType();
			List<Strip> stripList = gson.fromJson(data, collectionType);
			
			ArrayList<Strip> strips = new ArrayList<Strip>();
			
			for(int i = 0; i < stripList.size(); i++) {
				Strip s = new Strip();
				s.setAlt(stripList.get(i).getAlt());
				s.setDate(stripList.get(i).getDate());
				s.setDay(stripList.get(i).getDay());
				s.setImg(stripList.get(i).getImg());
				s.setMonth(stripList.get(i).getMonth());
				s.setNum(stripList.get(i).getNum());
				s.setTitle(stripList.get(i).getTitle());
				s.setYear(stripList.get(i).getYear());
				
				strips.add(s);
				
				Log.d(TAG, s.getImg());
			}
			
			return strips; 
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
