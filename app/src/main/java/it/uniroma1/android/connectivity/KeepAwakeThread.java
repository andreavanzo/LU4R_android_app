package it.uniroma1.android.connectivity;

import java.util.logging.Logger;

import it.uniroma1.android.activities.MainActivity;

/**
 * Created by nduccio on 06/06/16.
 */
public class KeepAwakeThread extends Thread {
    private volatile boolean running = true;

    public void terminate() {
        running = false;
    }

    public void run() {
        while (running) {
            MainActivity.getClient().send("KEEP_AWAKE");
            synchronized (this) {
                try {
                    wait(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
