package net.teamdouche.stats.CMStatsWidget.settings;

import java.util.HashMap;

import net.teamdouche.stats.CMStatsWidget.R;
import net.teamdouche.stats.CMStatsWidget.widget.CMStatsWidgetProvider;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class CMStatsWidget extends Activity {


    private static final String PREFS_NAME = "widget_prefs";
    private static Context mContext = null;

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private WebView adView = null;

    //private CheckBox background = null;
    //private CheckBox touch = null;
    private CheckBox intervalBox = null;
    private CheckBox layout = null;
    private Spinner intervalValue = null;
    private Button saveBtn = null;
    private Button cnclBtn = null;

    @SuppressWarnings("serial")
    private HashMap<String,Integer> intervalMap=new HashMap<String,Integer>(){
        {
            put("Never", 0);
            put("1 Second", 1000);
            put("5 Seconds", 5000);
            put("10 Seconds", 10000);
            put("30 Seconds", 30000);
            put("1 Minute", 60000);
            put("5 Minutes", 300000);
            put("10 Minutes", 600000);
            put("30 Minutes", 1800000);
            put("60 Minutes", 3600000);
        }
    };

    private boolean useBackground = false;
    private boolean useTouch = false;
    private boolean useInterval = false;
    private long interval = 0;

    public CMStatsWidget(){
        super();
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.configure);
        setResult(RESULT_CANCELED);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, 
                                       AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
        adView = (WebView) findViewById(R.id.adview);
        String url = "http://www.optedoblivion.com/stat_ad.html";
        adView.getSettings().setJavaScriptEnabled(true);
        adView.loadUrl(url);
        Log.i("FUU   ", "Loading: " + url);
        cnclBtn = (Button) findViewById(R.id.cancel);
        saveBtn = (Button) findViewById(R.id.save);
        //background = (CheckBox) findViewById(R.id.background);
        //useBackground = background.isChecked();
        //touch = (CheckBox) findViewById(R.id.touch);
        //useTouch = touch.isChecked();
        intervalBox = (CheckBox) findViewById(R.id.interval);
        layout = (CheckBox) findViewById(R.id.layout);
        useInterval = intervalBox.isChecked();
        intervalValue = (Spinner) findViewById(R.id.intervalValue);
        String i = intervalValue.getSelectedItem().toString();
        interval = intervalMap.get(i);
        intervalBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                useInterval = intervalBox.isChecked();
                if (useInterval){
                    intervalValue.setVisibility(View.VISIBLE);
                } else {
                    intervalValue.setVisibility(View.INVISIBLE);
                }
            }
        });

        cnclBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //useBackground = background.isChecked();
                //useTouch = touch.isChecked();
                useInterval = intervalBox.isChecked();
                String i = intervalValue.getSelectedItem().toString();
                interval = intervalMap.get(i);
                CMStatsWidgetProvider.prefs =
                                  mContext.getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = 
                                            CMStatsWidgetProvider.prefs.edit();
                editor.putBoolean("useBackground", useBackground);
                editor.putBoolean("useTouch", useTouch);
                editor.putBoolean("useInterval", useInterval);
                editor.putBoolean("useOldLayout", layout.isChecked());
                editor.putLong("interval", interval);
                editor.commit();
                if (useInterval && interval > 0){
                    CMStatsWidgetProvider.setTimer(interval);
                }
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 
                                                                 mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

}