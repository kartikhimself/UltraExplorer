package com.mirrorlabs.filebrowser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.widget.ImageView;

public class ImageThreadLoader {

    //the simplest in-memory cache implementation. This should be replaced with something like SoftReference or BitmapOptions.inPurgeable(since 1.6)
    /** The cache. */
    private HashMap<String, Bitmap> cache=new HashMap<String, Bitmap>();
  
 


    /** The cache dir. */
    private File cacheDir;
    private static final String PREF_CACHE= "cachefiles";
    private  SharedPreferences prefs; 
    private boolean cachefiles;



    /**
     * Instantiates a new image loader.
     *
     * @param context the context
     */
    public ImageThreadLoader(Context context){
        //Make the background thead low priority. This way it will not affect the UI performance
        photoLoaderThread.setPriority(Thread.NORM_PRIORITY-1);

        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory()+"/Ultra Explorer/","cache_img");
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
        
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        cachefiles=prefs.getBoolean(PREF_CACHE,true);
        cachefiles=PreferenceActivity.CacheFiles(context);


    }
    //This is used for a stub when the user can not see the actual image..
    //this images will be seen
    final int stub_id =R.drawable.image;


    /**
     * Display image.
     *
     * @param url the url
     * @param activity the activity
     * @param imageView the image view
     */
    

    
    public void displayImage(String url, Activity activity, ImageView imageView)
    {
        if(cache.containsKey(url))
            imageView.setImageBitmap(cache.get(url));
        else
        {
            queuePhoto(url, activity, imageView);
            imageView.setImageResource(stub_id);
        }    
    }
    
    /**
     * Cache image.
     *
     * @param bmp the Bitmap
     * @param url the filepath
     */
    
  public void CreateCacheFile(Bitmap bmp,String url){
    	
    	try{
    	File img = new File("/mnt/sdcard/Ultra Explorer/cache_img/"+String.valueOf(FileUtils.getFastHash(url)));
	    FileOutputStream fOut = new FileOutputStream(img);
        bmp.compress(Bitmap.CompressFormat.PNG, 85, fOut);
	    fOut.flush();
	    fOut.close();
    	}catch (Exception e) {
			// TODO: handle exception
		}
  }
    /**
     * Queue photo.
     *
     * @param url the url
     * @param activity the activity
     * @param imageView the image view
     */
    private void queuePhoto(String url, Activity activity, ImageView imageView)
    {
        //This ImageView may be used for other images before. So there may be some old tasks in the queue. We need to discard them. 
        photosQueue.Clean(imageView);
        PhotoToLoad p=new PhotoToLoad(url, imageView);
        synchronized(photosQueue.photosToLoad){
            photosQueue.photosToLoad.push(p);
            photosQueue.photosToLoad.notifyAll();
        }

        //start thread if it's not started yet
        if(photoLoaderThread.getState()==Thread.State.NEW)
            photoLoaderThread.start();
    }

    /**
     * Gets the bitmap.
     *
     * @param url the url
     * @return the bitmap
     */
    private Bitmap getBitmap(String url) 
    {
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        String filename=String.valueOf(FileUtils.getFastHash(url));
        File f=new File(cacheDir, filename);
        Bitmap d = BitmapFactory.decodeFile(f.getAbsolutePath());


        //from SD cache
        if(d!=null)
            return d;
        Bitmap b1 = getBitmapFromCache(url);
        if(b1!=null)
            return b1;
        

        //from web
        try {
            File image = new File(url);
            int size =72;
            
            InputStream photoStream = null;
    		Bitmap mBitmap = null;
    		try {
    			photoStream = new FileInputStream(image);
    			BitmapFactory.Options opts = new BitmapFactory.Options();
    			opts.inJustDecodeBounds = true;
    			opts.inSampleSize = 1;

    			mBitmap = BitmapFactory.decodeStream(photoStream, null, opts);
    			if (opts.outWidth > opts.outHeight && opts.outWidth > size) {
    				opts.inSampleSize = opts.outWidth / size;
    			} else if (opts.outWidth < opts.outHeight && opts.outHeight > size) {
    				opts.inSampleSize = opts.outHeight / size;
    			}
    			if (opts.inSampleSize < 1) {
    				opts.inSampleSize = 1;
    			}
    			opts.inJustDecodeBounds = false;
    			photoStream.close();
    			photoStream = new FileInputStream(image);
    			mBitmap = BitmapFactory.decodeStream(photoStream, null, opts);
    			
    	        System.gc();

    		} catch (Exception e) {
    			e.printStackTrace();
    		} finally {
    			if (photoStream != null) {
    				try {
    					photoStream.close();
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
    			}
    		}
    		
            cache.put(url, mBitmap);  

    		return mBitmap;
        } catch (Exception ex){
           ex.printStackTrace();
           return null;
        }
    }

    //decodes image and scales it to reduce memory consumption
    /**
     * Decode file.
     *
     * @param f the f
     * @return the bitmap
     */
    private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=72;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale++;
            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }

    //Task for the queue
    /**
     * The Class PhotoToLoad.
     */
    private class PhotoToLoad
    {

        /** The url. */
        public String url;
      
        /** The image view. */
        public ImageView imageView;

        /**
         * Instantiates a new photo to load.
         *
         * @param u the u
         * @param i the i
         */
        public PhotoToLoad(String u, ImageView i){
            url=u; 
            imageView=i;
        }
        
        
    }
    
    
    
    public Bitmap getBitmapFromCache(String url) {  
        if (cache.containsKey(url)) {  
            return cache.get(url); 
        }  
  
        return null;  
    }  
  

    /** The photos queue. */
    PhotosQueue photosQueue=new PhotosQueue();

    /**
     * Stop thread.
     */
    public void stopThread()
    {
        photoLoaderThread.interrupt();
    }

    //stores list of photos to download
    /**
     * The Class PhotosQueue.
     */
    class PhotosQueue
    {

        /** The photos to load. */
        private Stack<PhotoToLoad> photosToLoad=new Stack<PhotoToLoad>();

        //removes all instances of this ImageView
        /**
         * Clean.
         *
         * @param image the image
         */
        public void Clean(ImageView image)
        {
            for(int j=0 ;j<photosToLoad.size();){
                if(photosToLoad.get(j).imageView==image)
                    photosToLoad.remove(j);
                else
                    ++j;
            }
        }
    }

    /**
     * The Class PhotosLoader.
     */
    class PhotosLoader extends Thread {

        /* (non-Javadoc)
         * @see java.lang.Thread#run()
         */
    	
        public void run() {
            try {
                while(true)
                {
                    //thread waits until there are any images to load in the queue
                    if(photosQueue.photosToLoad.size()==0)
                        synchronized(photosQueue.photosToLoad){
                            photosQueue.photosToLoad.wait();
                        }
                    if(photosQueue.photosToLoad.size()!=0)
                    {
                        PhotoToLoad photoToLoad;
                        synchronized(photosQueue.photosToLoad){
                            photoToLoad=photosQueue.photosToLoad.pop();
                        }
                       

                        Bitmap bmp=getBitmap(photoToLoad.url);
                        if(cachefiles){
                        CreateCacheFile(bmp, photoToLoad.url);
                        }
                        cache.put(photoToLoad.url, bmp);
                        if(((String)photoToLoad.imageView.getTag()).equals(photoToLoad.url)){
                            BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad.imageView);
                            Activity a=(Activity)photoToLoad.imageView.getContext();
                            a.runOnUiThread(bd);
                        }
                    }
                    if(Thread.interrupted())
                        break;
                }
            } catch (InterruptedException e) {
                //allow thread to exit
            }
        }
    }

    /** The photo loader thread. */
    PhotosLoader photoLoaderThread=new PhotosLoader();

    //Used to display bitmap in the UI thread
    /**
     * The Class BitmapDisplayer.
     */
    class BitmapDisplayer implements Runnable
    {

        /** The bitmap. */
        Bitmap bitmap;

        /** The image view. */
        ImageView imageView;

        /**
         * Instantiates a new bitmap displayer.
         *
         * @param b the b
         * @param i the i
         */
        public BitmapDisplayer(Bitmap b, ImageView i){bitmap=b;imageView=i;}

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            if(bitmap!=null)
                imageView.setImageBitmap(bitmap);
            else
              imageView.setImageResource(stub_id);
        }
    }

    /**
     * Clear cache.
     */
    public void clearCache() {
        //clear memory cache
        cache.clear();

        //clear SD cache
        File[] files=cacheDir.listFiles();
        for(File f:files)
            f.delete();
    }

     public static void copyStream(InputStream is, OutputStream os) {
            final int buffer_size=1024;
            try
            {
                byte[] bytes=new byte[buffer_size];
                for(;;)
                {
                  int count=is.read(bytes, 0, buffer_size);
                  if(count==-1)
                      break;
                  os.write(bytes, 0, count);
                }
            }
            catch(Exception ex){}
        }
	


}

