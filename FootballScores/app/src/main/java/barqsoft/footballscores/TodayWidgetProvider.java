package barqsoft.footballscores;


import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;


public class TodayWidgetProvider extends AppWidgetProvider{
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId: appWidgetIds) {
            RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.widget_today);
            view.setTextViewText(R.id.score_textview, "hiiiiiiii");
            appWidgetManager.updateAppWidget(appWidgetId, view);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }
}
