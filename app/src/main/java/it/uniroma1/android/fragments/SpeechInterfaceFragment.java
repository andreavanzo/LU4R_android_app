package it.uniroma1.android.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import edu.cmu.pocketsphinx.Hypothesis;
import it.uniroma1.android.R;
import it.uniroma1.android.activities.MainActivity;
import it.uniroma1.android.fragments.speechAPI.PocketSphinx;
import it.uniroma1.android.fragments.speechAPI.PushToTalk;
import it.uniroma1.android.utils.FloatingActionButton;

import static android.widget.Toast.makeText;
import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

/**
 * A {@link Fragment} that creates two Speech Listener interfaces to be used by the user.<p/>
 * One Speech Listener is the PocketSphinx API, that listens continuously for keywords/keyphrases.<p/>
 * The other is Google Speech Recognition, that can either be used as Push-To-Talk interface or as phrase recognizer that gets called after PocketSphinx recognizes a keyword.<p/>
 * */
public class SpeechInterfaceFragment extends Fragment {

    public static final String ARG_SECTION_NUMBER = "section_number";

    private final int REQ_CODE_SPEECH_INPUT = 100;

    private TextView hypothesesContent, hypothesesTitle, commandTitle, commandContent;

    private FloatingActionButton fabButton;

    private SpeechRecognizer defaultRecognizer;

    private Intent recognizerIntent;

    private edu.cmu.pocketsphinx.SpeechRecognizer keywordRecognizer;

    private static final String KWS_SEARCH = "wakeup";

    private static final String KEYPHRASE = "ok robot";


    private boolean _SphinxStarted=false;
    private boolean _SwitchToGoogle=true;
    private boolean _changed=false;
    private boolean _PushStarted=false;

    private AudioManager mAudioManager;
    private int mStreamVolume = 0;

    private NotificationCompat.Builder mBuilder;
    private int mNotificationId = 001;

    //Settings for the program
    private boolean _continuousActive=false;
    private boolean _wifiOnly=false;
    private boolean _push=true;
    private boolean _offlinePref=true;
    private String _lang="-1";
    private boolean _logRecord=false;
    private boolean _debugEnabled=false;

    //ProgramModes
    private PushToTalk pushy;
    private PocketSphinx pocket;

    public static SpeechInterfaceFragment newInstance(int sectionNumber) {
        SpeechInterfaceFragment fragment = new SpeechInterfaceFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public SpeechInterfaceFragment() {}

    /*Old code*/
/*    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("onCreate");
        super.onCreate(savedInstanceState);
        defaultRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity(), ComponentName.unflattenFromString("com.google.android.googlequicksearchbox/com.google.android.voicesearch.serviceapi.GoogleRecognitionService"));
        defaultRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onBeginningOfSpeech() {
                System.out.println("Speech onBeginningOfSpeech");
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                System.out.println("Speech onBufferReceived");
            }

            @Override
            public void onEndOfSpeech() {
                System.out.println("Speech onEndOfSpeech");
            }

            @Override
            public void onError(int error) {
                System.out.println("Speech onError: " + error);
                //TODO
                //switchSearch(KWS_SEARCH);

            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                System.out.println("Speech onEvent");
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                System.out.println("Speech onPartialResults");
            }

            @Override
            public void onReadyForSpeech(Bundle params) {
                System.out.println("Speech onReadyForSpeech");
            }


            @Override
            public void onResults(Bundle results) {

                String hypoToSend = "{\"hypotheses\":[";
                ArrayList<String> strlist = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String hypoResults = "";
                String hypo;
                for (int i=0; i<strlist.size(); i++) {
                    hypo = strlist.get(i);
                    hypoResults += hypo + "\n";
                    hypoToSend += "{\"transcription\":\"" + hypo + "\",\"confidence\":0.0,\"rank\":0}";
                    if (i != strlist.size() - 1) {
                        hypoToSend += ",";
                    } else {
                        hypoToSend += "]}";
                    }
                }
                hypothesesContent.setText(strlist.get(0));
                if (((MainActivity) getActivity()).getClient().isConnected()) {
                    ((MainActivity) getActivity()).getClient().send(hypoToSend);
                    //String robotResponse = ((MainActivity) getActivity()).getClient().readResponse();
                    //robotResponse = robotResponse.replaceAll("\\|", "\n").replaceAll(" ", "\t");
                    //commandContent.setText(robotResponse);
                    *//*if (robotResponse.contains("Taking")) {
                        ((MainActivity) getActivity()).getTTS().speak("Okay, I'm going to take the book that is on the table.", TextToSpeech.QUEUE_FLUSH, null);
                        robotResponse = ((MainActivity) getActivity()).getClient().readResponse();
                        if (robotResponse.contains("DON"))
                            ((MainActivity) getActivity()).getTTS().speak("Please, could you put the book on my tray? Thank you!", TextToSpeech.QUEUE_FLUSH, null);
                    } else if (robotResponse.contains("Bringing")) {
                        if (robotResponse.contains("Lab1")) {
                            ((MainActivity) getActivity()).getTTS().speak("Okay, I'm going to bring the book to the table of the laboratory.", TextToSpeech.QUEUE_FLUSH, null);
                            robotResponse = ((MainActivity) getActivity()).getClient().readResponse();
                            if (robotResponse.contains("DON")) {
                                ((MainActivity) getActivity()).getTTS().speak("Please, could you put the book on my tray? Thank you!", TextToSpeech.QUEUE_FLUSH, null);
                                robotResponse = ((MainActivity) getActivity()).getClient().readResponse();
                                if (robotResponse.contains("DON"))
                                    ((MainActivity) getActivity()).getTTS().speak("Please, could you put this book on the table? Thank you!", TextToSpeech.QUEUE_FLUSH, null);
                            }
                        } else if (robotResponse.contains("Lab2")) {
                            ((MainActivity) getActivity()).getTTS().speak("Okay, I'm going to bring the book to the laboratory.", TextToSpeech.QUEUE_FLUSH, null);
                            robotResponse = ((MainActivity) getActivity()).getClient().readResponse();
                            if (robotResponse.contains("DON")) {
                                ((MainActivity) getActivity()).getTTS().speak("Please, could you put the book on my tray? Thank you!", TextToSpeech.QUEUE_FLUSH, null);
                                robotResponse = ((MainActivity) getActivity()).getClient().readResponse();
                                if (robotResponse.contains("DON"))
                                    ((MainActivity) getActivity()).getTTS().speak("Please, could you remove the book from my tray? Thank you!", TextToSpeech.QUEUE_FLUSH, null);
                            }
                        } else {
                            ((MainActivity) getActivity()).getTTS().speak("Okay, I'm going to bring the book to the table.", TextToSpeech.QUEUE_FLUSH, null);
                            robotResponse = ((MainActivity) getActivity()).getClient().readResponse();
                            if (robotResponse.contains("DON")) {
                                ((MainActivity) getActivity()).getTTS().speak("Please, could you put the book on my tray? Thank you!", TextToSpeech.QUEUE_FLUSH, null);
                                robotResponse = ((MainActivity) getActivity()).getClient().readResponse();
                                if (robotResponse.contains("DON"))
                                    ((MainActivity) getActivity()).getTTS().speak("Please, could you put this book on the table? Thank you!", TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }
                    }*//*
                    *//*if (robotResponse.contains("[SAY]")) {
                        if (robotResponse.contains("[COMMAND]")) {
                            String[] splitted = robotResponse.split("\\|");
                            ((MainActivity) getActivity()).getTTS().speak(splitted[0].replaceAll("\\[SAY\\]", ""), TextToSpeech.QUEUE_FLUSH, null);
                            commandContent.setText(splitted[1].replaceAll("\\[COMMAND\\]", ""));
                        } else {
                            ((MainActivity) getActivity()).getTTS().speak(robotResponse.replaceAll("\\[SAY\\]", ""), TextToSpeech.QUEUE_FLUSH, null);
                        }
                    } else {
                        commandContent.setText(robotResponse);
                    }*(

                    *//*TextView robotText = (TextView) view.findViewById(R.id.robotSaidContent);

                    robotText.setText(robotResponse);
                    ((MainActivity) getActivity()).getTTS().speak(robotResponse, TextToSpeech.QUEUE_FLUSH, null);*//*
                }
                //TODO
                //switchSearch(KWS_SEARCH);

            }

            @Override
            public void onRmsChanged(float rmsdB) {
                System.out.println("Speech onRmsChanged");
            }
        });
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, ((MainActivity) getActivity()).getSpeechLanguage().toString());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "it.uniroma1.android.fragments");

        fabButton = new FloatingActionButton.Builder(getActivity())
                .withDrawable(getResources().getDrawable(R.drawable.speak))
                .withButtonColor(Color.LTGRAY)
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 16)
                .create();
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defaultRecognizer.startListening(recognizerIntent);
            }
        });

        //TODO
        *//*new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(getActivity().getApplicationContext());
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    System.out.println("Failed to init recognizer " + result);
                } else {
                    switchSearch(KWS_SEARCH);
                    System.out.println("Keyword ASR is ready!");
                }
            }
        }.execute();*//*

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("onCreateView");
        View view = inflater.inflate(R.layout.fragment_nlp_chain, container, false);
        hypothesesTitle = (TextView) view.findViewById(R.id.youSaidTitle);
        hypothesesTitle.setText("Transcription");

        *//*commandTitle = (TextView) view.findViewById(R.id.commandTitle);
        commandTitle.setText(R.string.command);
        commandContent = (TextView) view.findViewById(R.id.commandContent);
        *//*
        hypothesesContent = (TextView) view.findViewById(R.id.youSaidContent);

        if (((MainActivity) getActivity()).getClient().isConnected()) {
            ((MainActivity) getActivity()).getClient().send("$NLP");
        }
        fabButton.showFloatingActionButton();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    private void switchSearch(String searchName) {
        keywordRecognizer.stop();
        keywordRecognizer.startListening(searchName);
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        keywordRecognizer = defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                .setRawLogDir(assetsDir)
                .setBoolean("-allphone_ci", true)
                // Threshold to tune for keyphrase to balance between false alarms and misses
                .setKeywordThreshold(1e-45f)
                .getRecognizer();
        keywordRecognizer.addListener(new edu.cmu.pocketsphinx.RecognitionListener() {
            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onEndOfSpeech() {
                if (!keywordRecognizer.getSearchName().equals(KWS_SEARCH))
                    switchSearch(KWS_SEARCH);
            }

            @Override
            public void onPartialResult(Hypothesis hypothesis) {
                if (hypothesis == null)
                    return;
                String text = hypothesis.getHypstr();
                if (text.equals(KEYPHRASE)) {
                    keywordRecognizer.stop();
                    if (defaultRecognizer != null)
                        defaultRecognizer.startListening(recognizerIntent);
                }
            }

            @Override
            public void onResult(Hypothesis hypothesis) {

            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onTimeout() {
                switchSearch(KWS_SEARCH);
            }
        });

        keywordRecognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fabButton.hideFloatingActionButton();
        //TODO
        //keywordRecognizer.stop();
        defaultRecognizer.stopListening();
        //keywordRecognizer.cancel();
        //keywordRecognizer.shutdown();
    }*/

    /**
     * Function that handles the destruction of the fragment.<p/>
     * The tasks in background are stopped, audio is set again at its original value, any notification is removed.<p/>
     * */
    @Override
    public void onDestroy() {
        super.onDestroy();
        fabButton.hideFloatingActionButton();

        pushy.destroy();

        pocket.cancelTask();
        pocket.destroyPocket();

        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mStreamVolume, 0);

        if(_SphinxStarted) //remove notification
        {
            NotificationManager mNotifyMgr =
                    (NotificationManager) getActivity().getSystemService(getActivity().getApplicationContext().NOTIFICATION_SERVICE);
            mNotifyMgr.cancel(mNotificationId);
        }

        super.onDestroy();

    }

    /**
     * At creation, all the preferences are loaded into private variables, and the floating action button is created (together with the event listeners for touch/click)<p/>
     * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("onCreate");
        super.onCreate(savedInstanceState);

        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        mStreamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(getActivity().getApplicationContext())
                .setSmallIcon(R.drawable.ic_mic_white_24dp)
                .setContentTitle("Continuous speech recognition running")
                .setContentText("Currently using the microphone to record speech");

        /**Check options and set all the flags**/
        //Continuous/push
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        _continuousActive = pref.getBoolean("ContinuousVsPush", true);

        //Push/Click
        _push = pref.getBoolean("PushVsClick", true);

        //Wifi only
        _wifiOnly=pref.getBoolean("WifiOnly", true);

        //Offline preferred
        _offlinePref=pref.getBoolean("OfflinePreferred", true);

        //Google language
        _lang=pref.getString("LanguageList", "-1");

        //Raw log
        _logRecord=pref.getBoolean("checkbox_preference", false);

        //Debug mode
        _debugEnabled=pref.getBoolean("DebugEnabled", false);


        String path="SpeechToRobot";
        createDirIfNotExists(path);


        /**Button creation**/
        int icon;

        if (!_continuousActive) {
            icon = R.drawable.speak;
        } else {
            icon = R.drawable.ic_power_settings_new_black_24dp;
        }

        Drawable drawable = ResourcesCompat.getDrawable(getResources(), icon, null);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);


        fabButton = new FloatingActionButton.Builder(getActivity())
                .withDrawable(new BitmapDrawable(getResources(), bitmap))
                .withButtonColor(Color.LTGRAY)
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 16)
                .create();


        if (fabButton != null) {
            fabButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clicked(view);
                }
            });

            fabButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    //Check wifi, if not active there is no sense in doing this (if wifi only is active)
                    ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo ni = cm.getActiveNetworkInfo();


                    if ((ni == null && !_offlinePref) || ni.getType() != ConnectivityManager.TYPE_WIFI &&
                            ni.getType() != ConnectivityManager.TYPE_MOBILE ||
                            (ni.getType() == ConnectivityManager.TYPE_MOBILE && _wifiOnly)) {

                        if (_debugEnabled) //Write error cause if debug enabled
                            Snackbar.make(hypothesesContent, "Error: check Wifi/3G connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        return true;
                    }

                    if (_continuousActive || _push == false) //if continuous is enabled, don't care about touch, only click
                        return false;

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            pushy.startListening();
                            break;

                        case MotionEvent.ACTION_UP:
                            pushy.stopListening();

                            break;
                    }
                    return true;
                }
            });

        }
    }

    /**
     * Function called at startup. Checks if the app directory exists on the SD or not.
     * */
    public boolean createDirIfNotExists(String path) {
        boolean ret = true;

        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            makeText(getActivity().getApplicationContext(), "Problem in reading the SD, keyword database actions will not be available.", Toast.LENGTH_SHORT).show();
            Log.wtf("SpeechToRobot::", Environment.getExternalStorageState());
            return false;
        }

        File file = new File(Environment.getExternalStorageDirectory(), path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("SpeechToRobot :: ", "Problem creating App folder");
                ret = false;
            }
        }
        return ret;
    }

    /**
     * Function called when the button is pushed and continuous mode is active.<p/>
     * If the Async Task has not started, start it (check if mic is busy).<p/>
     * If the Task is active, stop it.<p/>
     * The function will show a couple of Toast to signal the user that the choice has been successfully performed.<p/>
     * */
    public void clicked(View view){

        if(_continuousActive==false && _push==true) //If we are in push to talk mode we don't care about sphinx
            return;

        if(_push==false && _continuousActive==false)
        {
            NotificationManager mNotifyMgr =
                    (NotificationManager) getActivity().getSystemService(getActivity().getApplicationContext().NOTIFICATION_SERVICE);

            if(_PushStarted==false)
            {
                pushy.startListening();
                mNotifyMgr.notify(mNotificationId, mBuilder.build());
                _PushStarted=true;
            }
            else
            {
                pushy.bruteForceStop();
                _PushStarted=false;
                mNotifyMgr.cancel(mNotificationId);
            }
        }
        else
        {
            NotificationManager mNotifyMgr =
                    (NotificationManager) getActivity().getSystemService(getActivity().getApplicationContext().NOTIFICATION_SERVICE);

            if (_SphinxStarted == false) //start it
            {
                //Check if the mic is busy before starting, if it's busy tell the user to retry in few seconds
                if (checkIfMicrophoneIsBusy(getActivity().getApplicationContext()) == true) {
                    _SwitchToGoogle = true;
                    _SphinxStarted = true;
                    makeText(getActivity().getApplicationContext(), "Activating PocketSphinx", Toast.LENGTH_SHORT).show();

                    // Builds the notification and issues it.
                    mNotifyMgr.notify(mNotificationId, mBuilder.build());

                    //Snackbar.make(view, "Activating PocketSphinx", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    pocket.startTask();
                } else
                    makeText(getActivity().getApplicationContext(), "Mic still busy, retry in few seconds", Toast.LENGTH_SHORT).show();
            } else //stop it
            {
                _SphinxStarted = false;
                makeText(getActivity().getApplicationContext(), "Deactivating PocketSphinx", Toast.LENGTH_SHORT).show();

                mNotifyMgr.cancel(mNotificationId);

                //Snackbar.make(view, "Deactivating PocketSphinx", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                pocket.cancelTask();
                pushy.bruteForceStop();

            }
        }
    }

    /**
     * Function that handles the switching between PocketSphinx and Google Speech Recognition.<p/>
     * It is called only if continuous mode is active.<p/>
     * It removes the system tunes associated with the Speech Recognition as they tend to bug the user.<p/>
     * */
    public void speechSwitch()
    {
        if(_SwitchToGoogle==true) //Call google
        {
            _SwitchToGoogle=false;
            makeText(getActivity().getApplicationContext(), "Calling Google API", Toast.LENGTH_SHORT).show();

            mStreamVolume=mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);

            //Call google api
            pushy.startListening();
        }
        else //Call pocketsphinx
        {
            _SwitchToGoogle=true;

            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mStreamVolume, 0);

            makeText(getActivity().getApplicationContext(), "Switching back to PocketSphinx", Toast.LENGTH_SHORT).show();
            pocket.startSphinx();
        }
    }


    /**
     * Function called at button press, checks if the mic is currently busy before enabling the continuous listening mode.<p/>
     * Useful if the user does not give the app the time to close the async task before creating a new one.<p/>
     * */
    public static boolean checkIfMicrophoneIsBusy(Context ctx){
        AudioRecord audio = null;
        boolean ready = true;
        try{
            int baseSampleRate = 44100;
            int channel = AudioFormat.CHANNEL_IN_MONO;
            int format = AudioFormat.ENCODING_PCM_16BIT;
            int buffSize = AudioRecord.getMinBufferSize(baseSampleRate, channel, format );
            audio = new AudioRecord(MediaRecorder.AudioSource.MIC, baseSampleRate, channel, format, buffSize );
            audio.startRecording();
            short buffer[] = new short[buffSize];
            int audioStatus = audio.read(buffer, 0, buffSize);

            if(audioStatus == AudioRecord.ERROR_INVALID_OPERATION || audioStatus == AudioRecord.STATE_UNINITIALIZED /* For Android 6.0 */)
                ready = false;
        }
        catch(Exception e){
            ready = false;
        }
        finally {
            try{
                audio.release();
            }
            catch(Exception e){}
        }

        return ready;
    }

    /**
     * Function called at fragment creation.<p/>
     * If the app is connected to the sever python, a message with the fragment name gets sent.<p/>
     * This function takes care of initialising the google and pocketsphinx api, and inits also the view for hypothesis printing.<p/>
     * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("onCreateView");
        View view = inflater.inflate(R.layout.fragment_speech_interface, container, false);


        hypothesesContent = (TextView) view.findViewById(R.id.speechContent);
        hypothesesContent.setText("Hello");


        if (((MainActivity) getActivity()).getClient().isConnected()) {
            ((MainActivity) getActivity()).getClient().send("$VOC");
        }

        int language=-1;
        if(_lang.equals("0"))
            language=0;
        else if(_lang.equals("1"))
            language=1;
        else if(_lang.equals("2"))
            language=2;
        else if(_lang.equals("3"))
            language=3;
        else if(_lang.equals("4"))
            language=4;

        pushy=new PushToTalk(this, getActivity().getApplicationContext(),hypothesesContent, hypothesesContent, _offlinePref, language, _debugEnabled, _continuousActive, _push);
        pocket=new PocketSphinx(this, hypothesesContent, _debugEnabled, _logRecord, mAudioManager);


        fabButton.showFloatingActionButton();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

}
