		package com.rickreation.webcomicviewer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rickreation.webcomicviewer.models.Strip;

public class ComicDbAdapter {
	public static final String KEY_COMIC = "comic";
	public static final String KEY_IMG = "img";
	public static final String KEY_TITLE = "title";
	public static final String KEY_ALT = "alt";
	public static final String KEY_NUM = "num";
	
	public static final String KEY_DATE = "date";
	public static final String KEY_MONTH = "month";
	public static final String KEY_DAY = "day";
	public static final String KEY_YEAR = "year";
	
	public static final String KEY_TRANSCRIPT = "transcript";
	
	public static final String KEY_ROWID = "_id";
	
	private static final String TAG = "ComicDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	String[] allColumns = new String[] {
			KEY_ROWID, KEY_COMIC, KEY_TITLE, KEY_IMG, KEY_ALT, KEY_TRANSCRIPT,
			KEY_DATE, KEY_NUM, KEY_MONTH, KEY_YEAR, KEY_DAY
	};
	
	/*
	 * Database creation sql statement
	 */
	private static final String DATABASE_CREATE =
		"create table comics (_id integer primary key autoincrement, "
		+ "comic text not null, "
		+ "img text not null, "
		+ "title text not null, "
		+ "alt text, "
		+ "num integer, "
		+ "date text, "
		+ "day text, "
		+ "month integer, "
		+ "year integer, "
		+ "transcript text);";
	
	private static final String DATABASE_NAME = "webcomicviewer";
	private static final String DATABASE_TABLE = "comics";
	private static final int DATABASE_VERSION = 3;
	
	private final Context mCtx;
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS comics");
            onCreate(db);
		}
	}
	
	/*
	 * Constructor - takes the context to allow the database to be opened/created
	 */
	
	public ComicDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}
	
	public ComicDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		mDbHelper.close();
	}
	
	public long addStrip(Strip strip) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_COMIC, strip.getComic());
		initialValues.put(KEY_TITLE, strip.getTitle());
		initialValues.put(KEY_IMG, strip.getImg());
		initialValues.put(KEY_ALT, strip.getAlt());
		initialValues.put(KEY_TRANSCRIPT, strip.getTranscript());
		initialValues.put(KEY_DATE, strip.getDate());
		initialValues.put(KEY_NUM, strip.getNum());
		initialValues.put(KEY_MONTH, strip.getMonth());
		initialValues.put(KEY_YEAR, strip.getYear());
		initialValues.put(KEY_DAY, strip.getDay());
		
		return mDb.insert(DATABASE_TABLE, null, initialValues);		
	}
	
	public boolean deleteStrip(long rowId) {
		return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	public Cursor fetchAllStrips() {
		String[] columns = new String[] {
				KEY_ROWID, KEY_COMIC, KEY_TITLE, KEY_IMG, KEY_ALT, KEY_TRANSCRIPT,
				KEY_DATE, KEY_NUM, KEY_MONTH, KEY_YEAR, KEY_DAY
		};
		return mDb.query(DATABASE_TABLE, columns, null, null, null, null, null);
	}
	
	public Strip fetchStrip(long rowId) throws SQLException {
		Cursor mCursor = mDb.query(true, DATABASE_TABLE, allColumns, KEY_ROWID + "=" + rowId, null, null, null, null, null);
		Strip strip = new Strip();
		
		if(mCursor != null) {
			mCursor.moveToFirst();
		}
		
		strip.setAlt(mCursor.getString(mCursor.getColumnIndexOrThrow(ComicDbAdapter.KEY_ALT)));
		
		return strip;
	}
		
}
