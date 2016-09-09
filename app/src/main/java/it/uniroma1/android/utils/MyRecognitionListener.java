package it.uniroma1.android.utils;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by nduccio on 15/06/15.
 */
public class MyRecognitionListener implements RecognitionListener {

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
        System.out.println("Speech onResults");
        ArrayList<String> strlist = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        for (int i = 0; i < strlist.size();i++ ) {
            System.out.println("Speech result=" + strlist.get(i));
        }
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        System.out.println("Speech onRmsChanged");
    }

}
