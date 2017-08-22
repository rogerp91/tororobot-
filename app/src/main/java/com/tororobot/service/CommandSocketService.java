package com.tororobot.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.tororobot.util.Constants;
import com.tororobot.util.Networks;

import java.net.URI;

import co.lujun.lmbluetoothsdk.BluetoothController;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketOptions;

public class CommandSocketService extends Service {

    public static final String TAG = CommandSocketService.class.getSimpleName();
    private static final String ACTION_START = TAG + ".START"; // Action to start
    private static final String ACTION_STOP = TAG + ".STOP"; // Action to stop
    private static final String ACTION_KEEPALIVE = TAG + ".KEEPALIVE"; // Action to keep alive used by alarm manager
    private static final String ACTION_RECONNECT = TAG + ".RECONNECT"; // Action to reconnect
    private static final String ACTION_UPDATE = TAG + ".UPDATE"; // Action to reconnect

    /**
     * Socket
     */
    private BroadcastReceiver connectionReceiver;
    private static boolean isClosed = true;
    private static WebSocketConnection webSocketConnection;
    private static WebSocketOptions options = new WebSocketOptions();
    private static boolean isExitApp = false;
    private String websocketHost = "wss://tororobot-server.ingeniouskey.com";
    private AlarmManager mAlarmManager;
    private static final int KEEP_ALIVE = 30000;
    Handler handler = null;

    private BluetoothController mBluetoothController;
    private String mMacAddress = "";
    private String mDeviceName = "";


    /**
     * @param ctx Contexto
     */
    public static void actionStart(Context ctx) {
        Intent i = new Intent(ctx, CommandSocketService.class);
        i.setAction(ACTION_START);
        ctx.startService(i);
    }

    /**
     * @param ctx Contexto
     */
    public static void actionStop(Context ctx) {
        Intent i = new Intent(ctx, CommandSocketService.class);
        i.setAction(ACTION_STOP);
        ctx.startService(i);
    }

    public static void actionKeepalive(Context ctx) {
        Intent i = new Intent(ctx, CommandSocketService.class);
        i.setAction(ACTION_KEEPALIVE);
        ctx.startService(i);
    }


    public static void actionUpdate(Context ctx, String mac) {
        Intent i = new Intent(ctx, CommandSocketService.class);
        i.setAction(ACTION_UPDATE);
        i.putExtra("mac", mac);
        ctx.startService(i);
    }


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        handler = new Handler(Looper.getMainLooper());
        mBluetoothController = BluetoothController.getInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mReceiver);
        } catch (Exception e) {
            Log.e(TAG, "onDestroy: " + e.getMessage());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: " + intent.getAction());
        sendComment(intent);
        return START_REDELIVER_INTENT;
    }

    private void sendComment(Intent intent) {
        String action = intent.getAction();
        if (action.equals(ACTION_START)) {
            start();
        } else {
            if (action.equals(ACTION_STOP)) {
                stop();
            } else {
                if (action.equals(ACTION_KEEPALIVE)) {
                    keepAlive();
                } else {
                    if (action.equals(ACTION_RECONNECT)) {
                        reconnectIfNecessary();
                    } else {
                        if (action.equals(ACTION_UPDATE)) {
                            mMacAddress = intent.getExtras().getString("mac", "");
                            start();
                            Log.d(TAG, "MAC: " + mMacAddress);
                        }
                    }
                }
            }
        }
    }

    public void stop() {
        try {
            if (connectionReceiver != null) {
                unregisterReceiver(connectionReceiver);
            }
            closeWebsocket(false);
        } catch (Exception e) {
            Log.e(TAG, "onDestroy: " + e.getMessage());
        }
    }

    private void start() {

        //Comando Intent
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.UP);
        filter.addAction(Constants.LEFT);
        filter.addAction(Constants.DOWN);
        filter.addAction(Constants.RIGHT);
        filter.addAction(Constants.MOVE_UP);
        filter.addAction(Constants.MOVE_LEFT);
        filter.addAction(Constants.MOVE_DOWN);
        filter.addAction(Constants.MOVE_RIGHT);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, filter);

        if (Networks.isOnline(this)) { // Hay conexión?
            if (webSocketConnection != null) {
                webSocketConnection.disconnect();
            }
            if (isClosed) {
                webSocketConnect();
            }
        } else {
            handler.post(() -> Toast.makeText(CommandSocketService.this, "Hay problemas de conexión", Toast.LENGTH_SHORT).show());
        }
    }

    private void keepAlive() {
        if (Networks.isOnline(this)) { // Hay conexión?
            if (webSocketConnection != null) {
                webSocketConnection.disconnect();
            }
            if (isClosed) {
                webSocketConnect();
            }
        } else {
            handler.post(() -> Toast.makeText(CommandSocketService.this, "Hay problemas de conexión", Toast.LENGTH_SHORT).show());
        }
    }

    private void reconnectIfNecessary() {
        if (Networks.isOnline(this)) { // Hay conexión?
            if (webSocketConnection != null) {
                webSocketConnection.disconnect();
            }
            if (isClosed) {
                webSocketConnect();
            }
        } else {
            handler.post(() -> Toast.makeText(CommandSocketService.this, "Hay problemas de conexión", Toast.LENGTH_SHORT).show());
        }
    }


    private void startKeepAlives() {
        Intent i = new Intent();
        i.setClass(this, CommandSocketService.class);
        i.setAction(ACTION_KEEPALIVE);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), KEEP_ALIVE, pi);

    }

    private void stopKeepAlives() {
        Intent i = new Intent();
        i.setClass(this, CommandSocketService.class);
        i.setAction(ACTION_KEEPALIVE);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        mAlarmManager.cancel(pi);
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: " + intent.getAction());
            switch (intent.getAction()) {
                case ConnectivityManager.CONNECTIVITY_ACTION:
                    reconnectIfNecessary();
                    break;
                case Constants.UP:
                    sendMsg("w");
                    break;
                case Constants.LEFT:
                    sendMsg("a");
                    break;
                case Constants.DOWN:
                    sendMsg("s");
                    break;
                case Constants.RIGHT:
                    sendMsg("d");
                    break;
                case Constants.MOVE_UP:
                    sendMsg("i");
                    break;
                case Constants.MOVE_LEFT:
                    sendMsg("j");
                    break;
                case Constants.MOVE_DOWN:
                    sendMsg("k");
                    break;
                case Constants.MOVE_RIGHT:
                    sendMsg("l");
                    break;
            }
        }
    };


    public synchronized void closeWebsocket(boolean exitApp) {
        isExitApp = exitApp;
        if (webSocketConnection != null && webSocketConnection.isConnected()) {
            webSocketConnection.disconnect();
            webSocketConnection = null;
        }
    }

    public synchronized void webSocketConnect() {
        webSocketConnection = new WebSocketConnection();
        try {

            webSocketConnection.connect(URI.create(websocketHost), new WebSocketConnectionHandler() {

                @Override
                public void onOpen() {
                    super.onOpen();
                    Log.d(TAG, "SocketConnection - onOpen ");
                    isClosed = false;
                }

                @Override
                public void onTextMessage(String payload) {
                    super.onTextMessage(payload);
                    Log.d(TAG, "onTextMessage: " + payload);
                    Log.d(TAG, "onTextMessage - mac: " + mMacAddress);
                    try {
                        if (!TextUtils.isEmpty(mMacAddress)) {
                            mBluetoothController.connect(mMacAddress);
                        }
                    } catch (RuntimeException e) {
                        Log.e(TAG, "onTextMessage: " + e.getMessage());
                    }

                    switch (payload) {
                        case "w":
//                            sendBroadcast(new Intent(Constants.COMMAND_UP));
                            mBluetoothController.write(payload.getBytes());
                            break;
                        case "a":
//                            sendBroadcast(new Intent(Constants.COMMAND_LEFT));
                            mBluetoothController.write(payload.getBytes());
                            break;
                        case "s":
//                            sendBroadcast(new Intent(Constants.COMMAND_DOWN));
                            mBluetoothController.write(payload.getBytes());
                            break;
                        case "d":
//                            sendBroadcast(new Intent(Constants.COMMAND_RIGHT));
                            mBluetoothController.write(payload.getBytes());
                            break;
                        case "i":
//                            sendBroadcast(new Intent(Constants.COMMAND_MOVE_UP));
                            mBluetoothController.write(payload.getBytes());
                            break;
                        case "j":
//                            sendBroadcast(new Intent(Constants.COMMAND_MOVE_LEFT));
                            mBluetoothController.write(payload.getBytes());
                            break;
                        case "k":
//                            sendBroadcast(new Intent(Constants.COMMAND_MOVE_DOWN));
                            mBluetoothController.write(payload.getBytes());
                            break;
                        case "l":
//                            sendBroadcast(new Intent(Constants.COMMAND_MOVE_RIGHT));
                            mBluetoothController.write(payload.getBytes());
                            break;
                    }


                }

                @Override
                public void onClose(WebSocketCloseNotification code, String reason) {
                    super.onClose(code, reason);
                    Log.d(TAG, "SocketConnection - onClose ");
                    isClosed = true;
                    handler.post(() -> Toast.makeText(CommandSocketService.this, "Reconectando en 30 segundos...", Toast.LENGTH_LONG).show());
                    closeWebsocket(false);
                    startKeepAlives();
                }
            });
        } catch (WebSocketException e) {
            Log.e(TAG, "webSocketConnect: " + e.getMessage());
            isClosed = true;
            closeWebsocket(false);
            startKeepAlives();
        }
    }

    public static void sendMsg(String s) {
        Log.d(TAG, "sendMsg = " + s);
        if (!TextUtils.isEmpty(s))
            if (webSocketConnection != null) {
                webSocketConnection.sendTextMessage(s);
            }
    }

    private class ConnectSocket extends Thread {

        private WebSocketConnection webSocketConnection;

        public ConnectSocket() {
            this.webSocketConnection = new WebSocketConnection();
        }

        @Override
        public void run() {
            super.run();
            try {
                webSocketConnection.connect(URI.create(websocketHost), new WebSocketConnectionHandler(){

                    @Override
                    public void onOpen() {
                        super.onOpen();
                    }

                    @Override
                    public void onTextMessage(String payload) {
                        super.onTextMessage(payload);
                    }

                    @Override
                    public void onClose(WebSocketCloseNotification code, String reason) {
                        super.onClose(code, reason);
                    }
                });
            } catch (WebSocketException e) {
                e.printStackTrace();
            }

        }

        public synchronized void closeWebsocket() {
            if (webSocketConnection != null && webSocketConnection.isConnected()) {
                webSocketConnection.disconnect();
                webSocketConnection = null;
            }
        }

    }

}