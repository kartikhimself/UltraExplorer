package com.mirrorlabs.filebrowser;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;


@ReportsCrashes(formKey = "dHBUNExfOG9vUlZ5VlROMW5fRU1FU3c6MQ") 
public class MyApplication extends Application{
	
	 @Override
	    public void onCreate() {
	        // The following line triggers the initialization of ACRA
	        ACRA.init(this);
	        super.onCreate();
	    }

}
