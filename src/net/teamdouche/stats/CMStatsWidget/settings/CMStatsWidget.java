package net.teamdouche.stats.CMStatsWidget.settings;

import net.teamdouche.stats.CMStatsWidget.R;
import net.teamdouche.stats.CMStatsWidget.widget.CMStatsWidgetProvider;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;

import com.google.ads.AdSenseSpec;
import com.google.ads.AdSenseSpec.AdType;
import com.google.ads.GoogleAdView;

public class CMStatsWidget extends Activity {

    private static final boolean TEST = true;

    private static final String CLIENT_ID = "pub-8784552668996850";
    private static final String COMPANY_NAME = "Teamdouche";
    private static final String APP_NAME = "CMStatsWidget";
    private static final String KEYWORDS = "android";
    private static final String CHANNEL_ID = "9870669534";

    private static final String PREFS_NAME = "widget_prefs";
    private static Context mContext = null;

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private GoogleAdView adView = null;
    private AdSenseSpec adSenseSpec = null;

    //private CheckBox background = null;
    private CheckBox touch = null;
    private CheckBox intervalBox = null;
    private Spinner intervalValue = null;
    private Button saveBtn = null;
    private Button cnclBtn = null;

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
        adView = (GoogleAdView) findViewById(R.id.adview);
        adSenseSpec =
            new AdSenseSpec(CLIENT_ID)
            .setCompanyName(COMPANY_NAME)
            .setAppName(APP_NAME)
            .setChannel(CHANNEL_ID)
            .setKeywords(KEYWORDS)
            .setAdType(AdType.TEXT_IMAGE)
            .setAdTestEnabled(TEST);
        adView.showAds(adSenseSpec);
        cnclBtn = (Button) findViewById(R.id.cancel);
        saveBtn = (Button) findViewById(R.id.save);
        //background = (CheckBox) findViewById(R.id.background);
        //useBackground = background.isChecked();
        touch = (CheckBox) findViewById(R.id.touch);
        useTouch = touch.isChecked();
        intervalBox = (CheckBox) findViewById(R.id.interval);
        useInterval = intervalBox.isChecked();
        intervalValue = (Spinner) findViewById(R.id.intervalValue);
        interval = Long.parseLong(intervalValue.getSelectedItem().toString());
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
                useTouch = touch.isChecked();
                useInterval = intervalBox.isChecked();
                interval = Long.parseLong(
                                   intervalValue.getSelectedItem().toString());
                CMStatsWidgetProvider.prefs =
                                  mContext.getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = 
                                            CMStatsWidgetProvider.prefs.edit();
                editor.putBoolean("useBackground", useBackground);
                editor.putBoolean("useTouch", useTouch);
                editor.putBoolean("useInterval", useInterval);
                editor.putLong("interval", interval);
                editor.commit();
                if (useInterval){
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