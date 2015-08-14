package com.example.ashishkrishna.gps;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by noon on 8/7/15.
 */
public class CommentActivity extends ActionBarActivity {
    public Context context;
    public DatabaseService FirstServicer;
    public ServiceConnection mConnection;
    private String address, lat, lon, rating, ada, gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        context = getApplicationContext();

        final ActionBar actionBar = getSupportActionBar();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.rgb(3, 155, 229));
        }
        //actionBar.setLogo(R.drawable.action_bar_logo_32px);
        //actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setTitle("Review");
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            lat = extras.getString("Lat");
            lon = extras.getString("Lon");
            address = extras.getString("Address");
            rating = extras.getString("Rating");
            ada = extras.getString("Ada");
            gender = extras.getString("Gender");
        }

        mConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                DatabaseService.LocalBinder binder = (DatabaseService.LocalBinder) service;
                FirstServicer = binder.getService();
                //binded = true;

            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                unbindService(mConnection);

            }
        };

        EditText userComment = (EditText) findViewById(R.id.userComment);
        userComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText textEntry = (EditText) findViewById(R.id.userComment);
                textEntry.setTextColor(getResources().getColor(R.color.textFieldColor));
            }
        });

        Button submitButton = (Button) findViewById(R.id.submit_comment);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText textEntry = (EditText) findViewById(R.id.userComment);
                if (textEntry.getText().toString().isEmpty()) {
                    FirstServicer.createnewComment();
                    FirstServicer.commerror = "DNE";
                    FirstServicer.attempt8.execute(lat, lon, FirstServicer.commerror);
                    //getActivity().getSupportFragmentManager().popBackStack();
                    onBackPressed();


                } else {
                    FirstServicer.createnewComment();
                    FirstServicer.commerror = textEntry.getText().toString();
                    FirstServicer.attempt8.execute(lat, lon, FirstServicer.commerror);
                    //getActivity().getSupportFragmentManager().popBackStack();
                    onBackPressed();
                }
            }
        });
        buttonEffect(submitButton);

    }

    public void onBackPressed() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        }).start();
        this.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService

        Intent intent = new Intent(context, DatabaseService.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service

        unbindService(mConnection);

    }

    public static void buttonEffect(View button) {
        button.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(0xffBDBDBD, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }
}
