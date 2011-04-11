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

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.RemoteViews;

public class CMStatsWidgetProvider extends AppWidgetProvider {

    private final String TAG = "CMStatsWidget";
    private final String mainurl = "http://stats.cyanogenmod.com/live";
    private Context mContext = null;
    private Timer timer = null;
    private int mAppWidgetId = 0;
    private AppWidgetManager mAppWidgetManager = null;;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                                                           int[] appWidgetIds){
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        mContext = context;
        mAppWidgetManager = appWidgetManager;
        mAppWidgetId = appWidgetIds[0];
        if (timer == null){
            timer = new Timer();
            timer.scheduleAtFixedRate(new Task(), 1, 5000);
        }
    }

    private boolean hasConnection(){
        ConnectivityManager cm = (ConnectivityManager) 
                       mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo network_info = cm.getActiveNetworkInfo();
        if (network_info!=null && network_info.isConnected()) {
            return true;
        }
        return false;
    }

    private class Task extends TimerTask{
        @Override
        public void run() {
            new Thread(new Runnable(){
                @Override
                public void run(){
                    try{
                        if (!hasConnection()){
                            return;
                        }
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
                        mAppWidgetManager.updateAppWidget(mAppWidgetId, views);
                    } catch (Exception e){
                        Log.e(TAG, e.toString());
                    }
                }
            }).start();
            
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
