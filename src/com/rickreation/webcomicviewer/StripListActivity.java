package com.rickreation.webcomicviewer;

import java.util.ArrayList;

import org.apache.http.client.HttpClient;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.commonsware.cwac.endless.EndlessAdapter;
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
		mStripList.setAdapter(new StripListEndlessAdapter(mAdapter));
		
		mStripList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				Intent i = new Intent(getBaseContext(), ComicViewerActivity.class);				
				i.putExtra("strips", mStrips);
				i.putExtra("current_strip", position);
				i.putExtra("comic_id", mComicId);
				i.putExtra("from", mFrom);
				i.putExtra("count", mCount);
				startActivity(i);
			}
		});
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}	
	
	class StripListEndlessAdapter extends EndlessAdapter {
		ArrayList<Strip> strips;
		Boolean failed = true;
		
		public StripListEndlessAdapter(ListAdapter wrapped) {
			super(wrapped);
		}

		@Override
		protected void appendCachedData() {
			if(failed == false) {
				mStrips.addAll(strips);
			}			
		}

		@Override
		protected boolean cacheInBackground() {
			WebComicViewerApp app = (WebComicViewerApp) getApplication();
    		HttpClient httpClient = app.getHttpClient();
    		String apiUrl = getResources().getString(R.string.api_url);
    		
    		WebComicViewerApi api = new WebComicViewerApi(httpClient, apiUrl);
    		strips = api.getStrips(mComicId, mFrom, mCount);
    		
    		mFrom = mFrom + mCount;
    		
    		failed = false;
    		
			if(strips.size() < mCount) {				
				return false;
			}
			else {
				return true;
			}
		}

		@Override
		protected View getPendingView(ViewGroup arg0) {
			Log.d(TAG, "Getting pending view");
			View row = getLayoutInflater().inflate(R.layout.item_loading_row, null);
			
			return row;
		}
		
	}

}