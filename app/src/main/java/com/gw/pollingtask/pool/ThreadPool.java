package com.gw.pollingtask.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by GongWen on 17/1/16.
 */

public class ThreadPool {
    private static ExecutorService mExecutorService = Executors.newCachedThreadPool();

    public static void execute(Runnable runnable) {
        mExecutorService.execute(runnable);
    }
}
