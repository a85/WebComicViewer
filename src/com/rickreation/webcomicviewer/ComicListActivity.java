package com.rickreation.webcomicviewer;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.HttpClient;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.rickreation.webcomicviewer.api.WebComicViewerApi;
import com.rickreation.webcomicviewer.models.Comic;
import com.rickreation.webcomicviewer.models.Strip;


public class ComicListActivity extends ListActivity {
	public static final String TAG = "ComicListActivity";    
    
	ListView comicList;
	
	String[] from = new String[]{ "comic_title", "comic_num_strips" };
	int[] to = new int[] { R.id.item_comic_name, R.id.item_comic_num_strips };
	
	List<HashMap<String, String>> comics;
	
	private File cacheDir;
	
	private ComicDbAdapter mDbHelper;
	
	SimpleAdapter adapter;
		
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
        
        adapter = new SimpleAdapter(this, comics, R.layout.item_comic, from, to);
        comicList.setAdapter(adapter);
        
        comicList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {				
		    	HashMap<String, String> item = comics.get(position);
		    	String comic_title = (String)item.get("comic_title");
		    	int comic_id = Integer.parseInt(item.get("mComicId"));		    	
		    	startComicViewer(comic_id, comic_title);
			}
		});
                
        //Start AsyncTask to fetch stuff from wc.rickreation.com/api.php        
        new FetchComicsTask().execute((String)null);
    }
    
    @Override
    public void onStop() {
    	super.onStop();    	    
    }    
    
    private void startComicViewer(int id, String title) {
    	Intent i = new Intent(this, StripListActivity.class);
    	i.putExtra("title", title);
    	i.putExtra("id", id);
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
    
    public class FetchComicsTask extends AsyncTask<String, Integer, Long> {
    	@Override
    	protected Long doInBackground(String... params) {
    		//Need httpClient and apiUrl
    		//Get data
    		//Display data
    		WebComicViewerApp app = (WebComicViewerApp) getApplication();
    		HttpClient httpClient = app.getHttpClient();
    		String apiUrl = getResources().getString(R.string.api_url);
    		
    		WebComicViewerApi api = new WebComicViewerApi(httpClient, apiUrl);
    		List<Comic> comicsList = api.getComicList();
    		
    		comics.clear();
    		
    		for(int i = 0; i < comicsList.size(); i++) {
				Comic comic = comicsList.get(i);
			
				HashMap<String, String> hmap = new HashMap<String, String>();
				hmap.put("mComicId", Integer.toString(comic.getId()));
				hmap.put("comic_title", comic.getTitle());
				hmap.put("comic_num_strips", Integer.toString(comic.getNum_strips()));
				
				comics.add(hmap);
			}    		    		
    		
//    		Log.d(TAG, "Comic list is " + data);
    		return null;
    	}
    	
    	protected void onPostExecute(Long result) {
    		adapter.notifyDataSetChanged();
    		setProgressBarIndeterminateVisibility(false);
    	}
    }
    
}