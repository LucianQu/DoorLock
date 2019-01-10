package com.blg.rtu.frmFunction.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolUtils {
    private static volatile ExecutorService cachedThreadPool ;
    public static ExecutorService getInstance() {
        if (cachedThreadPool == null) {
            cachedThreadPool = Executors.newCachedThreadPool();
        }
        return cachedThreadPool ;
    }
}
