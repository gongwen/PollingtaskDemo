package com.gw.pollingtask;

import android.app.Application;

/**
 * Created by GongWen on 17/1/13.
 */

public class MainApplication extends Application {
    private static MainApplication INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }

    public static MainApplication getInstance() {
        return INSTANCE;
    }
}
