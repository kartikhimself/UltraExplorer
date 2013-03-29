package com.mirrorlabs.musicplayer;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private static final String TAB_NAME_MUSIC = "nevermusic";
	private static final String TAB_NAME_PLAYLIST = "playlist";
	private static final String CREATE_TAB_PLAYLIST = "create table "
			+ "playlist(_id integer primary key,listname text)";
	private static final String CREATE_TAB_MUSIC = "create table "
			+ "nevermusic(_id integer primary key autoincrement,music_id integer,clicks integer,"
			+ "latest text,list1 integer,list2 integer,list3 integer,list4 integer,list5 integer)";

	private SQLiteDatabase db = null;
	private Cursor c = null;

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		this.db = db;
		db.execSQL(CREATE_TAB_MUSIC);
		db.execSQL(CREATE_TAB_PLAYLIST);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	public void insertPlaylist(ContentValues values) {

		db = getWritableDatabase();

		db.insert(TAB_NAME_PLAYLIST, null, values);
		c = db.query(TAB_NAME_PLAYLIST, new String[] { "_id" }, null, null,
				null, null, null);
		int temp = c.getCount();
		c = db.query(TAB_NAME_PLAYLIST, null, null, null, null, null, null);
		temp = c.getCount();

		db.close();
	}

	public void clearTable(String tablename) {
		db = getWritableDatabase();
		c = db.query(tablename, new String[] { "_id" }, null, null, null, null,
				null);
		int temp = c.getCount();
		
		c.moveToFirst();
		for (int i = 0; i < c.getCount(); i++) {
			temp = c.getInt(c.getColumnIndex("_id"));
			db.delete(tablename, "_id=?", new String[] { String.valueOf(c
					.getInt(c.getColumnIndex("_id"))) });
			c.moveToNext();
		}
	}

	public void clearMusicDatabase() {
		clearTable(TAB_NAME_MUSIC);
		clearTable(TAB_NAME_PLAYLIST);
	}

	public void insertMusic(ContentValues values) {
		db = getWritableDatabase();
		db.insert(TAB_NAME_MUSIC, null, values);

		db.close();
	}

	public void deletePlayList(int id) {

		db = getWritableDatabase();

		db.delete(TAB_NAME_PLAYLIST, "_id=?",
				new String[] { String.valueOf(id) });
		db.close();
	}

	public void deleteMusic(int id) {

		db = getWritableDatabase();

		db.delete(TAB_NAME_MUSIC, "music_id=?",
				new String[] { String.valueOf(id) });
		db.close();
	}

	public void updateMusic(ContentValues values, int id) {

		db = getWritableDatabase();

		db.update(TAB_NAME_MUSIC, values, "music_id=" + id, null);
		db.close();
	}

	public void updatePlaylist(ContentValues values, int id) {

		db = getWritableDatabase();

		db.update(TAB_NAME_PLAYLIST, values, "_id=" + id, null);
		db.close();
	}

	public Cursor queryAllPlaylist() {

		db = getReadableDatabase();

		c = db.query(TAB_NAME_PLAYLIST, new String[] { "_id", "listname" },
				null, null, null, null, null);
		int temp = c.getCount();
		db.close();
		return c;
	}

	public Cursor queryPlaylistId() {

		db = getReadableDatabase();

		c = db.query(TAB_NAME_PLAYLIST, new String[] { "_id" }, null, null,
				null, null, null);
		int temp = c.getCount();
		c = db.query(TAB_NAME_PLAYLIST, null, null, null, null, null, null);
		temp = c.getCount();
		db.close();
		return c;
	}

	public Cursor queryMusic(int id) {

		db = getWritableDatabase();

		c = db.query(TAB_NAME_MUSIC, null, "music_id=?",
				new String[] { String.valueOf(id) }, null, null, null);
		db.close();
		return c;
	}

	public Cursor queryList(int pos) {

		db = getWritableDatabase();

		c = db.query(TAB_NAME_MUSIC, null, "list" + pos + "=?",
				new String[] { String.valueOf(1) }, null, null, null);
		return c;
	}

	public boolean queryIsInList(int pos, int id, String title) {

		db = getWritableDatabase();

		c = db.query(TAB_NAME_MUSIC, new String[] { "list" + pos },
				"music_id=?", new String[] { String.valueOf(id) }, null, null,
				null);

		if (c == null || c.getCount() == 0) {
			c.moveToFirst();
			ContentValues values = new ContentValues();
			Date currenttime = new Date();
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String dateStr = format.format(currenttime);
			values.put("music_id", id);
			values.put("clicks", 1);
			values.put("latest", dateStr);
			values.put("list1", 0);
			values.put("list2", 0);
			values.put("list3", 0);
			values.put("list4", 0);
			values.put("list5", 0);
			insertMusic(values);
			return false;
		} else {
			c.moveToFirst();
			if (c.getInt(c.getColumnIndex("list" + pos)) == 0) {
				return false;
			} else {
				return true;
			}
		}
	}

	public Cursor queryMusicByClicks() {

		db = getWritableDatabase();

		c = db.query(TAB_NAME_MUSIC, null, null, null, null, null,
				"clicks desc");
		int temp = c.getCount();
		c.moveToFirst();
		for (int i = 0; i < c.getCount(); i++) {
			temp = c.getInt(c.getColumnIndex("list1"));
			temp = c.getInt(1);
			c.moveToNext();
		}
		db.close();
		return c;
	}

	public Cursor queryMusicByRecently() {

		db = getWritableDatabase();

		c = db.query(TAB_NAME_MUSIC, null, null, null, null, null,
				"latest desc");
		int temp = c.getCount();
		String tempstr = "";
		c.moveToFirst();
		if (c != null || c.getCount() != 0) {
			for (int i = 0; i < c.getCount(); i++) {
				temp = c.getInt(1);
				tempstr = c.getString(2);
				c.moveToNext();
			}
		}

		db.close();
		return c;
	}

	public void close() {
		if (db != null) {
			db.close();
			db = null;
		}
		if (c != null) {
			c.close();
			c = null;
		}
	}
}
