package com.tororobot.di.module;

import com.tororobot.bluetooth.BluetoothActivity;
import com.tororobot.login.LoginActivity;
import com.tororobot.streaming.CallActivity;
import com.tororobot.ui.activity.MainActivity;

import dagger.Module;

@Module(
        injects = {
                MainActivity.class,
                LoginActivity.class,
                BluetoothActivity.class
        },
        complete = false
)
class ActivityGraphModule {

}
