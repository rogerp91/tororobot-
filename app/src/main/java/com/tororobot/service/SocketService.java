package com.tororobot.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.github.rogerp91.prefsshared.ManagerPrefs;
import com.tororobot.util.Constants;

import java.net.URI;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;

import static com.tororobot.util.Constants.INITIAL_RETRY_INTERVAL;
import static com.tororobot.util.Constants.KEEP_ALIVE_INTERVAL;
import static com.tororobot.util.Constants.MAXIMUM_RETRY_INTERVAL;
import static com.tororobot.util.Constants.RETRY_INTERVAL;

public class SocketService extends Service {

    public static final String TAG = SocketService.class.getSimpleName();
    private static final String ACTION_START = TAG + ".START"; // Action to start
    private static final String ACTION_STOP = TAG + ".STOP"; // Action to stop
    private static final String ACTION_KEEPALIVE = TAG + ".KEEPALIVE"; // Action to keep alive used by alarm manager
    private static final String ACTION_RECONNECT = TAG + ".RECONNECT"; // Action to reconnect
    private static final String ACTION_UPDATE = TAG + ".UPDATE"; // Action to reconnect

    /**
     * Socket
     */
    public static WebSocketConnection webSocketConnection = null;

    private static boolean isExitApp = false;

    private boolean mStarted;

    private AlarmManager mAlarmManager;
    private static final int KEEP_ALIVE = 30000;
    Handler handler = null;

    long interval;//scheduleReconnect
    private ConnectionSocket mConnection;

    private String mMacAddress = "";
    private String mDeviceName = "";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * @param ctx Context
     */

    public static void actionStart(Context ctx) {
        Intent i = new Intent(ctx, SocketService.class);
        i.setAction(ACTION_START);
        ctx.startService(i);
    }


    public static void actionStop(Context ctx) {
        Intent i = new Intent(ctx, SocketService.class);
        i.setAction(ACTION_STOP);
        ctx.startService(i);
    }

    public static void actionKeepalive(Context ctx) {
        Intent i = new Intent(ctx, SocketService.class);
        i.setAction(ACTION_KEEPALIVE);
        ctx.startService(i);
    }

    public static void actionUpdate(Context ctx, String mac) {
        Intent i = new Intent(ctx, SocketService.class);
        i.setAction(ACTION_UPDATE);
        i.putExtra("mac", mac);
        ctx.startService(i);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: " + intent.getAction());
        String action = intent.getAction();
        if (action.equals(ACTION_START)) {
            mMacAddress = intent.getExtras().getString("mac", "");
            start();
        } else {
            if (action.equals(ACTION_STOP)) {
                stop();
                stopSelf();
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
        return START_REDELIVER_INTENT;
    }

    private void keepAlive() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop();
    }

    private synchronized void setStarted(boolean started) {
        ManagerPrefs.putBoolean(Constants.STARTED, started);
        mStarted = started;
    }

    private synchronized void start() {
        if (mStarted) {
            Log.d(TAG, "Connection that is already");
            return;
        }

        //Cambio de internet
        IntentFilter filterChange = new IntentFilter();
        filterChange.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mConnectivityChanged, filterChange);

        //Comandos
        IntentFilter filterCommand = new IntentFilter();
        filterCommand.addAction(Constants.UP);
        filterCommand.addAction(Constants.LEFT);
        filterCommand.addAction(Constants.DOWN);
        filterCommand.addAction(Constants.RIGHT);
        filterCommand.addAction(Constants.MOVE_UP);
        filterCommand.addAction(Constants.MOVE_LEFT);
        filterCommand.addAction(Constants.MOVE_DOWN);
        filterCommand.addAction(Constants.MOVE_RIGHT);
        registerReceiver(mReceiverCommand, filterCommand);

        setStarted(true);

        mConnection = new ConnectionSocket();
        mConnection.start();
    }

    private synchronized void stop() {
        if (!mStarted) {
            Log.d(TAG, "Attempt to stop connection not active.");
            return;
        }

        setStarted(false);
        try {
            unregisterReceiver(mConnectivityChanged);
            unregisterReceiver(mReceiverCommand);
        } catch (Exception e) {
            Log.e(TAG, "onDestroy: " + e.getMessage());
        }

        cancelReconnect();
        if (mConnection != null) {
            mConnection.abort();
            mConnection = null;
        }
    }

    public synchronized void cancelReconnect() {
        Log.d(TAG, "cancelReconnect: ");
        Intent i = new Intent();
        i.setClass(this, SocketService.class);
        i.setAction(ACTION_RECONNECT);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmMgr.cancel(pi);
    }

    private synchronized void startKeepAlives() {
        Log.d(TAG, "startKeepAlives: ");
        Intent i = new Intent();
        i.setClass(this, SocketService.class);
        i.setAction(ACTION_KEEPALIVE);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + KEEP_ALIVE_INTERVAL, KEEP_ALIVE_INTERVAL, pi);
    }

    private synchronized void stopKeepAlives() {
        Log.d(TAG, "stopKeepAlives: ");
        Intent i = new Intent();
        i.setClass(this, SocketService.class);
        i.setAction(ACTION_KEEPALIVE);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmMgr.cancel(pi);
    }

    public synchronized void scheduleReconnect(long startTime) {
        Log.d(TAG, "scheduleReconnect: ");
        setStarted(false);
        interval = ManagerPrefs.getLong(RETRY_INTERVAL, INITIAL_RETRY_INTERVAL);

        long now = System.currentTimeMillis();
        long elapsed = now - startTime;

        if (elapsed < interval) {
            interval = Math.min(interval * 4, MAXIMUM_RETRY_INTERVAL);
        } else {
            interval = INITIAL_RETRY_INTERVAL;
        }

        interval = 1 * 60 * 300;
        Log.d(TAG, "Rescheduling connection in " + interval + "ms.");
        ManagerPrefs.putLong(RETRY_INTERVAL, interval);
        Intent i = new Intent();
        i.setClass(this, SocketService.class);
        i.setAction(ACTION_RECONNECT);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, now + interval, pi);
        showMessage("Trying Again In " + (((interval) / 1000) - 3) + "s");
    }

    private synchronized void reconnectIfNecessary() {
        Log.d(TAG, "reconnectIfNecessary: ");
        if ((mConnection == null || !mConnection.isAlive()) && !mStarted) {
            Log.d(TAG, "Reconnecting...");
            mConnection = new ConnectionSocket();
            mConnection.start();
        }
    }

    private class ConnectionSocket extends Thread {

        private volatile boolean mAbort = false;
        private volatile boolean mRun = true;

        private String websocketHost = "wss://tororobot-server.ingeniouskey.com";

        ConnectionSocket() {
            Log.d(TAG, "ConnectionSocket: ");
        }

        @Override
        public synchronized void start() {
            super.start();
            long startTime = System.currentTimeMillis();
            webSocketConnect(startTime);
        }

        /**
         * Preparar el Socket
         */
        private void webSocketConnect(final long startTime) {
            Log.d(TAG, "webSocketConnect: ");
            if (webSocketConnection == null || !webSocketConnection.isConnected()) {
                webSocketConnection = new WebSocketConnection();
                try {
                    webSocketConnection.connect(URI.create(websocketHost), new WebSocketConnectionHandler() {

                        @Override
                        public void onOpen() {
                            super.onOpen();
                            Log.d(TAG, "onOpen: ");
                            setStarted(true);
                        }

                        @Override
                        public void onTextMessage(String payload) {
                            super.onTextMessage(payload);
                            Log.d(TAG, "onTextMessage: " + payload);
                            Log.d(TAG, "Mac to: " + mMacAddress);
//                            if (!TextUtils.isEmpty(mMacAddress) && !TextUtils.isEmpty(payload)) {
//                                Intent intent = new Intent();
//                                intent.setAction(ACTION_COMMAND);
//                                intent.putExtra("mac", mMacAddress);
//                                intent.putExtra("command", payload);
//                                sendBroa  dcast(intent);
//                            }

                        }

                        @Override
                        public void onClose(WebSocketCloseNotification code, String reason) {
                            super.onClose(code, reason);
                            Log.d(TAG, "onClose: ");
                            showMessage("No se pudo conectar al servidor...");
                            webSocketConnection = null;
                            scheduleReconnect(startTime);
                        }

                    });
                } catch (WebSocketException e) {
                    Log.e(TAG, "run: " + e.getMessage());
                    webSocketConnection.disconnect();
                    webSocketConnection = null;
                    mRun = false;
                }
            } else {
                Log.d(TAG, "No connected because already connected");
            }
        }

        void abort() {
            mAbort = true;
            while (true) {
                try {
                    join();
                    break;
                } catch (InterruptedException e) {
                    Log.e(TAG, "Cant't join");
                }
            }
        }

    }

    private void showMessage(String s) {
        handler.post(() -> Toast.makeText(SocketService.this, s, Toast.LENGTH_LONG).show());
    }

    private final BroadcastReceiver mReceiverCommand = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: " + intent.getAction());
            switch (intent.getAction()) {
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

    public static void sendMsg(String s) {
        Log.d(TAG, "sendMsg = " + s);
        if (!TextUtils.isEmpty(s))
            if (webSocketConnection != null) {
                webSocketConnection.sendTextMessage(s);
            }
    }

    private BroadcastReceiver mConnectivityChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive change: ");
            ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = conn.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                reconnectIfNecessary();
            }
        }
    };

}