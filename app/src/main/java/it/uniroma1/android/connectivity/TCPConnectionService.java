package it.uniroma1.android.connectivity;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by nduccio on 23/03/15.
 */

public class TCPConnectionService extends AsyncTask<Void, Void, Void> {

    private Socket clientSocket = null;
    private PrintWriter out = null;
    private boolean isConnected = false;
    private KeepAwakeThread kat;



    public TCPConnectionService(){

    }

    public boolean connect(String addr, int port) {

        try {
            if (isConnected){
                return true;
            } else {
                clientSocket = new Socket();
                clientSocket.connect(new InetSocketAddress(addr, port), 5000);
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                isConnected = true;
                kat = new KeepAwakeThread();
                kat.start();
                System.out.print("OK");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            isConnected = false;
            return false;
        }
    }

    public boolean disconnect() {
        if (clientSocket != null && clientSocket.isConnected())
            try {
                clientSocket.close();
                isConnected = false;
                kat.terminate();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                isConnected = false;
                return false;
            }
        else
            return false;
    }

    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }

    public void send(String msg) {
        if (out != null)
            out.println(msg);
    }

    public String readResponse() {
        BufferedReader stdIn = null;
        String response = "";
        try {
            stdIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            response = stdIn.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String waitForResponse() {
        String response = readResponse();
        while (response.isEmpty())
            response = readResponse();
        return response;

    }

    public boolean isConnected() {
        return isConnected;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }
}

