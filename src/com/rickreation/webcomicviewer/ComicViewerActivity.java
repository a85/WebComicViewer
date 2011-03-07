package com.rickreation.webcomicviewer;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.rickreation.ui.ZoomableImageView;

public class ComicViewerActivity extends Activity {
	public static final String TAG = "ComicViewerActivity";
	String comicName;
	
	private ComicDbAdapter mDbHelper;
	
	ZoomableImageView mZoomImageView;
	String comic;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	Intent i = getIntent();
    	comic = i.getStringExtra("comic");
    	mDbHelper = new ComicDbAdapter(this);
        mDbHelper.open();        
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	mDbHelper.close();
    }    
}
