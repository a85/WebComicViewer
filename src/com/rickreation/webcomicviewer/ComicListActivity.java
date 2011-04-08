package com.rickreation.webcomicviewer;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.rickreation.R;
import com.rickreation.webcomicviewer.models.Strip;

public class ComicListActivity extends ListActivity {
	public static final String TAG = "ComicListActivity";    
    
	ListView comicList;
	
	String[] from = new String[]{ "comic_name", "comic_num_items" };
	int[] to = new int[] { R.id.item_comic_name, R.id.item_comic_num_items };
	
	List<HashMap<String, String>> comics;
	
	private File cacheDir;
	
	private ComicDbAdapter mDbHelper;
		
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminate(true);        
        setProgressBarIndeterminateVisibility(true);
        
        setContentView(R.layout.comiclist_activity);
                
        comicList = getListView();
        
        
        //This needs to be filled for the list to show up
        comics = new ArrayList<HashMap<String, String>>();                
        
        SimpleAdapter adapter = new SimpleAdapter(this, comics, R.layout.item_comic, from, to);
        comicList.setAdapter(adapter);
        
        comicList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {				
		    	HashMap<String, String> item = comics.get(position);
		    	String comicName = (String)item.get("comic_name");		    	
		    	
		    	startComicViewer(comicName);
			}
		});
        
        mDbHelper = new ComicDbAdapter(this);
        mDbHelper.open();        
        
        //Start AsyncTask to fetch stuff from wc.rickreation.com/get.php
        //Fill comics array
        //Display list
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	mDbHelper.close();    	
    }
    
    private void fillComicList() {
    	HashMap<String, String> item2 = new HashMap<String, String>();
    	item2.put("comic_id", "1");
    	item2.put("comic_name", "xkcd");
    	item2.put("comic_num_items", "0");
    	comics.add(item2);
    	
    	HashMap<String, String> item1 = new HashMap<String, String>();
    	item1.put("comic_id", "2");
    	item1.put("comic_name", "Dilbert");    	
    	item1.put("comic_num_items", "0");
    	comics.add(item1);
    	
    	
    }
    
    private void startComicViewer(String comicName) {
    	Intent i = new Intent(this, ComicViewerActivity.class);
    	i.putExtra("comic", comicName);
    	startActivity(i);    	
    }
    
    private void copyDataToCard() {
    	//Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
        	cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"WebComicViewer/Comics");
        }            
        else {
        	cacheDir = getCacheDir();
        }
            
        if(!cacheDir.exists()) {
        	cacheDir.mkdirs();
        }
            
        try {
        	//copy xkcd
        	File xkcdFile = new File(cacheDir, "xkcd");        	
        	if(!xkcdFile.exists()) {        		
        		xkcdFile.createNewFile();        		                
        		InputStream ins = getResources().openRawResource(R.raw.xkcd);
        		int size = ins.available();
        		
//            byte[] buffer = new byte[size];
//            ins.read(buffer);
//            
//            
//            FileOutputStream fos = new FileOutputStream(xkcdFile);
//            fos.write(buffer);
//            fos.close();
        		
        		//Read JSON and insert into the database
        		InputStreamReader insReader = new InputStreamReader(ins);
        		JsonReader js = new JsonReader(insReader);
        		
        		Type collectionType = new TypeToken<List<Strip>>() {}.getType();
        		List<Strip> strips = new Gson().fromJson(js, collectionType);
        		
        		for(int i = 0; i < strips.size(); i++) {
        			Strip s = strips.get(i);
        			s.setComic("xkcd");     
        			s.setDate("");            	            	
        			
        			try {
        				mDbHelper.addStrip(s);
        				Log.d(TAG, "Added " + s.getImg());
        				
        			}
        			catch(Exception e) {        				
        				e.printStackTrace();
        				return;
        			}            	
        		}
        		
        		ins.close();
        	}           
        	
        }
        catch(Exception e) {
        	e.printStackTrace();
        }        
    }
    
    public class WriteFilesTask extends AsyncTask<String, Integer, Long> {

		@Override
		protected Long doInBackground(String... params) {
			copyDataToCard();
			return null;
		}
				
		protected void onPostExecute(Long result) {
			setProgressBarIndeterminateVisibility(false);
		}
    };    
}