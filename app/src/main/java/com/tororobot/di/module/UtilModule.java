package com.tororobot.di.module;

import com.tororobot.util.MS;
import com.tororobot.util.MSImp;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by roger on 20/10/16.
 */

@Module(
        library = true,
        complete = false
)
public class UtilModule {

    @Singleton
    @Provides
    MS provideMS() {
        return new MSImp();
    }

}