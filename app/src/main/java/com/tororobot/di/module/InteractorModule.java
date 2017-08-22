package com.tororobot.di.module;

import com.tororobot.login.LoginContract;
import com.tororobot.login.LoginInteractor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Roger Patiño on 01/12/2015.
 */
@Module(
        library = true,
        includes = {
                ExecutorModule.class
        }
)
public class InteractorModule {

    @Provides
    @Singleton
    LoginContract.LoginInteractor provideLoginInteractor(LoginInteractor interactor) {
        return interactor;
    }

}