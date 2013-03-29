package com.mirrorlabs.filebrowser;






import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;



import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.mirrorlabs.filebrowser.FilebrowserULTRAActivity.Fonts;
import com.mirrorlabs.menupopup.MenuItem;
import com.mirrorlabs.menupopup.PopupMenu;
import com.mirrorlabs.menupopup.PopupMenu.OnItemSelectedListener;

import com.mirrorlabs.quickaction.ActionItem;
import com.mirrorlabs.quickaction.QuickAction;




public class BackupManager extends ListActivity {
	
    private static final String STAR_STATES = "mylist:star_states";
    private boolean[] mStarStates=null;

	private static String BACKUP_LOC;
	private static final int SET_PROGRESS = 0x00;
	private static final int FINISH_PROGRESS = 0x01;
	private static final int FLAG_UPDATED_SYS_APP = 0x80;
	private static final String[] Q = new String[]{"B", "KB", "MB", "GB", "TB", "PB", "EB"};

	
	private static ArrayList<ApplicationInfo> mAppList=null;
	private static TextView mAppLabel=null;
	private static ListView mylist=null;
	private static PackageManager mPackMag=null;
	private static ProgressDialog mDialog=null;
	private static PackageInfo  appinfo=null;
	private static ApplicationInfo  apkinfo=null;
	
	private static final int ID_LAUNCH = 1;
	private static final int ID_MANAGE = 2;
	private static final int ID_UNINSTALL = 3;
	private static final int ID_BACKUP = 4;
	private static final int ID_SEND = 5;
	private static final int ID_MARKET = 6;
	private static final int ID_SHORTCUT=7;
	
	private static final String root =Environment.getExternalStorageDirectory().getPath();
	private static ArrayList<ApplicationInfo> multiSelectData=null;



	
	/*
	 * Our handler object that will update the GUI from 
	 * our background thread. 
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			
			switch(msg.what) {
				case SET_PROGRESS:
					mDialog.setMessage((String)msg.obj);
					break;
				case FINISH_PROGRESS:
					mDialog.cancel();
					Toast.makeText(BackupManager.this, 
								   "Applications have been backed up", 
								   Toast.LENGTH_SHORT).show();
					break;
			}
		}
	};

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        BACKUP_LOC = getPrefs.getString("backupFolder",root+"/Backup/");
		
		initializeDrawbale();
		setContentView(R.layout.appbackup_layout);
		
        //Toast.makeText(BackupManager.this," allocated size  = " + getAsString(Debug.getNativeHeapAllocatedSize()), 1).show();      
		
		
		mAppLabel = (TextView)findViewById(R.id.backup_label);
        mAppLabel.setTypeface(Fonts.ICS);
        
		Button backup_button = (Button)findViewById(R.id.backup_button_all);
	    backup_button.setTypeface(Fonts.ICS);
		Button cancel_button = (Button)findViewById(R.id.cancel_button);
		cancel_button.setTypeface(Fonts.ICS);
		
		Button select_button =(Button)findViewById(R.id.selectButton);

		
       
		
		mAppList = new ArrayList<ApplicationInfo>();
		multiSelectData = new ArrayList<ApplicationInfo>();

		mPackMag = getPackageManager();
		
		
		
		    //setup ActionBar
	   final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
	         actionBar.setTitle("Application Manager");
	         actionBar.setHomeAction(new IntentAction(this, FilebrowserULTRAActivity.createIntent(this), R.drawable.ic_title_home_default));
	         actionBar.setDisplayHomeAsUpEnabled(true);
	       
	        final AsyncTask loadAppsTask = new AsyncTask<String[], Long, Long>(){
	 			
				  @Override
					protected Long doInBackground(String[]... params) {
						get_downloaded_apps();
						return null;
					}
					protected void onPreExecute() {
			    		
			    		 actionBar.setProgressBarVisibility(View.VISIBLE);
			    		 mAppLabel.setText("Listing Apps...");	
					}
					@Override
					protected void onProgressUpdate(Long... updatedSize){
						
					}
					
					@Override
					protected void onPostExecute(Long result){
						
						actionBar.setProgressBarVisibility(View.GONE);
						 
						 setListAdapter(new TableView());
						 
						 if (savedInstanceState != null) {
					            mStarStates = savedInstanceState.getBooleanArray(STAR_STATES);
					        } else {
					            mStarStates = new boolean[mAppList.size()];
					        }
						 mAppLabel.setText("You have " +mAppList.size() + " downloaded apps");
						
					}

					
					
					
	     	}.execute();
	        
	        
	        
	        
	        mylist = getListView();
	        mylist.setFastScrollEnabled(true);
	        mylist.setOnItemLongClickListener(new OnItemLongClickListener() {
                 
	    		
				public boolean onItemLongClick(AdapterView<?> av, View v,
						final int position , long id) {
					// TODO Auto-generated method stub
					multiSelectData.clear();
					    for(int i=0;i<mAppList.size();i++){
					    	if(mStarStates[i]){
					    		multiSelectData.add(mAppList.get(i));
					    	}
					    	else if(!mStarStates[i]){
					    		multiSelectData.remove(mAppList.get(i));
					    	}
					    }
					    getoptions(position,v);
				    return false;
				} 
	    	}); 	
	}
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray(STAR_STATES, mStarStates);
    }
	
	 @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	    	 
	         
	    	  
	    	 if ((keyCode == KeyEvent.KEYCODE_BACK)){ //Back key pressed
	                  
	    		     initializequit();
	    		     finish();
	         	     return true;
	        }
	    	 
	    	 
	       
	        return super.onKeyDown(keyCode, event);
	    }
	    
	 
	public void getOperations(final ApplicationInfo appinfo){
		final CharSequence[] multioperations = {"Backup","Send","Uninstall"};
		final CharSequence[] operations = {"Launch","Manage","Backup","Send","Uninstall","Market Link"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(BackupManager.this);
        

	    if(multiSelectData.size()>0 & multiSelectData!=null){
	    	builder.setTitle("Selected " +multiSelectData.size()+" items");
	    	 builder.setItems(multioperations, new DialogInterface.OnClickListener() {
        		    public void onClick(DialogInterface dInterface, int item) {
        	    		
        		    	switch(item){
        		    	case 0 :
        		    		mDialog = ProgressDialog.show(BackupManager.this, 
  								  "Backing up applications",
  								  "", true, false);

  			             Thread all = new Thread(new BackgroundWork(multiSelectData));
  			              all.start();
        		    		
        		    		break;
        		    	case 1:
        		    		
        		    		ArrayList<Uri> uris = new ArrayList<Uri>();
           					int length = multiSelectData.size();
           					Intent send_intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
           					send_intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

           	    			send_intent.setType("image/jpeg");
           	    			for(int j = 0; j < length; j++) {
           	    				
           	    				File file = new File( multiSelectData.get(j).sourceDir).getAbsoluteFile();
           	    				uris.add(Uri.fromFile(file));
           	    			}
           	    			send_intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                            startActivity(Intent.createChooser(send_intent, "Send via.."));
                            
                            
        		    		
        		    		break;
        		    	case 2:
        		    		
        		    		int length1 = multiSelectData.size();
           					Intent delete_intent = new Intent(Intent.ACTION_DELETE);

           	    			for(int j = 0; j < length1; j++) {
           	    				delete_intent.setData(Uri.parse("package:"+multiSelectData.get(j).packageName));
        						startActivity(delete_intent);
           	    			}
           					refreshList();

        		    		break;
        		    	}
        		    }
	    	 }); 
	    	  AlertDialog alert = builder.create();
	      		
	      		alert.show();
	    }
	    else{


			final String appname = appinfo.loadLabel(mPackMag).toString();
	    	builder.setTitle(appname);
	    	builder.setIcon(getAppIcon(appinfo.packageName));
	    	 builder.setItems(operations, new DialogInterface.OnClickListener() {
       		    public void onClick(DialogInterface dInterface, int item) {
       	    		
       		    	switch(item){
       		    	case 0 :
       		    		Intent i = mPackMag.getLaunchIntentForPackage(appinfo.packageName);
						startActivity(i);
			            
       		    		break;
       		    	case 1:
       		    		final int apiLevel = Build.VERSION.SDK_INT;
						Intent intent = new Intent();
						if (apiLevel >= 9) {
						    //TODO get working on gb
						    //Toast.makeText(SDMove.this, "Gingerbread Not Currently Supported", Toast.LENGTH_LONG).show();
						    startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
						                             Uri.parse("package:" + appinfo.packageName)));
						} else {
						    final String appPkgName = (apiLevel == 8 ? "pkg" : "com.android.settings.ApplicationPkgName");
						    intent.setAction(Intent.ACTION_VIEW);
						    intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
						    intent.putExtra(appPkgName, appinfo.packageName);
						    startActivity(intent);
						}
			        	
       		    		break;
       		    	case 2:
       		    		backupApp(appinfo.packageName);
						Toast.makeText(BackupManager.this, "Backup complete", Toast.LENGTH_SHORT).show();
       		    		
       		    		break;
       		    	case 3:
       		    		try{
							ApplicationInfo info = mPackMag.getApplicationInfo(appinfo.packageName, 0);
							String source_dir = info.sourceDir;
							File file = new File(source_dir);	
							Uri uri11 = Uri.fromFile(file.getAbsoluteFile());
							
				    		Intent infointent = new Intent(Intent.ACTION_SEND);
				    	    
				    	    infointent.setType("application/zip");
				    	    infointent.putExtra(Intent.EXTRA_STREAM, uri11);
				    		startActivity(Intent.createChooser(infointent, "Send"));
							}catch (Exception e) {
								// TODO: handle exception
								
								Toast.makeText(BackupManager.this, "unable to send !", Toast.LENGTH_SHORT).show();
							} 
       		    		break;
       		    	case 4:
       		    		Intent i1 = new Intent(Intent.ACTION_DELETE);
						i1.setData(Uri.parse("package:"+appinfo.packageName));
						startActivity(i1);
						refreshList();

						break;
       		    	case 5:
       		    		Intent intent1 = new Intent(Intent.ACTION_VIEW);
	                	 intent1.setData(Uri.parse("market://details?id="+appinfo.packageName));
	                	 startActivity(intent1);
       		    		
       		    		break;
       		    	}
       		    }
	    	 }); 
	    	  AlertDialog alert = builder.create();
	      		
	      	alert.show();
       
	    }
        
	}
	
	public void getoptions(final int position,View view){
		
		final ApplicationInfo info = mAppList.get(position);
		final String appname = info.loadLabel(mPackMag).toString();
	    PopupMenu menu = new PopupMenu(BackupManager.this);
	    
	    if(multiSelectData.size()>0 & multiSelectData!=null){
	    	 menu.setHeaderTitle(multiSelectData.size() + " apps selected");
	    }
	    else{
        menu.setHeaderTitle(appname);
        
		menu.setHeaderIcon(getAppIcon(info.packageName));
	    }
	
        // Set Listener
        menu.setOnItemSelectedListener(new OnItemSelectedListener() {
			
			public void onItemSelected(MenuItem item) {
				// TODO Auto-generated method stub
				 switch (item.getItemId()) {
			        case ID_LAUNCH:
			        	Intent i = mPackMag.getLaunchIntentForPackage(mAppList.get(position).packageName);
						startActivity(i);
			            break;

			        case ID_BACKUP:
			        	if(multiSelectData.size()>0 && multiSelectData!=null){
			        	mDialog = ProgressDialog.show(BackupManager.this, 
								  "Backing up applications",
								  "", true, false);

			            Thread all = new Thread(new BackgroundWork(multiSelectData));
			            all.start();
			        	}else{
			        	
			        	backupApp(mAppList.get(position).packageName);
						Toast.makeText(BackupManager.this, "Backup complete", Toast.LENGTH_SHORT).show();
			        	}
			            break;
			        case ID_SHORTCUT:
			        	   

			        	break;
			        case ID_MANAGE:
			        	final int apiLevel = Build.VERSION.SDK_INT;
						Intent intent = new Intent();
						if (apiLevel >= 9) {
						    //TODO get working on gb
						    //Toast.makeText(SDMove.this, "Gingerbread Not Currently Supported", Toast.LENGTH_LONG).show();
						    startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
						                             Uri.parse("package:" + mAppList.get(position).packageName)));
						} else {
						    final String appPkgName = (apiLevel == 8 ? "pkg" : "com.android.settings.ApplicationPkgName");
						    intent.setAction(Intent.ACTION_VIEW);
						    intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
						    intent.putExtra(appPkgName, mAppList.get(position).packageName);
						    startActivity(intent);
						}
						
			            break;
			        case ID_UNINSTALL:
			        	if(multiSelectData.size()>0 && multiSelectData!=null){
           					int length = multiSelectData.size();
           					Intent delete_intent = new Intent(Intent.ACTION_DELETE);

           	    			for(int j = 0; j < length; j++) {
           	    				delete_intent.setData(Uri.parse("package:"+multiSelectData.get(j).packageName));
        						startActivity(delete_intent);
           	    				
           	    			}
           	    			
			        	}else{
			        	Intent i1 = new Intent(Intent.ACTION_DELETE);
						i1.setData(Uri.parse("package:"+mAppList.get(position).packageName));
						startActivity(i1);
			        	}
			        	
			      
                        
			        	break;
			        case ID_MARKET:
			        	if(multiSelectData.size()>0 &&multiSelectData!=null){
			        		
			        		int length = multiSelectData.size();
			        		Intent intent1 = new Intent(Intent.ACTION_VIEW);
           	    			for(int j = 0; j < length; j++) {
           	    				
       	                	 intent1.setData(Uri.parse("market://details?id="+multiSelectData.get(j).packageName));
       	                	 startActivity(intent1);
           	    			}
			        	}
			        	else{
			        	 Intent intent1 = new Intent(Intent.ACTION_VIEW);
	                	 intent1.setData(Uri.parse("market://details?id="+mAppList.get(position).packageName));
	                	 startActivity(intent1);
			        	}
			        	break;
			        case ID_SEND:
			        	
			        	if(multiSelectData.size()>0 && multiSelectData!=null){
			        		
			        		ArrayList<Uri> uris = new ArrayList<Uri>();
           					int length = multiSelectData.size();
           					Intent send_intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
           					send_intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

           	    			send_intent.setType("image/jpeg");
           	    			for(int j = 0; j < length; j++) {
           	    				
           	    				File file = new File( multiSelectData.get(j).sourceDir).getAbsoluteFile();
           	    				uris.add(Uri.fromFile(file));
           	    			}
           	    			send_intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                            startActivity(Intent.createChooser(send_intent, "Send via.."));
			        		
			        	}
			        	else{
			        	try{
							ApplicationInfo info = mPackMag.getApplicationInfo(mAppList.get(position).packageName, 0);
							String source_dir = info.sourceDir;
							File file = new File(source_dir);	
							Uri uri11 = Uri.fromFile(file.getAbsoluteFile());
							
				    		Intent infointent = new Intent(Intent.ACTION_SEND);
				    	    
				    	    infointent.setType("application/zip");
				    	    infointent.putExtra(Intent.EXTRA_STREAM, uri11);
				    		startActivity(Intent.createChooser(infointent, "Send"));
							}catch (Exception e) {
								// TODO: handle exception
								
								Toast.makeText(BackupManager.this, "unable to send !", Toast.LENGTH_SHORT).show();
							} 
			        	}
			        	
			        	break;

			        }
			}
		});
        // Add Menu (Android menu like style)
        if(multiSelectData.size()>0 && multiSelectData!=null){
        	
        	menu.add(ID_BACKUP, R.string.backup).setIcon(
                    getResources().getDrawable(R.drawable.backup1));
            menu.add(ID_SEND, R.string.send).setIcon(
                    getResources().getDrawable(R.drawable.upload48));
            menu.add(ID_UNINSTALL, R.string.uninstall).setIcon(
                    getResources().getDrawable(R.drawable.uninstall));
            menu.add(ID_MARKET, R.string.market).setIcon(
                    getResources().getDrawable(R.drawable.market_48));
            
            menu.show(view);
        	
        }
        else{
        menu.add(ID_LAUNCH, R.string.launch).setIcon(
                getResources().getDrawable(R.drawable.install));
        menu.add(ID_MANAGE, R.string.manage).setIcon(
                getResources().getDrawable(R.drawable.advancedsettings));
        menu.add(ID_BACKUP, R.string.backup).setIcon(
                getResources().getDrawable(R.drawable.backup1));
        menu.add(ID_SEND, R.string.send).setIcon(
                getResources().getDrawable(R.drawable.upload48));
        menu.add(ID_UNINSTALL, R.string.uninstall).setIcon(
                getResources().getDrawable(R.drawable.uninstall));
        menu.add(ID_MARKET, R.string.market).setIcon(
                getResources().getDrawable(R.drawable.market_48));
       
        
        menu.show(view);
        }
	}
	
	
	 @SuppressWarnings("unused")
	private Intent createShareIntent() {
	        final Intent intent = new Intent(Intent.ACTION_SEND);
	        intent.setType("text/plain");
	        intent.putExtra(Intent.EXTRA_TEXT, "Shared from the ActionBar widget.");
	        return Intent.createChooser(intent, "Share");
	    }

	
	
	@Override
		protected void onPause() {
			
	        super.onPause();
	        
	        
	             
		}
	
	@Override
   	protected void onStop() {
           super.onStop();
           
           
         	}
    
	 @Override
	   	protected void onDestroy() {
 		       finish();

	           super.onDestroy();
	           System.gc();
	         
	         	}
	    
	    @Override
		protected void onResume() {
			
			super.onResume();
			
		
		}
	    
	    
	   public void refreshList(){
		   mAppList.clear();
		   get_downloaded_apps();
		   setListAdapter(new TableView());
		   
	   }
	   
	
	public void onClick(View view){
		switch (view.getId()) {
		case R.id.cancel_button :
			finish();
			break;
		case R.id.selectButton:
        	Button superButton = (Button)findViewById(R.id.selectButton);

			if(superButton.getText().toString().equals("Select all")){
			for (int i = 0 ; i <mAppList.size()  ;i ++){
				mStarStates[i]=true;
				multiSelectData.add(mAppList.get(i));
				
			}
			refreshList();
			superButton.setText("Unselect");
			}else{
				
				for (int i = 0 ; i < mAppList.size() ;i ++){
    				mStarStates[i]=false;
    				multiSelectData.remove(mAppList.get(i));


    			}
				refreshList();
         superButton.setText("Select all");

				
			}
			

			break;

		case R.id.backup_button_all:
		/*	mDialog = ProgressDialog.show(BackupManager.this, 
					  "Backing up applications",
					  "", true, false);

            Thread all = new Thread(new BackgroundWork(mAppList));
            all.start();*/
			multiSelectData.clear();
		    for(int i=0;i<mAppList.size();i++){
		    	if(mStarStates[i]){
		    		multiSelectData.add(mAppList.get(i));
		    	}
		    	else if(!mStarStates[i]){
		    		multiSelectData.remove(mAppList.get(i));
		    	}
		    }
			if(multiSelectData.size()>0 && multiSelectData!=null){
	        	mDialog = ProgressDialog.show(BackupManager.this, 
						  "Backing up applications",
						  "", true, false);

	            Thread all = new Thread(new BackgroundWork(multiSelectData));
	            all.start();
	        	}else{
	        	Toast.makeText(BackupManager.this, "No Apps Selected !", Toast.LENGTH_SHORT).show();
	        	}
			break;
		}
	}
	
	 @Override
	    protected void onListItemClick(ListView l, View v, final int position, long id){
		   ActionItem addItem 		= new ActionItem(ID_LAUNCH, "Launch", getResources().getDrawable(R.drawable.install));
			ActionItem acceptItem 	= new ActionItem(ID_MANAGE, "Manage", getResources().getDrawable(R.drawable.advancedsettings));
	        ActionItem uninstallItem 	= new ActionItem(ID_UNINSTALL, "Uninstall", getResources().getDrawable(R.drawable.uninstall));
	        ActionItem sendItem 	= new ActionItem(ID_SEND, "Send", getResources().getDrawable(R.drawable.upload48));
	        ActionItem backupItem 	= new ActionItem(ID_BACKUP, "Backup", getResources().getDrawable(R.drawable.backup1));
	        ActionItem marketItem 	= new ActionItem(ID_MARKET, "Market link", getResources().getDrawable(R.drawable.market_48));
   
		       
	       
	        final QuickAction mQuickAction 	= new QuickAction(this);
			
			mQuickAction.addActionItem(addItem);
			mQuickAction.addActionItem(acceptItem);
			mQuickAction.addActionItem(uninstallItem);
			mQuickAction.addActionItem(backupItem);
			mQuickAction.addActionItem(sendItem);
			mQuickAction.addActionItem(marketItem);

			
			
			mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
				public void onItemClick(QuickAction quickAction, int pos, int actionId) {
					@SuppressWarnings("unused")
					ActionItem actionItem = quickAction.getActionItem(pos);
					
					if (actionId == ID_LAUNCH) {
						Intent i = mPackMag.getLaunchIntentForPackage(mAppList.get(position).packageName);
						startActivity(i);
						
	                 } else if(actionId==ID_UNINSTALL){
						Intent i = new Intent(Intent.ACTION_DELETE);
						i.setData(Uri.parse("package:"+mAppList.get(position).packageName));
						startActivity(i);
						refreshList();

						
						
                    }
	                 else if(actionId==ID_MARKET){
	                	 Intent intent = new Intent(Intent.ACTION_VIEW);
	                	 intent.setData(Uri.parse("market://details?id="+mAppList.get(position).packageName));
	                	 startActivity(intent);
	                 }
					else if(actionId==ID_MANAGE)
					{
						final int apiLevel = Build.VERSION.SDK_INT;
						Intent intent = new Intent();
						if (apiLevel >= 9) {
						    //TODO get working on gb
						    //Toast.makeText(SDMove.this, "Gingerbread Not Currently Supported", Toast.LENGTH_LONG).show();
						    startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
						                             Uri.parse("package:" + mAppList.get(position).packageName)));
						} else {
						    final String appPkgName = (apiLevel == 8 ? "pkg" : "com.android.settings.ApplicationPkgName");
						    intent.setAction(Intent.ACTION_VIEW);
						    intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
						    intent.putExtra(appPkgName, mAppList.get(position).packageName);
						    startActivity(intent);
						}
						
						refreshList();

					}
					
					else if(actionId==ID_BACKUP){
						
						backupApp(mAppList.get(position).packageName);
						Toast.makeText(BackupManager.this, "Backup complete", Toast.LENGTH_SHORT).show();
					}
					else if (actionId==ID_SEND){
						
						
						
						try{
						ApplicationInfo info = mPackMag.getApplicationInfo(mAppList.get(position).packageName, 0);
						String source_dir = info.sourceDir;
						File file = new File(source_dir);	
						Uri uri11 = Uri.fromFile(file.getAbsoluteFile());
						
			    		Intent i1 = new Intent(Intent.ACTION_SEND);
			    	    
			    	    i1.setType("application/zip");
			    	    i1.putExtra(Intent.EXTRA_STREAM, uri11);
			    		startActivity(Intent.createChooser(i1, "Send"));
						}catch (Exception e) {
							// TODO: handle exception
							
							Toast.makeText(BackupManager.this, "backup doesn't exist !", Toast.LENGTH_SHORT).show();
						} 
					}
					
				}
			});
			
			mQuickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
				public void onDismiss() {
					
				}
			});
		   mQuickAction.show(v); 
		 
	 }
	 
	 
	private void backupApp(String pkgname){
		
		try {
			   ApplicationInfo info = mPackMag.getApplicationInfo(pkgname, 0);
			   String source_dir = info.sourceDir;
			   
		
			   String out_file = info.loadLabel(mPackMag).toString()+".apk";
			   BufferedInputStream mBuffIn;
			   BufferedOutputStream mBuffOut;
			
			   int read = 0;
			   File mDir = new File(BACKUP_LOC);
			   byte[] mData;
			
			   final int BUFFER = 256;
			   mData =  new byte[BUFFER];
			
			/*create dir if needed*/
			  File d = new File(BACKUP_LOC);
			  if(!d.exists()) {
				d.mkdir();
				
				//then create this directory
				mDir.mkdir();
				
			  } else {
				if(!mDir.exists())
					mDir.mkdir();
			  }
			
			try {
				mBuffIn = new BufferedInputStream(new FileInputStream(source_dir));
				mBuffOut = new BufferedOutputStream(new FileOutputStream(BACKUP_LOC + out_file));
				
				while((read = mBuffIn.read(mData, 0, BUFFER)) != -1)
					mBuffOut.write(mData, 0, read);
				
				mBuffOut.flush();
				mBuffIn.close();
				mBuffOut.close();
				
					
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				Toast.makeText(BackupManager.this, "Unable to backup", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(BackupManager.this, "Unable to backup", Toast.LENGTH_SHORT).show();
			}
		

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(BackupManager.this, "Unable to backup", Toast.LENGTH_SHORT).show();
		}
		
		
	}
	 
	
	 
	 
	private void get_downloaded_apps() {
		List<ApplicationInfo> all_apps = mPackMag.getInstalledApplications(
											PackageManager.GET_UNINSTALLED_PACKAGES);
		
		for(ApplicationInfo appInfo : all_apps) {
			if((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && 
			   (appInfo.flags & FLAG_UPDATED_SYS_APP) == 0 && 
			   appInfo.flags != 0)
				
				mAppList.add(appInfo);
			    
		}
		
		
	}


	/*
	 * This private inner class will perform the backup of applications
	 * on a background thread, while updating the user via a message being
	 * sent to our handler object.
	 */
	private class BackgroundWork implements Runnable {
		private static final int BUFFER = 256;
		
		private ArrayList<ApplicationInfo> mDataSource;
		private File mDir = new File(BACKUP_LOC);
		private byte[] mData;
		
		public BackgroundWork(ArrayList<ApplicationInfo> data)  {
			mDataSource = data;
			mData =  new byte[BUFFER];
						
			/*create dir if needed*/
			File d = new File(BACKUP_LOC);
			if(!d.exists()) {
				d.mkdir();
				
				//then create this directory
				mDir.mkdir();
				
			} else {
				if(!mDir.exists())
					mDir.mkdir();
			}
		}

		public void run() {
			BufferedInputStream mBuffIn;
			BufferedOutputStream mBuffOut;
			Message msg;
			int len = mDataSource.size();
			int read = 0;
			
			
			for(int i = 0; i < len; i++) {
				ApplicationInfo info = mDataSource.get(i);
				String source_dir = info.sourceDir;
				String out_file = info.loadLabel(mPackMag).toString()+".apk";
				try {
					mBuffIn = new BufferedInputStream(new FileInputStream(source_dir));
					mBuffOut = new BufferedOutputStream(new FileOutputStream(BACKUP_LOC + out_file));
					
					while((read = mBuffIn.read(mData, 0, BUFFER)) != -1)
						mBuffOut.write(mData, 0, read);
					
					mBuffOut.flush();
					mBuffIn.close();
					mBuffOut.close();
					
					msg = new Message();
					msg.what = SET_PROGRESS;
					msg.obj = i + " out of " + len + " apps backed up";
					mHandler.sendMessage(msg);
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(quit.mback==-1)
					break;
				
			
			}
			
			
			mHandler.sendEmptyMessage(FINISH_PROGRESS);
		}
	}
	
	
	
	
	
	
       
        
        public String getAsString(long bytes)
        {
            for (int i = 6; i > 0; i--)
            {
                double step = Math.pow(1024, i);
                if (bytes > step) return String.format("%3.1f %s", bytes / step, Q[i]);
            }
            return Long.toString(bytes);
        }
        
        class ViewHolder {
   		 public ImageView image=null;
   		 public CheckBox select=null;
   		 public TextView name=null;
   		 public TextView version =null;
   		 public TextView size=null;
   	      ViewHolder(View row){
   	    	  name = (TextView)row.findViewById(R.id.app_name);
   	    	  select =(CheckBox)row.findViewById(R.id.select_icon);
   	          name.setTypeface(Fonts.ICS);
   	          version = (TextView)row.findViewById(R.id.version);
   	          version.setTypeface(Fonts.ICS);
   	          size = (TextView)row.findViewById(R.id.installdate);
   	          size.setTypeface(Fonts.ICS);
   	          image = (ImageView)row.findViewById(R.id.icon);
   	    	
   	      }
   	      void populateFrom(String s)
   	      {
   	      name.setText(s);
   	      }
   		
   	}   
	
	private  class  TableView extends ArrayAdapter<ApplicationInfo> {
		
		private TableView() {
			super(BackupManager.this, R.layout.app_row, mAppList);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			ApplicationInfo info = mAppList.get(position);
			String appname = info.loadLabel(mPackMag).toString();
			
			
			try {
				  apkinfo= mPackMag.getApplicationInfo(mAppList.get(position).packageName, 0);
				
				} catch (NameNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			final String source_dir = apkinfo.sourceDir;
		    File  apkfile = new File(source_dir);
			final long apksize = apkfile.length();
			String apk_size =getAsString(apksize);
			
			
			
			if(convertView == null) {
				LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.app_row,null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
				
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
		    try {
				appinfo =mPackMag.getPackageInfo(info.packageName, 0);
			    
			} catch (NameNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		   
		    String version=appinfo.versionName;
	
			holder.select.setOnCheckedChangeListener(null);
			holder.select.setChecked(mStarStates[position]);
            holder.select.setOnCheckedChangeListener(mStarCheckedChanceChangeListener);
		   
			holder.name.setText(appname);
			holder.version.setText("version "+version);
			holder.size.setText(String.valueOf(apk_size));
			
			
			//this should not throw the exception
			
				holder.image.setImageDrawable(getAppIcon(info.packageName));
			
			return convertView;
		}
	}
        
	  private OnCheckedChangeListener mStarCheckedChanceChangeListener = new OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            final int position = getListView().getPositionForView(buttonView);
            if (position != ListView.INVALID_POSITION) {
                mStarStates[position] = isChecked;
            }
         }
      };
	
       	    
	    public static class quit{
	        public static int mback=0;
	    }

	    private void initializequit(){
	        quit.mback   = -1;

	    }
	    
	    
	    public static class AppIconManager{
	        private static ConcurrentHashMap<String, Drawable> cache;
	        }
	        
	        	
	    	private void initializeDrawbale(){
	      	  AppIconManager.cache = new ConcurrentHashMap<String, Drawable>();  
	      	  }

	        public Drawable getDrawableFromCache(String url) {  
	            if (AppIconManager.cache.containsKey(url)) {  
	                return AppIconManager.cache.get(url); 
	            }  
	      
	            return null;  
	        }  
	        
	        public Drawable getAppIcon(String packagename){
	        	Drawable drawable;
	        	drawable = getDrawableFromCache(packagename);
	        	if(drawable!=null){
	        		return drawable;
	        	}
	        	else{
				try {
					
					drawable = mPackMag.getApplicationIcon(packagename);
					AppIconManager.cache.put(packagename, drawable);
				    
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					return getResources().getDrawable(R.drawable.apk_file);
					
				}
	        	return drawable;
	        	}
	        }

	
	
}
