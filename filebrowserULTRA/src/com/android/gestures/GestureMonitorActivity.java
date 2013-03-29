package com.android.gestures;

import java.util.ArrayList;

import com.mirrorlabs.filebrowser.R;


import android.app.Activity;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
 
public class GestureMonitorActivity extends Activity {
 
GestureLibrary gestureLibrary = null;
GestureOverlayView gestureOverlayView;
 
 /** Called when the activity is first created. */
 @Override
 public void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     setContentView(R.layout.gesture_detect);
     gestureOverlayView = (GestureOverlayView)findViewById(R.id.gestures);
   
     gestureLibrary = GestureLibraries.fromFile(Environment.getExternalStorageDirectory()+"/Ultra Explorer/gestures");
     gestureLibrary.load();
   
     gestureOverlayView.addOnGesturePerformedListener(gesturePerformedListener);
 }
 
  OnGesturePerformedListener gesturePerformedListener= new OnGesturePerformedListener(){
 
 public void onGesturePerformed(GestureOverlayView view, Gesture gesture) {
  // TODO Auto-generated method stub
  ArrayList<Prediction> prediction = gestureLibrary.recognize(gesture);
    if(prediction.size() > 0){
    	String gesture_found = prediction.get(0).name;
    	Log.d("gesture tag", "gesture detected");
    	//Toast.makeText(GestureMonitorActivity.this, gesture_found, Toast.LENGTH_SHORT).show();

    	Intent intent= new Intent();
    	intent.putExtra("gesture",gesture_found);
    	setResult(RESULT_OK,intent);
    	finish();
    	
      }
  
  }};
}