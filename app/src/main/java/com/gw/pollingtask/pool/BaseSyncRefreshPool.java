package com.gw.pollingtask.pool;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by GongWen on 17/1/13.
 * 轮询时间间隔＝ period ＋ 获取数据的时间
 * （Android View的可见性检查方法）http://unclechen.github.io/2016/10/17/Android-View%E7%9A%84%E5%8F%AF%E8%A7%81%E6%80%A7%E6%A3%80%E6%9F%A5%E6%96%B9%E6%B3%95-%E4%B8%8A%E7%AF%87/
 */

public abstract class BaseSyncRefreshPool<T> extends BaseViewVisibility implements View.OnAttachStateChangeListener {
    private final int MSG_UPDATE_INFO = 0x110;

    private String name;
    private View mAttachedView;
    private long period;
    private HandlerThread mHandlerThread;
    private Handler mThreadHandler;
    private WeakReference<Handler> mUIHandlerRef;

    private boolean isRefreshing = false;
    private boolean isViewShown = false;

    public BaseSyncRefreshPool(String name, View mAttachedView, long period) {
        if (mAttachedView == null) {
            throw new IllegalArgumentException("Param mAttachedView " + mAttachedView + " must not be null!");
        }
        this.name = name;
        this.mAttachedView = mAttachedView;
        this.mAttachedView.addOnAttachStateChangeListener(this);
        this.period = period;
    }

    public void startRefresh() {
        if (!isRefreshing) {
            isRefreshing = true;

            mUIHandlerRef = new WeakReference<>(new Handler(Looper.getMainLooper()));
            mHandlerThread = new HandlerThread(name);
            mHandlerThread.start();

            mThreadHandler = new Handler(mHandlerThread.getLooper()) {

                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (isViewShown) {
                        if (isShown()) {
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
                        if (isRefreshing) {
                            mThreadHandler.sendEmptyMessageDelayed(MSG_UPDATE_INFO, period);
                        }
                    } else {
                        isViewShown = isShown();
                        mThreadHandler.sendEmptyMessageDelayed(MSG_UPDATE_INFO, 100);
                    }
                }
            };
            isViewShown = isShown();
            mThreadHandler.sendEmptyMessage(MSG_UPDATE_INFO);
        }
    }

    protected abstract T getData();

    protected abstract void setData(T data);

    public void stopRefresh() {
        if (isRefreshing) {
            mUIHandlerRef.clear();
            mThreadHandler.removeMessages(MSG_UPDATE_INFO);
            mHandlerThread.quit();
            isRefreshing = false;
        }
    }

    public boolean isShown() {
        return isShown(mAttachedView);
    }

    @Override
    public void onViewAttachedToWindow(View v) {
    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        stopRefresh();
    }
}
