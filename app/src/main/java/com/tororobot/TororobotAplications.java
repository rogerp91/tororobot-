package com.tororobot;

import android.app.Application;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.ContextWrapper;

import com.github.rogerp91.prefsshared.ManagerPrefs;
import com.tororobot.di.module.AppModule;
import com.tororobot.util.ThreadConnected;

import java.util.List;

import dagger.ObjectGraph;

/**
 * Created by roger on 18/10/16.
 */

public class TororobotAplications extends Application {

    private static TororobotAplications instance;

    public static TororobotAplications getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    private ObjectGraph objectGraph;
    private static ThreadConnected myThreadConnected;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initDependencyInjection();
        new ManagerPrefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true).build();

//        AndroidNetworking.initialize(getApplicationContext());
//        AndroidNetworking.enableLogging();
////        AndroidNetworking.setConnectionQualityChangeListener(new ConnectionQualityChangeListener() {
////            @Override
////            public void onChange(ConnectionQuality currentConnectionQuality, int currentBandwidth) {
////                Log.d("TororobotAplications", "onChange: currentConnectionQuality : " + currentConnectionQuality + " currentBandwidth : " + currentBandwidth);
////            }
////        });

    }

    private void initDependencyInjection() {
        objectGraph = ObjectGraph.create(new AppModule(this));
        objectGraph.inject(this);
        objectGraph.injectStatics();
    }

    public ObjectGraph buildGraphWithAditionalModules(List<Object> modules) {
        if (modules == null) {
            throw new IllegalArgumentException("You can't plus a null module, review your getModules() implementation");
        }
        return objectGraph.plus(modules.toArray());
    }

    public static ThreadConnected setThreadConnected(BluetoothSocket socket) {
        myThreadConnected = new ThreadConnected(socket);
        return myThreadConnected;
    };

    public static ThreadConnected getThreadConnected() {
        return myThreadConnected;
    };
}