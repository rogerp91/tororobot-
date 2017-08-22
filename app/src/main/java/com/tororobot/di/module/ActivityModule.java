package com.tororobot.di.module;

import android.app.Activity;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Roger Patiño on 01/12/2015.
 */
@Module(
        includes = {
                ActivityGraphModule.class,
                PresenterModule.class
        },
        library = true,
        complete = false)
public class ActivityModule {

    private Activity activityContext;

    public ActivityModule(Activity activityContext) {
        this.activityContext = activityContext;
    }

    @Singleton
    @Provides
    Context provideActivityContext() {
        return activityContext;
    }

    @Singleton
    @Provides
    Activity provideActivityActivity() {
        return activityContext;
    }
}