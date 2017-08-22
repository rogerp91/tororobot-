package com.tororobot.di.module;


import com.tororobot.login.LoginContract;
import com.tororobot.login.LoginPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Roger Patiño on 01/12/2015.
 */
@Module(
        library = true,
        complete = false
)
public class PresenterModule {

    @Provides
    @Singleton
    LoginContract.Presenter provideLoginPresenter(LoginPresenter presenter) {
        return presenter;
    }

}