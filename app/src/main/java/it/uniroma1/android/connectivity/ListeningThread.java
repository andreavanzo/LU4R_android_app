package it.uniroma1.android.connectivity;

import android.speech.tts.TextToSpeech;

import it.uniroma1.android.activities.MainActivity;

/**
 * Created by nduccio on 06/06/16.
 */
public class ListeningThread extends Thread {
    private volatile boolean running = true;

    public void terminate() {
        running = false;
    }

    public void run() {
        while (running) {
            if (MainActivity.getClient().isConnected()) {
                String response = MainActivity.getClient().readResponse();
                String utteranceId = this.hashCode() + "";
                MainActivity.getTTS().speak(response, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
            }
        }
    }
}
