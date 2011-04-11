package com.rickreation.webcomicviewer;

import java.util.ArrayList;

import org.apache.http.client.HttpClient;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

import com.rickreation.webcomicviewer.adapters.StripListAdapter;
import com.rickreation.webcomicviewer.api.WebComicViewerApi;
import com.rickreation.webcomicviewer.models.Strip;

public class StripListActivity extends ListActivity {
	public static final String TAG = "StripListActivity";
	
	ListView mStripList;
	
	int mComicId;
	int mFrom;
	int mCount;
	
	StripListAdapter mAdapter;
	
	ArrayList<Strip> mStrips;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminate(true);        
        setProgressBarIndeterminateVisibility(true);
        
        setContentView(R.layout.striplist_activity);
        
		mStripList = getListView();
		
		Intent i = getIntent();
		mComicId = i.getIntExtra("id", 0);
		mFrom = 0;
		mCount = 20;				
		
		mStrips = new ArrayList<Strip>();
		
		mAdapter = new StripListAdapter(this, mStrips);		
		mStripList.setAdapter(mAdapter);
		
		new FetchStripsTask().execute((String)null);
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}
	
	public class FetchStripsTask extends AsyncTask<String, Integer, Long> {
    	@Override
    	protected Long doInBackground(String... params) {
    		//Need httpClient and apiUrl
    		//Get data
    		//Display data
    		WebComicViewerApp app = (WebComicViewerApp) getApplication();
    		HttpClient httpClient = app.getHttpClient();
    		String apiUrl = getResources().getString(R.string.api_url);
    		
    		WebComicViewerApi api = new WebComicViewerApi(httpClient, apiUrl);
    		mStrips = api.getStrips(mComicId, mFrom, mCount);
    		    		
//    		Log.d(TAG, "Comic list is " + data);
    		return null;
    	}
    	
    	protected void onPostExecute(Long result) {    	
    		try {
    			mAdapter.changeStrips(mStrips);
        		mAdapter.notifyDataSetChanged();
    		}
    		catch(Exception e) {
    			e.printStackTrace();
    		}
    		
    	}
    }

}