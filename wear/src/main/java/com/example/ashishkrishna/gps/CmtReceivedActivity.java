package com.example.ashishkrishna.gps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


/**
 * Created by qinbian on 7/30/15.
 */
public class CmtReceivedActivity extends Activity {
    Runnable mRunnable;
    Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmtreceived);

        mRunnable = new Runnable(){
            @Override
            public void run(){
                Intent openLogResultIntent = new Intent(CmtReceivedActivity.this, LogResultActivity.class);
                CmtReceivedActivity.this.startActivity(openLogResultIntent);
            }
        };

        mHandler.postDelayed(mRunnable, 2000);
    }
}
