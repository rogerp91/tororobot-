package com.tororobot.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import co.lujun.lmbluetoothsdk.BluetoothController;

import static com.tororobot.util.Constants.ACTION_COMMAND;

public class CommandReceiver extends BroadcastReceiver {

    public static final String TAG = CommandReceiver.class.getSimpleName();
    //    BluetoothController mBTController = BluetoothController.getInstance().build(Context context);

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        BluetoothController mBluetoothController = BluetoothController.getInstance().build(context);
        if (intent.getAction().equals(ACTION_COMMAND)) {
            String mac = intent.getExtras().getString("mac");
            String command = intent.getExtras().getString("command");
            mBluetoothController.connect(mac);
            assert command != null;
            mBluetoothController.write(command.getBytes());
        }
    }
}