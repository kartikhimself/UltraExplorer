package com.mirrorlabs.filebrowser;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;

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
import android.widget.EditText;
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





public class ProcessManager extends ListActivity {
	private final int CONVERT = 1024;
	
	private static final String STAR_STATES = "mylist:star_states";
	private boolean[] mStarStates=null;
	
	private static final String[] Q = new String[]{"B", "KB", "MB", "GB", "T", "P", "E"};

	
	private static PackageManager pk;
	private static List<RunningAppProcessInfo> display_process;
	private static ActivityManager activity_man;
	private static TextView availMem_label, numProc_label;
	private static ListView mylist;
    private ApplicationInfo appinfo = null;
    private PackageInfo pkginfo = null;


	
	private static final int ID_LAUNCH     = 1;
	private static final int ID_DETAILS   = 2;
    private static final int ID_INFO   = 3;
	private static final int ID_UNINSTAL   = 4;
    private static final int ID_KILL     = 5;
	public static int[] pidvalue;
	
	private static List<String> multiSelectData=null;

	
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.manage_layout);	
		
        //Toast.makeText(ProcessManager.this," allocated size  = " + getAsString(Debug.getNativeHeapAllocatedSize()), 1).show();      
		pk = getPackageManager();
		
		availMem_label = (TextView)findViewById(R.id.available_mem_label);
        availMem_label.setTypeface(Fonts.ICS);
		numProc_label = (TextView)findViewById(R.id.num_processes_label);
        numProc_label.setTypeface(Fonts.ICS);
        Button killall_button = (Button)findViewById(R.id.killall_button);
        killall_button.setTypeface(Fonts.ICS);
        Button cancel_button = (Button)findViewById(R.id.cancel_button);
        cancel_button.setTypeface(Fonts.ICS);
        
		
	
		activity_man = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		
		display_process = new ArrayList<RunningAppProcessInfo>();
		
	   
					
		
	  
	   
		
		   //setup ActionBar
		   final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
	       actionBar.setTitle("Process Manager");
	       actionBar.setHomeAction(new IntentAction(this, FilebrowserULTRAActivity.createIntent(this), R.drawable.ic_title_home_default));
	       actionBar.setDisplayHomeAsUpEnabled(true);
	       
	       
	       final AsyncTask loadAppsTask = new AsyncTask<String[], Long, Long>(){
	 			
				  @Override
					protected Long doInBackground(String[]... params) {
					  update_list();
						return null;
					}
					protected void onPreExecute() {
			    		
			    		 actionBar.setProgressBarVisibility(View.VISIBLE);
			    		 availMem_label.setText("calculating...");
			    		  numProc_label.setText("Listing Processes...");	
					}
					@Override
					protected void onProgressUpdate(Long... updatedSize){
						
					}
					
					@Override
					protected void onPostExecute(Long result){
						
						actionBar.setProgressBarVisibility(View.GONE);
						 
						setListAdapter(new MyListAdapter());
						 
						 if (savedInstanceState != null) {
					            mStarStates = savedInstanceState.getBooleanArray(STAR_STATES);
					        } else {
					            mStarStates = new boolean[display_process.size()];
					        }
						 
						 
						 update_labels();
						
					}

					
					
					
	     	}.execute();
	     	
	     	
           mylist = getListView();
           mylist.setFastScrollEnabled(true);
	    	
	    	mylist.setOnItemLongClickListener(new OnItemLongClickListener() {

				
				public boolean onItemLongClick(AdapterView<?> av, View v,
						final int position , long id) {
					// TODO Auto-generated method stub
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
	
	public void getoptions(final int position , View view){
		 
		    String processname = display_process.get(position).processName.toString();
		    PopupMenu menu = new PopupMenu(ProcessManager.this);
	        menu.setHeaderTitle("Process Options");
	      
		   menu.setHeaderIcon(getProcessIcon(processname));
			
	        // Set Listener
	        menu.setOnItemSelectedListener(new OnItemSelectedListener() {
				
				
				public void onItemSelected(MenuItem item) {
					// TODO Auto-generated method stub
					 switch (item.getItemId()) {
				        case ID_LAUNCH:
				        	Intent i = pk.getLaunchIntentForPackage(display_process.get(position).processName);
							
							if(i != null)
								startActivity(i);
							else
								Toast.makeText(ProcessManager.this, "Could not launch", Toast.LENGTH_SHORT).show();	
							
				            break;

				      

				        case ID_KILL:
				        	try {
			                	killProcess(ProcessManager.this, display_process.get(position).pid, display_process.get(position).processName);
			                	
			                	}
			                	catch (Exception e) {
									// TODO: handle exception
			                		Toast.makeText(ProcessManager.this, "couldn't kill the process ",Toast.LENGTH_SHORT).show();
			                		}
			                	
			                	@SuppressWarnings("unchecked")
								final ArrayAdapter<RunningAppProcessInfo> adapter = (ArrayAdapter<RunningAppProcessInfo>) getListAdapter();
			                	Toast.makeText(ProcessManager.this,display_process.get(position).processName + " was killed !", Toast.LENGTH_SHORT).show(); 

			                	adapter.remove(display_process.get(position));
			                	update_labels();
				            break;
				        case ID_DETAILS:
				        	final int apiLevel = Build.VERSION.SDK_INT;
							Intent intent = new Intent();
							if (apiLevel >= 9) {
							    //TODO get working on gb
							    //Toast.makeText(SDMove.this, "Gingerbread Not Currently Supported", Toast.LENGTH_LONG).show();
							    startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
							                             Uri.parse("package:" + display_process.get(position).processName)));
							} else {
							    final String appPkgName = (apiLevel == 8 ? "pkg" : "com.android.settings.ApplicationPkgName");
							    intent.setAction(Intent.ACTION_VIEW);
							    intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
							    intent.putExtra(appPkgName, display_process.get(position).processName);
							    startActivity(intent);
							}

				        	break;
				        	
				        case ID_UNINSTAL:
				        	try{
				        	Intent uninstall_intent= new Intent(Intent.ACTION_DELETE);
							uninstall_intent.setData(Uri.parse("package:"+display_process.get(position).processName));
							startActivity(uninstall_intent);
				        	}
				        	catch (Exception e) {
								// TODO: handle exception
				        		Toast.makeText(ProcessManager.this,"Can't Uninstall" , Toast.LENGTH_SHORT).show();
							}
				        	
				        	break;
				        case ID_INFO:
				        	
							//Toast.makeText(ProcessManager.this, "Process : "+display_process.get(position).processName +" lru : " +display_process.get(position).lru + " Pid :  " +display_process.get(position).pid, Toast.LENGTH_SHORT).show();	
				        	 final AlertDialog alert1 = new AlertDialog.Builder(ProcessManager.this).create();
							 alert1.setTitle("Process Info");
							 alert1.setIcon(getProcessIcon(display_process.get(position).processName));
							 alert1.setMessage("Process : "+display_process.get(position).processName+ " \nlru : "+display_process.get(position).lru + "\nPid : " +display_process.get(position).pid );
							 alert1.show();
							
							 

				        }
				}
			});
	        // Add Menu (Android menu like style)
	        menu.add(ID_LAUNCH, R.string.launch).setIcon(
	                getResources().getDrawable(R.drawable.install));
	        menu.add(ID_DETAILS, R.string.details).setIcon(
	                getResources().getDrawable(R.drawable.advancedsettings));
	        menu.add(ID_INFO, R.string.info).setIcon(
	                getResources().getDrawable(R.drawable.info));
	        
	       // menu.add(ID_UNINSTAL, R.string.uninstall).setIcon(
	              //  getResources().getDrawable(R.drawable.uninstall));
	        
	        menu.add(ID_KILL, R.string.kill).setIcon(
	                getResources().getDrawable(R.drawable.delete));
	        
	        menu.show(view);
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
	   		
	           super.onDestroy();
	           System.gc();
	   		  
	           
	           
	          
	           
	         	}
	    
	    @Override
		protected void onResume() {
			
			super.onResume();
			
		
		}
	    
	 
	    
	   
	
	public void onClick(View view){
		switch (view.getId()) {
		case R.id.cancel_button :
			finish();
			break;

		case R.id.killall_button:
			
			
			//i hate this method it doesn't work...but still.
			//have to change it and write another code..
			
			List<RunningAppProcessInfo> total_process = activity_man.getRunningAppProcesses();
			
			int len;
			
			len = total_process.size();
			
			
			
			int count=0;
			
			for (int i = 0; i < len; i++){
				if(total_process.get(i).importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
					    total_process.get(i).importance != RunningAppProcessInfo.IMPORTANCE_SERVICE){
					count++;
					
					}
			}
			for (int i = 0;i<count;i++){
				
				try {
					 killProcess(ProcessManager.this, display_process.get(i).pid, display_process.get(i).processName);
					@SuppressWarnings("unchecked")
					final ArrayAdapter<RunningAppProcessInfo> adapter = (ArrayAdapter<RunningAppProcessInfo>) getListAdapter();
	                adapter.remove(display_process.get(i));
					
					} catch (Exception e) {
					   e.printStackTrace();
					              
					}
				
			

		}
			update_labels();
			Toast.makeText(ProcessManager.this, count + " processes were killed !", Toast.LENGTH_SHORT).show();
			
			
			break;
		}
	}
	
	@Override
	protected void onListItemClick(ListView mylist, View view, final int position, long id) {
		
		    ActionItem killItem     = new ActionItem(ID_KILL, "Kill this", getResources().getDrawable(R.drawable.delete));
	        ActionItem launchItem     = new ActionItem(ID_LAUNCH, "Launch", getResources().getDrawable(R.drawable.install));
	        ActionItem unisntallItem     = new ActionItem(ID_UNINSTAL, "Uninstall", getResources().getDrawable(R.drawable.uninstall));

	        ActionItem infoItem     = new ActionItem(ID_INFO, "Info", getResources().getDrawable(R.drawable.info));
	        ActionItem detailsItem       = new ActionItem(ID_DETAILS, "Manage", getResources().getDrawable(R.drawable.advancedsettings));
	        
	      
	        final QuickAction quickAction = new QuickAction(this);
	        
	        //add action items into QuickAction
	        quickAction.addActionItem(launchItem);
	        quickAction.addActionItem(detailsItem);
	        //quickAction.addActionItem(searchItem);
	        quickAction.addActionItem(infoItem);
	       // quickAction.addActionItem(unisntallItem);
	        //quickAction.addActionItem(eraseItem);
	        quickAction.addActionItem(killItem);

	        //Set listener for action item clicked
	        quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {          
	            
	            public void onItemClick(QuickAction source, int pos, int actionId) {
	                //here we can filter which action item was clicked with pos or actionId parameter
	                ActionItem actionItem = quickAction.getActionItem(pos);
	                switch (actionId) {
	                case ID_KILL:
	                	try {
	                	killProcess(ProcessManager.this, display_process.get(position).pid, display_process.get(position).processName);
	                	
	                	}
	                	catch (Exception e) {
							// TODO: handle exception
	                		Toast.makeText(ProcessManager.this, "couldn't kill the process ",Toast.LENGTH_SHORT).show();
	                		}
	                	
	                	@SuppressWarnings("unchecked")
						final ArrayAdapter<RunningAppProcessInfo> adapter = (ArrayAdapter<RunningAppProcessInfo>) getListAdapter();
	                	Toast.makeText(ProcessManager.this,display_process.get(position).processName + " was killed !", Toast.LENGTH_SHORT).show(); 

	                	adapter.remove(display_process.get(position));
	                	update_labels();
	                	
	                
	                	break;
					case ID_DETAILS:
						final int apiLevel = Build.VERSION.SDK_INT;
						Intent intent = new Intent();
						if (apiLevel >= 9) {
						    //TODO get working on gb
						    //Toast.makeText(SDMove.this, "Gingerbread Not Currently Supported", Toast.LENGTH_LONG).show();
						    startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
						                             Uri.parse("package:" + display_process.get(position).processName)));
						} else {
						    final String appPkgName = (apiLevel == 8 ? "pkg" : "com.android.settings.ApplicationPkgName");
						    intent.setAction(Intent.ACTION_VIEW);
						    intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
						    intent.putExtra(appPkgName, display_process.get(position).processName);
						    startActivity(intent);
						}

						break;
					case ID_LAUNCH:
						Intent i = pk.getLaunchIntentForPackage(display_process.get(position).processName);
						
						if(i != null)
							startActivity(i);
						else
							Toast.makeText(ProcessManager.this, "Could not launch", Toast.LENGTH_SHORT).show();	
						
						break;
					case ID_UNINSTAL:
						try{
				        	Intent uninstall_intent= new Intent(Intent.ACTION_DELETE);
							uninstall_intent.setData(Uri.parse("package:"+display_process.get(position).processName));
							startActivity(uninstall_intent);
				        	}
				        	catch (Exception e) {
								// TODO: handle exception
				        		Toast.makeText(ProcessManager.this,"Can't Uninstall" , Toast.LENGTH_SHORT).show();
							}
				        	
						break;
					case ID_INFO:
						 
						//Toast.makeText(ProcessManager.this, "Process : "+display_process.get(position).processName +" lru : " +display_process.get(position).lru + " Pid :  " +display_process.get(position).pid, Toast.LENGTH_SHORT).show();	
						 final AlertDialog alert1 = new AlertDialog.Builder(ProcessManager.this).create();
						 alert1.setTitle("Process Info");
						 alert1.setIcon(getProcessIcon(display_process.get(position).processName));
						 alert1.setMessage("Process : "+display_process.get(position).processName+ " \nlru : "+display_process.get(position).lru + "\nPid : " +display_process.get(position).pid );
						 alert1.show();
						
						
						break;

					default:
						
						Toast.makeText(ProcessManager.this, actionItem.getTitle() + " selected", Toast.LENGTH_SHORT).show(); 
						break;
					}

	                            
	            }
	            
	        });

	        //set listnener for on dismiss event, this listener will be called only if QuickAction dialog was dismissed
	        //by clicking the area outside the dialog.
	        quickAction.setOnDismissListener(new QuickAction.OnDismissListener() {          
	            
	            public void onDismiss() {
	                
	            }
	        });
	        quickAction.show(view);
	        
	}
	
	 @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	    	 
	         
	    	  
	    	 if ((keyCode == KeyEvent.KEYCODE_BACK)){ //Back key pressed
	               
	    		    finish();
	         	     return true;
	        }
	    	 
	    	 
	       
	        return super.onKeyDown(keyCode, event);
	    }
	
	public boolean killProcess(Context context, int pid, String packageName) {
	    ActivityManager manager  = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	      if (pid <= 0) { return false; }
	      if (pid == android.os.Process.myPid()) {
	            System.out.println("Killing own process");
	            android.os.Process.killProcess(pid);
	            return true;
	      }
	      Method method = null;
	      try {
	            // Since API_LEVEL 8 : v2.2
	            method = manager.getClass().getMethod("killBackgroundProcesses", new Class[] { String.class});
	      } catch (NoSuchMethodException e) {
	            // less than 2.2
	            try {
	                  method = manager.getClass().getMethod("restartPackage", new Class[] { String.class });
	            } catch (NoSuchMethodException ee) {
	                  ee.printStackTrace();
	            }
	      }
	      if (method != null) {
	            try {
	                  method.invoke(manager, packageName);
	                  System.out.println("kill method  " + method.getName()+ " invoked " + packageName);
	            } catch (Exception e) {
	                  e.printStackTrace();
	            }
	      }
	      android.os.Process.killProcess(pid);
	      return true;
	}
	
	
	private void update_labels() {
		MemoryInfo mem_info;
		double mem_size;
		
		
		mem_info = new ActivityManager.MemoryInfo();
		activity_man.getMemoryInfo(mem_info);
		mem_size = (mem_info.availMem / (CONVERT * CONVERT));
		
		
		availMem_label.setText(String.format("Available memory:\t %.2f Mb", mem_size));
		numProc_label.setText("Number of processes:\t " + display_process.size());
		
		
	}
	
	public Drawable getProcessIcon(String pkg_name){
		
		try {
			return pk.getApplicationIcon(pkg_name);
		}catch (Exception e) {
			// TODO: handle exception
			return getResources().getDrawable(R.drawable.apk_file);
		}
		
	}
	
	private void update_list() {
		List<RunningAppProcessInfo> total_process = activity_man.getRunningAppProcesses();
		int len;
		
		total_process = activity_man.getRunningAppProcesses();
		len = total_process.size();
		
		for (int i = 0; i < len; i++){
			if(total_process.get(i).importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
			    total_process.get(i).importance != RunningAppProcessInfo.IMPORTANCE_SERVICE)
				display_process.add(total_process.get(i));
			
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
		 public TextView name=null;
   		 public CheckBox select=null;
         public TextView version =null;
		 public TextView state=null;
	      ViewHolder(View row){
	    	  name = (TextView)row.findViewById(R.id.app_name);
	          name.setTypeface(Fonts.ICS);
	          select = (CheckBox)row.findViewById(R.id.select_icon);
	          select.setVisibility(View.GONE);
	          version = (TextView)row.findViewById(R.id.version);
	          version.setTypeface(Fonts.ICS);
	          state = (TextView)row.findViewById(R.id.installdate);
	          state.setTypeface(Fonts.ICS);
	          image = (ImageView)row.findViewById(R.id.icon);
	    	
	      }
	      void populateFrom(String s)
   	      {
   	      name.setText(s);
   	      }
	     
		
	}
	 
	private class MyListAdapter extends ArrayAdapter<RunningAppProcessInfo> {
		
		public MyListAdapter() {
			super(ProcessManager.this, R.layout.app_row, display_process);
		}
		
		public String parse_name(String pkgName) {
			String[] items = pkgName.split("\\.");
			String name = "";
			int len = items.length;
			
			for (int i = 0; i < len; i++){
				if(!items[i].equalsIgnoreCase("com") && !items[i].equalsIgnoreCase("android") &&
				   !items[i].equalsIgnoreCase("google") && !items[i].equalsIgnoreCase("process") &&
				   !items[i].equalsIgnoreCase("htc") && !items[i].equalsIgnoreCase("coremobility"))
					name = items[i];
			}		
			return name;
		}
		
		 
				
		@Override
		public View getView(int position,View convertView, ViewGroup parent) {
			ViewHolder holder;
			String pkg_name = display_process.get(position).processName;
			CharSequence processName;
			try {
				appinfo = pk.getApplicationInfo(pkg_name, 0);
				processName = appinfo.loadLabel(pk);
				
			} catch (NameNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				processName = parse_name(pkg_name);
			}
				
			if(convertView == null) {
				LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.app_row, parent, false);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
				
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.select.setOnCheckedChangeListener(null);
			holder.select.setChecked(mStarStates[position]);
            holder.select.setOnCheckedChangeListener(mStarCheckedChanceChangeListener);
				
			holder.name.setText(processName);
			holder.version.setText(String.format("%s",
							display_process.get(position).processName));
			if(display_process.get(position).importance==RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
			holder.state.setText("Foreground");
			}
			else if(display_process.get(position).importance==RunningAppProcessInfo.IMPORTANCE_BACKGROUND)
			{
				holder.state.setText("Background");
			}
			else if(display_process.get(position).importance==RunningAppProcessInfo.IMPORTANCE_VISIBLE)
			{
				holder.state.setText("Visible");
			}
			else if(display_process.get(position).importance==RunningAppProcessInfo.IMPORTANCE_PERCEPTIBLE)
			{
				holder.state.setText("Perceptible");
			}
			
			
			try {
				holder.image.setImageDrawable(pk.getApplicationIcon(pkg_name));
				
			} catch (NameNotFoundException e) {
				holder.image.setImageResource(R.drawable.apk_file);
			}
			
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
		
	
}
