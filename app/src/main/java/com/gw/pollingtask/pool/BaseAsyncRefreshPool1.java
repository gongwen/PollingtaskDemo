package com.gw.pollingtask.pool;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by GongWen on 16/11/30.
 * 轮询时间间隔＝ period
 */

public abstract class BaseAsyncRefreshPool1<T> extends BaseViewVisibility implements View.OnAttachStateChangeListener {

    private boolean isRefreshing = false;
    private long period;//轮询周期
    private View mAttachedView;
    private boolean isViewShown;
    private WeakReference<Handler> mUIHandlerRef;

    public BaseAsyncRefreshPool1(View mAttachedView, long period) {
        if (mAttachedView == null) {
            throw new IllegalArgumentException("Param mAttachedView " + mAttachedView + " must not be null!");
        }
        this.mAttachedView = mAttachedView;
        this.period = period;
        this.mAttachedView.addOnAttachStateChangeListener(this);
    }

    public void startRefresh() {
        if (!isRefreshing) {
            isRefreshing = true;
            isViewShown = isShown();
            mUIHandlerRef = new WeakReference<>(new Handler(Looper.getMainLooper()));
            mAttachedView.post(task);
        }
    }

    public void stopRefresh() {
        if (isRefreshing) {
            mAttachedView.removeCallbacks(task);
            mUIHandlerRef.clear();
            isRefreshing = false;
        }
    }

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            if (isViewShown) {
                if (isShown()) {
                    ThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
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
                    });
                }
                if (isRefreshing) {
                    mAttachedView.postDelayed(this, period);
                }
            } else {
                isViewShown = isShown();
                mAttachedView.postDelayed(this, 100);
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
