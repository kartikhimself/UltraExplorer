package com.mirrorlabs.musicplayer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.mirrorlabs.filebrowser.FileUtils;
import com.mirrorlabs.filebrowser.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;



public class PlayerActivity extends Activity implements
		MediaPlayer.OnCompletionListener {

	private ImageButton bt_play = null;
	private ImageButton bt_last = null;
	private ImageButton bt_next = null;
	private ImageButton bt_forward = null;
	private ImageButton bt_backward = null;

	private SeekBar sb_progress = null;

	private int[] musicids;
	private String[] musictitles;
	private String[] musicartists;
	private int position;
	
	String songname;
	String artistname;

	private TextView title;
	private TextView playtime;
	private TextView duration;
	private TextView artist;

	private MediaPlayer player;

	// private DelayThread dThread;
	private Handler fb_handler;
	private Handler refresh_handler;

	private final int RANDOM_MODE = 1;
	private final int ALL_LOOP_MODE = 2;
	private final int SINGLE_LOOP_MODE = 3;

	private int currentMode = -1;
	
	private int currentBg = 0;

	private DBHelper dbHelper = null;
	private Cursor cursor = null;
	private Uri path;

	/*
	 * private Handler mHandle = new Handler() {
	 * 
	 * @Override public void handleMessage(Message msg) { // TODO Auto-generated
	 * method stub super.handleMessage(msg);
	 * 
	 * if (null != player) { int position = player.getCurrentPosition(); int
	 * playMax = player.getDuration(); int sbMax = sb_progress.getMax();
	 * 
	 * sb_progress.setProgress(position * sbMax / playMax); } }
	 * 
	 * };
	 */

	@Override
	public void onCompletion(MediaPlayer _player) {
		// TODO Auto-generated method stub
		//position = getNext();
		resetPlayer();
		play();
	}

	private int getNext() {
		int nextPosition = 0;
		int sizeofAll = musicids.length;
		switch (currentMode) {
		case RANDOM_MODE:
			Random ran = new Random();
			nextPosition = ran.nextInt(sizeofAll-1);
			break;

		case SINGLE_LOOP_MODE:
			nextPosition = position;
			break;

		case ALL_LOOP_MODE:
			if (position == sizeofAll - 1) {
				nextPosition = 0;
			} else {
				nextPosition = position + 1;
			}
			break;

		default:
			break;
		}
		return nextPosition;
	}

	private boolean releaseAll() {
		if (null != player) {
			player.reset();
			player.release();
			player = null;
		}
		if (null != fb_handler) {
			fb_handler.removeCallbacks(forward);
			fb_handler.removeCallbacks(backward);
			fb_handler = null;
		}
		if (null != dbHelper) {
			dbHelper.close();
			dbHelper = null;
		}
		if (null != refresh_handler) {
			refresh_handler.removeCallbacks(refresh);
			refresh_handler = null;
		}
		sb_progress.setProgress(0);
		playtime.setText("00:00");
		duration.setText("00:00");
		return false;
	}

	private void setMedia() {

		title.setText(songname);
		artist.setText(artistname);
		//player = new MediaPlayer();

		//Uri uri = Uri
			//	.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					//	"" + musicids[pos]);

		fb_handler = new Handler();
		refresh_handler = new Handler();
		// player = MediaPlayer.create(getApplicationContext(), R.raw.a);
		try {
			player = new MediaPlayer();
			player.setDataSource(getApplicationContext(), path);
			player.prepare();
			player.setOnCompletionListener(this);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void resetPlayer() {
		releaseAll();
		setMedia();
	}

	private boolean play() {
		if (null != player) {
			try {
				// player.prepare();
				player.start();
				bt_play.setBackgroundResource(R.drawable.pause_selecor);
				// startProgressUpdate();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (null != refresh_handler) {
				refresh_handler.postDelayed(refresh, 500);
			}
			return true;
		}
		return false;
	}

	private void updateMusicDB(int position) {
		dbHelper = new DBHelper(this, "music.db", null, 3);
		Date currenttime = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = format.format(currenttime);
		cursor = dbHelper.queryMusic(position);
		cursor.moveToFirst();
		if (cursor == null || cursor.getCount() == 0) {
			ContentValues values = new ContentValues();
			values.put("music_id", position);
			values.put("clicks", 1);
			values.put("latest", dateStr);
			values.put("list1", 0);
			values.put("list2", 0);
			values.put("list3", 0);
			values.put("list4", 0);
			values.put("list5", 0);
			dbHelper.insertMusic(values);
		} else {
			int clicks = cursor.getInt(2);
			clicks++;
			ContentValues values = new ContentValues();
			values.put("clicks", clicks);
			values.put("latest", dateStr);
			dbHelper.updateMusic(values, position);
		}
		if (dbHelper != null) {
			dbHelper.close();
			dbHelper = null;
		}
	}

	@SuppressLint({ "NewApi", "NewApi", "NewApi", "NewApi", "NewApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_player);

		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		path =Uri.parse(bundle.getString("path"));
		
		File mFile = new File(bundle.getString("filePath"));
		

		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		mmr.setDataSource(mFile.getAbsolutePath());
		
	    songname = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
	    artistname =mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

       
        musicids = bundle.getIntArray("musicids");
		musictitles = bundle.getStringArray("musictitles");
		position = bundle.getInt("position");
		musicartists = bundle.getStringArray("musicartists");
		currentMode = bundle.getInt("playmode");
		
		

		title = (TextView) findViewById(R.id.name);
		playtime = (TextView) findViewById(R.id.playtime);
		duration = (TextView) findViewById(R.id.duration);
		artist = (TextView) findViewById(R.id.artist);
 
		bt_play = (ImageButton) findViewById(R.id.play);
		bt_play.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (null != player) {
					if (player.isPlaying()) {
						player.pause();
						// releaseAll();
						bt_play.setBackgroundResource(R.drawable.play_selecor);
					} else {
						player.start();
						// setMedia(0);
						play();
						bt_play.setBackgroundResource(R.drawable.pause_selecor);
					}
				} /*
				 * else { setMedia(0); play();
				 * bt_play.setBackgroundResource(R.drawable.pause_selecor); }
				 */
			}
		});

		sb_progress = (SeekBar) findViewById(R.id.seekbar);
		sb_progress.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar sb) {
				// TODO Auto-generated method stub
				if (null != player) {
					int dest = sb_progress.getProgress();
					int playerMax = player.getDuration();
					int sbMax = sb_progress.getMax();
					player.seekTo(dest * playerMax / sbMax);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar sb) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar sb, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				// if (arg2) {
				// player.seekTo(arg1);
				// }
			}
		});

		bt_forward = (ImageButton) findViewById(R.id.forward);
		bt_forward.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (null != player) {
						fb_handler.post(forward);
						player.pause();
					}
					break;

				case MotionEvent.ACTION_UP:
					if (null != player) {
						fb_handler.removeCallbacks(forward);
						player.start();
					}
					break;

				default:
					break;
				}
				return false;
			}
		});

		bt_backward = (ImageButton) findViewById(R.id.backward);
		bt_backward.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (null != player) {
						fb_handler.post(backward);
						player.pause();
					}
					break;

				case MotionEvent.ACTION_UP:
					if (null != player) {
						fb_handler.removeCallbacks(backward);
						player.start();
					}
					break;

				default:
					break;
				}
				return false;
			}
		});

		bt_next = (ImageButton) findViewById(R.id.next);
		bt_next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				int max = musicids.length;
				if (position == max - 1) {
					position = 0;
				} else {
					position++;
				}
				releaseAll();
				setMedia();
				play();
			}
		});

		bt_last = (ImageButton) findViewById(R.id.last);
		bt_last.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				int max = musicids.length;
				if (position == 0) {
					position = max - 1;
				} else {
					position--;
				}
				releaseAll();
				setMedia();
				play();
			}
		});
	}
	
	private String getRealPathFromURI(Uri contentURI) {
		String[] STAR = { "*" };     
        Uri allaudiosong = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		String audioselection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
		Cursor cursor;
		cursor = PlayerActivity.this.managedQuery(allaudiosong, STAR, audioselection, null, null);
	    cursor.moveToFirst(); 
	    int idx = cursor.getColumnIndex(MediaStore.Audio.Media.DATA); 
	    return cursor.getString(idx); 
	}


	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (null == player) {
			resetPlayer();
			//updateMusicDB(musicids[position]);
			play();
		}
	}

	private Runnable forward = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (null != player) {
				int current = player.getCurrentPosition();
				int duration = player.getDuration();
				if (current < duration) {
					current += 5000;
					player.seekTo(current);
					fb_handler.postDelayed(forward, 500);
				}
			} else {
				fb_handler.removeCallbacks(forward);
			}

		}
	};

	private Runnable backward = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (null != player) {
				int current = player.getCurrentPosition();
				if (current > 0) {
					current -= 5000;
					player.seekTo(current);
					fb_handler.postDelayed(backward, 500);
				}
			} else {
				fb_handler.removeCallbacks(forward);
			}

		}
	};

	private Runnable refresh = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			/*
			 * try { sleep(100); } catch (InterruptedException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); }
			 */

			// mHandle.sendEmptyMessage(0);
			if (null != player) {
				int position = player.getCurrentPosition();
				int playMax = player.getDuration();
				int sbMax = sb_progress.getMax();

				sb_progress.setProgress(position * sbMax / playMax);
				String cur = toTime(player.getCurrentPosition());
				String all = toTime(player.getDuration());
				playtime.setText(cur);
				duration.setText(all);
				refresh_handler.postDelayed(refresh, 500);
			}
		}
	};

	/*
	 * public class DelayThread extends Thread { // int milliseconds; private
	 * TextView pTime; private TextView aTime;
	 * 
	 * public DelayThread(TextView _pTime, TextView _aTime) { this.pTime =
	 * _pTime; this.aTime = _aTime; }
	 * 
	 * public void run() { while (true) { try { sleep(100); } catch
	 * (InterruptedException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * // mHandle.sendEmptyMessage(0); if (null != player) { int position =
	 * player.getCurrentPosition(); int playMax = player.getDuration(); int
	 * sbMax = sb_progress.getMax();
	 * 
	 * sb_progress.setProgress(position * sbMax / playMax); String cur =
	 * toTime(player.getCurrentPosition()); String all =
	 * toTime(player.getDuration()); playtime.setText(cur);
	 * duration.setText(all); } } } }
	 */

	/*
	 * public void startProgressUpdate() { // 开辟Thread 用于定期刷新SeekBar dThread =
	 * new DelayThread(playtime, duration); dThread.start(); }
	 */

	public String toTime(int time) {

		time /= 1000;
		int minute = time / 60;
		int second = time % 60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		releaseAll();
	}

}
