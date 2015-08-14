package com.example.ashishkrishna.gps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by qinbian on 7/30/15.
 */
public class LogResultActivity extends Activity {

    Runnable mRunnable;
    Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logresult);

        mRunnable = new Runnable(){
            @Override
            public void run(){
//                Intent openLogResultIntent = new Intent(LogResultActivity.this, MainActivity.class);
//                LogResultActivity.this.startActivity(openLogResultIntent);
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            }
        };

        mHandler.postDelayed(mRunnable, 2000);
    }

}
