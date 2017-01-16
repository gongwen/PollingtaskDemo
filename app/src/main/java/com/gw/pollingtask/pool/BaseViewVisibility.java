package com.gw.pollingtask.pool;

import android.graphics.Rect;
import android.view.View;

import com.gw.pollingtask.ScreenUtil;

/**
 * Created by GongWen on 17/1/16.
 */

public class BaseViewVisibility {
    public boolean isShown(View mView) {
        if (ScreenUtil.isScreenOn()) {
            return mView.isShown() && mView.getLocalVisibleRect(new Rect());
        }
        return false;
    }
}
