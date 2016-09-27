package it.uniroma1.android.fragments.speech;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import it.uniroma1.android.activities.MainActivity;
import it.uniroma1.android.fragments.SpeechInterfaceFragment;

/**
 * Class that implements Google's {@link RecognitionListener}.<p/>
 * It implements the Google API with  parameters chosen by the user.<p/>
 * */
public class GoogleSpeechAPI implements RecognitionListener {

    //private PocketSphinxActivity main;
    private SpeechInterfaceFragment main;
    private SpeechRecognizer speech;
    private Intent recogIntent;
    private TextView t;
    private boolean debugActive = false;
    private boolean continuousModeActive = false;
    private boolean pushActive = true;
    private boolean isEndOfSpeech = false;
    private View v;

    private boolean stop = false;

    private static final String TAG = "PushToTalk";

    /**
     * Constructor for the Google Speech Listener.<p/>
     * It needs the link to the Main Activity (necessary for switching calls), the context, view and textView for showing the results of the recognition.<p/>
     * Other parameters are: if the user prefers offline recognition or not, the language for the recognition, if debug mode is active (it will show error messages), and which mode to use.<p/>
     * Possible modes are push to talk or continuous mode. In continuous mode the class will call the {@link SpeechInterfaceFragment} speechSwitch() to release the mic for PocketSphinx.
     * */
    public GoogleSpeechAPI(SpeechInterfaceFragment mainActivity, Context context, View view, TextView text, boolean offline, Locale lang, boolean debug, boolean mode, boolean push){
        speech = SpeechRecognizer.createSpeechRecognizer(context);
        speech.setRecognitionListener(this);
        recogIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recogIntent.putExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES, true);
        //Offline speech has been added in Marshmallow (API 23)
        if (android.os.Build.VERSION.SDK_INT>=23) {
            recogIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, offline);
        }
        recogIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,lang);
        recogIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang);
        recogIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, lang);
        recogIntent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, lang);
        v = view;
        t = text;
        debugActive = debug;
        main = mainActivity;
        continuousModeActive = mode;
        pushActive = push;
    }

    /**
     * Call to the internal function for starting the recognition
     * */
    public void startListening(){
        isEndOfSpeech = false;
        stop = false;
        speech.startListening(recogIntent);
    }

    /**
     * Call to the internal function for stopping the recognition
     * */
    public void stopListening(){
        speech.stopListening();
    }

    /**
     * Stops the Speech Listener and destroys it.
     * */
    public void destroy(){
        speech.cancel();
        speech.destroy();
    }


    @Override
    public void onReadyForSpeech(Bundle params) {    }

    @Override
    public void onBeginningOfSpeech() {    }

    @Override
    public void onRmsChanged(float rmsdB) {    }

    @Override
    public void onBufferReceived(byte[] buffer) {    }

    @Override
    public void onEndOfSpeech() {
        isEndOfSpeech = true;
    }

    /**
     * Function that handles any error in the speech recognition.<p/>
     * If debug mode is active it shows a Snackbar with the error code.<p/>
     * If continuous mode is active the Listener will try again to listen for speech. To kill it use destroy or brute force stop.<p/>
     * */
    @Override
    public void onError(int error) {
        //If recognition did not understand or got a timeout, keep going
        if (!isEndOfSpeech) {
            return;
        }

        if (stop) {
            return;
        }

        boolean retry = false;
        String errorCode;

        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                Log.d(TAG, "ERROR_AUDIO");
                errorCode = "ERROR_AUDIO";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                Log.d(TAG, "ERROR_CLIENT");
                errorCode = "ERROR_CLIENT";
                retry=true;
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                Log.d(TAG, "ERROR_RECOGNIZER_BUSY");
                errorCode = "ERROR_RECOGNIZER_BUSY";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                Log.d(TAG, "ERROR_INSUFFICIENT_PERMISSIONS");
                errorCode = "ERROR_INSUFFICIENT_PERMISSIONS";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                Log.d(TAG, "ERROR_NETWORK_TIMEOUT");
                errorCode = "ERROR_NETWORK_TIMEOUT";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                Log.d(TAG, "ERROR_NETWORK");
                errorCode = "ERROR_NETWORK";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                Log.d(TAG, "ERROR_SERVER");
                errorCode = "ERROR_SERVER";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                Log.d(TAG, "ERROR_NO_MATCH");
                errorCode = "ERROR_NO_MATCH";
                retry=true;
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                Log.d(TAG, "ERROR_SPEECH_TIMEOUT");
                errorCode = "ERROR_SPEECH_TIMEOUT";
                retry=true;
                break;
            default:
                return;
        }
        if (debugActive && !retry) {
            Snackbar.make(v, "Error in google speech API: " + errorCode, Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
        speech.cancel();
        if ((continuousModeActive || pushActive==false) && retry) {
            startListening();
        }
    }

    /**
     * Function that stops the Google Speech Listener (but does not destroy it, so it is possible to start the Listener again)
     * */
    public void bruteForceStop() {
        //In case of error loop
        stop = true;
        speech.cancel();
    }

    /**
     * Function called when the speech is processed.<p/>
     * Receives the recognized sentence, prints the most likely hypothesis on the screen.<p/>
     * If the app is currently connected to the python server, the sentence is sent as json.<p/>
     * The json will contain all the possible sentences with the degree of confidence (if google provides it).<p/>
     * If we are working with PocketSphinx, the function speechSwitch() of MainSpeech gets called to give PocketSphinx access to the microphone.<p/>
     * */
    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        float[] confidence = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
        speech.cancel();
        /*Show on screen the most probable detection*/
        String currentString = t.getText().toString();
        if (currentString.equals("")) {
            t.setText(matches.get(0));
        } else {
            t.setText(currentString + "\n" + matches.get(0));
        }

        /*Send json formatted data to computer/robot if connected and if we have any hypotesis*/
        if (!matches.get(0).equals(null) && !matches.get(0).equals("")) {
            String hypoToSend = "{\"hypotheses\":[";
            String hypo;
            for (int i = 0; i < matches.size(); i++) {
                hypo = matches.get(i);
                hypoToSend += "{\"transcription\":\"" + hypo + "\",\"confidence\":"+ confidence[i] +",\"rank\":"+i+"}";
                if (i != matches.size() - 1) {
                    hypoToSend += ",";
                } else {
                    hypoToSend += "]}";
                }
            }
            if (((MainActivity) main.getActivity()).getClient().isConnected())
                ((MainActivity) main.getActivity()).getClient().send(hypoToSend);

        }
        if (stop) {
            return;
        }
        //if we did not hear anything try again?
        if (continuousModeActive && (matches.get(0).equals(null) || matches.get(0).equals(""))) {
            startListening();
        } else if (continuousModeActive) {
            //if we have pocketsphinx in the loop, go back to pocket after the first result
            main.speechSwitch();
        } else if (pushActive == false) {
            startListening();
        }
    }

    /**
     * Function that handles partial results from the speech recognition.<p/>
     * Called when the recognizer is unsure of a part of the sentence or accuracy is too low.<p/>
     * Currently not used.
     * */
    public void onPartialResults(Bundle partialResults) {
        ArrayList<String> data = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        ArrayList<String> unstableData = partialResults.getStringArrayList("android.speech.extra.UNSTABLE_TEXT");
        String mResult = data.get(0) + unstableData.get(0);
        Log.d("PartialRes", mResult);
    }

    @Override
    public void onEvent(int eventType, Bundle params) {     }

}