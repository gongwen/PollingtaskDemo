package com.gw.pollingtask;

import android.os.Build;
import android.os.PowerManager;

/**
 * Created by GongWen on 17/1/16.
 */

public class ScreenUtil {
    public static boolean isScreenOn() {
        PowerManager pm = (PowerManager) MainApplication.getInstance().getSystemService(MainApplication.getInstance().POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            if (pm.isInteractive()) {
                return true;
            }
        } else {
            if (pm.isScreenOn()) {
                return true;
            }
        }
        return false;
    }
}
