package net.teamdouche.stats.CMStatsWidget.widget;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import net.teamdouche.stats.CMStatsWidget.R;

import org.json.JSONObject;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

public class CMStatsWidgetProvider extends AppWidgetProvider {

    private static Timer timer = null;
    private static final String TAG = "CMStatsWidget";
    private static final String mainurl = "http://stats.cyanogenmod.com/live";
    private static Context mContext = null;
    private static int mAppWidgetId = 0;
    private static AppWidgetManager mAppWidgetManager = null;
    private static final String PREFS_NAME = "widget_prefs";
    public static SharedPreferences prefs = null;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                                                           int[] appWidgetIds){
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        mContext = context;
        prefs = mContext.getSharedPreferences(PREFS_NAME, 0);
        mAppWidgetManager = appWidgetManager;
        mAppWidgetId = appWidgetIds[0];
        doWork();
    }

    public static void cancelTimer(){
        if (timer != null){
            timer.cancel();
        }
    }

    public static void setTimer(long interval){
        if (timer == null){
            timer = new Timer();
            timer.scheduleAtFixedRate(new Task(), 1, interval);
        }
    }

    private static boolean hasConnection(){
        ConnectivityManager cm = (ConnectivityManager) 
                       mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo network_info = cm.getActiveNetworkInfo();
        if (network_info!=null && network_info.isConnected()) {
            return true;
        }
        return false;
    }

    private static class Task extends TimerTask{
        @Override
        public void run() {
            new Thread(new Runnable(){
                @Override
                public void run(){
                    doWork();
                }
            }).start();
            
        }
        
    }

    private static void doWork(){
        if (!hasConnection()){
            return;
        }
        try{
            URL url = new URL(mainurl);
            HttpURLConnection connection =
                      (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept",
                                            "application/xml");
            connection.setRequestProperty("Content-Type",
                                            "application/xml");
            connection.setRequestProperty("X-Requested-With",
                                             "XMLHttpRequest");
            InputStream is = connection.getInputStream();
            String jsonStr = inputStreamToString(is);
            JSONObject json = new JSONObject(jsonStr);
            RemoteViews views = new RemoteViews(
                   mContext.getPackageName(), R.layout.widget);
            String count = json.getString("count");
            views.setTextViewText(R.id.counter, count);
            Log.i("FUUU    ", "\n\n\n\n\nFUUU: " + count + "\n\n\n\n");
            boolean useTouch = prefs.getBoolean("useTouch", false);
            //boolean useBackground = prefs.getBoolean(
            //                           "useBackground", false);
            if (useTouch){
                int widgetIds[] = {mAppWidgetId};
                Intent i = new Intent();
                i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
                i.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, i,
                                            PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.counter, pi);
                views.setOnClickPendingIntent(R.id.title, pi);
            }
            mAppWidgetManager.updateAppWidget(mAppWidgetId, views);
        } catch (Exception e){
            Log.e(TAG, e.toString());
        }
    }

    public static String inputStreamToString(InputStream in) 
    throws IOException {
    
        BufferedInputStream bis = new BufferedInputStream(in);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while(result != -1) {
          byte b = (byte)result;
          buf.write(b);
          result = bis.read();
        }
        return buf.toString();
    }
}
