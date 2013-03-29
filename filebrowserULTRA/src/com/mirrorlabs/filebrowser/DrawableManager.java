package com.mirrorlabs.filebrowser;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;


public class DrawableManager {
	
	static final String TAG = "DrawableManager";
	
	private  ConcurrentMap<String, Drawable> cache;
	private   ExecutorService pool;  
	private Activity activity;
    private  Map<ImageView, String> imageViews = Collections  
	            .synchronizedMap(new ConcurrentHashMap<ImageView, String>());  
	private static Drawable placeholder;
	 
	  
	  public DrawableManager(Activity activity) {
	        this.activity = activity;
	    }


  DrawableManager(){
	  cache = new ConcurrentHashMap<String, Drawable>();  
      pool = Executors.newFixedThreadPool(5); 
	
   }

public static void setPlaceholder(Drawable drawable) {  
    placeholder = drawable;  
}  

public void clearCache(){
	cache.clear();
}

public  Drawable getDrawableFromCache(String url) {  
    if (cache.containsKey(url)) {  
        return cache.get(url); 
    }  

    return null;  
}  

public void queueJob(final String url, final ImageView imageView) {  
    /* Create handler in UI thread. */  
    final   Handler handler = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {  
            String tag = imageViews.get(imageView);  
            if (tag != null && tag.equals(url)) {  
                if (msg.obj != null) {  
                    imageView.setImageDrawable((Drawable) msg.obj);  
                } else {  
                    imageView.setImageDrawable(placeholder);  
                    Log.d(null, "fail " + url);  
                }  
            }  
        }  
    };  

    pool.submit(new Runnable() {  
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
    imageViews.put(imageView, url);  
    Drawable drawable = getDrawableFromCache(url); 

    // check in UI thread, so no concurrency issues  
    if (drawable != null) {  
        Log.d(null, "Item loaded from cache: " + url);  
        imageView.setImageDrawable(drawable);  
    } else {  
        imageView.setImageDrawable(placeholder);  
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
	  String filePath = url;
	  try{
	  PackageInfo packageInfo = activity.getPackageManager().getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
	  ApplicationInfo appInfo = packageInfo.applicationInfo;
	 
	 
	  if (Build.VERSION.SDK_INT >= 5) {
	     appInfo.sourceDir = filePath;
	     appInfo.publicSourceDir = filePath;
	 
	 
	     icon = appInfo.loadIcon(activity.getPackageManager());
		 Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
         int dp5 = (int)(75*activity.getResources().getDisplayMetrics().density);

		 icon= new BitmapDrawable(Bitmap.createScaledBitmap(bitmap, dp5, dp5, true));
       }
	   else {
		  icon = activity.getResources().getDrawable(R.drawable.apk_file);
		 
		 }
	 
	  
	 
	cache.put(url, icon);
	return icon;
	 }
	 catch (Exception e) {
		// TODO: handle exception
		 return activity.getResources().getDrawable(R.drawable.apk_file);
	     }
   }

	
 
}
}
