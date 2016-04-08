package com.example.ashishkrishna.gps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by ashishkrishna on 8/2/15.
 */
    public class InvisibleActivity extends Activity {

        public static int setter = 0;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    startService(new Intent(InvisibleActivity.this, DisconnectListenerService.class));
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    startService(new Intent(InvisibleActivity.this, Listener.class));


                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(InvisibleActivity.this, MainActivity.class));
                }
            }).start();
            this.finish();



        }



}
