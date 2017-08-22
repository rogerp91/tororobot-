package com.tororobot.di.module;

import android.app.Activity;
import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Roger Patiño on 22/12/2015.
 */
@Module(
        includes = {
                FragmentGraphInjectModule.class,
                PresenterModule.class,
                UtilModule.class
        },
        library = true,
        complete = false)
public class FragmentModule {

    private final Activity activityContext;

    public FragmentModule(Activity activityContext) {
        this.activityContext = activityContext;
    }

    @Provides
    Context provideActivityContext() {
        return activityContext;
    }

    @Provides
    Activity provideActivityActivity() {//para el fragment
        return activityContext;
    }
}