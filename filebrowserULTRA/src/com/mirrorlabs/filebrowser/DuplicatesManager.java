package com.mirrorlabs.filebrowser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.mirrorlabs.customtoast.Toaster;

import com.mirrorlabs.filebrowser.FilebrowserULTRAActivity.Fonts;
import com.twmacinta.util.MD5;
import com.widget.radialmenu.RadialMenuItem;
import com.widget.radialmenu.RadialMenuItem.RadialMenuItemClickListener;
import com.widget.radialmenu.RadialMenuWidget;






import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DuplicatesManager extends ListActivity{
	
    static final String TAG = "DuplicatesManager";
  
	final ArrayList<String> pathList = new ArrayList<String>();
	
    private static ListView myList;
	private static String[] fileList;
    private static TextView mListLabel=null;
    private static final String STAR_STATES = "mylist:star_states";
    private boolean[] mStarStates=null;
    private static ArrayList<String> multiSelectData=null;
    private RadialMenuWidget pieMenu;
    private static  RadialMenuItem closeItem,deleteItem,backItem;
    private ActionBar actionBar;

  
   
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.duplicatelist);
		myList=(ListView)findViewById(android.R.id.list);
		mListLabel = (TextView)findViewById(R.id.duplicates_label);
		mListLabel.setText("No files to display");
		
		 Calendar c = Calendar.getInstance();
		 final int curtime = c.get(c.SECOND);
		
		//declare RadialMenuWidget
		pieMenu = new RadialMenuWidget(this);
		
		closeItem = new RadialMenuItem("cancel","Cancel");
		closeItem.setDisplayIcon(R.drawable.delete1);
		
		deleteItem = new RadialMenuItem("delete","Delete");
		deleteItem.setDisplayIcon(R.drawable.delete48);
		
		backItem = new RadialMenuItem("back",null);
		backItem.setDisplayIcon(R.drawable.refresh_icon);
		
		
		pieMenu.setIconSize(15, 30);
		pieMenu.setTextSize(13);
		
		
		pieMenu.setInnerRingColor(0x171717, 180);
		pieMenu.setOuterRingColor(0x0099CC, 180);
		
		pieMenu.addMenuEntry(new ArrayList<RadialMenuItem>() {{
			add(closeItem);
			add(deleteItem); 
			}});
		
		initializeDrawable();
		pieMenu.setSourceLocation(200, 200);
		
		fileList = new String[1];
		fileList[0]=getIntent().getExtras().getString("dir");
		
		final File file = new File(fileList[0]);
		
		
		final IconicList madapter;
		madapter = new IconicList();
		
		multiSelectData = new ArrayList<String>();
		 if (savedInstanceState != null) {
	            mStarStates = savedInstanceState.getBooleanArray(STAR_STATES);
	        } else {
	            mStarStates = new boolean[1000];
	        }
		 
		 closeItem.setOnMenuItemPressed(new RadialMenuItemClickListener() {
				
				public void execute() {
					// TODO Auto-generated method stub
					pieMenu.dismiss();
				}
			});
			
			deleteItem.setOnMenuItemPressed(new RadialMenuItemClickListener() {
				
				public void execute() {
					// TODO Auto-generated method stub
					if(multiSelectData!=null && multiSelectData.size()>0){
					for(int i=0 ;i <multiSelectData.size();i++){		  
			              FileUtils.deleteTarget(multiSelectData.get(i));
			              
			              madapter.remove(multiSelectData.get(i));
				    	   }
				    	 
				    	  myList.setAdapter(madapter);
				    	  multiSelectData.clear();
			              for(int i=0;i<pathList.size();i++){
			            	  mStarStates[i]=false;
			              }
			              mListLabel.setText(pathList.size()+ " Duplicate items");
					}else{
						showMessage("No files selected ");
					}
			              pieMenu.dismiss();
					
				}
			});
			
		myList.setOnItemClickListener(new OnItemClickListener() {
            
			public void onItemClick(AdapterView<?> av, View v, int position,
					long id) {
				// TODO Auto-generated method stub
				mStarStates[position]= !mStarStates[position];
				
				if(mStarStates[position]){
		    		   multiSelectData.add(pathList.get(position));        // add to multiselectdata if item is selected
		    	     }
		    	   else if(!mStarStates[position]){
		    		   multiSelectData.remove(pathList.get(position));
		    	   }
				   if(multiSelectData.size()>0){
		    	   mListLabel.setText(multiSelectData.size() + " items selected");
				   }else{
				   mListLabel.setText(pathList.size() +" Duplicate items");	   
				   }
			}
		});
		
		myList.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> av, View v,
					final int pos, long id) {
				// TODO Auto-generated method stub
				multiSelectData.clear();
				  for(int i=0;i<pathList.size();i++){
				    	if(mStarStates[i]){
				    		multiSelectData.add(pathList.get(i));
				    	}
				    	else if(!mStarStates[i]){
				    		multiSelectData.remove(pathList.get(i));
				    	}
				    }
			    getoptions(pos,madapter);
				return false;
			}
		});
		
		actionBar = (ActionBar) findViewById(R.id.actionbar);
        
        actionBar.setTitle("Duplicates Manager");
        
	    actionBar.setHomeAction(new IntentAction(this, FilebrowserULTRAActivity.createIntent(this), R.drawable.ic_title_home_default));
        actionBar.setDisplayHomeAsUpEnabled(true);
		
       
		
		//DuplicateFinder(fileList,madapter);
		
		 final AsyncTask loadDuplicatesTask = new AsyncTask<String[], Long, Long>(){
 			
			 private ProgressDialog pr_dialog;
 			
				@Override
				protected Long doInBackground(String[]... params) {
					DuplicateFinder(params[0],madapter);
					return null;
				}
				protected void onPreExecute() {
		    		Context mContext =DuplicatesManager.this;
		    	     pr_dialog = ProgressDialog.show(mContext, "Please wait", 
		    												"Searching duplicates in \n"+file.getName()+"...",
		    												true, true);
		    	     actionBar.setProgressBarVisibility(View.VISIBLE);
		    			
				}
				@Override
				protected void onProgressUpdate(Long... updatedSize){
				}
				
				@Override
				protected void onPostExecute(Long result){
					
					Calendar c = Calendar.getInstance();
			          int seconds = c.get(c.SECOND);

			          int timespent = seconds - curtime;
			          if (timespent < 0) {
			            timespent += 60;
			          }
					
					pr_dialog.dismiss();
					actionBar.setProgressBarVisibility(View.GONE);
					if(pathList.size()>0){
						myList.setAdapter(madapter);
					}
					else{
						showMessage("no duplicates found ");
					}
					
					mListLabel.setText(pathList.size()+ " Duplicate items "+"[" + timespent + " sec]");
					
				}

				
				
				
     	}.execute(fileList);
		
		
	}
	
	public void onClick(View view){
		switch (view.getId()) {
		case R.id.optionButton:
			
			
			pieMenu.show(view);
			
			
			
			
			break;

		default:
			break;
		}
	}
	 private void showMessage(String message) {
	        Toast.makeText(DuplicatesManager.this, message, Toast.LENGTH_SHORT).show();
	    }
	
	private void getoptions(final int pos,final IconicList madapter){
		if(multiSelectData.size()>0 &&multiSelectData!=null){
			final CharSequence[] items = {"Delete"};
			AlertDialog.Builder builder = new AlertDialog.Builder(DuplicatesManager.this);
			final File file = new File(pathList.get(pos));
			builder.setTitle(multiSelectData.size()+" items selected");
			builder.setItems(items, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dInterface, int item) {
			    	switch(item){
			    	case 0 :
			    		
			            new AsyncTask<String[], Long, Long>(){
			     			
			   			 private ProgressDialog pr_dialog;
			    			
			   				@Override
			   				protected Long doInBackground(String[]... params) {
			   					
			   					for(int i=0 ;i <multiSelectData.size();i++){	
						    		
			  		              FileUtils.deleteTarget(multiSelectData.get(i));
			  		              
			  		              madapter.remove(multiSelectData.get(i));
			  			    	   }
			   					return null;
			   				}
			   				protected void onPreExecute() {
			   		    		
			   		    	     pr_dialog = ProgressDialog.show(DuplicatesManager.this, "Please wait", 
			   		    												"Deleting duplicates....",
			   		    												true, true);
			   		    	     actionBar.setProgressBarVisibility(View.VISIBLE);
			   		    			
			   				}
			   				@Override
			   				protected void onProgressUpdate(Long... updatedSize){
			   				}
			   				
			   				@Override
			   				protected void onPostExecute(Long result){
			   					
			   				  myList.setAdapter(madapter);
					    	  multiSelectData.clear();
				              for(int i=0;i<pathList.size();i++){
				            	  mStarStates[i]=false;
				              }
				              mListLabel.setText(pathList.size()+ " Duplicate items");
			   					
			   					pr_dialog.dismiss();
			   					actionBar.setProgressBarVisibility(View.GONE);
			   					
			   				}

			   				
			   				
			   				
			        	}.execute();
			        	
			        	
			    	
			    	 
			    	 
			    		break;
			    	
			    	}
			    }
			});
			
			AlertDialog alert = builder.create();
			
			alert.show();
		}else{
		final CharSequence[] items = {"Delete","Show MD5"};
			AlertDialog.Builder builder = new AlertDialog.Builder(DuplicatesManager.this);
			final File file = new File(pathList.get(pos));
			builder.setTitle(file.getName());
			builder.setItems(items, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dInterface, int item) {
			    	switch(item){
			    	case 0 :
			    		  try{
			    			  
		              FileUtils.deleteTarget(pathList.get(pos));
		              
		              Toast.makeText(DuplicatesManager.this,file.getName()+"was deleted", Toast.LENGTH_SHORT).show();
			    		  }catch (Exception e) {
						// TODO: handle exception
			    			 Toaster.showToast(DuplicatesManager.this, R.string.deletefail, true);
					}
			    		  
                      madapter.remove(pathList.get(pos));
		              madapter.notifyDataSetChanged();
		              
		              mListLabel.setText(pathList.size()+ " Duplicate items");

			    		break;
			    	case 1:
			    		try {
						new AlertDialog.Builder(DuplicatesManager.this)
						.setMessage(MD5.asHex(MD5.getHash(new File(pathList.get(pos)))))
						.setIcon(getIcon(pathList.get(pos))).setTitle("MD5 Checksum")
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
							}
						}).show();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
					}
			    		break;
			    	}
			    }
			});
			builder.setIcon(getIcon(pathList.get(pos)));
			AlertDialog alert = builder.create();
			
			alert.show();
		}
	}
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray(STAR_STATES, mStarStates);
    }
	
	
	

	
    //simple function to get hash
    public static String getHash(File file){
    	   MessageDigest md;
    	   String hash;
    	  try{
            try {
                md = MessageDigest.getInstance("MD5");
                
                 } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException("cannot initialize MD5 hash function", e);
                 }
                 FileInputStream fin = new FileInputStream(file);
                 if(file.length()>1024*1024){
                 byte data[] = new byte[(int) file.length()/100];
                 fin.read(data);
                 fin.close();
                 hash =new BigInteger(1, md.digest(data)).toString(16);
                 }else if(file.length() > 1024*10){
                	 byte data[] = new byte[(int) file.length()/10];
                     fin.read(data);
                     fin.close();
                     hash =new BigInteger(1, md.digest(data)).toString(16);
                 }else{
                	 byte data[] = new byte[(int) file.length()];
                     fin.read(data);
                     fin.close();
                     hash =new BigInteger(1, md.digest(data)).toString(16);
                 }
            }catch (IOException e) {
				// TODO: handle exception
                throw new RuntimeException("cannot read file " + file.getAbsolutePath(), e);

			}
        
    	
		return hash ;
    	
    }

    public static void find(HashMap<String, ArrayList<String>> lists, File directory) {
        for (File child : FileUtils.getDuplicates(directory)) {
            if (child.isDirectory()) {
                find(lists, child);
            } else {
                     String hash = getHash(child);  // simple function 
					
				    ArrayList<String> list = lists.get(hash);
		                    if (list == null) {
		                        list = new ArrayList<String>();
		                        lists.put(hash, list);
		                    }
		                    list.add(child.getAbsolutePath());
					
                
            }
        }
        
        
    }
    
    public  void DuplicateFinder(String[] args, IconicList madapter) {
        
        File dir = new File(args[0]);
        
        HashMap<String, ArrayList<String>> lists = new HashMap<String, ArrayList<String>>();
        DuplicatesManager.find(lists, dir);
        
        for (ArrayList<String> list : lists.values()) {

        	if (list.size() > 1) {
               
                for (String filepath : list) {
                	
                	File file = new File(filepath);
                	pathList.add(filepath);
                	
                    madapter.notifyDataSetChanged();
                	
                }
                
            }
        }
        
        
    }
	
    
    public String getFileExtension(String filepath){
  	   
  	   final File file = new File(filepath);
     	   String filename = file.getName().toString();
     	
      String ext = null;
     	
     	 try {
     		 ext = filename.substring(filename.lastIndexOf("."), filename.length());
     		
     	     } 
     	 catch(IndexOutOfBoundsException e) {	
     	    	 
     		     ext = ""; 
     		    
     	    }
  	   return ext;
     }
     
     Drawable getIcon(String filepath){
     	File file = new File(filepath);
     	String filename=file.getName().toString();//get the file according the position
 		String ext = null;
     	
     	try {
     		 ext = filename.substring(filename.lastIndexOf("."), filename.length());
     		
     	} catch(IndexOutOfBoundsException e) {	
     		ext = ""; 
     	}
 		
     	 if(file.isDirectory()){ //decide are the file folder or file
 				return getResources().getDrawable(R.drawable.myfolder72) ;
 				
 			}
 			 
 			 else if (ext.equalsIgnoreCase(".zip")){
 				 return getResources().getDrawable(R.drawable.myzip);
 			}
 			 else if (ext.equalsIgnoreCase(".rar")){
 				return getResources().getDrawable(R.drawable.rar) ;
 			}
 			 
 			 else if (ext.equalsIgnoreCase(".pdf")){
 				 return getResources().getDrawable(R.drawable.pdf_icon);
 			}
 			 
 			 else if (ext.equalsIgnoreCase(".txt") ){
 				return getResources().getDrawable(R.drawable.textpng) ;
 			}
 			 else if (ext.equalsIgnoreCase(".html") ){
 				return getResources().getDrawable(R.drawable.html);
 			} 
 			 else if (ext.equalsIgnoreCase(".jpg")
 	    			 ||ext.equalsIgnoreCase(".png")
 	    			 ||ext.equalsIgnoreCase(".gif")
 	    			 ||ext.equalsIgnoreCase(".jpeg")
 	    			 ||ext.equalsIgnoreCase(".tiff")){
 				 return getResources().getDrawable(R.drawable.image);
 			}
 			 
 			 else if (ext.equalsIgnoreCase(".mp3")
 					 ||ext.equalsIgnoreCase(".wav")||
 					 ext.equalsIgnoreCase(".m4a")){
 				 return getResources().getDrawable(R.drawable.audio) ;
 			}
 	    	 
 	    	 
 			 
 			 else if (ext.equalsIgnoreCase(".apk")){
 				 
 			
 	         return getapkicon(filepath);
 		
 			 }
 			 else if (ext.equalsIgnoreCase(".mp4")
 					 ||ext.equalsIgnoreCase(".3gp")
 					 ||ext.equalsIgnoreCase(".flv")
 					 || ext.equalsIgnoreCase(".ogg")
 					 ||ext.equalsIgnoreCase(".m4v")){
 				 return getResources().getDrawable(R.drawable.videos_new);
 			}
     	 
 			 else if (ext.equalsIgnoreCase(".sh")
 					 ||ext.equalsIgnoreCase(".rc")){
 				 return getResources().getDrawable(R.drawable.script_file64);
 			}
     	 
 			 else if (ext.equalsIgnoreCase(".prop")){
 				 return getResources().getDrawable(R.drawable.build_file64);
 			}
 			 else if (ext.equalsIgnoreCase(".xml")){
 				 return getResources().getDrawable(R.drawable.xml64);
 			}
 			 else if (ext.equalsIgnoreCase(".doc")
 	    			 ||ext.equalsIgnoreCase(".docx")){
 				 return getResources().getDrawable(R.drawable.nsword64);

 				 
 			 }
 			 else if(ext.equalsIgnoreCase(".ppt")
 	    			 ||ext.equalsIgnoreCase(".pptx")){
 				 return getResources().getDrawable(R.drawable.ppt64);

 				 
 			 }
 			 else if(ext.equalsIgnoreCase(".xls")
 	    			 ||ext.equalsIgnoreCase(".xlsx"))
 	    	 {
 		 			    		
 				
 				 return getResources().getDrawable(R.drawable.spreadsheet64);

 		    		
 		    	}
 			
 			
 			else
 			{
 				return getResources().getDrawable(R.drawable.miscellaneous);
 			}
 		
     	
     }
    
    class ViewHolder {
        public TextView name=null,path=null,md5=null;
        public CheckBox select=null;
        public ImageView image=null;
      
        ViewHolder(View row){
      	  name = (TextView)row.findViewById(R.id.label);
      	  select =(CheckBox)row.findViewById(R.id.select_icon);
      	  name.setTypeface(Fonts.ICS);
      	  md5=(TextView)row.findViewById(R.id.md5_hash);
      	  md5.setTypeface(Fonts.ICS);
      	  path = (TextView)row.findViewById(R.id.label_info);
    	  path.setTypeface(Fonts.ICS);
      	  image = (ImageView)row.findViewById(R.id.icon);
      	
      	  
        }
        void populateFrom(String s)
        {
        name.setText(s);
        }
      }
    
   
    
    class IconicList extends ArrayAdapter<String> {

    	
    	public IconicList() {
    		super(DuplicatesManager.this,R.layout.duplicaterow, pathList);

    		// TODO Auto-generated constructor stub
    	}
    	
    	
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent){
    		ViewHolder holder;
    		
    		
    		if(convertView==null){
    		LayoutInflater inflater = getLayoutInflater(); //to instantiate layout XML file into its corresponding View objects
    	    convertView= inflater.inflate(R.layout.duplicaterow, null); //to Quick access to the LayoutInflater  instance that this Window retrieved from its Context.
    	    holder = new ViewHolder(convertView);
    		convertView.setTag(holder);

    		}  
    		else
    		{
    			holder = (ViewHolder)convertView.getTag();
    		}
    		
    		holder.select.setOnCheckedChangeListener(null);
			holder.select.setChecked(mStarStates[position]);
            holder.select.setOnCheckedChangeListener(mStarCheckedChanceChangeListener);
    		
    		
            File f = new File(pathList.get(position));
            holder.path.setText(pathList.get(position));
            holder.name.setText(f.getName());
            
	        String ext = getFileExtension(pathList.get(position));
	        
    		 if(f.isDirectory()){ //decide are the file folder or file
	    		 holder.image.setImageDrawable(getIcon(pathList.get(position)));
				
			}
			 
			 
			 if(f.isFile() && ext.equalsIgnoreCase(".apk")){ //decide are the file folder or file
				 
				 
				 
					 setPlaceholder(getResources().getDrawable(R.drawable.ic_launcher));
					 holder.image.setTag(pathList.get(position));
				     loadDrawable(f.getAbsolutePath(),holder.image);
					
			}
			
	    	 else if (f.isFile() &&ext.equalsIgnoreCase(".jpg")
	    			 ||ext.equalsIgnoreCase(".png")
	    			 ||ext.equalsIgnoreCase(".gif")
	    			 ||ext.equalsIgnoreCase(".jpeg")
	    			 ||ext.equalsIgnoreCase(".tiff")){
				 
	    		  
                    Drawable icon = getResources().getDrawable(R.drawable.image);	    		  
	    		    Bitmap bitmap = ((BitmapDrawable)icon ).getBitmap();
	    		    BitmapManager.INSTANCE.setPlaceholder(bitmap);
			        holder.image.setTag(f.getAbsolutePath());  

			        BitmapManager.INSTANCE.loadBitmap(f.getAbsolutePath(),holder.image);  
			  
			}
			 
	    	 else if (f.isFile() && !ext.equalsIgnoreCase(".apk")){
				 holder.image.setImageDrawable(getIcon(pathList.get(position)));

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
    

	
	
	public static class DrawableManager {
    	private static ConcurrentMap<String, Drawable> cache;
    	private static  ExecutorService pool;  
       
        private static Map<ImageView, String> imageViews = Collections  
    	            .synchronizedMap(new ConcurrentHashMap<ImageView, String>());  
    	private static Drawable placeholder;
		 
    	  
       
    }

    private static void initializeDrawable(){
    	  DrawableManager.cache = new ConcurrentHashMap<String, Drawable>();  
          DrawableManager.pool = Executors.newFixedThreadPool(5); 
    	
       }
    
    public static void setPlaceholder(Drawable drawable) {  
        DrawableManager.placeholder = drawable;  
    }  
    
    public static Drawable getDrawableFromCache(String url) {  
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
    
    
 public  Drawable getapkicon(String url){
    	
	   
    	Drawable icon ;
    	icon = getDrawableFromCache(url);
    	if(icon!=null){
    		return icon;
    	}
    	
    	else{
		icon= new FileUtils(DuplicatesManager.this).getapkicon(url);
        DrawableManager.cache.put(url, icon);
		return icon;
		
    	}
    	
    }
 

}

