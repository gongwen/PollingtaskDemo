package com.gw.pollingtask.pool;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by GongWen on 16/11/30.
 * 轮询时间间隔＝ period
 */

public abstract class BaseAsyncRefreshPool2<T> extends BaseViewVisibility implements View.OnAttachStateChangeListener {

    private boolean isRefreshing = false;
    private long initialDelay;
    private long period;//轮询周期
    private View mAttachedView;
    private WeakReference<Handler> mUIHandlerRef;
    private ScheduledThreadPoolExecutor mExecutorService;

    public BaseAsyncRefreshPool2(View mAttachedView, long initialDelay, long period) {
        if (mAttachedView == null) {
            throw new IllegalArgumentException("Param mAttachedView " + mAttachedView + " must not be null!");
        }
        this.mAttachedView = mAttachedView;
        this.period = period;
        this.mAttachedView.addOnAttachStateChangeListener(this);
        this.initialDelay = initialDelay;
    }

    public void startRefresh() {
        if (!isRefreshing) {
            isRefreshing = true;
            mUIHandlerRef = new WeakReference<>(new Handler(Looper.getMainLooper()));
            mExecutorService = new ScheduledThreadPoolExecutor(1);
            mExecutorService.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.MILLISECONDS);
        }
    }

    public void stopRefresh() {
        if (isRefreshing) {
            mUIHandlerRef.clear();
            isRefreshing = false;
            if (mExecutorService != null) {
                mExecutorService.shutdown();
            }
        }
    }

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            if (isRefreshing && isShown()) {
                final T data = getData();
                if (mUIHandlerRef.get() != null) {
                    mUIHandlerRef.get().post(new Runnable() {
                        @Override
                        public void run() {
                            setData(data);
                        }
                    });
                }
            }
        }
    };

    public boolean isShown() {
        return isShown(mAttachedView);
    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        stopRefresh();
    }

    @Override
    public void onViewAttachedToWindow(View v) {
    }

    protected abstract T getData();

    protected abstract void setData(T data);
}
