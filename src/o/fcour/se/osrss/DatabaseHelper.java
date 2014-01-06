package o.fcour.se.osrss;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "OsRSS";
	private static final int DATABASE_VERSION = 1;
	
	private static final String TABLE_URLS = "URLS";
	
	private static final String COLUMN_URL = "URL";
	
	public DatabaseHelper(Context c) {
		super(c,DATABASE_NAME,null,DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		/* Create a single column table to store URLS in */
		String CREATE_URLS_TABLE = "CREATE TABLE "+TABLE_URLS+"("+
					COLUMN_URL+" TEXT UNIQUE)";
		db.execSQL(CREATE_URLS_TABLE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int OLD_VERSION, int NEW_VERSION) {
		db.execSQL("DROP TABLE IF EXISTS "+DATABASE_NAME);
		onCreate(db);
	}
	
	/**
	 * Returns true if inserted, false not. Only reason this should fail
	 * is if it already exists.
	 */
	public boolean addURL(URL url) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_URL,url.toString());
		try {
			db.insertOrThrow(TABLE_URLS, null, values);
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	
	public List<URL> getUrls() {
		List<URL> result = new ArrayList<URL>();
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = this.getReadableDatabase();
			cursor = db.rawQuery("SELECT * FROM "+TABLE_URLS,null);
			if(cursor.moveToFirst()) {
				do {
					URL next;
					try {
						next = new URL(cursor.getString(0));
					} catch(Exception e) {
						continue;
					}
					result.add(next);
				} while(cursor.moveToNext());
			}
		} finally {
			if(cursor!=null) cursor.close();
			if(db!=null) db.close();
		}
		return result;
	}
	
	public void deleteURL(URL url) {
		
	}
}
