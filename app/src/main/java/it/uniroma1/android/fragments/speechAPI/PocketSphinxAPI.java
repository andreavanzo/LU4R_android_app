/* ====================================================================
 * Copyright (c) 2014 Alpha Cephei Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ALPHA CEPHEI INC. ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL CARNEGIE MELLON UNIVERSITY
 * NOR ITS EMPLOYEES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 */

package it.uniroma1.android.fragments.speechAPI;


import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;
import it.uniroma1.android.fragments.SpeechInterfaceFragment;

import static android.widget.Toast.makeText;

/**
 * Class that implements PocketSphinx android implementation, made by Alpha Cephei Inc.<p/>
 * The class creates an Async Task that uses an internal US vocabulary and training data to listen to the microphone for a series of keywords.<p/>
 * When a keyword/keyphrase is found, the user is alerted, as well as the main activity.<p/>
 * The main activity takes care of handling the subsequent steps (right now by calling Google's speech API).<p/>
 * */
public class PocketSphinxAPI implements RecognitionListener {

    private static final String KWS_SEARCH = "wakeup";
    private SpeechInterfaceFragment main;
    private Context c;
    private View v;

    private boolean _debugActive=false;
    private boolean _logActive=false;
    private boolean _keepRunning=false;
    private int _volume=0;

    private SpeechRecognizer recognizer;
    AsyncTask<Void, Void, Exception> _async=null;
    private AudioManager audio;


    /**
     * Constructor for the PocketSphinx API.<p/>
     * Receives te link to the main activity, the view (for generating alerts), and the user choices.<p/>
     * It will search for the keyword recognition file on the SD, if available, and setup the recognition with the options selected.<p/>
     * */
    public PocketSphinxAPI(SpeechInterfaceFragment main, View view, boolean debug, boolean log, AudioManager audio)
    {
        c=main.getActivity().getApplicationContext();
        v=view;
        this.main=main;
        _debugActive=debug;
        _logActive=log;
        this.audio=audio;

        _volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);


        try {
            File keyDir;
            Assets assets = new Assets(main.getActivity());
            File assetDir = assets.syncAssets();

            if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                keyDir = null;
            }
            else
            {
                keyDir = new File(Environment.getExternalStorageDirectory().getPath().toString() + "/SpeechToRobot/config.gram");
                if (!keyDir.exists())
                    keyDir = null;
            }
            setupRecognizer(assetDir, keyDir);
        } catch(Exception e)
        {
            if(_debugActive)
                Snackbar.make(v, "Unable to create PocketSphinx: "+e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }


    }

    private void setupRecognizer(File assetsDir, File keyDir) throws IOException {

        if(_logActive && !Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            recognizer = SpeechRecognizerSetup.defaultSetup()
                    .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                    .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                    .setRawLogDir(new File(Environment.getExternalStorageDirectory().getPath().toString()+"/SpeechToRobot"))
                    .setBoolean("-allphone_ci", true)
                    .setBoolean("-backtrace", true)
                    .setBoolean("-bestpath", false)
                    .getRecognizer();
        else
            recognizer = SpeechRecognizerSetup.defaultSetup()
                    .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                    .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                    .setBoolean("-allphone_ci", true)
                    .setBoolean("-backtrace", true)
                    .setBoolean("-bestpath", false)
                    .getRecognizer();

        recognizer.addListener(this);


        File keyWords = new File(assetsDir, "key.gram");
        if(keyDir!=null)
            keyWords=new File(Environment.getExternalStorageDirectory().getPath().toString()+"/SpeechToRobot", "config.gram");

        recognizer.addKeywordSearch(KWS_SEARCH, keyWords);
    }


    /**
     * Stops the Speech Recognition and shuts down PocketSphinx.
     * */
    public void destroyPocket() {
        recognizer.cancel();
        recognizer.shutdown();
    }

    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;

        recognizer.stop();
    }

    /**
     * This callback is called when we stop the recognizer.<p/>
     * Gets the recognized String and calls the private performAction(String).<p/>
     * PerformAction plays a tune for the recognized keyword and switches the mic control to the Google API.<p/>
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;

        String text = hypothesis.getHypstr();
        //makeText(getApplicationContext(), "Res:*"+text+"*", Toast.LENGTH_SHORT).show();

        recognizer.cancel();
        performAction(text);
    }

    @Override
    public void onBeginningOfSpeech() {    }

    /**
     * We stop recognizer here to get a final result.
     */
    @Override
    public void onEndOfSpeech() {    }

    @Override
    public void onTimeout() {    }


    /**
     * Here we handle possible errors in the Speech Recognition.<p/>
     * In case the debug mode is active, we show a Snackbar with the error message.<p/>
     * */
    @Override
    public void onError(Exception error) {
        if(_debugActive)
            Snackbar.make(v, "PocketSpinx error: "+error.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }


    /**
     * Called when the Speech Recognition is complete and the result is not null.<p/>
     * The volume is turned on again, a tone is played to alert the user of the recognition.<p/>
     * If the user has not stopped the program, call the {@link SpeechInterfaceFragment} speechSwitch.<p/>
     * */
    private void performAction(String text) {

        audio.setStreamVolume(AudioManager.STREAM_MUSIC, _volume, 0);

        final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
        tg.startTone(ToneGenerator.TONE_SUP_RADIO_ACK);

        if(_keepRunning==true)
            main.speechSwitch();

    }

    /**
     * Function that handles the cancelling the Async task.<p/>
     * Sets the bool of keepRunning to false (so listener knows of the user choice) and sets the Async Task cancel bool as true.<p/>
     * */
    public void cancelTask()
    {
        _keepRunning=false;
        if(_async!=null)
        {
            recognizer.stop();
            _async.cancel(true);
            _async = null;
        }

    }

    /**
     * Called when the user wants to start a new Async Task with PocketSphinx in the background.<p/>
     * If there is not an other instance currently active, generates the Async Task and calls startSphinx() as doInBackground action.<p/>
     * */
    public void startTask() {
        _keepRunning = true;

        if (_async == null) {
            _async = new AsyncTask<Void, Void, Exception>() {
                @Override
                protected Exception doInBackground(Void... params) {

                    startSphinx();

                    return null;
                }

            };

            _async.execute();
        }
        else
            makeText(c, "PocketSphinx is already running", Toast.LENGTH_SHORT).show();

    }

    /**
     * Removes the sound and starts the speech recognition.<p/>
     * The sound removal is necessary because the speech recognition has some system tones that are unwanted for the user.<p/>
     * The sound will be turned back on after the recognition is done.<p/>
     * */
    public void startSphinx()
    {
        _volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (_keepRunning == true) {
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            recognizer.startListening(KWS_SEARCH);
        }
    }



}
