package com.rickreation.webcomicviewer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.rickreation.ui.ZoomableImageView;
import com.rickreation.webcomicviewer.models.Strip;
import com.rickreation.webcomicviewer.utils.BitmapUtils;

public class ComicViewerActivity extends Activity {
	public static final String TAG = "ComicViewerActivity";	
	private ComicDbAdapter mDbHelper;
	
	ZoomableImageView mZoomImageView;
	
	Strip s;
	
	String fileUrl;
	
	private Handler mHandler;
	
	private ProgressBar mStripProgress;
	private ArrayList<Strip> mStrips;
	
	private int mCurrentStrip;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.comicviewer_activity);
    	    	
    	Intent i = getIntent();    	    
    	    	
    	s = (Strip)i.getParcelableExtra("strip");    	
    	
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
}
