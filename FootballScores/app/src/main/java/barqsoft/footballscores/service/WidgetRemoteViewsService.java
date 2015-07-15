package barqsoft.footballscores.service;


import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;

/**
 * RemoteViewsService controlling the data being shown in the scrollable weather detail widget
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetRemoteViewsService extends RemoteViewsService {

    public final String LOG_TAG = WidgetRemoteViewsService.class.getSimpleName();

    private static final String[] SCORES_COLUMNS = {
            DatabaseContract.scores_table._ID,
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL,
            DatabaseContract.scores_table.DATE_COL,
            DatabaseContract.scores_table.TIME_COL,
    };

    // these indices must match the projection
    static final int INDEX_SCORE_ID = 0;
    static final int INDEX_SCORE_HOME = 1;
    static final int INDEX_SCORE_AWAY = 2;
    static final int INDEX_SCORE_HOME_GOALS = 3;
    static final int INDEX_SCORE_AWAY_GOALS = 4;
    static final int INDEX_SCORE_DATE = 5;
    static final int INDEX_SCORE_TIME = 6;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
                Log.d(LOG_TAG, "onCreate");
            }

            @Override
            public void onDataSetChanged() {
                Log.d(LOG_TAG, "onDataSetChanged");
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();

                Date dateNow = new Date(System.currentTimeMillis());
                SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");

                String[] fragmentdate = new String[1];
                fragmentdate[0] = mformat.format(dateNow);

                Uri matchUri = DatabaseContract.scores_table.buildScoreWithDate();
                data = getContentResolver().query(matchUri, SCORES_COLUMNS, null, fragmentdate, null);

                Log.d(LOG_TAG, "dataCount:" + data.getCount());

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_scores_list_item);

                // set data from cursor...
                String description = "";
                String homeName = data.getString(INDEX_SCORE_HOME);
                String awayName = data.getString(INDEX_SCORE_AWAY);
                String score = data.getString(INDEX_SCORE_HOME_GOALS) + " - " + data.getString(INDEX_SCORE_AWAY_GOALS);
                String date = data.getString(INDEX_SCORE_DATE);
                String time = data.getString(INDEX_SCORE_TIME);


                if (data.getString(INDEX_SCORE_HOME_GOALS).equals("-1") ||  data.getString(INDEX_SCORE_AWAY_GOALS).equals("-1")) {
                    score = " - ";
                }

                views.setTextViewText(R.id.home_name, homeName);
                views.setTextViewText(R.id.score_textview, score);
                views.setTextViewText(R.id.away_name, awayName);
                views.setTextViewText(R.id.data_textview, time);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(views, description);
                }

                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(R.id.icon, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_scores_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position)) {
                    return data.getLong(INDEX_SCORE_ID);
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
