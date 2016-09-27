package it.uniroma1.android.activities;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Locale;

import it.uniroma1.android.R;
import it.uniroma1.android.connectivity.TCPConnectionService;
import it.uniroma1.android.fragments.JoypadFragment;
import it.uniroma1.android.fragments.NavigationDrawerFragment;
import it.uniroma1.android.fragments.HomeFragment;
import it.uniroma1.android.fragments.SpeechInterfaceFragment;



public class MainActivity extends FragmentActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Fragment frag = null;
    private static TCPConnectionService client = null;
    private SharedPreferences sharedPref = null;
    private PreferenceChangeListener mPreferenceListener = null;
    private static String ip_address = "127.0.0.1";
    private static int port = 4567;
    private static Locale sttLanguage = Locale.getDefault();
    private static String ttsLanguage = Locale.getDefault().toString();
    private static TextToSpeech tts = null;
    private static String activeFragment = "";
    private static String connectionType = "wifi";
    private static boolean continuousActive = true;
    private static boolean push = false;
    private static boolean wifiOnly = true;
    private static boolean offlinePref = true;
    private static boolean logRecord = false;
    private static boolean debugEnabled = false;
    private String sentenceTTSExample = "This is an example of speech synthesis in English";
    protected PowerManager.WakeLock mWakeLock;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (connectionType.equals("wifi") && client == null) {
            client = new TCPConnectionService();
        }
        setContentView(R.layout.activity_main);

        //Init preferences
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mPreferenceListener = new PreferenceChangeListener();
        sharedPref.registerOnSharedPreferenceChangeListener(mPreferenceListener);

        //Init drawer
        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        if (tts == null) {
            tts = new TextToSpeech(this.getApplicationContext(),
                    new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            applyAllSettings();
                        }
                    });
            tts.setSpeechRate(2.0f);
            tts.setPitch(1.8f);
        }
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();
    }

    /**
     * Receiving speech input
     * */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the activities content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        String sectionName = "";
        switch(position) {
            case 0:
                //Home
                frag = HomeFragment.newInstance(position + 1);
                sectionName = getString(R.string.title_section1);
                activeFragment = "$HOME";
                break;
            case 1:
                //Joypad
                frag = JoypadFragment.newInstance(position + 1);
                sectionName = getString(R.string.title_section2);
                activeFragment = "$JOY";
                break;
            case 2:
                //SpeechInterface
                frag = SpeechInterfaceFragment.newInstance(position + 1);
                sectionName = getString(R.string.title_section3);
                activeFragment = "$SLU";
                break;
        }
        fragmentTransaction.replace(R.id.container, frag, activeFragment);
        fragmentTransaction.commit();
        Toast.makeText(this, sectionName, Toast.LENGTH_SHORT).show();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    @Override
    public void onDestroy() {
        this.mWakeLock.release();
        super.onDestroy();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 0.0f;
        getWindow().setAttributes(lp);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar == null) throw new AssertionError();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mNavigationDrawerFragment == null) {
            mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        }
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 1.0f;
        getWindow().setAttributes(lp);
    }

    private class PreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            applySingleSetting(key);
        }
    }

    public void applyAllSettings() {
        ip_address = sharedPref.getString("ip_address", "127.0.0.1");
        port = Integer.parseInt(sharedPref.getString("port", "4567"));
        connectionType = sharedPref.getString("connection", "wifi");
        continuousActive = sharedPref.getBoolean("continuous_speech", true);
        push = sharedPref.getBoolean("push_settings", true);
        String tempLanguage = sharedPref.getString("speech_language", "default");
        if (tempLanguage.equals("default"))
            sttLanguage = Locale.getDefault();
        else
            sttLanguage = new Locale(tempLanguage);
        tempLanguage = sharedPref.getString("tts_language", "default");
        if (tempLanguage.equals("default"))
            tts.setLanguage(Locale.getDefault());
        else
            tts.setLanguage(new Locale(tempLanguage));
        offlinePref = sharedPref.getBoolean("offline_speech", true);
        wifiOnly = sharedPref.getBoolean("wifi_speech", true);
        debugEnabled = sharedPref.getBoolean("debug_speech", false);
        logRecord = sharedPref.getBoolean("recording_preference", false);
        float newPitch = ((float)sharedPref.getInt("pitchSeekBar", 1)) / 10.0f;
        tts.setPitch(newPitch);
        float newRate = ((float)sharedPref.getInt("rateSeekBar", 1)) / 10.0f;
        tts.setSpeechRate(newRate);
    }

    public void applySingleSetting(String key) {
        switch (key) {
            case "ip_address":
                ip_address = sharedPref.getString(key, "127.0.0.1");
                break;
            case "port":
                port = Integer.parseInt(sharedPref.getString(key, "4567"));
                break;
            case "connection":
                connectionType = sharedPref.getString(key, "wifi");
                break;
            case "continuous_speech":
                continuousActive = sharedPref.getBoolean(key, true);
                if (continuousActive) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("push_settings", false);
                    editor.commit();
                    push = false;
                }
                break;
            case "push_settings":
                push = sharedPref.getBoolean(key, true);
                break;
            case "speech_language":
                String tempLanguage = sharedPref.getString(key, "default");
                if (tempLanguage.equals("default")) {
                    sttLanguage = Locale.getDefault();
                } else {
                    sttLanguage = new Locale(tempLanguage);
                }
                break;
            case "tts_language":
                ttsLanguage = sharedPref.getString(key, "default");
                if (ttsLanguage.equals("default")) {
                    tts.setLanguage(Locale.getDefault());
                } else {
                    tts.setLanguage(new Locale(ttsLanguage));
                    switch(ttsLanguage) {
                        case "default":
                            sentenceTTSExample = getString(R.string.ex_en);
                            break;
                        case "en_UK":
                            sentenceTTSExample = getString(R.string.ex_en);
                            break;
                        case "en_US":
                            sentenceTTSExample = getString(R.string.ex_en);
                            break;
                        case "it":
                            sentenceTTSExample = getString(R.string.ex_it);
                            break;
                        case "fr":
                            sentenceTTSExample = getString(R.string.ex_fr);
                            break;
                        case "es":
                            sentenceTTSExample = getString(R.string.ex_es);
                            break;
                        case "de":
                            sentenceTTSExample = getString(R.string.ex_de);
                            break;
                    }
                }
                break;
            case "offline_speech":
                offlinePref = sharedPref.getBoolean(key, true);
                break;
            case "wifi_speech":
                wifiOnly = sharedPref.getBoolean(key, true);
                break;
            case "debug_speech":
                debugEnabled = sharedPref.getBoolean(key, false);
                break;
            case "recording_preference":
                logRecord = sharedPref.getBoolean(key, false);
                break;
            case "pitchSeekBar":
                float newPitch = ((float)sharedPref.getInt("pitchSeekBar", 1)) / 10.0f;
                tts.setPitch(newPitch);
                Toast.makeText(getApplicationContext(), "Pitch: " + newPitch, Toast.LENGTH_SHORT).show();
                tts.speak(sentenceTTSExample, TextToSpeech.QUEUE_FLUSH, null, null);
                break;
            case "rateSeekBar":
                float newRate = ((float)sharedPref.getInt("rateSeekBar", 1)) / 10.0f;
                tts.setSpeechRate(newRate);
                Toast.makeText(getApplicationContext(), "Rate: " + newRate, Toast.LENGTH_SHORT).show();
                tts.speak(sentenceTTSExample, TextToSpeech.QUEUE_FLUSH, null, null);
                break;
        }
    }

    public static TCPConnectionService getClient() {
        return client;
    }

    public String getIpAddress() {
        return ip_address;
    }

    public int getPort() {
        return port;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public boolean getContinuousActive() {
        return continuousActive;
    }

    public boolean getPush() {
        return push;
    }

    public boolean getOfflinePref() {
        return offlinePref;
    }

    public boolean getWifiOnly() {
        return wifiOnly;
    }

    public boolean getDebugEnabled() {
        return debugEnabled;
    }

    public boolean getLogRecord() {
        return logRecord;
    }

    public static TextToSpeech getTTS() {
        return tts;
    }

    public Locale getSpeechLanguage() {
        return sttLanguage;
    }

    public String getActiveFragmentString() {
        return activeFragment;
    }

}
