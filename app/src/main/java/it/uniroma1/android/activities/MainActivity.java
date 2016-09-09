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
import it.uniroma1.android.connectivity.BluetoothConnectionService;
import it.uniroma1.android.connectivity.Constants;
import it.uniroma1.android.connectivity.TCPConnectionService;
import it.uniroma1.android.fragments.DialogueInterfaceFragment;
import it.uniroma1.android.fragments.JoypadFragment;
import it.uniroma1.android.fragments.NLPChainFragment;
import it.uniroma1.android.fragments.NavigationDrawerFragment;
import it.uniroma1.android.fragments.PlaceholderFragment;
import it.uniroma1.android.fragments.QuestionnaireFragment;
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
    private static Locale speechLanguage = Locale.getDefault();
    private static TextToSpeech tts = null;
    private static String activeFragment = "";
    private static String connectionType = "wifi";
    private static boolean continuousActive = true;
    private static boolean push = false;
    private static boolean wifiOnly = true;
    private static boolean offlinePref = true;
    private static String lang = "-1";
    private static boolean logRecord = false;
    private static boolean debugEnabled = false;
    private BluetoothConnectionService bcs = null;
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

        if (connectionType.equals("wifi") && client == null)
            client = new TCPConnectionService();
        if (connectionType.equals("blue") && bcs == null)
            bcs = new BluetoothConnectionService(getApplicationContext());
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
                frag = PlaceholderFragment.newInstance(position + 1);
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
                activeFragment = "$VOC";
                break;
            case 3:
                //DialogueInterface
                frag = DialogueInterfaceFragment.newInstance(position + 1);
                sectionName = getString(R.string.title_section4);
                activeFragment = "$DIA";
                break;
            case 4:
                //NLPchain
                frag = NLPChainFragment.newInstance(position +1 );
                sectionName = getString(R.string.title_section5);
                activeFragment = "$NLP";
                break;
            case 5:
                //Questionnaire
                frag = QuestionnaireFragment.newInstance(position +1);
                sectionName = getString(R.string.title_section6);
                activeFragment = "$QUE";
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
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                break;
            case 6:
                mTitle = getString(R.string.title_section6);
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
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
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

    @Override
    public void onBackPressed() {

    }
    public void applyAllSettings() {
        ip_address = sharedPref.getString("ip_address", "127.0.0.1");
        port = Integer.parseInt(sharedPref.getString("port", "4567"));
        connectionType = sharedPref.getString("connection", "wifi");

        continuousActive = sharedPref.getBoolean("continuous_speech", true);
        push = sharedPref.getBoolean("push_settings", true);

        String tempLanguage = sharedPref.getString("speech_language", "default");
        if (tempLanguage.equals("default"))
            speechLanguage = Locale.getDefault();
        else
            speechLanguage = new Locale(tempLanguage);
        tempLanguage = sharedPref.getString("tts_language", "default");
        if (tempLanguage.equals("default"))
            tts.setLanguage(Locale.getDefault());
        else
            tts.setLanguage(new Locale(tempLanguage));
        offlinePref = sharedPref.getBoolean("offline_speech", true);
        wifiOnly = sharedPref.getBoolean("wifi_speech", true);
        debugEnabled = sharedPref.getBoolean("debug_speech", false);

        logRecord = sharedPref.getBoolean("recording_preference", false);
        lang = sharedPref.getString("LanguageList", "-1");


    }

    public void applySingleSetting(String key) {
        System.out.println(key);
        switch (key) {
            case "ip_address":
                ip_address = sharedPref.getString(key, "127.0.0.1");
                System.out.println(ip_address);
                break;
            case "port":
                port = Integer.parseInt(sharedPref.getString(key, "4567"));
                System.out.println(port);
                break;
            case "connection":
                connectionType = sharedPref.getString("connection", "wifi");
                break;
            case "continuous_speech":
                continuousActive = sharedPref.getBoolean("continuous_speech", true);
                break;
            case "push_settings":
                push = sharedPref.getBoolean("push_settings", true);
                break;
            case "speech_language":
                String tempLanguage = sharedPref.getString(key, "default");
                if (tempLanguage.equals("default"))
                    speechLanguage = Locale.getDefault();
                else
                    speechLanguage = new Locale(tempLanguage);
                System.out.println(speechLanguage);
                break;
            case "tts_language":
                String ttsLanguage = sharedPref.getString("tts_language", "default");
                if (ttsLanguage.equals("default"))
                    tts.setLanguage(Locale.getDefault());
                else
                    tts.setLanguage(new Locale(ttsLanguage));
                System.out.println(tts.getLanguage());
                break;
            case "offline_speech":
                offlinePref = sharedPref.getBoolean("offline_speech", true);
                break;
            case "wifi_speech":
                wifiOnly = sharedPref.getBoolean("wifi_speech", true);
                break;
            case "debug_speech":
                debugEnabled = sharedPref.getBoolean("debug_speech", false);
                break;
            case "recording_preference":
                logRecord = sharedPref.getBoolean("recording_preference", false);
                break;
            case "LanguageList":
                lang = sharedPref.getString("LanguageList", "-1");
                break;
        }
    }

    public static TCPConnectionService getClient() {
        return client;
    }

    /*public BluetoothConnectionService getBluetoothConnectionService() {
        return bcs;
    }*/

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

    public String getLang() {
        return lang;
    }

    public TextToSpeech getTTS() {
        return tts;
    }

    public Locale getSpeechLanguage() {
        return speechLanguage;
    }


    public SharedPreferences getSharedPref() {
        return sharedPref;
    }

    public String getActiveFragmentString() {
        return activeFragment;
    }

    public Fragment getActiveFragment() {
        return frag;
    }

    public BluetoothConnectionService getBlouetoothConnectionService() {
        return bcs;
    }

}
