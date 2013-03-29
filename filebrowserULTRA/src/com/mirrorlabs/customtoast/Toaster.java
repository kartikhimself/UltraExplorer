package com.mirrorlabs.customtoast;



import com.mirrorlabs.filebrowser.R;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;
import android.widget.Toast;

public class Toaster {

	/**
	 * Show a toast message to the user
	 * 
	 * @param ctx
	 *            The context to use
	 * @param resId
	 *            the id of the string to display
	 * @param error
	 *            is the message an error (changes the text to red)
	 */
	public static void showToast(Context ctx, int resId, boolean error) {
		Toast toast;
		TextView tv;
		

		toast = Toast.makeText(ctx, resId, Toast.LENGTH_SHORT);
		if (error) {
			tv = (TextView) toast.getView().findViewById(android.R.id.message);
			
			tv.setTextColor(mError);
			tv.setTextSize(15);
			tv.setCompoundDrawablesWithIntrinsicBounds(ctx.getResources().getDrawable(R.drawable.smiley_frown),null,null,null);
			int dp5 = (int) (5 * ctx.getResources().getDisplayMetrics().density + 0.5f);
            tv.setCompoundDrawablePadding(dp5);
			toast.setDuration(Toast.LENGTH_LONG);
		}
		toast.show();
	}

	protected static final int mError = Color.rgb(00,00,00);
}
