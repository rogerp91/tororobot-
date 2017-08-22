package com.tororobot.di.module;

import com.tororobot.executor.Executor;
import com.tororobot.executor.MainThread;
import com.tororobot.executor.MainThreadImpl;
import com.tororobot.executor.ThreadExecutor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by roger on 18/10/16.
 */

@Module(
        library = true
)
public class ExecutorModule {

    @Provides
    @Singleton
    Executor provideExecutor(ThreadExecutor threadExecutor) {
        return threadExecutor;
    }

    @Provides
    @Singleton
    MainThread provideMainThread(MainThreadImpl impl) {
        return impl;
    }

}