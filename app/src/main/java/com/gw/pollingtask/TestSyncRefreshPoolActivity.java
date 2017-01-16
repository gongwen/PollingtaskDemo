package com.gw.pollingtask;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.gw.pollingtask.pool.BaseSyncRefreshPool;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by GongWen on 17/1/16.
 */

public class TestSyncRefreshPoolActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sync_activity);
        final TextView tv = (TextView) findViewById(R.id.tv);
        final BaseSyncRefreshPool refreshPool = new BaseSyncRefreshPool<String>("StockPool", tv, 1000) {

            @Override
            protected String getData() {
                Log.i(TAG, "轮询加载数据");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd hh:mm:ss");
                return format.format(Calendar.getInstance().getTime());
            }

            @Override
            protected void setData(String data) {
                tv.setText("当前时间  " + data);
            }
        };

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshPool.startRefresh();
            }
        });
        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshPool.stopRefresh();
            }
        });
        findViewById(R.id.goTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestSyncRefreshPoolActivity.this, TestActivity.class));
            }
        });

    }
}
