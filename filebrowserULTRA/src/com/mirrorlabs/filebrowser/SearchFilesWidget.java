package com.mirrorlabs.filebrowser;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.andpdf.pdfviewer.PdfViewerActivity;

import com.markupartist.android.widget.ScrollingTextView;
import com.mirrorlabs.filebrowser.FilebrowserULTRAActivity.DrawableManager;
import com.mirrorlabs.filebrowser.FilebrowserULTRAActivity.Fonts;
import com.mirrorlabs.filebrowser.FilebrowserULTRAActivity.Item;
import com.mirrorlabs.filebrowser.FilebrowserULTRAActivity.mykey;
import com.mirrorlabs.filebrowser.SearchFilesDialog.FoundList;
import com.mirrorlabs.filebrowser.SearchFilesDialog.ViewHolder;




import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

// fix this class...lots of bugs

public class SearchFilesWidget extends ListActivity implements TextWatcher{

	private static  EditText txtSearch;
	private static  ListView searchList;
	private static ProgressBar progress;
	private static String rootdir=Environment.getExternalStorageDirectory().getAbsolutePath();
	private static String[] founditems=null;
	private static String SearchString;
	FilebrowserULTRAActivity activity;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_files_dialog);
		txtSearch = (EditText)findViewById(R.id.fileSearch);
	    progress = (ProgressBar)findViewById(R.id.progressBar);
	    progress.setVisibility(View.GONE);
	    searchList =getListView();
	    searchList.setVisibility(View.GONE);
	    txtSearch.addTextChangedListener(SearchFilesWidget.this);
	    activity = new FilebrowserULTRAActivity();
	    
	    

		
	}
	
	public void closeDialog(View v) {
		this.finish();
	}

	public void afterTextChanged(Editable text) {
		final String tempSearchString = text.toString();
		SearchString=text.toString();
		
	
		if (text.toString().length()>0) {
			
			 new AsyncTask<String[], Long, Long>(){
		 			
				  @Override
					protected Long doInBackground(String[]... params) {
					  ArrayList<String> found =FileUtils.searchInDirectory(rootdir, tempSearchString);
				       int len = found != null ? found.size() : 0;
				       founditems = new String[len];
                       found.toArray(founditems);
				        Arrays.sort(founditems,type);
				        found.clear();

						return null;
					}
					protected void onPreExecute() {
						searchList.setVisibility(View.GONE);
                       progress.setVisibility(View.VISIBLE);
						searchList.setAdapter(null);

					}
	
					@Override
					protected void onPostExecute(Long result){
						if(!txtSearch.getText().toString().equals(SearchString)){
							progress.setVisibility(View.GONE);
							searchList.setVisibility(View.GONE);
							searchList.setAdapter(null);
						 
						}else if(txtSearch.getText().toString().length()==0){
							  
							progress.setVisibility(View.GONE);
							searchList.setVisibility(View.GONE);
							searchList.setAdapter(null);
						 

						}else{
							 
							 searchList.setAdapter(new FoundList());
							  progress.setVisibility(View.GONE);
							  searchList.setVisibility(View.VISIBLE);
							 
						}
						

					}
			
	     	}.execute();
		
		}
		else{
			progress.setVisibility(View.GONE);
			searchList.setVisibility(View.GONE);
			return;
	   	
      }
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
       searchList.setOnItemClickListener(new OnItemClickListener() {

			
			public void onItemClick(AdapterView<?> av, View v, int position,
					long id) {
				// TODO Auto-generated method stub
				File file = new File(founditems[position]);
				if (file.isDirectory()){
					String opendir = founditems[position];
					Intent intent = new Intent(SearchFilesWidget.this,FilebrowserULTRAActivity.class);
					intent.putExtra("dir", opendir);
					startActivity(intent);
					finish();
			     
 					}
				else{
					OnclickOperation(founditems[position]);
				}
			
			}
		});
		
		searchList.setOnItemLongClickListener(new OnItemLongClickListener() {

			
			public boolean onItemLongClick(AdapterView<?> av, View v,
					int position, long id) {
				// TODO Auto-generated method stub
				getOperations(founditems[position]);
				return false;
			}
		});
	}
	
	

	public void onTextChanged(final CharSequence s, int start, int before, int count) {
		
		
		
	}
	 private void showMessage(String message) {
	        Toast.makeText(SearchFilesWidget.this, message, Toast.LENGTH_SHORT).show();
	    }
	    
	    private Animation launchAnimation(int id) {
	    	
	    	return AnimationUtils.loadAnimation(SearchFilesWidget.this, id);
	    }
	    
	    public void getOperations(final String filepath){
	    	/*
	    	if(searchflag==true){
			searchflag=false;        //was implemented
			manageUi();
	    	}  */
			
			final Item[] items = {
				    new Item("Delete", R.drawable.delete48),
				    new Item("Send", R.drawable.send48),
				    new Item("Share", R.drawable.share48),
				    new Item("Add Bookmark" ,R.drawable.favorites),
				    new Item("Email", R.drawable.email_send_48),
				    new Item("Properties", R.drawable.settings_ldpi),
				   
				};

				ListAdapter adapter = new ArrayAdapter<Item>(
				    this,
				    android.R.layout.select_dialog_item,
				    android.R.id.text1,
				    items){
				        public View getView(int position, View convertView, ViewGroup parent) {
				            //User super class to create the View
				            View v = super.getView(position, convertView, parent);
				            TextView tv = (TextView)v.findViewById(android.R.id.text1);

				            //Put the image on the TextView
				            tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);

				            //Add margin between image and text (support various screen densities)
				            int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
				            tv.setCompoundDrawablePadding(dp5);

				            return v;
				        }
				    };  
	                   
	          
	         
	    	
	    	 final File file = new File(filepath);
	    	AlertDialog.Builder builder = new AlertDialog.Builder(SearchFilesWidget.this);
	  		builder.setTitle(file.getName().toString());
	  	
	  		builder.setIcon(getScaledIcon(file.getAbsolutePath()));
			
			builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			    @SuppressWarnings("deprecation")
				
				public void onClick(DialogInterface dInterface, int item) {
		    		
			    	switch(item){
			    	
			    	
			    		
	                 case 0:
			    		
			    	    final AlertDialog alertDialog1 = new AlertDialog.Builder(SearchFilesWidget.this).create();
						alertDialog1.setTitle("Delete File");
						
			            alertDialog1.setIcon(R.drawable.warning_icon2);
						alertDialog1.setMessage("Are you sure you want to delete "+file.getName()+ " ? ");
						alertDialog1.setButton("Yes", new DialogInterface.OnClickListener() {
						      
							public void onClick(DialogInterface dialog, int which) {
						    	  
									FileUtils.deleteTarget(file.getAbsolutePath());
									showMessage(file.getName()+" was deleted !");
									finish();
									

						    	  
						     }}); 
						alertDialog1.setButton2("No", new DialogInterface.OnClickListener() {
						      
							public void onClick(DialogInterface dialog, int which) {
						 
						       alertDialog1.dismiss();
						 
						    } }); 
						alertDialog1.show();
						
			    		
			            break;
			    	
			    	case 1:
			    		
			    		if(file.isDirectory()){
			    			Toast.makeText(SearchFilesWidget.this,"can't send a folder...create a zip file !", Toast.LENGTH_SHORT).show();

			    		}
			    		else {
			    			
			    			
			    				Uri uri11 = Uri.fromFile(file.getAbsoluteFile());
					    		Intent i1 = new Intent(Intent.ACTION_SEND);
					    	    
					    	    i1.setType("application/zip");
					    	    i1.putExtra(Intent.EXTRA_STREAM, uri11);
					    		startActivity(Intent.createChooser(i1, "Send"));
			    			
			    		}
			    		break;
			    		
			    		
			    	case 2:
			    		
			    		if(file.isDirectory()){
			    			Toast.makeText(SearchFilesWidget.this,"can't send a folder...create a zip file !", Toast.LENGTH_SHORT).show();

			    		}
			    		else{
			    		Uri uri = Uri.fromFile(file.getAbsoluteFile());
			    		Intent i = new Intent(Intent.ACTION_SEND);
			    	    i.putExtra(Intent.EXTRA_SUBJECT, file.getName());
			    		i.putExtra(Intent.EXTRA_TEXT,"Check this out");
			    	    i.setType("text/plain");
			    	    i.putExtra(Intent.EXTRA_STREAM, uri);
			    		startActivity(Intent.createChooser(i, "Send via"));
			    		}
			    		break;
			    		
			    	case 3:
			    		String path = file.getAbsolutePath();
						Cursor query = managedQuery(BookmarksProvider.CONTENT_URI,
													new String[]{BookmarksProvider._ID},
													BookmarksProvider.PATH + "=?",
													new String[]{path},
													null);
						if(!query.moveToFirst()){
							ContentValues values = new ContentValues();
							values.put(BookmarksProvider.NAME, file.getName());
							values.put(BookmarksProvider.PATH, path);
							getContentResolver().insert(BookmarksProvider.CONTENT_URI, values);
							Toast.makeText(SearchFilesWidget.this,"Bookmark Added", Toast.LENGTH_SHORT).show();
						}
						else{
							Toast.makeText(SearchFilesWidget.this, "Bookmark Exists ", Toast.LENGTH_SHORT).show();
						}
						
			    		break;
			    		
			    	case 4:
			    		if(file.isDirectory()){
			    			Toast.makeText(SearchFilesWidget.this,"can't send a folder...create a zip file !", Toast.LENGTH_SHORT).show();

			    		}
			    		else {
			    		
			       Uri uri1 = Uri.fromFile(file.getAbsoluteFile());
		    		Intent email = new Intent(Intent.ACTION_SEND);
		    	    email.putExtra(Intent.EXTRA_SUBJECT, file.getName());
		    		email.putExtra(Intent.EXTRA_TEXT,"Check this out");
		    	    email.setType("message/rfc822");
		    	    email.putExtra(Intent.EXTRA_STREAM, uri1);
		    		startActivity(Intent.createChooser(email,"Send mail"));
			    		}
			    		break;
			    		
			    	
			    	
			    	case 5 :
			    		
			    		getProperties(file.getAbsoluteFile());
			    		
			    		break;
			    
			             
			    	}
			    }});
			 AlertDialog alert = builder.create();
			
			alert.show();
	      
	    	
	    }
		@SuppressLint({ "NewApi", "NewApi" })
		public void getProperties(File file){
	    	
	    	String file_path = file.getAbsolutePath().toString();
	    	StatFs stat = new StatFs(file_path);
	    	final Dialog dialog1 = new Dialog(SearchFilesWidget.this);
			TextView type,path,name;
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
	        file_icon = (ImageView)dialog1.findViewById(R.id.file_image);
	        file_icon.setImageDrawable(getScaledIcon(file_path));
	        
	       
	        //
	        name = (ScrollingTextView)dialog1.findViewById(R.id.file_name);
	        name.setText(" "+file.getName().toString());
	        //
	        path = (TextView)dialog1.findViewById(R.id.file_path);
	        path.setText(" "+file.getAbsolutePath().toString());
	        
	        //
	        size = (TextView)dialog1.findViewById(R.id.file_size);
	        
	      
	            String total_size = FileUtils.formatSize(SearchFilesWidget.this,file.length());
	            size.setText(" "+String.valueOf(total_size));

	        
	           if(file.isDirectory()){
	    		  new AsyncTask<File, Long, Long>(){
	    			
	    			protected long totalSize = 0L;
	    			
					@Override
					protected Long doInBackground(File... file) {
						sizeOf(file[0]);
						return totalSize;
					}
	        		
					@Override
					protected void onProgressUpdate(Long... updatedSize){
						size.setText(FileUtils.formatSize(size.getContext(), updatedSize[0]));
					}
					
					@Override
					protected void onPostExecute(Long result){
						size.setText(FileUtils.formatSize(size.getContext(), result));
					}
					
					private void sizeOf(File file){
						if(file.isFile()){
							totalSize += file.length();
							publishProgress(totalSize);
						} else {
							File[] files = file.listFiles();
							
							if(files != null && files.length != 0){
	    						for(File subFile : files){
	    							sizeOf(subFile);
	    						}
							}
						}
					}
	        	}.execute(file);
	        	
	        	
	    	   }
	        
	        
	        
	        type = (TextView)dialog1.findViewById(R.id.file_type);
	        if(file.isDirectory()){
	        type.setText(" Folder ");
	        }
	        if(file.isFile()){
	        	type.setText(" " + FileUtils.getExtension(file.getAbsolutePath()).toString().replace(".","").toUpperCase() + " file");
	        }
	        //
	        modified = (TextView)dialog1.findViewById(R.id.file_modified);
	        long lastModified = file.lastModified();
	        Date date = new Date(lastModified);
	        modified.setText(""+String.valueOf(date));
	        //
	        contains = (TextView)dialog1.findViewById(R.id.file_contains);
	        if(file.isFile()){
	        contains.setText(" NA ");
	        }
	        else if (file.isDirectory()){
	        	
				int len = 0;

				File[] list = file.listFiles();
				if(list != null)
					len = list.length;
				contains.setText(" "+String.valueOf(len));
	        }
	        //
	        sdcard_free = (TextView)dialog1.findViewById(R.id.sdcard_free);
	        sdcard_total = (TextView)dialog1.findViewById(R.id.sdcard_total);

	         if (Build.VERSION.SDK_INT >= 8) {
	        sdcard_free.setText(" "+String.valueOf(FileUtils.formatSize(SearchFilesWidget.this,file.getFreeSpace())));
	        sdcard_total.setText(" "+String.valueOf(FileUtils.formatSize(SearchFilesWidget.this,file.getTotalSpace())));
	      
	         }
	         else{
	             sdcard_free.setText(" "+String.valueOf(FileUtils.formatSize(SearchFilesWidget.this,stat.getAvailableBlocks()*stat.getBlockSize())));
	             sdcard_total.setText(" "+String.valueOf(FileUtils.formatSize(SearchFilesWidget.this,stat.getBlockCount()*stat.getBlockSize())));
	           
	         }
	         
	     
	        //
	        read = (TextView)dialog1.findViewById(R.id.file_read_access);
	        read.setText(" "+String.valueOf(file.canWrite()));
	        //
	        write = (TextView)dialog1.findViewById(R.id.file_write_access);
	        write.setText(" "+String.valueOf(file.canWrite()));
	        //
	        hidden = (TextView)dialog1.findViewById(R.id.file_hidden);
	        hidden.setText(" "+String.valueOf(file.isHidden()));
	        //
	        
	        dialog1.show(); 
	        dialog1.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.settings_mdpi);
	        

			
	    	
	    }
    
	 public void OnclickOperation(final String filepath){
	    	
	    	
	    	
	    	final File file = new File(filepath);
	    	final String ext = FileUtils.getExtension(filepath);
	    	
	    	
	     	 
	     	 
	   if (ext.equalsIgnoreCase(".pdf"))
			 {
		    Intent pdfIntent = new Intent();
   		    pdfIntent.setAction(android.content.Intent.ACTION_VIEW);
   		    pdfIntent.setDataAndType(Uri.fromFile(file), "application/pdf");
   	      	try{
			   startActivity(pdfIntent);
   		       }catch (Exception e) {
				// TODO: handle exception
   			    final Intent intent = new Intent(SearchFilesWidget.this, PDFViewer.class);
                intent.putExtra(PdfViewerActivity.EXTRA_PDFFILENAME, filepath);
   		        startActivity(intent);

   			//showMessage("couldn't find a PDF viewer");
			   }
	        } 
	 
        else {
		    String mimeType = MimeTypes.getMimeType(file.getName());
            Intent myIntent = new Intent();
			myIntent.setAction(android.content.Intent.ACTION_VIEW);
			myIntent.setDataAndType(Uri.fromFile(file), mimeType);
			try {
			startActivity(myIntent);
			}catch (Exception e) {
			// TODO: handle exception
				Toast.makeText(this, "No application to open  file", 
					Toast.LENGTH_SHORT).show();
		     }
	  			
	     
	     		
	     	}


	    	
	    }
	
	
	private final static Comparator<? super String> type = new Comparator<String>() {
		
		public int compare(String arg0, String arg1) {
			String ext = null;
			String ext2 = null;
			int ret;
			File file1 = new File(arg0);
			File file2 = new File(arg1);
			
			
			try {
				ext = arg0.substring(arg0.lastIndexOf(".") + 1, arg0.length()).toLowerCase();
				ext2 = arg1.substring(arg1.lastIndexOf(".") + 1, arg1.length()).toLowerCase();
				
			} catch (IndexOutOfBoundsException e) {
				return 0;
			}
			ret = ext.compareTo(ext2);
			
			if (ret == 0)
					return arg0.toLowerCase().compareTo(arg1.toLowerCase());
			else{
				if ((file1.isDirectory()) && (!file2.isDirectory()))
     				return -1;
     			if ((!file1.isDirectory()) && (file2.isDirectory()))
     				return 1;
				
			}
			
			return ret;
		}
	};
	
	
	 class ViewHolder {
	        public TextView name=null;
	        public TextView info=null;
	        public ImageView image=null;
	      
	        ViewHolder(View row){
	      	  name = (TextView)row.findViewById(R.id.label);
	      	  name.setTypeface(Fonts.ICS);
	      	  info = (TextView)row.findViewById(R.id.info);
	     	  info.setTypeface(Fonts.ICS);
	      	  
	      	  image = (ImageView)row.findViewById(R.id.icon);
	      	 
	      	  
	        }
	        void populateFrom(String s)
	        {
	        name.setText(s);
	        }
	      }
	
	 class FoundList extends ArrayAdapter<String> {
		 private ImageThreadLoader imageLoader;
		 private DrawableThreadLoader drawableLoader;


		
		public FoundList() {
			super(SearchFilesWidget.this, R.layout.searchrow,founditems);
	        imageLoader = new ImageThreadLoader(SearchFilesWidget.this);
	        drawableLoader = new DrawableThreadLoader(SearchFilesWidget.this);



			// TODO Auto-generated constructor stub
		}
		
		
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			ViewHolder holder;
			
			
			if(convertView==null){
			LayoutInflater inflater = getLayoutInflater(); //to instantiate layout XML file into its corresponding View objects
		    convertView= inflater.inflate(R.layout.searchrow, null); //to Quick access to the LayoutInflater  instance that this Window retrieved from its Context.
		    holder = new ViewHolder(convertView);
			convertView.setTag(holder);

			}  
			else
			{
				holder = (ViewHolder)convertView.getTag();
			}
			
	    

			final String filepath =founditems[position];
			File f = new File(filepath);
			
		    String ext = FileUtils.getExtension(filepath);
			String filename =f.getName().toLowerCase();
			String highlightString =SearchString.toLowerCase();
			int startIndex = filename.indexOf(highlightString);
			if(startIndex!=-1){
				int len = SearchString.length();
				String strPart = f.getName().substring(startIndex, startIndex + len);
				filename = f.getName().replace(strPart, "<font color=\"#91DEF7\">" + strPart + "</font>");
				holder.name.setText(Html.fromHtml(filename), TextView.BufferType.SPANNABLE);
			}else{
	    	
			holder.name.setText(f.getName());
			}
		
			 if(f.isDirectory()){ //decide are the file folder or file
					holder.image.setTag(filepath);  
		 			holder.info.setTag(filepath);  

	             //holder.info.setText("<DIR>");
	 		     holder.image.setImageDrawable(getIcon(filepath));
				
			  }
			 
			 
            if(f.isFile() && ext.equalsIgnoreCase(".apk")){ //decide are the file folder or file
				 
				 //setPlaceholder(getResources().getDrawable(R.drawable.apk_file));
				 holder.image.setTag(filepath);
				 drawableLoader.displayImage(filepath,SearchFilesWidget.this,holder.image);
			     //loadDrawable(f.getAbsolutePath(),holder.image);
				
			}
	 	 else if (f.isFile() &&ext.equalsIgnoreCase(".jpg")
	 			 ||ext.equalsIgnoreCase(".png")
	 			 ||ext.equalsIgnoreCase(".gif")
	 			 ||ext.equalsIgnoreCase(".jpeg")
	 			 ||ext.equalsIgnoreCase(".tiff")){
				 
	 		  
	             //Drawable icon = getResources().getDrawable(R.drawable.image);	    		  
	 		    //Bitmap bitmap = ((BitmapDrawable)icon ).getBitmap();
	 		   // BitmapManager.INSTANCE.setPlaceholder(bitmap);
			        holder.image.setTag(f.getAbsolutePath());  
			        imageLoader.displayImage(filepath,SearchFilesWidget.this, holder.image);

			        //BitmapManager.INSTANCE.loadBitmap(f.getAbsolutePath(),holder.image);  
		 			holder.info.setTag(filepath);  

					//holder.info.setText(FileUtils.formatSize(SearchFilesDialog.this,f.length()));

			  
			}
			 
	 	 else if (f.isFile() &&!ext.equalsIgnoreCase(".apk")){
				holder.image.setTag(filepath);  

			   holder.image.setImageDrawable(getIcon(filepath));
				holder.info.setTag(filepath);  

			  // holder.info.setText(FileUtils.formatSize(SearchFilesDialog.this,f.length()));


	 	 }
			 
			 
			return(convertView);
		}
		
		
		
	}
	Drawable getIcon(String filepath){
    	File file = new File(filepath);
    	String filename=file.getName().toString(); //get the file according the position
		
    	String ext = null;
    	try {
    		 ext = filename.substring(filename.lastIndexOf("."), filename.length());
    		
    	    } catch(IndexOutOfBoundsException e) {	
    		  ext = ""; 
    	    }
		
    	 if(file.isDirectory()){ 
				return getResources().getDrawable(R.drawable.myfolder72) ;
				
			 }
    	 else{
			 
			  if (ext.equalsIgnoreCase(".zip")){
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
				 
			     return new FileUtils(SearchFilesWidget.this).getapkicon(filepath);
		
			 }
			  else if (ext.equalsIgnoreCase(".mp4")
					 ||ext.equalsIgnoreCase(".3gp")
					 ||ext.equalsIgnoreCase(".flv")
					 || ext.equalsIgnoreCase(".ogg")
					 ||ext.equalsIgnoreCase(".m4v")){
				 return getResources().getDrawable(R.drawable.videos_new);
			 }
    	 
			 else if (ext.equalsIgnoreCase(".sh")||ext.equalsIgnoreCase(".rc")){
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
    }
	
	public Drawable getScaledIcon(String filepath){
    	Drawable icon = getIcon(filepath);
  		Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
        int dp5 = (int)(getResources().getDisplayMetrics().densityDpi/120);
		icon= new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(bitmap, 50*dp5, 50*dp5, true));
		return icon;

    }
	
	
	
	
}
