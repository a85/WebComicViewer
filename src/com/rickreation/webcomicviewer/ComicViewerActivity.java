package com.rickreation.webcomicviewer;

import java.util.ArrayList;

import org.apache.http.client.HttpClient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.rickreation.ui.ZoomableImageView;
import com.rickreation.webcomicviewer.api.WebComicViewerApi;
import com.rickreation.webcomicviewer.models.Strip;
import com.rickreation.webcomicviewer.utils.BitmapUtils;

public class ComicViewerActivity extends Activity {
	public static final String TAG = "ComicViewerActivity";
	public static final int ACTION_PREVIOUS_STRIP = 0;
	public static final int ACTION_NEXT_STRIP = 1;
	
	private ComicDbAdapter mDbHelper;
	
	ZoomableImageView mZoomImageView;
	
	Strip s;
	
	String fileUrl;
	
	private Handler mHandler;
	
	private ProgressBar mStripProgress;
	private ArrayList<Strip> mStrips;
	
	private int mCurrentStrip;
	private int mComicId;
	private int mFrom;
	private int mCount;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.comicviewer_activity);
    	    	
    	Intent i = getIntent();    	    
    	    	
    	s = (Strip)i.getParcelableExtra("strip");    	
    	
    	mComicId = i.getIntExtra("comic_id", 0);
    	mFrom = i.getIntExtra("from", 0);
    	mCount = i.getIntExtra("count", 20);
    	    	
    	if(s == null) {
    		mStrips = (ArrayList)i.getParcelableArrayListExtra("strips");
    		mCurrentStrip = (int)i.getIntExtra("current_strip", 0);    		    		    	
    		
    		s = mStrips.get(mCurrentStrip);
    		fileUrl = s.getImg();
    	}
    	else {
    		fileUrl = s.getImg();
    	}
    	
    	
    	mHandler = new Handler();    	    	
    	
        mZoomImageView = (ZoomableImageView) findViewById(R.id.comic_viewer);
        mStripProgress = (ProgressBar) findViewById(R.id.strip_progress);
        
        mStripProgress.setVisibility(View.VISIBLE);
        mZoomImageView.setDefaultScale(ZoomableImageView.DEFAULT_SCALE_ORIGINAL);
        
        new LoadPhotoBitmapThread().start();
    }
    
    @Override
    public void onStop() {
    	super.onStop();    	
    }    
    
    /**
	 * Utility to load a larger version of the image in a separate thread.
	 * 
	 */
	private class LoadPhotoBitmapThread extends Thread {

		public LoadPhotoBitmapThread() {
		}

		@Override
		public void run() {
			try {
				String uri = fileUrl;
				final Bitmap b = BitmapUtils.loadBitmap(uri);
				mHandler.post(new Runnable() {
					public void run() {
						Log.d(TAG, "FInished loading file");
						drawImage(b);
					}
				});
			} catch (Exception e) {
				Log.d(TAG, e.toString());
			}
		}
	}

	// Draw the image inside the environment view
	private void drawImage(Bitmap b) {
		mStripProgress.setVisibility(View.INVISIBLE);
		
		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		Log.d(TAG, "Drawing image");
		animation.setDuration(500);
		animation.setRepeatCount(0);
		animation.setFillAfter(true);
		
		if (b == null) {
			Toast.makeText(getBaseContext(), "Could not get the image.", Toast.LENGTH_SHORT).show();
		} else {						
			mZoomImageView.setBitmap(b);
			mZoomImageView.startAnimation(animation);
		}		
	}
	
	public void gotoWebsite(View v) {
		Log.d(TAG, "Goto website");				
		Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(s.getUrl()));
		startActivity(browserIntent);
	}
	
	public void nextStrip(View v) {
		Log.d(TAG, "Show next strip");
		
		int size = mStrips.size();
		if(mCurrentStrip < size - 1) {
			showNextStrip();
		}
		else {
			new FetchStripsTask().execute(ComicViewerActivity.ACTION_NEXT_STRIP);
		}
	}
	
	public void previousStrip(View v) {
		Log.d(TAG, "Show previous strip");
		
		if(mCurrentStrip >= 0) {
			showPreviousStrip();
		}
		else {
			new FetchStripsTask().execute(ComicViewerActivity.ACTION_PREVIOUS_STRIP);
		}
	}
	
	private void showNextStrip() {
		mCurrentStrip += 1;
		if(mCurrentStrip < mStrips.size()) {
			s = mStrips.get(mCurrentStrip);
			mStripProgress.setVisibility(View.VISIBLE);
			
			Animation animation = new AlphaAnimation(1.0f, 0.0f);		
			animation.setDuration(500);
			animation.setRepeatCount(0);
			animation.setFillAfter(true);
					
			mZoomImageView.startAnimation(animation);
			
			fileUrl = s.getImg();
			new LoadPhotoBitmapThread().start();
		}
		else {
			Toast.makeText(getBaseContext(), "The End!", Toast.LENGTH_SHORT).show();
		}
				
	}
	
	private void showPreviousStrip() {
		mCurrentStrip -= 1;
		if(mCurrentStrip >= 0) {
			s = mStrips.get(mCurrentStrip);
			mStripProgress.setVisibility(View.VISIBLE);
			
			Animation animation = new AlphaAnimation(1.0f, 0.0f);		
			animation.setDuration(500);
			animation.setRepeatCount(0);
			animation.setFillAfter(true);
					
			mZoomImageView.startAnimation(animation);
			
			fileUrl = s.getImg();
			new LoadPhotoBitmapThread().start();
		}
		else {
			Toast.makeText(getBaseContext(), "The End!", Toast.LENGTH_SHORT).show();
		}
	}
	
	public class FetchStripsTask extends AsyncTask<Integer, Integer, Long> {
		ArrayList<Strip> newStrips;
		int action;
		
    	@Override
    	protected Long doInBackground(Integer... params) {
    		//Need httpClient and apiUrl
    		//Get data
    		//Display data    		
    		action = params[0];
    		newStrips = new ArrayList<Strip>();
    		WebComicViewerApp app = (WebComicViewerApp) getApplication();
    		HttpClient httpClient = app.getHttpClient();
    		String apiUrl = getResources().getString(R.string.api_url);    		
    		WebComicViewerApi api = new WebComicViewerApi(httpClient, apiUrl);
    		newStrips = api.getStrips(mComicId, mFrom, mCount);
    		return null;
    	}
    	
    	protected void onPostExecute(Long result) {    	
    		try {    			    			    			
    			if(action == ComicViewerActivity.ACTION_NEXT_STRIP) {
    				mFrom += mCount;
    				showNextStrip();
    				mStrips.addAll(newStrips);
    			}
    			else if(action == ComicViewerActivity.ACTION_PREVIOUS_STRIP) {
    				mFrom -= 2 * mCount;
    				showPreviousStrip();
    				mStrips.addAll(0, newStrips);
    			}
    			
    		}
    		catch(Exception e) {
    			e.printStackTrace();
    		}
    		
    	}
    }
}
