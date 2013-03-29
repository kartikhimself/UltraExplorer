package com.mirrorlabs.widgets;



import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.mirrorlabs.filebrowser.ProcessManager;
import com.mirrorlabs.filebrowser.R;
import com.mirrorlabs.filebrowser.SearchFilesDialog;
import com.mirrorlabs.filebrowser.SearchFilesWidget;

public class UpdateWidgetService extends Service {
	private static final String LOG = "com.mirrorlabs.filebrowser";

	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(LOG, "Called");
		// Create some random data

		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this
				.getApplicationContext());

		int[] allWidgetIds = intent
				.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

		ComponentName thisWidget = new ComponentName(getApplicationContext(),
				ExampleAppWidgetProvider.class);
		int[] allWidgetIds2 = appWidgetManager.getAppWidgetIds(thisWidget);
		Log.w(LOG, "From Intent" + String.valueOf(allWidgetIds.length));
		Log.w(LOG, "Direct" + String.valueOf(allWidgetIds2.length));

		for (int widgetId : allWidgetIds) {
			
			
			RemoteViews remoteViews = new RemoteViews(this
					.getApplicationContext().getPackageName(),
					R.layout.widget_layout);
			
			// Register an onClickListener
			Intent clickIntent = new Intent(this.getApplicationContext(),
					ExampleAppWidgetProvider.class);
			Intent intent1 = new Intent(this.getApplicationContext(),SearchFilesWidget.class);
            
			clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
					allWidgetIds);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(
					getApplicationContext(), 0, clickIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			PendingIntent pendingIntent1 = PendingIntent.getActivity(this.getApplicationContext(), 0, intent1, 0);
			remoteViews.setOnClickPendingIntent(R.id.processWidget, pendingIntent1);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
		stopSelf();

		super.onStart(intent, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
