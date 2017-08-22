package com.tororobot.util;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.tororobot.TororobotAplications;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by bemonio on 7/17/2016.
 */
    /*
    ThreadConnected:
    Background Thread to handle Bluetooth data communication
    after connected
     */

public class ThreadConnected extends Thread {

    public static final String TAG = ThreadConnected.class.getSimpleName();


    private final BluetoothSocket connectedBluetoothSocket;
    private final InputStream connectedInputStream;
    private final OutputStream connectedOutputStream;

    Context context;

    public ThreadConnected(BluetoothSocket socket) {
        context = TororobotAplications.getContext();
        connectedBluetoothSocket = socket;
        InputStream in = null;
        OutputStream out = null;

        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        connectedInputStream = in;
        connectedOutputStream = out;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;

        while (true) {
            try {
                bytes = connectedInputStream.read(buffer);
                String strReceived = new String(buffer, 0, bytes);
                final String msgReceived = String.valueOf(bytes) +
                        " bytes received:\n"
                        + strReceived;
                /*
                runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        //textStatus.setText(msgReceived);
                    }});
                */

            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, e.getMessage());
                final String msgConnectionLost = "Connection lost:\n" + e.getMessage();
                /*
                runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        //textStatus.setText(msgReceived);
                    }});
                */
            }
        }
    }

    public void write(byte[] buffer) {
        try {
            connectedOutputStream.write(buffer);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void cancel() {
        try {
            connectedBluetoothSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
