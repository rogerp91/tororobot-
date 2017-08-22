package com.tororobot.di.module;

import android.content.Context;

import com.tororobot.ForApplication;
import com.tororobot.TororobotAplications;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by roger on 18/10/16.
 */

@Module(
        injects = TororobotAplications.class,
        library = true
)
public class AppModule {

    private final TororobotAplications application;

    public AppModule(TororobotAplications application) {
        this.application = application;
    }

    @Provides
    @Singleton
    @ForApplication
    Context provideApplicationContext() {
        return application;
    }

}