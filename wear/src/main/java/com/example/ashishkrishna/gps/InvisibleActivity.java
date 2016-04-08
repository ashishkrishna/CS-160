package com.example.ashishkrishna.gps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by ashishkrishna on 7/31/15.
 */
public class InvisibleActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(new Runnable() {
            @Override
                    public void run() {
                startService(new Intent(InvisibleActivity.this, Listener.class));
            }

            }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                startService(new Intent(InvisibleActivity.this, DisconnectListenerService.class));
                //startService(new Intent(InvisibleActivity.this, Listener.class));
            }

        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(InvisibleActivity.this, MainActivity.class));
                //startService(new Intent(InvisibleActivity.this, Listener.class));
            }

        }).start();


        this.finish();



    }


}
