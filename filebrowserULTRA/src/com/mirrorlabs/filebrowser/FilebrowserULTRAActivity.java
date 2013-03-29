package com.mirrorlabs.filebrowser;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.FileNameMap;
import java.net.URLConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import net.sf.andpdf.pdfviewer.PdfViewerActivity;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.preference.PreferenceManager;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;

import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;

import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.ViewFlipper;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gestures.CreateGestureActivity;
import com.android.gestures.GestureBuilderActivity;
import com.android.gestures.GestureMonitorActivity;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ScrollingTextView;

import com.mirrorlabs.imageviewer.ExpandImage;
import com.mirrorlabs.musicplayer.PlayerActivity;
import com.mirrorlabs.quickaction3D.ActionItem;
import com.mirrorlabs.quickaction3D.QuickAction;
import com.mirrorlabs.ui.widgets.IcsListPopupWindow;

public class FilebrowserULTRAActivity extends ListActivity implements
		OnSharedPreferenceChangeListener {

	// private static List<String> item = null;
	private static List<String> path = null;
	private static ListView mylist = null;
	private static GridView myGrid = null;
	private static ViewFlipper viewflipper = null;
	// private static TextView fileInfo=null;
	private static String currentfile = null;
	private static String currentdir = null;
	private static String parentfile = null;
	private static String[] items = null;
	private static String[] paths = null;
	private static String[] founditems = null;

	private static String permkey = "[B@50607";
	private static byte[] encodedkey = null;
	private static int encrypt_success;
	private static int decrypt_success;
	private static int deleteCheck = 0;

	private static int multiItems = 0;

	private static int searchlength = 0;

	private static boolean multiselectflag = false;
	private static boolean searchflag = false;
	private static boolean delete_after_copy = false;
	private static boolean unselectflag = false;
	private static boolean apkicon = true;

	private static int index;
	private static int index_grid;
	private static int index_back;

	private static final String STAR_STATES = "mylist:star_states";

	private boolean[] mStarStates = null;

	private static OnItemClickListener listener;
	private static List<String> multiSelectData = null;

	private static final String PREF_HIDDEN = "displayhiddenfiles";
	private static final String PREF_SORT = "sortby";
	private static final String PREF_VIEW = "viewAs";
	private static final String PREF_HOME = "home";

	private static int sort_mode;
	private static boolean hidden;
	private static String root = Environment.getExternalStorageDirectory()
			.getPath();
	private static String mSort;
	private static String mView;

	private static TextView progress_used;
	private static TextView progress_free;
	private static ProgressBar sdcardBar;

	final static int SEARCH_RESULT = 0;
	final static int GESTURE_RESULT = 1;

	private boolean mUseBackKey = true;

	private static final int SEARCH_TYPE = 0x00;
	private static final int COPY_TYPE = 0x01;
	private static final int DELETE_TYPE = 0x03;
	private static final int ENCRYPT_TYPE = 0x04;
	private static final int DECRYPT_TYPE = 0x05;
	private static final int INFO_TYPE = 0x06;

	private static final int ID_NEW_FOLDER = 1;
	private static final int ID_NEW_FILE = 2;

	private static final int ID_PROCCESS = 1;
	private static final int ID_BACKUP_APPS = 2;
	private static final int ID_GESTURE = 3;

	private static final String[] Q = new String[] { "Bytes", "Kb", "Mb", "Gb",
			"T", "P", "E" };
	private static final int ID_SD_CARD = 1;
	private static final int ID_BACKUP = 2;
	private static final int ID_PROCESS = 3;
	private static final int ID_BACKUP_FOLDER = 4;
	private static final int ID_BlUETOOTH = 5;
	private static final int ID_CAMERA = 6;
	private static final int ID_MUSIC = 7;
	private static final int ID_DOWNLOAD = 8;
	private static final int NOTIFICATION_ID = 0;

	private static String SearchString;

	private ArrayAdapter<String> mAdapter;
	private ArrayList<String> mdata;

	protected ListView mList;
	private static ActionBar actionBar;
	private LinearLayout mDirectoryButtons;
	private SharedPreferences prefs;
	private ArrayList<File> files;
	private UltraBaseAdapter madapter;

	protected String[] mStrings = { "Preferences", "View", "Sort", "Theme",
			"Backup Folder", "Root Folder", "Font Size", "Font Style" };

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!getMounted()) {
			fatalError("Fatal error",
					"Cannot find sdcard. Perhaps your sdcard is not mounted ?");
			return;
		}
		setContentView(R.layout.main);
		// Toast.makeText(FilebrowserULTRAActivity.this," allocated size  = " +
		// getAsString(Debug.getNativeHeapAllocatedSize()), 1).show();

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);

		mSort = prefs.getString(PREF_SORT, "Type");
		mView = prefs.getString(PREF_VIEW, "List");
		hidden = prefs.getBoolean(PREF_HIDDEN, false);

		// Intent intent= getIntent();
		sdcardBar = (ProgressBar) findViewById(R.id.progress_sdcard);

		progress_free = (TextView) findViewById(R.id.progress_free);
		progress_used = (TextView) findViewById(R.id.progress_used);

		mDirectoryButtons = (LinearLayout) findViewById(R.id.directory_buttons);

		myGrid = (GridView) findViewById(R.id.grid);
		mylist = (ListView) findViewById(android.R.id.list);
		viewflipper = (ViewFlipper) findViewById(R.id.myFlipper);

		mylist.setFastScrollEnabled(true);
		myGrid.setFastScrollEnabled(true);

		View copybutton = (ImageButton) findViewById(R.id.paste);
		copybutton.setVisibility(View.GONE);

		// displayNotification("Welcome");

		initializeTypefaces();
		initializekey();
		initializeDrawable();

		// displayNotification("Buy Ultra File Explore");
		// fileInfo = (TextView)findViewById(R.id.info);

		// fileInfo.setTypeface(Fonts.ICS);

		multiSelectData = new ArrayList<String>();

		try {
			if (getIntent().getExtras() != null) {
				Intent intent = getIntent();
				String openDir = intent.getExtras().getString("dir");
				getFileList(openDir);
			} else {

				getFileList(root);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		if (savedInstanceState != null) {
			mStarStates = savedInstanceState.getBooleanArray(STAR_STATES);
		} else {
			mStarStates = new boolean[1000];
		}

		// Configure actionbar
		actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle("Ultra Explorer");
		actionBar.addAction(new ExampleAction());
		setDirectoryButtons();
		checkEnvironment();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBooleanArray(STAR_STATES, mStarStates);
	}

	private Intent createShareIntent() {
		final Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(
				Intent.EXTRA_TEXT,
				"Hey Guys Checkout This awesome File Explorer for Android.It's name is File Explorer Ultra");
		return Intent.createChooser(intent, "Share");
	}

	public static Intent createIntent(Context context) {
		Intent i = new Intent(context, FilebrowserULTRAActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return i;
	}

	private Intent appbackupIntent() {
		final Intent intent = new Intent(FilebrowserULTRAActivity.this,
				BackupManager.class);

		return intent;

	}

	private Intent processIntent() {
		final Intent intent = new Intent(FilebrowserULTRAActivity.this,
				ProcessManager.class);

		return intent;

	}

	private class MoreAction extends AbstractAction {

		public MoreAction() {
			super(R.drawable.ic_menu_moreoverflow_normal_holo_dark);

		}

		@Override
		public void performAction(View view) {
			// TODO Auto-generated method stub

			IcsListPopupWindow dropdown = new IcsListPopupWindow(
					FilebrowserULTRAActivity.this);
			dropdown.setAdapter(null);
			dropdown.dismiss();

			dropdown.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.panel_background));
			dropdown.setAnchorView(actionBar);
			mdata = new ArrayList<String>();
			for (int i = 0; i < 27; i++) {
				mdata.add("My Apps " + i);
			}

			mAdapter = new ArrayAdapter<String>(FilebrowserULTRAActivity.this,
					android.R.layout.simple_list_item_1, mdata);

			dropdown.setAdapter(mAdapter);

			dropdown.show();
		}

	}

	private class ExampleAction extends AbstractAction {

		public ExampleAction() {
			super(R.drawable.ic_menu_moreoverflow_normal_holo_dark);

		}

		public void performAction(View view) {
			// mLayout.toggleSidebar();
			// Toast.makeText(FilebrowserULTRAActivity.this,
			// "Example action", Toast.LENGTH_SHORT).show();
			final QuickAction quickAction = new QuickAction(
					FilebrowserULTRAActivity.this, QuickAction.VERTICAL);
			ActionItem backupItem = new ActionItem(ID_BACKUP_APPS,
					"Backup Manager", getResources().getDrawable(
							R.drawable.android_mdpi));
			ActionItem processItem = new ActionItem(ID_PROCCESS,
					"Process Manager", getResources().getDrawable(
							R.drawable.task_manager_mdpi));
			ActionItem gestureItem = new ActionItem(ID_GESTURE,
					"Gesture Library", getResources().getDrawable(
							R.drawable.hand_icon));
			ActionItem duplicateItem = new ActionItem(4, "Duplicates Manager",
					getResources().getDrawable(R.drawable.duplicates48));
			ActionItem dropboxItem = new ActionItem(5, "DropBox",
					getResources().getDrawable(R.drawable.dropbox));

			quickAction.addActionItem(gestureItem);
			// quickAction.addActionItem(dropboxItem);
			quickAction.addActionItem(duplicateItem);
			quickAction.addActionItem(processItem);
			quickAction.addActionItem(backupItem);

			quickAction
					.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {

						public void onItemClick(QuickAction source, int pos,
								int actionId) {
							// here we can filter which action item was clicked
							// with pos or actionId parameter

							switch (actionId) {
							case ID_GESTURE:

								final Intent intent2 = new Intent(
										FilebrowserULTRAActivity.this,
										GestureBuilderActivity.class);
								startActivity(intent2);

								break;
							case ID_PROCCESS:

								final Intent intent1 = new Intent(
										FilebrowserULTRAActivity.this,
										ProcessManager.class);
								startActivity(intent1);
								break;
							case ID_BACKUP_APPS:

								final Intent intent = new Intent(
										FilebrowserULTRAActivity.this,
										BackupManager.class);
								startActivity(intent);

								break;
							case 4:
								String currentDir = currentdir;
								final Intent duplicate_intent = new Intent(
										FilebrowserULTRAActivity.this,
										DuplicatesManager.class);
								duplicate_intent.putExtra("dir", currentDir);
								startActivity(duplicate_intent);

								break;
							case 5:
								try {
									Intent LaunchIntent = getPackageManager()
											.getLaunchIntentForPackage(
													"com.dropbox.android");
									startActivity(LaunchIntent);
								} catch (Exception e) {
									// TODO: handle exception
									fatalError("No Wifi Connection",
											"This service requires Internet .You might incur charges ");
								}

								break;

							}

						}
					});

			quickAction
					.setOnDismissListener(new QuickAction.OnDismissListener() {

						public void onDismiss() {

						}
					});
			quickAction.setAnimStyle(QuickAction.ANIM_AUTO);

			quickAction.show(view);
		}

	}

	public void displayNotification(String msg) {

		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.explorer48,
				"Welcome", System.currentTimeMillis());

		// The PendingIntent will launch activity if the user selects this
		// notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, FilebrowserULTRAActivity.class), 0);
		RemoteViews contentView = new RemoteViews(getPackageName(),
				R.layout.custom_notification);
		contentView.setImageViewResource(R.id.image, R.drawable.explore64);
		contentView.setTextViewText(R.id.notification_title,
				"Ultra File Explorer");
		contentView.setTextViewText(R.id.notification_text,
				"Welcome to Mirrorlabs");
		notification.contentIntent = contentIntent;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.contentView = contentView;
		manager.notify(NOTIFICATION_ID, notification);

	}

	private void checkEnvironment() {

		File f = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			f = Environment.getExternalStorageDirectory();
			if (f != null) {
				root = f.getAbsolutePath();
			}
		}
		else{

			f = Environment.getRootDirectory();
			if (f != null) {
				root = f.getAbsolutePath();
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case SEARCH_RESULT:
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				String dirname = extras.getString("dir");
				getFileList(dirname);

			}
			Log.d("activity started", "search detected");

			break;
		case GESTURE_RESULT:
			Log.d("activity started", "gesture detected");

			if (resultCode == RESULT_OK) {
				Log.d("activity started", "gesture found");
				Bundle extras = data.getExtras();
				String gestname = extras.getString("gesture");
				File file = new File(gestname);
				if (file.isDirectory()) {
					getFileList(gestname);
				} else {
					OnclickOperation(gestname);
				}

			}
			break;

		}

	}

	public File currentDirectory() {
		return new File(currentdir);
	}

	private void setDirectoryButtons() {
		HorizontalScrollView scrolltext = (HorizontalScrollView) findViewById(R.id.scroll_text);

		String[] parts = currentDirectory().getAbsolutePath().split("/");

		mDirectoryButtons.removeAllViews();

		int WRAP_CONTENT = LinearLayout.LayoutParams.WRAP_CONTENT;
		int MATCH_PARENT = FrameLayout.LayoutParams.MATCH_PARENT;

		// Add home button separately
		ImageButton ib = new ImageButton(this);

		ib.setImageResource(R.drawable.home);
		ib.setBackgroundColor(getResources().getColor(R.color.transparent));
		ib.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT,
				WRAP_CONTENT));
		ib.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				getFileList("/");
			}
		});
		mDirectoryButtons.addView(ib);
		FrameLayout fv = new FrameLayout(this);
		fv.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.ic_list_more));
		fv.setLayoutParams(new FrameLayout.LayoutParams(WRAP_CONTENT,
				MATCH_PARENT));

		mDirectoryButtons.addView(fv);

		// Add other buttons

		String dir = "";

		for (int i = 1; i < parts.length; i++) {
			dir += "/" + parts[i];
			if (dir.equals("/mnt/sdcard")) {
				// Add SD card button
				FrameLayout fv1 = new FrameLayout(this);
				fv1.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.ic_list_more));
				fv1.setLayoutParams(new FrameLayout.LayoutParams(WRAP_CONTENT,
						MATCH_PARENT));

				ib = new ImageButton(this);
				ib.setImageResource(R.drawable.sdcard_48);
				ib.setBackgroundColor(getResources().getColor(
						R.color.transparent));
				ib.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT,
						WRAP_CONTENT));
				ib.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						getFileList(root);
					}
				});
				mDirectoryButtons.addView(ib);
				mDirectoryButtons.addView(fv1);

			} else if (dir.equals("/system")) {

				FrameLayout fv1 = new FrameLayout(this);
				fv1.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.ic_list_more));
				fv1.setLayoutParams(new FrameLayout.LayoutParams(WRAP_CONTENT,
						MATCH_PARENT));
				ib = new ImageButton(this);
				ib.setImageResource(R.drawable.system32);
				ib.setBackgroundColor(getResources().getColor(
						R.color.transparent));
				ib.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT,
						WRAP_CONTENT));
				ib.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						getFileList(root);
					}
				});
				mDirectoryButtons.addView(ib);
				mDirectoryButtons.addView(fv1);

			} else {
				FrameLayout fv1 = new FrameLayout(this);
				fv1.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.ic_list_more));
				fv1.setLayoutParams(new FrameLayout.LayoutParams(WRAP_CONTENT,
						MATCH_PARENT));
				Button b = new Button(this);
				b.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT,
						WRAP_CONTENT));
				b.setTextAppearance(this, R.style.ButtonText);
				b.setBackgroundColor(getResources().getColor(
						R.color.transparent));
				b.setText(parts[i]);
				b.setTag(dir);
				b.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						String dir = (String) view.getTag();
						getFileList(dir);
					}
				});
				mDirectoryButtons.addView(b);
				mDirectoryButtons.addView(fv1);
				scrolltext.postDelayed(new Runnable() {
					public void run() {
						HorizontalScrollView hv = (HorizontalScrollView) findViewById(R.id.scroll_text);
						hv.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
					}
				}, 100L);

			}
		}

		// checkButtonLayout();
	}

	public void UponeLevel() {
		searchflag = false;
		if (currentdir.equals(root)) {

			Toast.makeText(FilebrowserULTRAActivity.this, "press back to exit",
					Toast.LENGTH_SHORT).show();

		} else {
			getFileList(parentfile);
			getListView().setSelectionFromTop(index_back, 0);
			myGrid = (GridView) findViewById(R.id.grid);
			myGrid.setSelection(index_grid);

		}
	}

	private void checkButtonLayout() {

		// Let's measure how much space we need:
		int spec = View.MeasureSpec.UNSPECIFIED;
		mDirectoryButtons.measure(spec, spec);
		int count = mDirectoryButtons.getChildCount();

		int requiredwidth = mDirectoryButtons.getMeasuredWidth();
		int width = getWindowManager().getDefaultDisplay().getWidth();

		if (requiredwidth > width) {
			int WRAP_CONTENT = LinearLayout.LayoutParams.WRAP_CONTENT;

			// Create a new button that shows that there is more to the left:
			ImageButton ib = new ImageButton(this);
			ib.setImageResource(R.drawable.back_icon);
			ib.setBackgroundColor(getResources().getColor(R.color.transparent));

			ib.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT,
					WRAP_CONTENT));
			//
			ib.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					// Up one directory.
					UponeLevel();
				}
			});
			mDirectoryButtons.addView(ib, 0);

			// New button needs even more space
			ib.measure(spec, spec);
			requiredwidth += ib.getMeasuredWidth();

			// Need to take away some buttons
			// but leave at least "back" button and one directory button.
			while (requiredwidth > width
					&& mDirectoryButtons.getChildCount() > 2) {
				View view = mDirectoryButtons.getChildAt(1);
				requiredwidth -= view.getMeasuredWidth();

				mDirectoryButtons.removeViewAt(1);
			}
		}
	}

	public void setViewMode(int viewmode) {
		switch (viewmode) {
		case UltraBaseAdapter.VIEWMODE_LIST:
			madapter.setViewMode(UltraBaseAdapter.VIEWMODE_LIST);
			mylist.setAdapter(madapter);
			myGrid.setAdapter(null);
			madapter.notifyDataSetChanged();
			break;
		case UltraBaseAdapter.VIEWMODE_ICON:
			madapter.setViewMode(UltraBaseAdapter.VIEWMODE_ICON);
			mylist.setAdapter(null);
			myGrid.setAdapter(madapter);
			madapter.notifyDataSetChanged();
			break;
		default:
			break;
		}

	}

	public void swapViewMode() {
		switch (madapter.getViewMode()) {
		case UltraBaseAdapter.VIEWMODE_LIST:
			setViewMode(UltraBaseAdapter.VIEWMODE_ICON);
			break;
		case UltraBaseAdapter.VIEWMODE_ICON:
			setViewMode(UltraBaseAdapter.VIEWMODE_LIST);
			break;
		default:
			break;
		}

	}

	// To make listview for the list of file
	public void getFileList(String dirPath) {

		File file = new File(dirPath); // get the file

		StatFs stat;
		stat = new StatFs(root);
		if (Build.VERSION.SDK_INT >= 8) {
			sdcardBar.setMax((int) file.getTotalSpace()/100);

			sdcardBar.setProgress((int) (file.getTotalSpace()-file.getFreeSpace())/100);

			progress_free.setText(" "
					+ String.valueOf(getAsString(file.getFreeSpace()))
					+ " free ");
			progress_used.setText(" "
					+ String.valueOf(getAsString(file.getTotalSpace()-file.getFreeSpace()))
					+ " used");

		} else {

			sdcardBar.setMax(stat.getBlockCount() * stat.getBlockSize());

			sdcardBar.setProgress((stat.getBlockCount() - stat
					.getAvailableBlocks()) * stat.getBlockSize());

			progress_free.setText(" "
					+ FileUtils.formatSize(FilebrowserULTRAActivity.this,
							stat.getAvailableBlocks() * stat.getBlockSize())
					+ " free ");
			progress_used.setText(" "
					+ FileUtils.formatSize(FilebrowserULTRAActivity.this,
							(stat.getBlockCount() - stat.getAvailableBlocks())
									* stat.getBlockSize()) + " used");

		}

		/*
		 * files = new ArrayList<File>(); files =
		 * FileUtil.getFileList(currentdir,true); madapter = new
		 * UltraBaseAdapter(this, files);
		 * setViewMode(UltraBaseAdapter.VIEWMODE_LIST);
		 */

		// item = new ArrayList<String>(); //Declare as Array list
		path = new ArrayList<String>();

		hidden = PreferenceActivity
				.getDisplayHiddenFiles(getApplicationContext());

		currentdir = dirPath; // get the current directory we are in
		parentfile = file.getParent();
		if (file.canRead()) {
			File[] files = file.listFiles();// get the list array of file
			for (int i = 0; i < files.length; i++) {

				File fileItem = files[i];

				if (fileItem.getName().toString().startsWith(".")) {
					if (hidden) {
						// item.add(fileItem.getName()); // input name directory
						// to array list
						path.add(fileItem.getPath());
					}

				}

				else {
					// item.add(fileItem.getName()); // input name file or
					// directory to array list
					path.add(fileItem.getPath());
				}
			}
			// fileInfo.setText("Info: "+dirPath+" [ " + files.length
			// +" item ]");
			// items = new String[item.size()]; //declare array with specific
			// number off item
			// item.toArray(items); //send data arraylist(item) to array(items)
			paths = new String[path.size()];
			path.toArray(paths);
			Arrays.sort(paths, getSortMode(sort_mode));
			items = new String[paths.length];
			for (int i = 0; i < paths.length; i++) {
				items[i] = new File(paths[i]).getName();
			}

		} else {

			BufferedReader reader = null; // errReader = null;
			try {

				reader = LinuxShell
						.execute("IFS='\n';CURDIR='"
								+ LinuxShell.getCmdPath(dirPath)
								+ "';for i in `ls $CURDIR`; do if [ -d $CURDIR/$i ]; then echo \"d $CURDIR/$i\";else echo \"f $CURDIR/$i\"; fi; done");

				File f;
				String line;
				while ((line = reader.readLine()) != null) {
					f = new File(line.substring(2));
					if (line.startsWith("d")) {
						path.add(f.getAbsolutePath());
					} else {
						path.add(f.getAbsolutePath());
					}
				}
			} catch (Exception e) {
				String title = "No Root";
				String msg = "The phone is not rooted !";
				AlertDialog alertDialog = new AlertDialog.Builder(this)
						.create();

				Log.e("Ultra File Browser", title);

				alertDialog.setTitle(title);
				alertDialog.setIcon(R.drawable.error);
				alertDialog.setMessage(msg);
				alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								getFileList(parentfile);
							}
						});
				alertDialog
						.setOnCancelListener(new DialogInterface.OnCancelListener() {
							public void onCancel(DialogInterface dialog) {
								getFileList(parentfile);
							}
						});
				alertDialog.show();
			}
			paths = new String[path.size()];
			path.toArray(paths);
			Arrays.sort(paths, getSortMode(sort_mode));
			items = new String[paths.length];
			for (int i = 0; i < paths.length; i++) {
				items[i] = new File(paths[i]).getName();

			}

		}

		myGrid.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> av, View v,
					int position, long id) {
				// TODO Auto-generated method stub
				if (searchflag == true && searchlength > 0) {

					String filepath = founditems[position];

					getOperations(filepath);

				}

				else {
					String filepath = paths[position];
					if (multiselectflag == true && multiSelectData.size() > 0
							&& multiSelectData != null) {
						getmultiOperations(filepath);
					} else {
						getOperations(filepath);
					}
				}

				return false;
			}

		});

		// mylist = getListView();
		mylist.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> av, View v,
					final int position, long id) {
				// TODO Auto-generated method stub
				if (searchflag == true && searchlength > 0) {

					String filepath = founditems[position];

					getOperations(filepath);

				}

				else {
					String filepath = paths[position];
					if (multiselectflag == true && multiSelectData.size() > 0
							&& multiSelectData != null) {
						getmultiOperations(filepath);
					} else {
						getOperations(filepath);
					}
				}

				return false;
			}
		});

		listener = new OnItemClickListener() {

			public void onItemClick(AdapterView<?> av, View v, int position,
					long id) {
				// TODO Auto-generated method stub
				// recent_pos=position;
				
				if(currentdir.equals(root)){
				index_back = getListView().getFirstVisiblePosition(); // set
																		// last_visited_pos
																		// in
																		// list
																		// view
				index_grid = position; // set last_visited_pos in grid view
				}
				if (searchflag == true && searchlength > 0) {
					String filepath = founditems[position];
					File file = new File(filepath);
					if (file.isDirectory()) {
						OnclickOperation(filepath);
					}

					else {
						OnclickOperation(filepath);
					}
				}

				else {

					if (multiselectflag) {
						multiSelectData.clear();
						int last_pos = position;
						myGrid.setAdapter(new IconicGrid());
						myGrid.setSelection(last_pos);

						for (int i = 0; i < multiItems; i++) {
							if (mStarStates[i]) {
								multiSelectData.add(paths[i]);

							} else if (!mStarStates[i]) {
								multiSelectData.remove(paths[i]);
							}
						}

						mStarStates[position] = !mStarStates[position]; // invert
																		// selection

						if (mStarStates[position]) {
							multiSelectData.add(paths[position]); // add to
																	// multiselectdata
																	// if item
																	// is
																	// selected
						} else if (!mStarStates[position]) {
							multiSelectData.remove(paths[position]);
						}
						// fileInfo.setText(multiSelectData.size() +
						// " items selected");
					}

					else {
						String filepath = paths[position];
						OnclickOperation(filepath);

					}

				}

			}
		};

		myGrid.setOnItemClickListener(listener); // set both grid and list
													// layout with
													// onclicklistener
		mylist.setOnItemClickListener(listener);

		mylist.setAdapter(new IconicList());// set the list with icon
		myGrid.setAdapter(new IconicGrid());// set the Grid with icon
		AnimationSet set = new AnimationSet(true);

		// get the parent of the directory we are in
		multiItems = items.length; // get the no of items in the directory we
									// are in

		final EditText quicksearch = (EditText) findViewById(R.id.search_box);
		quicksearch.setHint("Search " + new File(currentdir).getName());
		quicksearch.addTextChangedListener(new TextWatcher() {

			public void beforeTextChanged(CharSequence s, int start, int end,
					int count) {
				// TODO Auto-generated method stub

			}

			public void afterTextChanged(final Editable text) {
				// TODO Auto-generated method stub
				searchlength = text.toString().length();
				final String filesearch = text.toString();
				SearchString = text.toString();

				if (searchflag == true) {
					if (searchlength > 0) {
						new AsyncTask<String[], Long, Long>() {

							@Override
							protected Long doInBackground(String[]... params) {

								ArrayList<String> found = filterSearch(text
										.toString());
								int len = found != null ? found.size() : 0;
								founditems = new String[len];
								found.toArray(founditems);
								Arrays.sort(founditems, type);
								found.clear();

								return null;
							}

							protected void onPreExecute() {
								actionBar
										.setProgressBarVisibility(View.VISIBLE);
								mylist.setAdapter(null);
								myGrid.setAdapter(null);
							}

							@Override
							protected void onPostExecute(Long result) {
								if (filesearch.equals(text.toString())) {
									searchflag = true;
									mylist.setAdapter(new SearchList());
									myGrid.setAdapter(new SearchGrid());
									actionBar.setProgressBarVisibility(View.GONE);

								} else if (text.toString().length() == 0) {
									myGrid.setAdapter(new IconicGrid());
									mylist.setAdapter(new IconicList());
									actionBar.setProgressBarVisibility(View.GONE);

								} else {
									mylist.setAdapter(null);
									myGrid.setAdapter(null);
									actionBar.setProgressBarVisibility(View.GONE);

								}

							}

						}.execute();
					} else {
						myGrid.setAdapter(new IconicGrid());
						mylist.setAdapter(new IconicList());
					}

				}

			}

			public void onTextChanged(CharSequence s, int start, int end,
					int count) {
				// TODO Auto-generated method stub

			}
		});

		manageUi();
		setDirectoryButtons();

	}

	private void compressMultiFile(String out) {
		List<File> files = new ArrayList<File>();
		for (String s : multiSelectData) {

			files.add(new File(s));

		}
		new CompressManager(FilebrowserULTRAActivity.this).compress(files, out
				+ ".zip");
	}

	public void manageUi() {
		Button superbutton = (Button) findViewById(R.id.superButton);
		HorizontalScrollView scrolltext = (HorizontalScrollView) findViewById(R.id.scroll_text);
		EditText quicksearch = (EditText) findViewById(R.id.search_box);
		ViewFlipper topflipper = (ViewFlipper) findViewById(R.id.flipper_top);
		findViewById(R.id.bottomFlipper);
		// HorizontalScrollView scrollButtons =
		// (HorizontalScrollView)findViewById(R.id.scroll_buttons);
		HorizontalScrollView multiscrollButtons = (HorizontalScrollView) findViewById(R.id.multi_scroll_buttons);

		if (searchflag == true) {
			multiselectflag = false;
			superbutton.setText("Back");
			quicksearch.requestFocus();
			topflipper.setAnimation(launchAnimation(R.anim.fade));
			topflipper.setDisplayedChild(topflipper.indexOfChild(quicksearch));

		} else if (searchflag == false) {
			quicksearch.setText("");
			superbutton.setText("Search");
			topflipper.setAnimation(launchAnimation(R.anim.appear));
			topflipper.setDisplayedChild(topflipper.indexOfChild(scrolltext));

			if (multiselectflag == true) {
				// scrollButtons.setVisibility(View.GONE);
				multiscrollButtons.setVisibility(View.VISIBLE);
				// bottomflipper.setAnimation(launchAnimation(R.anim.appear));
				// bottomflipper.setDisplayedChild(bottomflipper.indexOfChild(multiscrollButtons));

				if (unselectflag == true) {

					superbutton.setText("Unselect");
				} else {
					superbutton.setText("Select all");

				}
			} else if (multiselectflag == false) {

				// bottomflipper.setAnimation(launchAnimation(R.anim.disappear));
				// bottomflipper.setDisplayedChild(bottomflipper.indexOfChild(scrollButtons));
				// scrollButtons.setVisibility(View.VISIBLE);
				multiscrollButtons.setVisibility(View.GONE);

			}

		}

	}

	@SuppressWarnings("deprecation")
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.new_file:

			ActionItem newfileItem = new ActionItem(ID_NEW_FILE, "New File",
					getResources().getDrawable(R.drawable.text));
			ActionItem newfolderItem = new ActionItem(ID_NEW_FOLDER,
					"New Folder", getResources().getDrawable(
							R.drawable.new_folder2));
			final QuickAction quickAction = new QuickAction(this,
					QuickAction.VERTICAL);
			quickAction.addActionItem(newfolderItem);
			quickAction.addActionItem(newfileItem);

			quickAction
					.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {

						public void onItemClick(QuickAction source, int pos,
								int actionId) {
							// here we can filter which action item was clicked
							// with pos or actionId parameter

							switch (actionId) {
							case ID_NEW_FILE:
								LayoutInflater factory = LayoutInflater
										.from(FilebrowserULTRAActivity.this);
								final View OpenTxtView = factory.inflate(
										R.layout.input, null);
								final AlertDialog alert1 = new AlertDialog.Builder(
										FilebrowserULTRAActivity.this).create();
								alert1.setTitle("New file");
								alert1.setView(OpenTxtView);
								alert1.setIcon(R.drawable.textpng);

								alert1.setButton("Okay",
										new DialogInterface.OnClickListener() {

											public void onClick(
													DialogInterface arg0,
													int whoch) {
												// TODO Auto-generated method
												// stub
												EditText savefile = (EditText) alert1
														.findViewById(R.id.savetext);
												final String filename = savefile
														.getText().toString();
												if (filename.length() > 0) {
													FileUtils.createFile(
															currentdir,
															filename + ".txt");
													Toast.makeText(
															FilebrowserULTRAActivity.this,
															filename
																	+ ".txt"
																	+ " was created !",
															Toast.LENGTH_SHORT)
															.show();
													refreshList();

												} else {
													Toast.makeText(
															FilebrowserULTRAActivity.this,
															" file not created !",
															Toast.LENGTH_SHORT)
															.show();
												}
											}
										});
								alert1.setButton2("Cancel",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {

												alert1.dismiss();

											}
										});

								alert1.show();

								break;
							case ID_NEW_FOLDER:

								LayoutInflater factory1 = LayoutInflater
										.from(FilebrowserULTRAActivity.this);
								final View OpenTxtView1 = factory1.inflate(
										R.layout.input, null);
								final AlertDialog alert2 = new AlertDialog.Builder(
										FilebrowserULTRAActivity.this).create();
								alert2.setTitle("New folder");
								alert2.setView(OpenTxtView1);
								alert2.setIcon(R.drawable.myfolder72);

								alert2.setButton("Okay",
										new DialogInterface.OnClickListener() {

											public void onClick(
													DialogInterface arg0,
													int whoch) {
												// TODO Auto-generated method
												// stub
												EditText savefile = (EditText) alert2
														.findViewById(R.id.savetext);
												final String filename = savefile
														.getText().toString();
												if (filename.length() > 0) {
													FileUtils.createDir(
															currentdir,
															filename);
													Toast.makeText(
															FilebrowserULTRAActivity.this,
															filename
																	+ " folder was created !",
															Toast.LENGTH_SHORT)
															.show();
													refreshList();
												} else {
													Toast.makeText(
															FilebrowserULTRAActivity.this,
															" folder not created !",
															Toast.LENGTH_SHORT)
															.show();

												}

											}
										});
								alert2.setButton2("Cancel",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {

												alert2.dismiss();

											}
										});

								alert2.show();

								break;
							}

						}
					});

			quickAction
					.setOnDismissListener(new QuickAction.OnDismissListener() {

						public void onDismiss() {

						}
					});
			quickAction.setAnimStyle(QuickAction.ANIM_REFLECT);

			quickAction.show(view);

			break;

		case R.id.navigate:
			ActionItem sdcardItem = new ActionItem(ID_SD_CARD, "SD-Card",
					getResources().getDrawable(R.drawable.hd));
			ActionItem backupItem = new ActionItem(ID_BACKUP, "App Manager",
					getResources().getDrawable(R.drawable.android_mdpi));
			ActionItem processItem = new ActionItem(ID_PROCESS,
					"Process Manager", getResources().getDrawable(
							R.drawable.task_manager_mdpi));
			ActionItem backupfolderItem = new ActionItem(ID_BACKUP_FOLDER,
					"Backup Folder", getResources().getDrawable(
							R.drawable.backup_mdpi));
			ActionItem cameraItem = new ActionItem(ID_CAMERA, "Camera Folder",
					getResources().getDrawable(R.drawable.gallery_mdpi));
			ActionItem bluetoothItem = new ActionItem(ID_BlUETOOTH,
					"Bluetooth Folder", getResources().getDrawable(
							R.drawable.bluetooth_mdpi));
			ActionItem downloadItem = new ActionItem(ID_DOWNLOAD,
					"Download Folder", getResources().getDrawable(
							R.drawable.browser_mdpi));
			ActionItem musicItem = new ActionItem(ID_MUSIC, "Music Folder",
					getResources().getDrawable(R.drawable.music_mdpi));

			final QuickAction quickAction1 = new QuickAction(this,
					QuickAction.VERTICAL);
			quickAction1.addActionItem(sdcardItem);
			// quickAction1.addActionItem(backupItem);
			// quickAction1.addActionItem(processItem);

			if (new File(root + "/Music").exists()) {
				quickAction1.addActionItem(musicItem);
			}
			if (new File(root + "/Download").exists()) {
				quickAction1.addActionItem(downloadItem);
			}

			if (new File(root + "/Backup").exists()) {
				quickAction1.addActionItem(backupfolderItem);
			}
			if (new File(root + "/DCIM").exists()) {
				quickAction1.addActionItem(cameraItem);
			}
			if (new File(root + "/Bluetooth").exists()) {
				quickAction1.addActionItem(bluetoothItem);
			}

			quickAction1
					.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {

						public void onItemClick(QuickAction source, int pos,
								int actionId) {
							// here we can filter which action item was clicked
							// with pos or actionId parameter

							switch (actionId) {
							case ID_SD_CARD:
								getFileList(root);
								break;
							case ID_BACKUP:
								final Intent intent = new Intent(
										FilebrowserULTRAActivity.this,
										BackupManager.class);
								startActivity(intent);

								break;
							case ID_PROCESS:
								final Intent intent1 = new Intent(
										FilebrowserULTRAActivity.this,
										ProcessManager.class);
								startActivity(intent1);
								break;
							case ID_BACKUP_FOLDER:

								getFileList(root + "/Backup");
								break;
							case ID_CAMERA:
								getFileList(root + "/DCIM");
								break;
							case ID_BlUETOOTH:
								getFileList(root + "/Bluetooth");
								break;
							case ID_MUSIC:
								getFileList(root + "/Music");
								break;
							case ID_DOWNLOAD:
								getFileList(root + "/Download");
								break;
							}
						}
					});
			quickAction1
					.setOnDismissListener(new QuickAction.OnDismissListener() {

						public void onDismiss() {

						}
					});

			quickAction1.setAnimStyle(QuickAction.ANIM_REFLECT);

			quickAction1.show(view);

			break;
		case R.id.previous:
			searchflag = false;
			if (currentdir.equals(root)) {

				Toast.makeText(FilebrowserULTRAActivity.this,
						"press back to exit", Toast.LENGTH_SHORT).show();

			} else {
				getFileList(parentfile);
				getListView().setSelectionFromTop(index_back, 0);
				myGrid = (GridView) findViewById(R.id.grid);
				myGrid.setSelection(index_grid);

			}

			break;

		case R.id.bookmarks_list:

			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			final Cursor bookmarksCursor = getBookmarks();

			builder.setTitle("Bookmarks");

			builder.setCursor(bookmarksCursor,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							if (bookmarksCursor.moveToPosition(item)) {
								String path = bookmarksCursor.getString(bookmarksCursor
										.getColumnIndex(BookmarksProvider.PATH));
								File file = new File(path);
								if (file != null) {
									if (file.isDirectory()) {
										getFileList(path);
									} else {
										getOperations(path);
									}

								}
							} else {
								Toast.makeText(FilebrowserULTRAActivity.this,
										"No Bookmarks", Toast.LENGTH_SHORT)
										.show();
							}
						}
					}, BookmarksProvider.NAME);
			builder.create();
			builder.show();

			break;
		case R.id.exit:

			finish();

			break;

		case R.id.refresh:
			searchflag = false;
			refreshList();
			Toast.makeText(FilebrowserULTRAActivity.this, "refreshed..",
					Toast.LENGTH_SHORT).show();
			int len1 = items.length;
			for (int i = 0; i < len1; i++) {

				mStarStates[i] = false;
			}
			break;

		case R.id.multi_select:

			searchflag = false;
			unselectflag = false;
			refreshList();
			multiselectflag = !multiselectflag;

			manageUi();
			int len = items.length;
			for (int i = 0; i < len; i++) {
				mStarStates[i] = false;
			}
			multiSelectData.clear();

			/*
			 * int len = items.length; String check=""; for(int i=0;i<len;i++){
			 * if(mStarStates[i]==true) check = check +items[i] +" , " ; }
			 * 
			 * showMessage(check);
			 */
			break;

		case R.id.search:
			// searchflag=false;

			// getSearchDialog();
			Intent intent = new Intent(FilebrowserULTRAActivity.this,
					SearchFilesDialog.class);
			startActivityForResult(intent, SEARCH_RESULT);

			break;

		case R.id.findDuplicates:

			String currentDir = currentdir;
			final Intent duplicate_intent = new Intent(
					FilebrowserULTRAActivity.this, DuplicatesManager.class);
			duplicate_intent.putExtra("dir", currentDir);
			startActivity(duplicate_intent);

			break;

		case R.id.superButton:
			Button superButton = (Button) findViewById(R.id.superButton);
			String supertext = superButton.getText().toString();

			if (supertext.equals("Search")) {
				searchflag = !searchflag;
				manageUi();

			} else if (supertext.equals("Back")) {
				searchflag = !searchflag;
				refreshList();
				manageUi();
			} else if (supertext.equals("Select all")) {
				unselectflag = true;
				for (int i = 0; i < multiItems; i++) {
					mStarStates[i] = true;
					multiSelectData.add(paths[i]);
				}

				refreshList();

			} else if (supertext.equals("Unselect")) {
				unselectflag = false;
				for (int i = 0; i < multiItems; i++) {
					mStarStates[i] = false;
					multiSelectData.remove(paths[i]);

				}
				refreshList();

			}

			break;

		case R.id.info_button:
			/*
			 * int len2 = items.length; String check=""; for(int
			 * i=0;i<len2;i++){ if(mStarStates[i]==true) check = check +items[i]
			 * +"\n" ; }
			 * 
			 * showMessage(check); String multiData ="\n"; for(int
			 * i=0;i<multiSelectData.size();i++){ multiData = multiData +
			 * multiSelectData.get(i) +"\n";
			 * 
			 * }
			 * 
			 * showMessage("Size : " + multiSelectData.size() + multiData );
			 */
			new BackgroundWork(INFO_TYPE).execute(currentdir);
			// showMessage("multi : " + new Boolean(multiselectflag).toString()
			// + "\nSearch: " + new Boolean(searchflag).toString() +"\nSelect :"
			// + new Boolean(selectallflag).toString() + "\nUnselect :" +new
			// Boolean(unselectflag).toString());

			break;
		case R.id.gesture_button:
			Intent gesture = new Intent(FilebrowserULTRAActivity.this,
					GestureMonitorActivity.class);
			startActivityForResult(gesture, GESTURE_RESULT);
			break;

		case R.id.home_grid:
			searchflag = false;
			refreshList();
			viewflipper.showNext();

			break;
		case R.id.paste:

			if (multiSelectData != null && multiSelectData.size() > 0) {
				View copybutton1 = (ImageButton) findViewById(R.id.paste);

				String[] data1;
				int index = 1;
				data1 = new String[multiSelectData.size() + 1];
				data1[0] = currentdir;

				for (String s : multiSelectData)
					data1[index++] = s;

				new BackgroundWork(COPY_TYPE).execute(data1);

				copybutton1.setVisibility(View.GONE);
				copybutton1.setClickable(false);
			}

			else {
				View copybutton1 = (ImageButton) findViewById(R.id.paste);
				String[] data = { currentfile, currentdir };
				new BackgroundWork(COPY_TYPE).execute(data);
				copybutton1.setVisibility(View.GONE);
				copybutton1.setClickable(false);
			}

			break;
		case R.id.cancelButton:
			multiSelectData.clear();
			multiselectflag = false;
			refreshList();
			break;
		case R.id.copyButton:

			if (multiSelectData.size() > 0) {
				delete_after_copy = false;
				View copyButton = (ImageButton) findViewById(R.id.paste);

				copyButton.setVisibility(View.VISIBLE);
				copyButton.startAnimation(launchAnimation(R.anim.shake));
				copyButton.setClickable(true);

				multiselectflag = false;
				refreshList();
				showMessage(multiSelectData.size()
						+ " files copied to clipboard");
			}
			break;

		case R.id.moveButton:

			if (multiSelectData.size() > 0) {
				delete_after_copy = true;
				View copyButton = (ImageButton) findViewById(R.id.paste);

				copyButton.setVisibility(View.VISIBLE);
				copyButton.startAnimation(launchAnimation(R.anim.shake));

				copyButton.setClickable(true);

				multiselectflag = false;
				refreshList();
				showMessage(multiSelectData.size()
						+ " files copied to clipboard");
			}

			break;
		case R.id.zipButton:

			if (multiSelectData.size() > 0) {
				LayoutInflater factory = LayoutInflater
						.from(FilebrowserULTRAActivity.this);
				final View OpenTxtView = factory.inflate(R.layout.input, null);
				final AlertDialog alert1 = new AlertDialog.Builder(
						FilebrowserULTRAActivity.this).create();
				alert1.setTitle("Zip files");
				alert1.setView(OpenTxtView);
				alert1.setIcon(R.drawable.myzip);

				alert1.setButton("Okay", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int which) {
						// TODO Auto-generated method stub
						EditText savefile = (EditText) alert1
								.findViewById(R.id.savetext);
						final String filename = savefile.getText().toString();

						compressMultiFile(filename);
						multiselectflag = false;
						multiSelectData.clear();
						refreshList();
						InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						inputMethodManager.hideSoftInputFromWindow(
								savefile.getWindowToken(), 0);

					}
				});
				alert1.setButton2("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								alert1.dismiss();

							}
						});

				alert1.show();

			}

			break;

		case R.id.deleteButton:

			if (multiSelectData.size() > 0) {
				final AlertDialog alertDialog = new AlertDialog.Builder(
						FilebrowserULTRAActivity.this).create();
				alertDialog.setTitle("Delete Files");

				final String[] data1 = new String[multiSelectData.size()];
				int a = 0;
				for (String string : multiSelectData)
					data1[a++] = string;
				alertDialog.setIcon(R.drawable.warning_icon2);
				alertDialog
						.setMessage("Are you sure you want to delete selected files");
				alertDialog.setButton("Yes",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {

								new BackgroundWork(DELETE_TYPE).execute(data1);

							}
						});
				alertDialog.setButton2("No",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {

								alertDialog.dismiss();

							}
						});
				alertDialog.show();
				multiselectflag = false;
				manageUi();
			}
			break;

		case R.id.sendButton:

			if (multiSelectData.size() > 0) {
				ArrayList<Uri> uris = new ArrayList<Uri>();
				int length = multiSelectData.size();
				Intent send_intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
				send_intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

				send_intent.setType("image/jpeg");
				for (int i = 0; i < length; i++) {
					File file = new File(multiSelectData.get(i))
							.getAbsoluteFile();
					uris.add(Uri.fromFile(file));
				}
				send_intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
						uris);
				startActivity(Intent.createChooser(send_intent, "Send via.."));
				multiselectflag = false;
				refreshList();

			}

			break;
		case R.id.emailButton:

			if (multiSelectData.size() > 0) {
				ArrayList<Uri> uris = new ArrayList<Uri>();
				int length = multiSelectData.size();
				Intent mail_intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
				mail_intent.setType("message/rfc822");
				mail_intent.putExtra(Intent.EXTRA_BCC, "");
				mail_intent.putExtra(Intent.EXTRA_SUBJECT, " ");

				for (int i = 0; i < length; i++) {
					File file = new File(multiSelectData.get(i))
							.getAbsoluteFile();
					uris.add(Uri.fromFile(file));
				}

				mail_intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
						uris);
				startActivity(Intent
						.createChooser(mail_intent, "Email using.."));

				multiselectflag = false;
				refreshList();

			}

			break;

		}

	}

	@SuppressWarnings("deprecation")
	public void getSearchDialog() {

		LayoutInflater factory = LayoutInflater
				.from(FilebrowserULTRAActivity.this);
		final View OpenTxtView = factory.inflate(R.layout.input, null);
		final AlertDialog alert1 = new AlertDialog.Builder(
				FilebrowserULTRAActivity.this).create();
		alert1.setTitle("Search");
		alert1.setView(OpenTxtView);
		alert1.setIcon(R.drawable.search_icon);

		alert1.setButton("Search", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface arg0, int which) {
				// TODO Auto-generated method stub
				EditText savefile = (EditText) alert1
						.findViewById(R.id.savetext);
				final String filename = savefile.getText().toString();

				new BackgroundWork(SEARCH_TYPE).execute(filename);
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(
						savefile.getWindowToken(), 0);

			}
		});
		alert1.setButton2("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				alert1.dismiss();

			}
		});

		alert1.show();

	}

	/*
	 * 
	 * public void saveSelectedStates(){ File myfile = new File(currentdir);
	 * File[] files = myfile.listFiles(); int len = files.length;
	 * 
	 * mStarStates = new boolean[len];
	 * 
	 * mSelected = new int[multiItems]; for(int i = 0 ;i <multiItems ; i++){
	 * if(mStarStates[i]==true){ mSelected[i]=1; } else { mSelected[i]=0; } }
	 * 
	 * }
	 * 
	 * public void loadSelectedStates(){ File myfile = new File(currentdir);
	 * File[] files = myfile.listFiles(); int len = files.length;
	 * 
	 * mStarStates = new boolean[len];
	 * 
	 * 
	 * mSelected = new int[multiItems];
	 * 
	 * for(int i = 0 ;i <multiItems ; i++){ if(mSelected[i]==1){
	 * mStarStates[i]=true; } else { mStarStates[i]=false;
	 * 
	 * c }
	 * 
	 * 
	 * }
	 */

	public ArrayList<String> filterSearch(String s) {
		ArrayList<String> result;
		result = new ArrayList<String>();
		for (int i = 0; i < items.length; i++) {
			if (items[i].toLowerCase().contains(s.toLowerCase()))
				result.add(paths[i]);
		}
		return result;
	}

	public void getSearchResults(final ArrayList<String> file, String file_name) {
		final String[] names;
		final ItemDrawable[] items;
		int len = file != null ? file.size() : 0;
		if (len == 0) {
			Toast.makeText(FilebrowserULTRAActivity.this,
					"Couldn't find " + file_name, Toast.LENGTH_SHORT).show();

		} else {
			names = new String[len];
			items = new ItemDrawable[len];

			for (int i = 0; i < len; i++) {
				String entry = file.get(i);
				names[i] = entry.substring(entry.lastIndexOf("/") + 1,
						entry.length());

				items[i] = new ItemDrawable(entry.substring(
						entry.lastIndexOf("/") + 1, entry.length()),
						getIcon(entry));

			}

			AlertDialog.Builder builder = new AlertDialog.Builder(
					FilebrowserULTRAActivity.this);
			builder.setTitle("Found " + len + " file(s)");

			ListAdapter adapter = new ArrayAdapter<ItemDrawable>(this,
					android.R.layout.select_dialog_item, android.R.id.text1,
					items) {
				public View getView(int position, View convertView,
						ViewGroup parent) {
					// User super class to create the View
					View v = super.getView(position, convertView, parent);
					TextView tv = (TextView) v.findViewById(android.R.id.text1);

					// Put the image on the TextView

					tv.setCompoundDrawablesWithIntrinsicBounds(
							items[position].icon, null, null, null);

					// Add margin between image and text (support various screen
					// densities)
					int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
					tv.setCompoundDrawablePadding(dp5);

					return v;
				}
			};

			builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int position) {
					final String entry = file.get(position);
					final File file = new File(entry);
					if (file.isFile()) {
						final CharSequence[] items = { "Open file",
								"Open Containing folder" };
						AlertDialog.Builder builder = new AlertDialog.Builder(
								FilebrowserULTRAActivity.this);
						builder.setTitle("Options");
						builder.setItems(items,
								new DialogInterface.OnClickListener() {

									public void onClick(
											DialogInterface dInterface, int item) {
										switch (item) {
										case 0:

											OnclickOperation(entry);
											break;
										case 1:

											getFileList(file.getParent());
											break;
										}
									}
								});
						AlertDialog alert = builder.create();
						alert.show();

					} else {
						OnclickOperation(entry);
					}

				}

			});

			final AlertDialog dialog = builder.create();
			dialog.setButton("cancel", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub

					dialog.dismiss();
				}
			});
			dialog.show();
		}

	}

	private void showMessage(String message) {
		Toast.makeText(FilebrowserULTRAActivity.this, message,
				Toast.LENGTH_SHORT).show();
	}

	private Animation launchAnimation(int id) {

		return AnimationUtils.loadAnimation(FilebrowserULTRAActivity.this, id);
	}

	public void getmultiOperations(final String filepath) {

		final CharSequence[] multioperations1 = { "Delete", "Send", "Email",
				"Copy", "Move" };

		AlertDialog.Builder builder = new AlertDialog.Builder(
				FilebrowserULTRAActivity.this);

		builder.setTitle("Selected " + multiSelectData.size() + " items");
		builder.setItems(multioperations1,
				new DialogInterface.OnClickListener() {
					@SuppressWarnings("deprecation")
					public void onClick(DialogInterface dInterface, int item) {

						switch (item) {
						case 0:
							if (multiSelectData.size() > 0) {
								final AlertDialog alertDialog = new AlertDialog.Builder(
										FilebrowserULTRAActivity.this).create();
								alertDialog.setTitle("Delete Files");

								final String[] data = new String[multiSelectData
										.size()];
								int a = 0;
								for (String string : multiSelectData)
									data[a++] = string;
								alertDialog.setIcon(R.drawable.warning_icon2);
								alertDialog
										.setMessage("Are you sure you want to delete selected files");
								alertDialog.setButton("Yes",
										new DialogInterface.OnClickListener() {

											public void onClick(
													DialogInterface dialog,
													int which) {

												new BackgroundWork(DELETE_TYPE)
														.execute(data);

											}
										});
								alertDialog.setButton2("No",
										new DialogInterface.OnClickListener() {

											public void onClick(
													DialogInterface dialog,
													int which) {

												alertDialog.dismiss();

											}
										});
								alertDialog.show();
								multiselectflag = false;

								manageUi();
							}
							break;
						case 1:
							if (multiSelectData.size() > 0) {
								ArrayList<Uri> uris = new ArrayList<Uri>();
								int length = multiSelectData.size();
								Intent send_intent = new Intent(
										Intent.ACTION_SEND_MULTIPLE);
								send_intent
										.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

								send_intent.setType("image/jpeg");
								for (int i = 0; i < length; i++) {
									File file = new File(multiSelectData.get(i))
											.getAbsoluteFile();
									uris.add(Uri.fromFile(file));
								}
								send_intent.putParcelableArrayListExtra(
										Intent.EXTRA_STREAM, uris);
								startActivity(Intent.createChooser(send_intent,
										"Send via.."));
								multiselectflag = false;
								refreshList();

							}
							break;
						case 2:
							if (multiSelectData.size() > 0) {
								ArrayList<Uri> uris = new ArrayList<Uri>();
								int length = multiSelectData.size();
								Intent mail_intent = new Intent(
										Intent.ACTION_SEND_MULTIPLE);
								mail_intent.setType("message/rfc822");
								mail_intent.putExtra(Intent.EXTRA_BCC, "");
								mail_intent.putExtra(Intent.EXTRA_SUBJECT, " ");

								for (int i = 0; i < length; i++) {
									File file = new File(multiSelectData.get(i))
											.getAbsoluteFile();
									uris.add(Uri.fromFile(file));
								}

								mail_intent.putParcelableArrayListExtra(
										Intent.EXTRA_STREAM, uris);
								startActivity(Intent.createChooser(mail_intent,
										"Email using.."));

								multiselectflag = false;
								refreshList();

							}
							break;

						case 3:
							if (multiSelectData.size() > 0) {
								delete_after_copy = false;
								View copyButton = (ImageButton) findViewById(R.id.paste);

								copyButton.setVisibility(View.VISIBLE);
								copyButton
										.startAnimation(launchAnimation(R.anim.shake));
								copyButton.setClickable(true);

								multiselectflag = false;
								refreshList();
								showMessage(multiSelectData.size()
										+ " files copied to clipboard");
							}
							break;
						case 4:
							if (multiSelectData.size() > 0) {
								View copyButton1 = (ImageButton) findViewById(R.id.paste);
								delete_after_copy = true;

								copyButton1.setVisibility(View.VISIBLE);
								copyButton1.setVisibility(View.VISIBLE);
								copyButton1
										.startAnimation(launchAnimation(R.anim.shake));
								copyButton1.setClickable(true);
								multiselectflag = false;
								refreshList();
								showMessage(multiSelectData.size()
										+ " files copied to clipboard");
							}
							break;

						}
					}
				});

		AlertDialog alert = builder.create();

		alert.show();
	}

	public Drawable getScaledIcon(String filepath) {
		Drawable icon = getIcon(filepath);
		Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
		int dp5 = (int) (getResources().getDisplayMetrics().densityDpi / 120);
		icon = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(
				bitmap, 50 * dp5, 50 * dp5, true));
		return icon;

	}

	public void getOperations(final String filepath) {
		/*
		 * if(searchflag==true){ searchflag=false; //was implemented manageUi();
		 * }
		 */

		final Item[] items = {
				new Item("Open As", R.drawable.myfolder48),
				new Item("Rename", R.drawable.rename48),
				new Item("Copy", R.drawable.copy48),
				new Item("Cut", R.drawable.cut_icon),
				new Item("Delete", R.drawable.delete48),
				new Item("Send", R.drawable.send48),
				new Item("Share", R.drawable.share48),
				new Item("Add Bookmark", R.drawable.favorites),
				new Item("Email", R.drawable.email_send_48),
				new Item("Zip it", R.drawable.myzip48),
				// new Item("Encrypt", R.drawable.locked48),
				// new Item("Decrypt", R.drawable.unlocked48),
				new Item("Properties", R.drawable.settings_mdpi),
				new Item("Add Gesture", R.drawable.hand_icon),

		};

		ListAdapter adapter = new ArrayAdapter<Item>(this,
				android.R.layout.select_dialog_item, android.R.id.text1, items) {
			public View getView(int position, View convertView, ViewGroup parent) {
				// User super class to create the View
				View v = super.getView(position, convertView, parent);
				TextView tv = (TextView) v.findViewById(android.R.id.text1);

				// Put the image on the TextView
				tv.setCompoundDrawablesWithIntrinsicBounds(
						items[position].icon, 0, 0, 0);

				// Add margin between image and text (support various screen
				// densities)
				int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
				tv.setCompoundDrawablePadding(dp5);

				return v;
			}
		};

		final File file = new File(filepath);
		AlertDialog.Builder builder = new AlertDialog.Builder(
				FilebrowserULTRAActivity.this);
		builder.setTitle(file.getName().toString());

		builder.setIcon(getScaledIcon(filepath));

		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			@SuppressWarnings("deprecation")
			public void onClick(DialogInterface dInterface, int item) {
				final View copybutton = (ImageButton) findViewById(R.id.paste);

				switch (item) {
				case 0:
					final CharSequence[] openAsOperations = { "Text", "Image",
							"Video", "Music", "File" };

					AlertDialog.Builder builder = new AlertDialog.Builder(
							FilebrowserULTRAActivity.this);
					builder.setItems(openAsOperations,
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dInterface,
										int item) {

									switch (item) {
									case 0:
										Intent txtIntent = new Intent();
										txtIntent
												.setAction(android.content.Intent.ACTION_VIEW);
										txtIntent.setDataAndType(
												Uri.fromFile(file),
												"text/plain");
										startActivity(txtIntent);

										break;
									case 1:
										Intent imageIntent = new Intent();
										imageIntent
												.setAction(android.content.Intent.ACTION_VIEW);
										imageIntent.setDataAndType(
												Uri.fromFile(file), "image/*");
										startActivity(imageIntent);

										break;

									case 2:
										Intent movieIntent = new Intent();
										movieIntent
												.setAction(android.content.Intent.ACTION_VIEW);
										movieIntent.setDataAndType(
												Uri.fromFile(file), "image/*");
										startActivity(movieIntent);

										break;
									case 3:

										Intent i = new Intent();
										i.setAction(android.content.Intent.ACTION_VIEW);
										i.setDataAndType(Uri.fromFile(file),
												"audio/*");
										startActivity(i);

										break;

									case 4:
										Intent i1 = new Intent();
										i1.setAction(android.content.Intent.ACTION_VIEW);
										i1.setDataAndType(Uri.fromFile(file),
												"*/*");
										startActivity(i1);

										break;

									}
								}
							});

					AlertDialog alert = builder.create();

					alert.show();

					break;
				case 1:
					// declaration of edit dialog box

					final Dialog dialog = new Dialog(
							FilebrowserULTRAActivity.this);
					dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
					dialog.setTitle("Rename");
					dialog.setContentView(R.layout.rename_dialog);
					dialog.setCancelable(true);
					// end of dialog declaration

					// define the contents of edit dialog
					final TextView rename = (TextView) dialog
							.findViewById(R.id.rename_file_text);

					rename.setText(file.getName());
					// dialog save button to save the edited item
					Button saveButton = (Button) dialog
							.findViewById(R.id.rename_button);
					// for updating the list item
					saveButton.setOnClickListener(new View.OnClickListener() {

						public void onClick(View v) {
							// TODO Auto-generated method stub
							final TextView rename = (TextView) dialog
									.findViewById(R.id.rename_file_text);
							final CharSequence name = rename.getText();
							if (name.length() == 0) {
								rename.setError(
										"Enter a valid name !",
										getResources().getDrawable(
												R.drawable.warning_icon1));

								return;
							}
							FileUtils.renameTarget(filepath, rename.getText()
									.toString());

							refreshList();
							dialog.dismiss();

						}
					});

					// cancel button declaration
					Button cancelButton = (Button) dialog
							.findViewById(R.id.cancel_dialog);
					cancelButton.setOnClickListener(new View.OnClickListener() {

						public void onClick(View v) {
							// TODO Auto-generated method stub
							dialog.dismiss();

						}
					});

					dialog.show();
					dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
							R.drawable.rename48);

					break;

				case 2:

					currentfile = file.getAbsolutePath();
					delete_after_copy = false;

					copybutton.setVisibility(View.VISIBLE);

					copybutton.startAnimation(launchAnimation(R.anim.shake));

					copybutton.setClickable(true);
					showMessage("item copied to clipboad");

					break;

				case 3:
					currentfile = file.getAbsolutePath();

					delete_after_copy = true;

					copybutton.setVisibility(View.VISIBLE);
					copybutton.startAnimation(AnimationUtils.loadAnimation(
							FilebrowserULTRAActivity.this, R.anim.shake));
					copybutton.setClickable(true);
					showMessage("item copied to clipboad");

					break;

				case 4:

					final AlertDialog alertDialog1 = new AlertDialog.Builder(
							FilebrowserULTRAActivity.this).create();
					alertDialog1.setTitle("Delete File");

					alertDialog1.setIcon(R.drawable.warning_icon2);
					alertDialog1.setMessage("Are you sure you want to delete "
							+ file.getName() + " ? ");
					alertDialog1.setButton("Yes",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									/*
									 * if(LinuxShell.isRoot()){
									 * RootUtils.DeleteFileRoot(filepath); }
									 */
									new BackgroundWork(DELETE_TYPE)
											.execute(filepath);

								}
							});
					alertDialog1.setButton2("No",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {

									alertDialog1.dismiss();

								}
							});
					alertDialog1.show();

					break;

				case 5:

					if (file.isDirectory()) {
						Toast.makeText(FilebrowserULTRAActivity.this,
								"can't send a folder...create a zip file !",
								Toast.LENGTH_SHORT).show();

					} else {

						Uri uri11 = Uri.fromFile(file.getAbsoluteFile());
						Intent i1 = new Intent(Intent.ACTION_SEND);

						i1.setType("application/zip");
						i1.putExtra(Intent.EXTRA_STREAM, uri11);
						startActivity(Intent.createChooser(i1, "Send"));

					}

					break;

				case 6:

					if (file.isDirectory()) {
						Toast.makeText(FilebrowserULTRAActivity.this,
								"can't send a folder...create a zip file !",
								Toast.LENGTH_SHORT).show();

					} else {
						Uri uri = Uri.fromFile(file.getAbsoluteFile());
						Intent i = new Intent(Intent.ACTION_SEND);
						i.putExtra(Intent.EXTRA_SUBJECT, file.getName());
						i.putExtra(Intent.EXTRA_TEXT, "Check this out");
						i.setType("text/plain");
						i.putExtra(Intent.EXTRA_STREAM, uri);
						startActivity(Intent.createChooser(i, "Send via"));
					}
					break;

				case 7:
					String path = file.getAbsolutePath();
					Cursor query = managedQuery(BookmarksProvider.CONTENT_URI,
							new String[] { BookmarksProvider._ID },
							BookmarksProvider.PATH + "=?",
							new String[] { path }, null);
					if (!query.moveToFirst()) {
						ContentValues values = new ContentValues();
						values.put(BookmarksProvider.NAME, file.getName());
						values.put(BookmarksProvider.PATH, path);
						getContentResolver().insert(
								BookmarksProvider.CONTENT_URI, values);
						Toast.makeText(FilebrowserULTRAActivity.this,
								"Bookmark Added", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(FilebrowserULTRAActivity.this,
								"Bookmark Exists ", Toast.LENGTH_SHORT).show();
					}

					break;

				case 8:
					if (file.isDirectory()) {
						Toast.makeText(FilebrowserULTRAActivity.this,
								"can't send a folder...create a zip file !",
								Toast.LENGTH_SHORT).show();

					} else {

						Uri uri1 = Uri.fromFile(file.getAbsoluteFile());
						Intent email = new Intent(Intent.ACTION_SEND);
						email.putExtra(Intent.EXTRA_SUBJECT, file.getName());
						email.putExtra(Intent.EXTRA_TEXT, "Check this out");
						email.setType("message/rfc822");
						email.putExtra(Intent.EXTRA_STREAM, uri1);
						startActivity(Intent.createChooser(email, "Send mail"));
					}
					break;

				case 9:
					try {

						new CompressManager(FilebrowserULTRAActivity.this)
								.compress(file.getAbsoluteFile(),
										file.getName() + ".zip");

					} catch (Exception e) {
						// TODO: handle exception
						Toast.makeText(FilebrowserULTRAActivity.this,
								"coudn't zip it!", Toast.LENGTH_SHORT).show();
					}

					break;

				/*
				 * case 10:
				 * 
				 * LayoutInflater factory =
				 * LayoutInflater.from(FilebrowserULTRAActivity.this); final
				 * View OpenTxtView = factory.inflate(R.layout.crypt_input,
				 * null); final AlertDialog alert1 = new
				 * AlertDialog.Builder(FilebrowserULTRAActivity.this).create();
				 * alert1.setTitle("Encrypt As"); alert1.setView(OpenTxtView);
				 * 
				 * alert1.setIcon(R.drawable.locked48);
				 * 
				 * alert1.setButton("Okay", new
				 * DialogInterface.OnClickListener() {
				 * 
				 * 
				 * public void onClick(DialogInterface arg0, int whoch) { //
				 * TODO Auto-generated method stub EditText encryptfile =
				 * (EditText)alert1.findViewById(R.id.savetext); CheckBox
				 * delete_chkbox
				 * =(CheckBox)alert1.findViewById(R.id.check_delete); CheckBox
				 * des_chkbox =(CheckBox)alert1.findViewById(R.id.check_des);
				 * CheckBox aes_chkbox
				 * =(CheckBox)alert1.findViewById(R.id.check_aes);
				 * aes_chkbox.setEnabled(false); final String filename =
				 * encryptfile.getText().toString(); try { // Generate a
				 * temporary key. In practice, you would save this key. // See
				 * also Encrypting with DES Using a Pass Phrase. //SecretKey key
				 * = KeyGenerator.getInstance("DES").generateKey(); //encoded =
				 * key.getEncoded(); //Toast.makeText(getApplicationContext(),
				 * encoded.toString(), Toast.LENGTH_LONG).show();
				 * 
				 * 
				 * if(des_chkbox.isChecked()==true){
				 * 
				 * new BackgroundWork(ENCRYPT_TYPE).execute(filename,file.
				 * getAbsolutePath());
				 * 
				 * 
				 * }else if (aes_chkbox.isChecked()==true) {
				 * 
				 * DesEncrypter encrypter = new DesEncrypter(mykey.secretkey);
				 * encrypter.encrypt(new
				 * FileInputStream(file.getAbsoluteFile()), new
				 * FileOutputStream(
				 * currentdir+"/"+filename+getFileExtension(file
				 * .getAbsolutePath())));
				 * 
				 * }
				 * 
				 * if(delete_chkbox.isChecked()==true){ deleteCheck = -1; } else
				 * { deleteCheck = 0; }
				 * 
				 * } catch (Exception e) {
				 * Toast.makeText(FilebrowserULTRAActivity
				 * .this,"coudn't encrypt !", Toast.LENGTH_SHORT).show();
				 * 
				 * }
				 * 
				 * } }); alert1.setButton2("Cancel", new
				 * DialogInterface.OnClickListener() { public void
				 * onClick(DialogInterface dialog, int which) {
				 * 
				 * alert1.dismiss();
				 * 
				 * } });
				 * 
				 * alert1.show();
				 * 
				 * 
				 * 
				 * 
				 * 
				 * break;
				 * 
				 * case 11:
				 * 
				 * LayoutInflater factory1 =
				 * LayoutInflater.from(FilebrowserULTRAActivity.this); final
				 * View OpenTxtView1 = factory1.inflate(R.layout.decrypt_input,
				 * null); final AlertDialog alert11 = new
				 * AlertDialog.Builder(FilebrowserULTRAActivity.this).create();
				 * alert11.setTitle("Decrypt As");
				 * alert11.setView(OpenTxtView1);
				 * alert11.setIcon(R.drawable.unlocked48);
				 * 
				 * 
				 * alert11.setButton("Okay", new
				 * DialogInterface.OnClickListener() {
				 * 
				 * 
				 * public void onClick(DialogInterface arg0, int whoch) { //
				 * TODO Auto-generated method stub EditText decryptfile =
				 * (EditText)alert11.findViewById(R.id.savetext); CheckBox
				 * delete_chkbox
				 * =(CheckBox)alert11.findViewById(R.id.check_delete); CheckBox
				 * des_chkbox =(CheckBox)alert11.findViewById(R.id.check_des);
				 * CheckBox aes_chkbox
				 * =(CheckBox)alert11.findViewById(R.id.check_aes);
				 * 
				 * 
				 * 
				 * final String filename = decryptfile.getText().toString(); try
				 * { if(des_chkbox.isChecked()==true){
				 * //Toast.makeText(getApplicationContext(), encoded.toString(),
				 * Toast.LENGTH_LONG).show(); new
				 * BackgroundWork(DECRYPT_TYPE).execute
				 * (filename,file.getAbsolutePath());
				 * 
				 * } else if (aes_chkbox.isChecked()==true){
				 * 
				 * 
				 * DesEncrypter encrypter = new DesEncrypter(mykey.secretkey);
				 * 
				 * encrypter.decrypt(new
				 * FileInputStream(file.getAbsoluteFile()), new
				 * FileOutputStream(
				 * currentdir+"/"+filename+getFileExtension(file
				 * .getAbsolutePath())));
				 * 
				 * } if(delete_chkbox.isChecked()==true){ deleteCheck = -1 ; }
				 * else { deleteCheck = 0; }
				 * 
				 * } catch (Exception e) {
				 * Toast.makeText(FilebrowserULTRAActivity
				 * .this,"coudn't decrypt !", Toast.LENGTH_SHORT).show(); }
				 * 
				 * } }); alert11.setButton2("Cancel", new
				 * DialogInterface.OnClickListener() { public void
				 * onClick(DialogInterface dialog, int which) {
				 * 
				 * alert11.dismiss();
				 * 
				 * } });
				 * 
				 * alert11.show();
				 * 
				 * 
				 * 
				 * 
				 * break;
				 */
				case 10:

					getProperties(file.getAbsoluteFile());

					break;
				case 11:

					Intent intent = new Intent(FilebrowserULTRAActivity.this,
							CreateGestureActivity.class);
					intent.putExtra("path", filepath);
					startActivity(intent);
					break;

				}
			}
		});
		AlertDialog alert = builder.create();

		alert.show();

	}

	private boolean getMounted() {
		boolean haveSdcard = false;

		try {
			Process p = Runtime.getRuntime().exec("mount");
			DataInputStream mountStream = new DataInputStream(
					p.getInputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(
					mountStream));
			String line;
			Pattern pat = Pattern
					.compile("/[^ ]+ on /mnt/sdcard/([^ /]+) type");
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (!haveSdcard && line.contains("/mnt/sdcard"))
					haveSdcard = true;
				Matcher m = pat.matcher(line);

			}
			return haveSdcard;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}

	@SuppressLint({ "NewApi", "NewApi" })
	public void getProperties(File file) {

		String file_path = file.getAbsolutePath().toString();

		final Dialog dialog1 = new Dialog(FilebrowserULTRAActivity.this);
		TextView type, path, name;
		final TextView size;
		TextView modified, contains, sdcard_free, sdcard_total, read, write, hidden;
		ImageView file_icon;
		// ProgressBar sdcardBar;

		dialog1.requestWindowFeature(Window.FEATURE_LEFT_ICON);
		dialog1.requestWindowFeature(Window.FEATURE_RIGHT_ICON);
		dialog1.setTitle("Properties");
		dialog1.setContentView(R.layout.file_properties);
		dialog1.setCancelable(true);

		//
		file_icon = (ImageView) dialog1.findViewById(R.id.file_image);

		file_icon.setImageDrawable(getScaledIcon(file_path));

		//
		name = (ScrollingTextView) dialog1.findViewById(R.id.file_name);
		name.setText(" " + file.getName().toString());
		//
		path = (TextView) dialog1.findViewById(R.id.file_path);
		path.setText(" " + file.getAbsolutePath().toString());

		//
		size = (TextView) dialog1.findViewById(R.id.file_size);

		if (file_path == root) {
			if (Build.VERSION.SDK_INT >= 8) {
				size.setText(" " + String.valueOf(getAsString(getSize(file))));

			} else {
				StatFs stat = new StatFs(file_path);

				size.setText(" "
						+ String.valueOf(getAsString((stat.getBlockCount() - stat
								.getAvailableBlocks()) * stat.getBlockSize())));
			}

		} else {
			String total_size = getAsString(getSize(file));
			size.setText(" " + String.valueOf(total_size));

			if (file.isDirectory()) {
				new AsyncTask<File, Long, Long>() {

					protected long totalSize = 0L;

					@Override
					protected Long doInBackground(File... file) {
						sizeOf(file[0]);
						return totalSize;
					}

					@Override
					protected void onProgressUpdate(Long... updatedSize) {
						size.setText(FileUtils.formatSize(size.getContext(),
								updatedSize[0]));
					}

					@Override
					protected void onPostExecute(Long result) {
						size.setText(FileUtils.formatSize(size.getContext(),
								result));
					}

					private void sizeOf(File file) {
						if (file.isFile()) {
							totalSize += file.length();
							publishProgress(totalSize);
						} else {
							File[] files = file.listFiles();

							if (files != null && files.length != 0) {
								for (File subFile : files) {
									sizeOf(subFile);
								}
							}
						}
					}
				}.execute(file);

			}
		}

		type = (TextView) dialog1.findViewById(R.id.file_type);
		if (file.isDirectory()) {
			type.setText(" Folder ");
		}
		if (file.isFile()) {
			if (getFileExtension(file.getAbsolutePath()).contains(".")) {
				type.setText(" "
						+ getFileExtension(file.getAbsolutePath()).toString()
								.replace(".", "").toUpperCase() + " file");
			} else {
				type.setText("file");

			}
		}
		//
		modified = (TextView) dialog1.findViewById(R.id.file_modified);
		long lastModified = file.lastModified();
		Date date = new Date(lastModified);
		modified.setText("" + String.valueOf(date));
		//
		contains = (TextView) dialog1.findViewById(R.id.file_contains);
		if (file.isFile()) {
			contains.setText(" NA ");
		} else if (file.isDirectory()) {

			int len = 0;

			File[] list = file.listFiles();
			if (list != null)
				len = list.length;
			contains.setText(" " + String.valueOf(len));
		}
		//
		sdcard_free = (TextView) dialog1.findViewById(R.id.sdcard_free);
		sdcard_total = (TextView) dialog1.findViewById(R.id.sdcard_total);

		if (Build.VERSION.SDK_INT >= 8) {
			sdcard_free.setText(" "
					+ String.valueOf(getAsString(file.getFreeSpace())));
			sdcard_total.setText(" "
					+ String.valueOf(getAsString(file.getTotalSpace())));

		} else {
			StatFs stat = new StatFs(file_path);

			sdcard_free.setText(" "
					+ String.valueOf(getAsString(stat.getAvailableBlocks()
							* stat.getBlockSize())));
			sdcard_total.setText(" "
					+ String.valueOf(getAsString(stat.getBlockCount()
							* stat.getBlockSize())));

		}

		//
		read = (TextView) dialog1.findViewById(R.id.file_read_access);
		read.setText(" " + String.valueOf(file.canWrite()));
		//
		write = (TextView) dialog1.findViewById(R.id.file_write_access);
		write.setText(" " + String.valueOf(file.canWrite()));
		//
		hidden = (TextView) dialog1.findViewById(R.id.file_hidden);
		hidden.setText(" " + String.valueOf(file.isHidden()));
		//

		dialog1.show();
		dialog1.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
				R.drawable.settings_mdpi);

	}

	public static class Item {
		public final String text;
		public final int icon;

		public Item(String text, Integer icon) {
			this.text = text;
			this.icon = icon;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	public static class ItemDrawable {
		public final String text;
		public final Drawable icon;

		public ItemDrawable(String text, Drawable icon) {
			this.text = text;
			this.icon = icon;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	public String getAsString(long bytes) {
		for (int i = 6; i >= 0; i--) {
			double step = Math.pow(1024, i);
			if (bytes > step)
				return String.format("%3.2f %s", bytes / step, Q[i]);
		}
		return Long.toString(bytes);
	}

	public long getSize(File file) {

		long size = 0;
		int len = 0;
		if (file.isFile()) {
			size = file.length();
		} else if (file.isDirectory()) {
			File[] list = file.listFiles();
			if (list != null) {
				len = list.length;
			}

			for (int j = 0; j < len; j++) {
				if (list[j].isFile()) {
					size = size + list[j].length();
				} else if (list[j].isDirectory()) {
					size = size + getSize(list[j]);

				}

			}

		}
		return size;
	}

	public String getFileExtension(String filepath) {

		final File file = new File(filepath);
		String filename = file.getName().toString();

		String ext = null;

		try {
			ext = filename.substring(filename.lastIndexOf("."),
					filename.length());

		} catch (IndexOutOfBoundsException e) {

			ext = "";

		}
		return ext;
	}

	private final static Comparator<? super String> name = new Comparator<String>() {

		public int compare(String arg0, String arg1) {
			return arg0.toLowerCase().compareTo(arg1.toLowerCase());
		}
	};

	private final static Comparator<? super String> type = new Comparator<String>() {

		public int compare(String arg0, String arg1) {
			String ext = null;
			String ext2 = null;
			int ret;
			File file1 = new File(arg0);
			File file2 = new File(arg1);

			try {
				ext = arg0.substring(arg0.lastIndexOf(".") + 1, arg0.length())
						.toLowerCase();
				ext2 = arg1.substring(arg1.lastIndexOf(".") + 1, arg1.length())
						.toLowerCase();

			} catch (IndexOutOfBoundsException e) {
				return 0;
			}
			ret = ext.compareTo(ext2);

			if (ret == 0)
				return arg0.toLowerCase().compareTo(arg1.toLowerCase());
			else {
				if ((file1.isDirectory()) && (!file2.isDirectory()))
					return -1;
				if ((!file1.isDirectory()) && (file2.isDirectory()))
					return 1;
				if ((file1.getName().startsWith("."))
						&& (!file2.getName().startsWith(".")))
					return -1;
				if ((!file1.getName().startsWith("."))
						&& (file2.getName().startsWith(".")))
					return 1;
			}

			return ret;
		}
	};

	private final static Comparator<? super String> modified = new Comparator<String>() {

		public int compare(String arg0, String arg1) {
			File file1 = new File(arg0);
			File file2 = new File(arg1);

			Long first = file1.lastModified();
			Long second = file2.lastModified();

			return first.compareTo(second);

		}
	};

	Comparator<? super File> filecomparator_name = new Comparator<File>() {

		public int compare(File file1, File file2) {
			// sort folders first
			if ((file1.isDirectory()) && (!file2.isDirectory()))
				return -1;
			if ((!file1.isDirectory()) && (file2.isDirectory()))
				return 1;

			// here both are folders or both are files : sort alpha
			return file1.getName().toLowerCase()
					.compareTo(file2.getName().toLowerCase());
		}

	};

	Comparator<? super File> filecomparator_size = new Comparator<File>() {

		public int compare(File file1, File file2) {

			if ((file1.isDirectory()) && (!file2.isDirectory())) {
				return -1;
			}
			if ((!file1.isDirectory()) && (file2.isDirectory())) {
				return 1;
			}

			Long first = getSize(file1);
			Long second = getSize(file2);

			return second.compareTo(first);
		}
	};

	Comparator<? super File> filecomparator_type = new Comparator<File>() {

		public int compare(File file1, File file2) {

			String arg0 = file1.getName().toString();
			String arg1 = file2.getName().toString();

			final int s1Dot = arg0.lastIndexOf('.');
			final int s2Dot = arg1.lastIndexOf('.');

			if ((s1Dot == -1) == (s2Dot == -1)) { // both or neither

				arg0 = arg0.substring(s1Dot + 1);
				arg1 = arg1.substring(s2Dot + 1);
				return (arg0.toLowerCase()).compareTo((arg1.toLowerCase()));
			}

			else if (s1Dot == -1) { // only s2 has an extension, so s1 goes
									// first
				return -1;
			} else { // only s1 has an extension, so s1 goes second
				return 1;
			}
		}

	};

	Comparator<? super File> filecomparator_modified = new Comparator<File>() {

		public int compare(File file1, File file2) {

			Long first = file1.lastModified();
			Long second = file2.lastModified();

			return first.compareTo(second);
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		EditText quicksearch = (EditText) findViewById(R.id.search_box);

		if ((keyCode == KeyEvent.KEYCODE_BACK) && multiselectflag == true
				|| searchflag == true
				|| quicksearch.getText().toString().length() > 0) { // Back key
																	// pressed

			quicksearch.setText("");
			multiselectflag = false;
			searchflag = false;
			refreshList();

			return true;
		}
		/*
		 * 
		 * else if((keyCode == KeyEvent.KEYCODE_BACK) && mLayout.isOpening()){
		 * mLayout.closeSidebar(); return true; }
		 */

		else if ((keyCode == KeyEvent.KEYCODE_BACK) && !currentdir.equals(root)
				&& !currentdir.equals("/"))

		{

			getFileList(parentfile);
			getListView().setSelectionFromTop(index_back, 0);
			myGrid = (GridView) findViewById(R.id.grid);
			myGrid.setSelection(index_grid);

			return true;
		} else if ((keyCode == KeyEvent.KEYCODE_BACK)
				&& multiselectflag == false && searchflag == false
				&& mUseBackKey && currentdir.equals(root)) {

			Toast.makeText(FilebrowserULTRAActivity.this,
					"Press back again to exit.", Toast.LENGTH_SHORT).show();
			mUseBackKey = false;
			return true;

		} else if ((keyCode == KeyEvent.KEYCODE_BACK)
				&& multiselectflag == false && !mUseBackKey
				&& currentdir.equals(root) || currentdir.equals("/")) {

			finish();
			return false;

		}

		if ((keyCode == KeyEvent.KEYCODE_SEARCH)) { // Back key pressed

			Intent intent = new Intent(FilebrowserULTRAActivity.this,
					SearchFilesDialog.class);
			startActivityForResult(intent, SEARCH_RESULT);

			return false;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_option, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.manage_apps:

			Intent process_intent = new Intent(FilebrowserULTRAActivity.this,
					ProcessManager.class);
			startActivity(process_intent);
			break;
		case R.id.backup_apps:
			Intent backup_intent = new Intent(FilebrowserULTRAActivity.this,
					BackupManager.class);
			startActivity(backup_intent);
			break;

		case R.id.app_settings:
			Intent intent = new Intent(this, PreferenceActivity.class);
			startActivity(intent);
			break;

		case R.id.search_files:
			getSearchDialog();
			break;

		case R.id.menu_multiselect:
			searchflag = false;
			unselectflag = false;
			refreshList();
			multiselectflag = !multiselectflag;

			manageUi();
			int len = items.length;
			for (int i = 0; i < len; i++) {
				mStarStates[i] = false;
			}
			multiSelectData.clear();

			break;
		case R.id.menu_refresh:
			refreshList();
			showMessage("refreshed");

			break;
		case R.id.about_us:
			// declare the dialog box for About us menu option
			final Dialog dialog = new Dialog(FilebrowserULTRAActivity.this);
			dialog.setContentView(R.layout.about_us_dialog);
			dialog.setTitle("About");
			dialog.setCancelable(true);

			TextView app_name,
			version_name,
			developer_name,
			twitter_name;
			app_name = (TextView) dialog.findViewById(R.id.name_app);
			version_name = (TextView) dialog.findViewById(R.id.version_app);
			developer_name = (TextView) dialog
					.findViewById(R.id.name_developer);
			twitter_name = (TextView) dialog.findViewById(R.id.name_twitter);

			app_name.setTypeface(Fonts.SONY);
			version_name.setTypeface(Fonts.SONY);
			developer_name.setTypeface(Fonts.SONY);
			twitter_name.setTypeface(Fonts.SONY);

			Button cancelButton = (Button) dialog
					.findViewById(R.id.cancel_button);
			cancelButton.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			Button emailButton = (Button) dialog
					.findViewById(R.id.contact_button);
			emailButton.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {

					final Intent i = new Intent(Intent.ACTION_SEND);
					String[] recipients = new String[] { "kshark05@gmail.com" };
					i.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
					i.putExtra(Intent.EXTRA_EMAIL, recipients);
					i.putExtra(Intent.EXTRA_TEXT,
							"Write us a feedback about what you liked or didn't like about the app.");
					i.setType("message/rfc822");
					startActivity(Intent.createChooser(i, "Contact Us"));
					dialog.dismiss();
				}
			});

			Button marketButton = (Button) dialog
					.findViewById(R.id.market_link);
			marketButton.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			dialog.show();

			break;

		case R.id.bookmarks:

			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			final Cursor bookmarksCursor = getBookmarks();

			builder.setTitle("Bookmarks");

			builder.setCursor(bookmarksCursor,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							if (bookmarksCursor.moveToPosition(item)) {
								String path = bookmarksCursor.getString(bookmarksCursor
										.getColumnIndex(BookmarksProvider.PATH));
								File file = new File(path);
								if (file != null) {
									if (file.isDirectory()) {
										getFileList(path);
									} else {
										getOperations(path);
									}

								}
							} else {
								Toast.makeText(FilebrowserULTRAActivity.this,
										"No Bookmarks", Toast.LENGTH_SHORT)
										.show();
							}
						}
					}, BookmarksProvider.NAME);
			builder.create();
			builder.show();

			break;

		case R.id.contact_us:

			final Intent i = new Intent(Intent.ACTION_SEND);
			String[] recipients = new String[] { "mirrorlabs.android@gmail.com" };
			i.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
			i.putExtra(Intent.EXTRA_EMAIL, recipients);
			i.putExtra(Intent.EXTRA_TEXT,
					"Write us a feedback about what you liked or didn't like about the app.");
			i.setType("message/rfc822");
			startActivity(Intent.createChooser(i, "Contact Us"));
			break;

		}
		return true;
	}

	@SuppressWarnings("deprecation")
	private Cursor getBookmarks() {
		return managedQuery(BookmarksProvider.CONTENT_URI, new String[] {
				BookmarksProvider._ID, BookmarksProvider.NAME,
				BookmarksProvider.PATH, }, null, null, null);
	}

	public void refreshList() {
		searchflag = false;
		manageUi();
		mylist = getListView();
		index = mylist.getFirstVisiblePosition(); // set last_visited_pos in
													// list view
		getFileList(currentdir);

		mylist.setSelectionFromTop(index, 0);
		myGrid.setSelection(index);

	}

	public String getCurrentDir() {
		return currentdir;
	}

	public void OnclickOperation(final String filepath) {

		final File file = new File(filepath);
		final String ext = getFileExtension(filepath);

		if (file.isDirectory()) {
			if (file.canRead()) {
				searchflag = false;
				getFileList(filepath);

			}

			else {
				getFileList(filepath);
			}
		}

		else if (ext.equalsIgnoreCase(".zip")) {
			final CharSequence[] items = { "Extract here", "Extract to",
					"More Options" };
			AlertDialog.Builder builder = new AlertDialog.Builder(
					FilebrowserULTRAActivity.this);
			builder.setTitle("Options");
			builder.setItems(items, new DialogInterface.OnClickListener() {
				@SuppressWarnings("deprecation")
				public void onClick(DialogInterface dInterface, int item) {
					switch (item) {
					case 0:
						new ExtractManager(FilebrowserULTRAActivity.this)
								.extract(file.getAbsoluteFile(), currentdir);

						break;

					case 1:

						LayoutInflater factory = LayoutInflater
								.from(FilebrowserULTRAActivity.this);
						final View OpenTxtView = factory.inflate(
								R.layout.extract_input, null);
						final AlertDialog alert1 = new AlertDialog.Builder(
								FilebrowserULTRAActivity.this).create();
						alert1.setTitle("Extract file");
						alert1.setView(OpenTxtView);
						alert1.setIcon(R.drawable.zip);

						alert1.setButton("Okay",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface arg0,
											int which) {
										// TODO Auto-generated method stub
										EditText savefile = (EditText) alert1
												.findViewById(R.id.savetext);
										CheckBox current_chkbox = (CheckBox) alert1
												.findViewById(R.id.check_current);
										if (current_chkbox.isChecked() == true) {
											savefile.setText(file.getParent()
													.toString());
										}
										String unzipfilepath = savefile
												.getText().toString();
										if (new File(unzipfilepath).exists()) {

											new ExtractManager(
													FilebrowserULTRAActivity.this)
													.extract(file
															.getAbsoluteFile(),
															unzipfilepath);

										} else {
											showMessage("Path doesn't exist");
										}

										getFileList(unzipfilepath);
									}
								});
						alert1.setButton2("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {

										alert1.dismiss();

									}
								});

						alert1.show();

						break;
					case 2:
						Intent zipIntent = new Intent();
						zipIntent.setAction(android.content.Intent.ACTION_VIEW);
						zipIntent.setDataAndType(Uri.fromFile(file),
								"application/zip");
						try {
							startActivity(zipIntent);
						} catch (Exception e) {
							// TODO: handle exception
							showMessage("No application to open Zip file");
						}
						break;
					}
				}
			});
			AlertDialog alert = builder.create();
			alert.show();

		} else if (ext.equalsIgnoreCase(".mp3") || ext.equalsIgnoreCase(".wav")) {

			try {
				Intent i = new Intent();
				i.setAction(android.content.Intent.ACTION_VIEW);
				i.setDataAndType(Uri.fromFile(file), "audio/*");
				startActivity(i);

			} catch (Exception e) {
				// TODO: handle exception
				Intent i1 = new Intent(FilebrowserULTRAActivity.this,
						PlayerActivity.class);
				Bundle extras = new Bundle();
				extras.putString("path", String.valueOf(Uri.fromFile(file)));
				extras.putString("filePath", file.getAbsolutePath());
				i1.putExtras(extras);
				startActivity(i1);

			}
		}

		else if (ext.equalsIgnoreCase(".pdf")) {
			Intent pdfIntent = new Intent();
			pdfIntent.setAction(android.content.Intent.ACTION_VIEW);
			pdfIntent.setDataAndType(Uri.fromFile(file), "application/pdf");
			try {
				startActivity(pdfIntent);
			} catch (Exception e) {
				// TODO: handle exception
				final Intent intent = new Intent(FilebrowserULTRAActivity.this,
						PDFViewer.class);
				intent.putExtra(PdfViewerActivity.EXTRA_PDFFILENAME, filepath);
				startActivity(intent);

				// showMessage("couldn't find a PDF viewer");
			}
		}

		else {
			String mimeType = MimeTypes.getMimeType(file.getName());
			Intent myIntent = new Intent();
			myIntent.setAction(android.content.Intent.ACTION_VIEW);
			myIntent.setDataAndType(Uri.fromFile(file), mimeType);
			try {
				startActivity(myIntent);
			} catch (Exception e) {
				// TODO: handle exception
				Toast.makeText(this, "No application to open  file",
						Toast.LENGTH_SHORT).show();
			}

			/*
			 * 
			 * new AlertDialog.Builder(this) .setIcon(R.drawable.unknown)
			 * .setTitle(file.getName()+" is unknown ") .setPositiveButton("OK",
			 * new DialogInterface.OnClickListener() {
			 * 
			 * @Override public void onClick(DialogInterface dialog, int which)
			 * { // TODO Auto-generated method stub
			 * 
			 * } }).show();
			 */

		}

	}

	@Override
	protected void onPause() {

		super.onPause();

	}

	@Override
	protected void onStop() {

		multiSelectData.clear();
		DrawableManager.cache.clear();
		super.onStop();

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}

	@Override
	protected void onDestroy() {

		multiSelectData.clear();
		DrawableManager.cache.clear();
		super.onDestroy();

	}

	@Override
	protected void onResume() {

		super.onResume();

	}

	private void fatalError(String title, String msg) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();

		Log.e("Ultra File Browser", title);

		alertDialog.setTitle(title);
		alertDialog.setIcon(R.drawable.error);
		alertDialog.setMessage(msg);
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
		alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		});
		alertDialog.show();
	}

	Drawable getIcon(String filepath) {
		File file = new File(filepath);
		String filename = file.getName().toString(); // get the file according
														// the position

		String ext = null;
		try {
			ext = filename.substring(filename.lastIndexOf("."),
					filename.length());

		} catch (IndexOutOfBoundsException e) {
			ext = "";
		}

		if (file.isDirectory()) {
			if (file.getName().equals("sdcard")) {
				return getResources().getDrawable(R.drawable.sdcard_72);
			} else if (file.getName().equals("system")) {
				return getResources().getDrawable(R.drawable.system72);
			} else {
				return getResources().getDrawable(R.drawable.myfolder72);
			}

		} else if (file.isFile()) {
			if (file.getName().equals("vendor")) {
				return getResources().getDrawable(R.drawable.miscellaneous);

			}
			if (ext.equalsIgnoreCase(".zip")) {
				return getResources().getDrawable(R.drawable.myzip);
			} else if (ext.equalsIgnoreCase(".rar")) {
				return getResources().getDrawable(R.drawable.rar);
			}

			else if (ext.equalsIgnoreCase(".pdf")) {
				return getResources().getDrawable(R.drawable.pdf_icon);
			}

			else if (ext.equalsIgnoreCase(".txt")) {
				return getResources().getDrawable(R.drawable.textpng);
			} else if (ext.equalsIgnoreCase(".html")) {
				return getResources().getDrawable(R.drawable.html);
			} else if (ext.equalsIgnoreCase(".jpg")
					|| ext.equalsIgnoreCase(".png")
					|| ext.equalsIgnoreCase(".gif")
					|| ext.equalsIgnoreCase(".jpeg")
					|| ext.equalsIgnoreCase(".tiff")) {
				return getResources().getDrawable(R.drawable.image);
			}

			else if (ext.equalsIgnoreCase(".mp3")
					|| ext.equalsIgnoreCase(".wav")
					|| ext.equalsIgnoreCase(".m4a")) {
				return getResources().getDrawable(R.drawable.audio);
			}

			else if (ext.equalsIgnoreCase(".apk")) {

				return getapkicon(filepath);

			} else if (ext.equalsIgnoreCase(".mp4")
					|| ext.equalsIgnoreCase(".3gp")
					|| ext.equalsIgnoreCase(".flv")
					|| ext.equalsIgnoreCase(".ogg")
					|| ext.equalsIgnoreCase(".m4v")) {
				return getResources().getDrawable(R.drawable.videos_new);
			}

			else if (ext.equalsIgnoreCase(".sh") || ext.equalsIgnoreCase(".rc")) {
				return getResources().getDrawable(R.drawable.script_file64);
			}

			else if (ext.equalsIgnoreCase(".prop")) {
				return getResources().getDrawable(R.drawable.build_file64);
			} else if (ext.equalsIgnoreCase(".xml")) {
				return getResources().getDrawable(R.drawable.xml64);
			} else if (ext.equalsIgnoreCase(".doc")
					|| ext.equalsIgnoreCase(".docx")) {
				return getResources().getDrawable(R.drawable.nsword64);

			} else if (ext.equalsIgnoreCase(".ppt")
					|| ext.equalsIgnoreCase(".pptx")) {
				return getResources().getDrawable(R.drawable.ppt64);

			} else if (ext.equalsIgnoreCase(".xls")
					|| ext.equalsIgnoreCase(".xlsx")) {
				return getResources().getDrawable(R.drawable.spreadsheet64);
			} else {
				return getResources().getDrawable(R.drawable.miscellaneous);
			}

		} else {
			return getResources().getDrawable(R.drawable.miscellaneous);
		}
	}

	class SearchGrid extends ArrayAdapter<String> {

		private ImageThreadLoader imageLoader;
		private DrawableThreadLoader drawableLoader;

		public SearchGrid() {
			super(FilebrowserULTRAActivity.this, R.layout.searchrow, founditems);
			imageLoader = new ImageThreadLoader(FilebrowserULTRAActivity.this);
			drawableLoader = new DrawableThreadLoader(
					FilebrowserULTRAActivity.this);

			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolderGrid holder;

			if (convertView == null) {
				LayoutInflater inflater = getLayoutInflater(); // to instantiate
																// layout XML
																// file into its
																// corresponding
																// View objects
				convertView = inflater.inflate(R.layout.searchrow2, null); // to
																			// Quick
																			// access
																			// to
																			// the
																			// LayoutInflater
																			// instance
																			// that
																			// this
																			// Window
																			// retrieved
																			// from
																			// its
																			// Context.
				holder = new ViewHolderGrid(convertView);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolderGrid) convertView.getTag();
			}

			final String filepath = founditems[position];

			File f = new File(filepath);

			String ext = FileUtils.getExtension(filepath);

			String filename = f.getName().toLowerCase();
			String highlightString = SearchString.toLowerCase();
			int startIndex = filename.indexOf(highlightString);
			if (startIndex != -1) {
				int len = SearchString.length();
				String strPart = f.getName().substring(startIndex,
						startIndex + len);
				filename = f.getName().replace(strPart,
						"<font color=\"#91DEF7\">" + strPart + "</font>");
				holder.name.setText(Html.fromHtml(filename),
						TextView.BufferType.SPANNABLE);
			} else {

				holder.name.setText(f.getName());
			}

			if (f.isFile() && ext.equalsIgnoreCase(".apk")) {

				if (apkicon == true) {
					// setPlaceholder(getResources().getDrawable(R.drawable.apk_file));
					holder.image.setTag(filepath);
					drawableLoader.displayImage(filepath,
							FilebrowserULTRAActivity.this, holder.image);
					// loadDrawable(f.getAbsolutePath(),holder.image);
				} else {
					holder.image.setImageResource(R.drawable.apk_file);
				}

			}

			else if (f.isFile() && ext.equalsIgnoreCase(".jpg")
					|| ext.equalsIgnoreCase(".png")
					|| ext.equalsIgnoreCase(".gif")
					|| ext.equalsIgnoreCase(".jpeg")
					|| ext.equalsIgnoreCase(".tiff")) {

				// Drawable icon = getResources().getDrawable(R.drawable.image);
				// Bitmap bitmap = ((BitmapDrawable)icon ).getBitmap();
				// BitmapManager.INSTANCE.setPlaceholder(bitmap);
				holder.image.setTag(f.getAbsolutePath());
				imageLoader.displayImage(filepath,
						FilebrowserULTRAActivity.this, holder.image);

				// BitmapManager.INSTANCE.loadBitmap(f.getAbsolutePath(),holder.image);

			}

			else {
				if (f.isDirectory()) {
					holder.image.setTag(f.getAbsolutePath());
					holder.image.setImageDrawable(getIcon(filepath));

				}
				{
					holder.image.setTag(f.getAbsolutePath());
					holder.image.setImageDrawable(getIcon(filepath));
				}
			}

			return (convertView);
		}

	}

	class SearchList extends ArrayAdapter<String> {

		private ImageThreadLoader imageLoader;
		private DrawableThreadLoader drawableLoader;

		public SearchList() {
			super(FilebrowserULTRAActivity.this, R.layout.searchrow, founditems);
			imageLoader = new ImageThreadLoader(FilebrowserULTRAActivity.this);
			drawableLoader = new DrawableThreadLoader(
					FilebrowserULTRAActivity.this);

			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if (convertView == null) {
				LayoutInflater inflater = getLayoutInflater(); // to instantiate
																// layout XML
																// file into its
																// corresponding
																// View objects
				convertView = inflater.inflate(R.layout.searchrow, null); // to
																			// Quick
																			// access
																			// to
																			// the
																			// LayoutInflater
																			// instance
																			// that
																			// this
																			// Window
																			// retrieved
																			// from
																			// its
																			// Context.
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final String filepath = founditems[position];
			File f = new File(filepath);

			String ext = FileUtils.getExtension(filepath);

			String filename = f.getName().toLowerCase();
			String highlightString = SearchString.toLowerCase();
			int startIndex = filename.indexOf(highlightString);
			if (startIndex != -1) {
				int len = SearchString.length();
				String strPart = f.getName().substring(startIndex,
						startIndex + len);
				filename = f.getName().replace(strPart,
						"<font color=\"#91DEF7\">" + strPart + "</font>");
				holder.name.setText(Html.fromHtml(filename),
						TextView.BufferType.SPANNABLE);
			} else {

				holder.name.setText(f.getName());
			}

			if (f.isFile() && ext.equalsIgnoreCase(".apk")) { // decide are the
																// file folder
																// or file

				if (apkicon == true) {
					// setPlaceholder(getResources().getDrawable(R.drawable.apk_file));
					holder.image.setTag(filepath);
					drawableLoader.displayImage(filepath,
							FilebrowserULTRAActivity.this, holder.image);
					// loadDrawable(f.getAbsolutePath(),holder.image);
				} else {
					holder.image.setImageResource(R.drawable.apk_file);
				}

				holder.info.setText(FileUtils.formatSize(
						FilebrowserULTRAActivity.this, f.length()));

			}

			else if (f.isFile() && ext.equalsIgnoreCase(".jpg")
					|| ext.equalsIgnoreCase(".png")
					|| ext.equalsIgnoreCase(".gif")
					|| ext.equalsIgnoreCase(".jpeg")
					|| ext.equalsIgnoreCase(".tiff")) {

				// Drawable icon = getResources().getDrawable(R.drawable.image);
				// Bitmap bitmap = ((BitmapDrawable)icon ).getBitmap();
				// BitmapManager.INSTANCE.setPlaceholder(bitmap);
				holder.image.setTag(f.getAbsolutePath());
				imageLoader.displayImage(filepath,
						FilebrowserULTRAActivity.this, holder.image);

				// BitmapManager.INSTANCE.loadBitmap(f.getAbsolutePath(),holder.image);
				holder.info.setTag(filepath);

				holder.info.setText(FileUtils.formatSize(
						FilebrowserULTRAActivity.this, f.length()));

			}

			else {
				if (f.isDirectory()) { // decide are the file folder or file
					holder.image.setTag(filepath);
					holder.info.setTag(filepath);
					holder.info.setText("<DIR>");
					holder.image.setImageDrawable(getIcon(filepath));

				} else {
					holder.image.setTag(filepath);
					holder.image.setImageDrawable(getIcon(filepath));
					holder.info.setTag(filepath);
					holder.info.setText(FileUtils.formatSize(
							FilebrowserULTRAActivity.this, f.length()));

				}
			}

			return (convertView);
		}

	}

	class IconicList extends ArrayAdapter<String> {

		private ImageThreadLoader imageLoader;
		private DrawableThreadLoader drawableLoader;

		public IconicList() {
			super(FilebrowserULTRAActivity.this, R.layout.row, items);
			imageLoader = new ImageThreadLoader(FilebrowserULTRAActivity.this);
			drawableLoader = new DrawableThreadLoader(
					FilebrowserULTRAActivity.this);

			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if (convertView == null) {
				LayoutInflater inflater = getLayoutInflater(); // to instantiate
																// layout XML
																// file into its
																// corresponding
																// View objects
				convertView = inflater.inflate(R.layout.row, null); // to Quick
																	// access to
																	// the
																	// LayoutInflater
																	// instance
																	// that this
																	// Window
																	// retrieved
																	// from its
																	// Context.
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			

			if (multiselectflag == true) {
				holder.select.setVisibility(View.VISIBLE);
				holder.select.setOnCheckedChangeListener(null);
				holder.select.setChecked(mStarStates[position]);
				holder.select.setOnCheckedChangeListener(mStarCheckedChanceChangeListener);
			} else {
				holder.select.setVisibility(View.GONE);

			}

			holder.name.setText(items[position]);

			final String filepath = paths[position];
			File f = new File(filepath);

			String ext = getFileExtension(filepath);

			if (f.isFile() && ext.equalsIgnoreCase(".apk")) { // decide are the
																// file folder
																// or file

				if (apkicon == true) {
					// setPlaceholder(getResources().getDrawable(R.drawable.apk_file));
					holder.image.setTag(filepath);
					drawableLoader.displayImage(filepath,
							FilebrowserULTRAActivity.this, holder.image);
					// loadDrawable(f.getAbsolutePath(),holder.image);
				} else {
					holder.image.setImageResource(R.drawable.apk_file);
				}
				holder.info.setTag(filepath);

				holder.info.setText(FileUtils.formatSize(
						FilebrowserULTRAActivity.this, f.length()));

			}

			else if (f.isFile() && ext.equalsIgnoreCase(".jpg")
					|| ext.equalsIgnoreCase(".png")
					|| ext.equalsIgnoreCase(".gif")
					|| ext.equalsIgnoreCase(".jpeg")
					|| ext.equalsIgnoreCase(".tiff")) {

				// Drawable icon = getResources().getDrawable(R.drawable.image);
				// Bitmap bitmap = ((BitmapDrawable)icon ).getBitmap();
				// BitmapManager.INSTANCE.setPlaceholder(bitmap);
				holder.image.setTag(f.getAbsolutePath());
				// imageDownloader.download(filepath, holder.image);

				imageLoader.displayImage(filepath,
						FilebrowserULTRAActivity.this, holder.image);

				// BitmapManager.INSTANCE.loadBitmap(f.getAbsolutePath(),holder.image);
				holder.info.setTag(filepath);

				holder.info.setText(FileUtils.formatSize(
						FilebrowserULTRAActivity.this, f.length()));

			}

			else {
				if (f.isDirectory()) { // decide are the file folder or file
					holder.image.setTag(filepath);

					holder.image.setImageDrawable(getIcon(filepath));

				} else {
					holder.image.setTag(filepath);

					holder.image.setImageDrawable(getIcon(filepath));
				}

			}

			return (convertView);
		}

	}

	class ViewHolder {
		public TextView name = null;
		public TextView info = null;
		public CheckBox select = null;
		public ImageView image = null;
		public LinearLayout rowlayout;

		ViewHolder(View row) {
			name = (TextView) row.findViewById(R.id.label);
			name.setTypeface(Fonts.ICS);
			info = (TextView) row.findViewById(R.id.info);
			info.setTypeface(Fonts.ICS);

			image = (ImageView) row.findViewById(R.id.icon);
			select = (CheckBox) row.findViewById(R.id.select_icon);

			rowlayout = (LinearLayout) findViewById(R.id.row_layout);

		}

		void populateFrom(String s) {
			name.setText(s);
		}
	}

	class ViewHolderGrid {
		public TextView name = null;
		public TextView info = null;
		public CheckBox select = null;
		public ImageView image = null;

		ViewHolderGrid(View row) {
			name = (TextView) row.findViewById(R.id.label_grid);
			name.setTypeface(Fonts.ICS);
			image = (ImageView) row.findViewById(R.id.icon_grid);
			select = (CheckBox) row.findViewById(R.id.select_icon_grid);

		}

		void populateFrom(String s) {
			name.setText(s);
		}
	}

	class IconicGrid extends ArrayAdapter<String> {

		private ImageThreadLoader imageLoader;
		private DrawableThreadLoader drawableLoader;

		public IconicGrid() {
			super(FilebrowserULTRAActivity.this, R.layout.row, items);
			imageLoader = new ImageThreadLoader(FilebrowserULTRAActivity.this);
			drawableLoader = new DrawableThreadLoader(
					FilebrowserULTRAActivity.this);

			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolderGrid holder;

			if (convertView == null) {
				LayoutInflater inflater = getLayoutInflater(); // to instantiate
																// layout XML
																// file into its
																// corresponding
																// View objects
				convertView = inflater.inflate(R.layout.gridrow, null); // to
																		// Quick
																		// access
																		// to
																		// the
																		// LayoutInflater
																		// instance
																		// that
																		// this
																		// Window
																		// retrieved
																		// from
																		// its
																		// Context.
				holder = new ViewHolderGrid(convertView);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolderGrid) convertView.getTag();
			}

			
			if (multiselectflag == true) {
				holder.select.setVisibility(View.VISIBLE);
				holder.select.setOnCheckedChangeListener(null);
				holder.select.setChecked(mStarStates[position]);
				holder.select.setOnCheckedChangeListener(mStarCheckedChanceChangeListener1);

			} else {
				holder.select.setVisibility(View.GONE);

			}
			holder.name.setText(items[position]);

			final String filepath = paths[position];
			final File f = new File(filepath);

			String ext = getFileExtension(filepath);

			if (f.isFile() && ext.equalsIgnoreCase(".apk")) { // decide are the
																// file folder
																// or file

				// setPlaceholder(getResources().getDrawable(R.drawable.apk_file));
				holder.image.setTag(filepath);
				drawableLoader.displayImage(filepath,
						FilebrowserULTRAActivity.this, holder.image);
				// loadDrawable(f.getAbsolutePath(),holder.image);

			} else if (f.isFile() && ext.equalsIgnoreCase(".jpg")
					|| ext.equalsIgnoreCase(".png")
					|| ext.equalsIgnoreCase(".gif")
					|| ext.equalsIgnoreCase(".jpeg")
					|| ext.equalsIgnoreCase(".tiff")) {

				// Drawable icon = getResources().getDrawable(R.drawable.image);
				// Bitmap bitmap = ((BitmapDrawable)icon ).getBitmap();
				// BitmapManager.INSTANCE.setPlaceholder(bitmap);
				holder.image.setTag(filepath);

				imageLoader.displayImage(filepath,
						FilebrowserULTRAActivity.this, holder.image);

				// BitmapManager.INSTANCE.loadBitmap(f.getAbsolutePath(),holder.image);

			}

			else {
				if (f.isDirectory()) { // decide are the file folder or file
					holder.image.setImageDrawable(getIcon(filepath));
					holder.image.setTag(filepath);

				} else {
					holder.image.setTag(filepath);
					holder.image.setImageDrawable(getIcon(filepath));

				}
			}

			return (convertView);
		}

	}

	public class UltraBaseAdapter extends BaseAdapter {
		ViewHolderGrid holderGrid;
		ViewHolder holderList;
		// viewmode
		public static final int VIEWMODE_LIST = 0;
		public static final int VIEWMODE_ICON = 1;

		private Context mcontext = null;
		private List<File> mfiles = null;

		private int mViewMode = VIEWMODE_ICON;

		public UltraBaseAdapter(Context context, List<File> files) {
			mcontext = context;
			mfiles = files;
		}

		@Override
		public int getCount() {
			int msize = 0;

			if (mfiles != null)
				msize = mfiles.size();

			return msize;
		}

		public int getViewMode() {
			return mViewMode;
		}

		public void setViewMode(int ViewMode) {
			mViewMode = ViewMode;
		}

		@Override
		public File getItem(int position) {

			if ((position >= 0) && (position < this.getCount()))
				return mfiles.get(position);

			return null;
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public void notifyDataSetChanged() {

			super.notifyDataSetChanged();

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			switch (mViewMode) {
			case VIEWMODE_LIST: {
				final ViewHolder holder;
				if (convertView == null) {
					LayoutInflater inflater = getLayoutInflater(); // to
																	// instantiate
																	// layout
																	// XML file
																	// into its
																	// corresponding
																	// View
																	// objects
					convertView = inflater.inflate(R.layout.row, null); // to
																		// Quick
																		// access
																		// to
																		// the
																		// LayoutInflater
																		// instance
																		// that
																		// this
																		// Window
																		// retrieved
																		// from
																		// its
																		// Context.
					holder = new ViewHolder(convertView);
					convertView.setTag(holder);

				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				holder.select.setOnCheckedChangeListener(null);
				holder.select.setChecked(mStarStates[position]);
				holder.select
						.setOnCheckedChangeListener(mStarCheckedChanceChangeListener);

				if (multiselectflag == true) {
					holder.select.setVisibility(View.VISIBLE);
				} else {
					holder.select.setVisibility(View.GONE);

				}

				holder.name.setText(items[position]);
				holder.name.setTypeface(Fonts.ICS);

				final String filepath = paths[position];
				File f = new File(filepath);

				String ext = getFileExtension(filepath);

				if (f.isFile() && ext.equalsIgnoreCase(".apk")) { // decide are
																	// the file
																	// folder or
																	// file

					if (apkicon == true) {
						setPlaceholder(getResources().getDrawable(
								R.drawable.apk_file));
						holder.image.setTag(filepath);
						loadDrawable(f.getAbsolutePath(), holder.image);
					} else {
						holder.image.setImageResource(R.drawable.apk_file);
					}
					holder.info.setTag(filepath);

					holder.info.setText(FileUtils.formatSize(
							FilebrowserULTRAActivity.this, f.length()));

				}

				else if (f.isFile() && ext.equalsIgnoreCase(".jpg")
						|| ext.equalsIgnoreCase(".png")
						|| ext.equalsIgnoreCase(".gif")
						|| ext.equalsIgnoreCase(".jpeg")
						|| ext.equalsIgnoreCase(".tiff")) {

					Drawable icon = getResources()
							.getDrawable(R.drawable.image);
					Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
					BitmapManager.INSTANCE.setPlaceholder(bitmap);
					holder.image.setTag(f.getAbsolutePath());

					BitmapManager.INSTANCE.loadBitmap(f.getAbsolutePath(),
							holder.image);
					holder.info.setTag(filepath);

					holder.info.setText(FileUtils.formatSize(
							FilebrowserULTRAActivity.this, f.length()));

				}

				else {
					if (f.isDirectory()) { // decide are the file folder or file
						holder.image.setTag(filepath);
						holder.info.setTag(filepath);

						holder.info.setText("<DIR>");
						holder.image.setImageDrawable(getIcon(filepath));

					} else {
						holder.image.setTag(filepath);

						holder.image.setImageDrawable(getIcon(filepath));
						holder.info.setTag(filepath);

						holder.info.setText(FileUtils.formatSize(
								FilebrowserULTRAActivity.this, f.length()));
					}

				}

			}
				break;
			case VIEWMODE_ICON: {

				final ViewHolderGrid holder;

				if (convertView == null) {
					LayoutInflater inflater = getLayoutInflater(); // to
																	// instantiate
																	// layout
																	// XML file
																	// into its
																	// corresponding
																	// View
																	// objects
					convertView = inflater.inflate(R.layout.gridrow, null); // to
																			// Quick
																			// access
																			// to
																			// the
																			// LayoutInflater
																			// instance
																			// that
																			// this
																			// Window
																			// retrieved
																			// from
																			// its
																			// Context.
					holder = new ViewHolderGrid(convertView);
					convertView.setTag(holder);

				} else {
					holder = (ViewHolderGrid) convertView.getTag();
				}
				holder.select.setOnCheckedChangeListener(null);
				holder.select.setChecked(mStarStates[position]);
				holder.select
						.setOnCheckedChangeListener(mStarCheckedChanceChangeListener1);

				if (multiselectflag == true) {
					holder.select.setVisibility(View.VISIBLE);
				} else {
					holder.select.setVisibility(View.GONE);

				}
				holder.name.setText(items[position]);

				holder.name.setTypeface(Fonts.ICS);
				final String filepath = paths[position];
				final File f = new File(filepath);

				String ext = getFileExtension(filepath);

				if (f.isFile() && ext.equalsIgnoreCase(".apk")) { // decide are
																	// the file
																	// folder or
																	// file

					setPlaceholder(getResources().getDrawable(
							R.drawable.apk_file));
					holder.image.setTag(filepath);
					loadDrawable(f.getAbsolutePath(), holder.image);

				} else if (f.isFile() && ext.equalsIgnoreCase(".jpg")
						|| ext.equalsIgnoreCase(".png")
						|| ext.equalsIgnoreCase(".gif")
						|| ext.equalsIgnoreCase(".jpeg")
						|| ext.equalsIgnoreCase(".tiff")) {

					Drawable icon = getResources()
							.getDrawable(R.drawable.image);
					Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
					BitmapManager.INSTANCE.setPlaceholder(bitmap);
					holder.image.setTag(filepath);
					BitmapManager.INSTANCE.loadBitmap(f.getAbsolutePath(),
							holder.image);

				}

				else {
					if (f.isDirectory()) { // decide are the file folder or file
						holder.image.setImageDrawable(getIcon(filepath));
						holder.image.setTag(filepath);

					} else {
						holder.image.setTag(filepath);
						holder.image.setImageDrawable(getIcon(filepath));

					}
				}

			}
				break;
			default:
				break;
			}

			return convertView;
		}

	}

	private OnCheckedChangeListener mStarCheckedChanceChangeListener = new OnCheckedChangeListener() {

		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {

			mylist = (ListView) findViewById(android.R.id.list);
			final int position = mylist.getPositionForView(buttonView);
			if (position != ListView.INVALID_POSITION) {
				mStarStates[position] = isChecked;

			}

		}
	};

	private OnCheckedChangeListener mStarCheckedChanceChangeListener1 = new OnCheckedChangeListener() {

		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {

			final int position = myGrid.getPositionForView(buttonView);
			if (position != GridView.INVALID_POSITION) {
				mStarStates[position] = isChecked;
			}

		}
	};

	private class BackgroundWork extends
			AsyncTask<String, Void, ArrayList<String>> {
		private String file_name;
		private String file_path;
		private int copy_rtn;
		private ProgressDialog pr_dialog;
		private int type;
		private int isComplete = 0;

		private BackgroundWork(int type) {
			this.type = type;
		}

		/**
		 * This is done on the EDT thread. this is called before doInBackground
		 * is called
		 */
		@Override
		protected void onPreExecute() {
			Context mContext = FilebrowserULTRAActivity.this;
			switch (type) {
			case SEARCH_TYPE:

				pr_dialog = ProgressDialog.show(mContext, "Please wait",
						"Searching current file system...", true, true);
				break;

			case COPY_TYPE:
				pr_dialog = new ProgressDialog(mContext);
				pr_dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pr_dialog.setMessage("Copying Files..");
				pr_dialog.show();
				pr_dialog.setProgress(0);
				break;

			case DELETE_TYPE:
				/*
				 * pr_dialog = new ProgressDialog(mContext);
				 * pr_dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				 * pr_dialog.setMessage("Deleting Files.."); pr_dialog.show();
				 * pr_dialog.setProgress(0);
				 */
				pr_dialog = ProgressDialog.show(mContext, "Please wait",
						"Deleting files...", true, false);
				break;
			case INFO_TYPE:
				pr_dialog = ProgressDialog.show(mContext, "Please wait",
						"Fetching info...", true, false);
				break;
			case ENCRYPT_TYPE:
				pr_dialog = ProgressDialog.show(mContext, "Please wait",
						"Encryting file...", true, false);
				break;
			case DECRYPT_TYPE:
				pr_dialog = ProgressDialog.show(mContext, "Please wait",
						"Decrypting file...", true, false);
				break;

			}
		}

		/**
		 * background thread here
		 */
		@Override
		protected ArrayList<String> doInBackground(String... params) {

			switch (type) {
			case SEARCH_TYPE:
				file_name = params[0];
				ArrayList<String> found = FileUtils.searchInDirectory(
						currentdir, file_name);

				return found;

			case COPY_TYPE:
				int len1 = params.length;

				if (multiSelectData.size() > 0) {
					for (int i = 1; i < len1; i++) {
						try {

							copy_rtn = FileUtils.copyToDirectory(params[i],
									params[0]);
							isComplete++;
							pr_dialog.setProgress((isComplete * 100)
									/ multiSelectData.size());

						} catch (Exception e) {
							// TODO: handle exception
						}

						if (delete_after_copy)
							FileUtils.deleteTarget(params[i]);
					}
				} else {
					try {
						copy_rtn = FileUtils.copyToDirectory(params[0],
								params[1]);
						isComplete++;
						pr_dialog.setProgress(100 * isComplete / 1);
					} catch (Exception e) {
						// TODO: handle exception

					}

					if (delete_after_copy)
						FileUtils.deleteTarget(params[0]);
				}

				delete_after_copy = false;
				return null;

			case DELETE_TYPE:

				int len2 = params.length;
				file_path = params[0];
				if (multiSelectData.size() > 0) {
					for (int i = 0; i < len2; i++) {
						try {

							FileUtils.deleteTarget(params[i]);
							isComplete++;
							// pr_dialog.setProgress((isComplete * 100)/ len2);

						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				} else {

					FileUtils.deleteTarget(params[0]);
					isComplete++;
					pr_dialog.setProgress(100 * isComplete / 1);
				}

				return null;
			case ENCRYPT_TYPE:
				file_name = params[0];
				file_path = params[1];

				File file1 = new File(file_path);
				DesEncrypter encrypter = new DesEncrypter(mykey.secretkey);
				try {
					encrypter
							.encrypt(
									new FileInputStream(file1.getAbsoluteFile()),
									new FileOutputStream(currentdir
											+ "/"
											+ file_name
											+ getFileExtension(file1
													.getAbsolutePath())));

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block

					e.printStackTrace();
					encrypt_success = -1;

				}
				if (deleteCheck == -1) {
					FileUtils.deleteTarget(file1.getAbsolutePath());
					deleteCheck = 0;
				}

				return null;

			case DECRYPT_TYPE:

				file_name = params[0];
				file_path = params[1];
				File file11 = new File(file_path);
				DesEncrypter encrypter1 = new DesEncrypter(mykey.secretkey);
				try {
					encrypter1.decrypt(
							new FileInputStream(file11.getAbsoluteFile()),
							new FileOutputStream(
									currentdir
											+ "/"
											+ file_name
											+ getFileExtension(file11
													.getAbsolutePath())));

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block

					e.printStackTrace();

					decrypt_success = -1;
				}

				if (deleteCheck == -1) {
					FileUtils.deleteTarget(file11.getAbsolutePath());
					deleteCheck = 0;
				}

				return null;

			case INFO_TYPE:

				StatFs stat = new StatFs(currentdir);

				return null;

			}
			return null;

		}

		/**
		 * This is called when the background thread is finished. Like
		 * onPreExecute, anything here will be done on the EDT thread.
		 */
		@Override
		protected void onPostExecute(final ArrayList<String> file) {
			switch (type) {

			case SEARCH_TYPE:
				getSearchResults(file, file_name);

				pr_dialog.dismiss();
				break;

			case COPY_TYPE:

				if (multiSelectData != null && !multiSelectData.isEmpty()) {
					multiselectflag = false;
					multiSelectData.clear();

				}

				if (copy_rtn == 0)
					showMessage("File successfully copied and pasted");

				else
					showMessage("copy operation failed");

				refreshList();
				pr_dialog.dismiss();

				break;

			case DELETE_TYPE:

				if (multiSelectData != null && !multiSelectData.isEmpty()) {
					multiselectflag = false;
				}
				Toast.makeText(FilebrowserULTRAActivity.this,
						" Delete successfull !", Toast.LENGTH_SHORT).show();
				refreshList();
				pr_dialog.dismiss();

				break;
			case ENCRYPT_TYPE:
				if (encrypt_success == -1) {
					Toast.makeText(FilebrowserULTRAActivity.this,
							"coudn't encrypt !", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(FilebrowserULTRAActivity.this,
							"Encrytion successfull !", Toast.LENGTH_SHORT)
							.show();

					refreshList();
				}
				pr_dialog.dismiss();
				break;
			case DECRYPT_TYPE:
				if (decrypt_success == -1) {
					Toast.makeText(FilebrowserULTRAActivity.this,
							"coudn't decrypt !", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(FilebrowserULTRAActivity.this,
							"Decryption successfull !", Toast.LENGTH_SHORT)
							.show();

					refreshList();
				}
				pr_dialog.dismiss();
				break;

			case INFO_TYPE:
				pr_dialog.dismiss();
				File currentfile = new File(currentdir);
				getProperties(currentfile);

				break;

			}

		}
	}

	private Comparator<? super String> getSortMode(int sortmode) {

		switch (sortmode) {
		case 1:

			return name;

		case 2:

			return modified;

		case 3:

			return type;

		default:
			return type;

		}

	}

	public static class Fonts {
		public static Typeface SONY;
		public static Typeface ICS;
		public static Typeface DEFAULT;

	}

	private void initializeTypefaces() {
		Fonts.DEFAULT = Typeface.DEFAULT;
		Fonts.SONY = Typeface.createFromAsset(getAssets(),
				"fonts/SonySketch.ttf");
		Fonts.ICS = Typeface.createFromAsset(getAssets(), "fonts/roboto.ttf");

	}

	public static class mykey {
		public static SecretKey secretkey;
	}

	private void initializekey() {

		encodedkey = permkey.getBytes();
		mykey.secretkey = new SecretKeySpec(encodedkey, 0, encodedkey.length,
				"DES");

	}

	public static class DrawableManager {
		private static ConcurrentMap<String, Drawable> cache;
		private static ExecutorService pool;

		private static Map<ImageView, String> imageViews = Collections
				.synchronizedMap(new ConcurrentHashMap<ImageView, String>());
		private static Drawable placeholder;

	}

	private void initializeDrawable() {
		DrawableManager.cache = new ConcurrentHashMap<String, Drawable>();
		DrawableManager.pool = Executors.newFixedThreadPool(5);

	}

	public static void setPlaceholder(Drawable drawable) {
		DrawableManager.placeholder = drawable;
	}

	public Drawable getDrawableFromCache(String url) {
		if (DrawableManager.cache.containsKey(url)) {
			return DrawableManager.cache.get(url);
		}

		return null;
	}

	public void queueJob(final String url, final ImageView imageView) {
		/* Create handler in UI thread. */
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String tag = DrawableManager.imageViews.get(imageView);
				if (tag != null && tag.equals(url)) {
					if (msg.obj != null) {
						imageView.setImageDrawable((Drawable) msg.obj);
					} else {
						imageView.setImageDrawable(DrawableManager.placeholder);
						Log.d(null, "fail " + url);
					}
				}
			}
		};

		DrawableManager.pool.submit(new Runnable() {

			public void run() {
				final Drawable drawable = getapkicon(url);
				Message message = Message.obtain();
				message.obj = drawable;
				Log.d(null, "Item downloaded: " + url);

				handler.sendMessage(message);
			}
		});
	}

	public void loadDrawable(final String url, final ImageView imageView) {
		DrawableManager.imageViews.put(imageView, url);
		Drawable drawable = getDrawableFromCache(url);

		// check in UI thread, so no concurrency issues
		if (drawable != null) {
			Log.d(null, "Item loaded from cache: " + url);
			imageView.setImageDrawable(drawable);
		} else {
			imageView.setImageDrawable(DrawableManager.placeholder);
			queueJob(url, imageView);
		}
	}

	public Drawable getapkicon(String url) {

		Drawable icon;
		icon = getDrawableFromCache(url);
		if (icon != null) {
			return icon;
		}

		else {
			icon = new FileUtils(FilebrowserULTRAActivity.this).getapkicon(url);
			DrawableManager.cache.put(url, icon);
			return icon;

		}

	}

	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		// TODO Auto-generated method stub
		if (// When the user chooses to show/hide hidden files, update the list
			// to correspond with the user's choice
		PreferenceActivity.PREFS_DISPLAYHIDDENFILES.equals(key)
				// When the user changes the sortBy settings, update the list
				|| PreferenceActivity.PREFS_SORTBY.equals(key)
				|| PreferenceActivity.PREFS_ASCENDING.equals(key)) {

			refreshList();
		}
	}

}