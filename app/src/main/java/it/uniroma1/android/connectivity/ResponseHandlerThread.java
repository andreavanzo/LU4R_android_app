package it.uniroma1.android.connectivity;

import android.speech.tts.TextToSpeech;

import it.uniroma1.android.activities.MainActivity;

/**
 * Created by nduccio on 06/06/16.
 */
public class ResponseHandlerThread extends Thread {

    public void run() {
        String response = MainActivity.getClient().waitForResponse();
        //TODO response contains whatever is sent back to the app from the python server. You need to process the data here
        String utteranceId = this.hashCode() + "";
        MainActivity.getTTS().speak(response, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }
}
