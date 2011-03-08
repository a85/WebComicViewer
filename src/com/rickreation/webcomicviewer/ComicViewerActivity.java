package com.rickreation.webcomicviewer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.rickreation.R;
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
    	setContentView(R.layout.comicviewer_activity);
    	
    	Intent i = getIntent();
    	comic = i.getStringExtra("comic");
    	mDbHelper = new ComicDbAdapter(this);
        mDbHelper.open();        
        
        mZoomImageView = (ZoomableImageView) findViewById(R.id.comic_viewer);
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.server_attention_span);
        mZoomImageView.setBitmap(b);
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	mDbHelper.close();
    }    
}
