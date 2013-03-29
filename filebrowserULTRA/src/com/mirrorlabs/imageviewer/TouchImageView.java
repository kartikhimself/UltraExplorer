package com.mirrorlabs.imageviewer;

import java.io.File;

import com.mirrorlabs.filebrowser.FileUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class TouchImageView extends ImageView {

	private static final String TAG = "Touch";
	private static final int MAX_PIXELS = 1000*8*32; // Megabytes to pixels
	// These matrices will be used to move and zoom image
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
	private Bitmap image;
	private File imageFile;

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	// Remember some things for zooming
	private PointF start = new PointF();
	private PointF mid = new PointF();
	private float oldDist = 1f;
	
	private Context context;
	
	public TouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
		super.setClickable(true);
		this.context = context;
		
		matrix.setTranslate(1f, 1f);
		setImageMatrix(matrix);
		setScaleType(ScaleType.MATRIX);
		
		setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent rawEvent) {
				WrapMotionEvent event = WrapMotionEvent.wrap(rawEvent);

				// Dump touch event to log
				if (FileUtils.isDebug){
					dumpEvent(event);
				}

				// Handle touch events here...
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					savedMatrix.set(matrix);
					start.set(event.getX(), event.getY());
					Log.d(TAG, "mode=DRAG");
					mode = DRAG;
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					oldDist = spacing(event);
					Log.d(TAG, "oldDist=" + oldDist);
					if (oldDist > 10f) {
						savedMatrix.set(matrix);
						midPoint(mid, event);
						mode = ZOOM;
						Log.d(TAG, "mode=ZOOM");
					}
					break;
				case MotionEvent.ACTION_UP:
					int xDiff = (int) Math.abs(event.getX() - start.x);
					int yDiff = (int) Math.abs(event.getY() - start.y);
					if (xDiff < 15 && yDiff < 15){
						performClick();
					}
				case MotionEvent.ACTION_POINTER_UP:
					mode = NONE;
					Log.d(TAG, "mode=NONE");
					break;
				case MotionEvent.ACTION_MOVE:
					if (mode == DRAG) {

						matrix.set(savedMatrix);
						matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
					} else if (mode == ZOOM) {
						float newDist = spacing(event);
						Log.d(TAG, "newDist=" + newDist);
						if (newDist > 10f) {
							matrix.set(savedMatrix);
							float scale = newDist / oldDist;
							matrix.postScale(scale, scale, mid.x, mid.y);
						}
					}
					break;
				}

				setImageMatrix(matrix);
				return true; // indicate event was handled
			}

		});
	}
	
	
	 public void setImage(Bitmap img, int displayWidth, int displayHeight) { 
		super.setImageBitmap(img);
		image = img;
		centerImage();
	 }
	 
	 public File getFile(){
		 return imageFile;
	 }
	 
	 @Override
	 public void onSizeChanged(int displayWidth, int displayHeight,int s, int d){
		 centerImage();
	 }
	 
	 private void centerImage(){
		 int width = super.getWidth();
		 int height = super.getHeight();
		 	FileUtils.printDebug("Centered and resized image");
			float scale;
			if ((height / image.getHeight()) >= (width / image.getWidth())){
				scale =  (float)width / (float)image.getWidth();
			} else {
				scale = (float)height / (float)image.getHeight();
			}
			
			matrix.reset();
			savedMatrix.reset();
			setImageMatrix(matrix);
			
			matrix.postScale(scale, scale, mid.x, mid.y);
			setImageMatrix(matrix);
			
			float redundantYSpace = (float)height - (scale * (float)image.getHeight()) ;
			float redundantXSpace = (float)width - (scale * (float)image.getWidth());
			
			redundantYSpace /= (float)2;
			redundantXSpace /= (float)2;

			savedMatrix.set(matrix);
			matrix.postTranslate(redundantXSpace, redundantYSpace);
			
			setImageMatrix(matrix);
	 }
	
	 
	/** Show an event in the LogCat view, for debugging */
	 private void dumpEvent(WrapMotionEvent event) {
		// ...
		String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
				"POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
		StringBuilder sb = new StringBuilder();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		sb.append("event ACTION_").append(names[actionCode]);
		if (actionCode == MotionEvent.ACTION_POINTER_DOWN
				|| actionCode == MotionEvent.ACTION_POINTER_UP) {
			sb.append("(pid ").append(
					action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			sb.append(")");
		}
		sb.append("[");
		for (int i = 0; i < event.getPointerCount(); i++) {
			sb.append("#").append(i);
			sb.append("(pid ").append(event.getPointerId(i));
			sb.append(")=").append((int) event.getX(i));
			sb.append(",").append((int) event.getY(i));
			if (i + 1 < event.getPointerCount())
				sb.append(";");
		}
		sb.append("]");
		Log.d(TAG, sb.toString());
	}

	/** Determine the space between the first two fingers */
	private float spacing(WrapMotionEvent event) {
		// ...
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/** Calculate the mid point of the first two fingers */
	private void midPoint(PointF point, WrapMotionEvent event) {
		// ...
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}
}
