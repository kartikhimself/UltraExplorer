package com.mirrorlabs.filebrowser;

import android.view.KeyEvent;




public abstract class BaseActivity extends FilebrowserULTRAActivity {
	

@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
     
	  
	
	  if ((keyCode == KeyEvent.KEYCODE_BACK)){
		  
         this.finish();
         return false;
        
        }
	 
	 
   
    return super.onKeyDown(keyCode, event);
   }
}