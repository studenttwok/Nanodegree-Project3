package barqsoft.footballscores;


import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.service.WidgetRemoteViewsService;
import barqsoft.footballscores.service.myFetchService;


public class TodayWidgetProvider extends AppWidgetProvider{
    private final String LOG_TAG = TodayWidgetProvider.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId: appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_today);

            // get current Date
            long currentTime = System.currentTimeMillis();
            Date dateNow = new Date(currentTime);
            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = mformat.format(dateNow);

            views.setTextViewText(R.id.date_textview, dateStr);

            // Crweate intent to refresh
            Intent refreshIntent = new Intent();
            refreshIntent.setClass(context, TodayWidgetProvider.class);

            refreshIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");

            PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, 0);
            views.setOnClickPendingIntent(R.id.refresh_button, refreshPendingIntent);


            // Create an Intent to launch MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.date_textview, pendingIntent);

            // Set up the collection
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context, views);
            } else {
                setRemoteAdapterV11(context, views);
            }

            Intent clickIntentTemplate = new Intent(context, MainActivity.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context).addNextIntentWithParentStack(clickIntentTemplate).getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.scores_list, clickPendingIntentTemplate);
            views.setEmptyView(R.id.scores_list, R.id.widget_empty);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "onReceive: " + intent.getAction());

        if (intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE")) {
            update_scores(context);

        } else if (intent.getAction().equals("barqsoft.footballscores.ACTION_DATA_UPDATED")) {

            // update the data
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, TodayWidgetProvider.class));

            onUpdate(context, appWidgetManager, appWidgetIds);

        }
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.scores_list, new Intent(context, WidgetRemoteViewsService.class).putExtra("date", System.currentTimeMillis()+""));
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(0, R.id.scores_list, new Intent(context, WidgetRemoteViewsService.class).putExtra("date", System.currentTimeMillis()+""));
    }

    private void update_scores(Context context)
    {
        Intent service_start = new Intent(context, myFetchService.class);
        context.startService(service_start);
    }

}
