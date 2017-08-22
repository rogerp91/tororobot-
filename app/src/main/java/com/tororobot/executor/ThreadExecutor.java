package com.tororobot.executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

/**
 * Executor implementation based on ThreadPoolExecutor.
 * ThreadPoolExecutorConfig:
 * <p/>
 * Core pool size: 3. Max pool size: 5. Keep alive time: 120. Time unit:
 * seconds. Work queue: LinkedBlockingQueue.
 */
public class ThreadExecutor implements Executor {

    private static final int CORE_POOL_SIZE = 3;
    private static final int MAX_POOL_SIZE = 5;
    private static final int KEEP_ALIVE_TIME = 120;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private static final BlockingQueue<Runnable> WORK_QUEUE = new LinkedBlockingQueue<>();

    private ThreadPoolExecutor threadPoolexecutor;

    @Inject
    public ThreadExecutor() {
        int corePoolSize = CORE_POOL_SIZE;
        int maxPoolSize = MAX_POOL_SIZE;
        int keepAliveTime = KEEP_ALIVE_TIME;
        TimeUnit timeUnit = TIME_UNIT;
        BlockingQueue<Runnable> workQueue = WORK_QUEUE;

        threadPoolexecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, timeUnit, workQueue);
    }

    public ThreadExecutor(final int corePoolSize, final int maxPoolSize, final int keepAliveTime, final TimeUnit timeUnit) {
        BlockingQueue<Runnable> workQueue = WORK_QUEUE;
        threadPoolexecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, timeUnit, workQueue);
    }

    @Override
    public void run(final Interactor interactor) {
        if (interactor == null) {
            throw new IllegalArgumentException("InteractorCallback to execute can't be null");
        }
        threadPoolexecutor.submit(interactor);
    }

}