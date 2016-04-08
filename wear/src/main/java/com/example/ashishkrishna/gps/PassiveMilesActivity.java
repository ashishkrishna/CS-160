package com.example.ashishkrishna.gps;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.ConfirmationActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.TimeUnit;

/**
 * Created by qinbian on 8/6/15.
 */
public class PassiveMilesActivity extends Activity {

    NumberPicker milesNumberPicker;
    ImageButton confirmButton;
    public GoogleApiClient client;
    public int CONNECTION_TIME_OUT_MS = 1000;
    public String START_ACTIVITY_PATH;
    Handler mHandler;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passive_miles);
        milesNumberPicker = (NumberPicker) findViewById(R.id.milesNumberPicker);
        confirmButton = (ImageButton) findViewById(R.id.confirm_icon);
        mHandler = new Handler();
        initApi();

        setDividerColor(milesNumberPicker, Color.parseColor("#E81D58"));
        createWordSpinner();
        addListenerOnConfirm();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean openListFlag = intent.getBooleanExtra("open_error", false);
        if (openListFlag) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    START_ACTIVITY_PATH = "openerr";
                    client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    sendMessage(DisconnectListenerService.bestNode);
                    Log.w(DisconnectListenerService.bestNode, "Node here");
                    client.disconnect();
                }


            }).start();
            
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent helpMeIDKIntent = new Intent(PassiveMilesActivity.this, ConfirmationActivity.class);
                    helpMeIDKIntent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
                    helpMeIDKIntent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                            getString(R.string.phone_opened));
                    startActivity(helpMeIDKIntent);

                }
            }).start();
        }

    }

    private void addListenerOnConfirm() {
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmButton.setImageResource(R.drawable.check_circle_clicked);
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        confirmButton.setImageResource(R.drawable.check_circle);
                    }
                }, 2000);

                createNotification();


                SharedPreferences m = getSharedPreferences("prefs", 0);
                boolean running = m.getBoolean("passiveRunning", false);
                if (!running) {
                    int temporaryvar = milesNumberPicker.getValue() * 5260;
                    START_ACTIVITY_PATH = "/onpassive" + Integer.toString(temporaryvar);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                            sendMessage(DisconnectListenerService.bestNode);
                            Log.w(DisconnectListenerService.bestNode, "Node here");
                            client.disconnect();
                        }
                    }).start();
                    SharedPreferences.Editor editing = m.edit();
                    editing.putBoolean("passiveRunning", true);
                    editing.commit();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Intent startMain = new Intent(Intent.ACTION_MAIN);
                            startMain.addCategory(Intent.CATEGORY_HOME);
                            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(startMain);
                        }
                    }).start();
                    finish();

                }
            }
        });
    }


    private void createNotification() {
        int notificationId = 001;
        // Build intent for notification content
        Intent viewIntent = new Intent(this, PassiveMilesActivity.class);
        viewIntent.putExtra("open_error", true);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);


        Intent helpMeIDKIntent = new Intent(this, PassiveStop.class);
        //MapsActivity.lat = "37.874760";
        //MapsActivity.lon = "-122.258480";
        //Intent NavIntent = new Intent(this, MapsActivity.class);



        Bitmap bitmapbg = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.active_bg);
        PendingIntent helpPendingIntent =
                PendingIntent.getActivity(this, 0, helpMeIDKIntent, 0);

        //PendingIntent NavigateIntent =
                //PendingIntent.getActivity(this, 0, NavIntent, 0);




        // Build the notification and issues it with notification manager.
        // Get an instance of the NotificationManager service


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.watch_icon)
                        .setContentTitle("Passive Mode")
                        .setContentText("G-Pee-S is searching bathrooms for you")
                        .setContentIntent(viewPendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .addAction(R.drawable.cancel_notif, "Cancel", helpPendingIntent)
                        .extend(new NotificationCompat.WearableExtender().setBackground(bitmapbg));

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);



        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());



    }
    private void initApi() {
        Log.w("H", "H");
        client = getGoogleApiClient(this);
    }

    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
    }

    private void sendMessage(String node) {
        Log.w("I", "I");
        Log.w("Node Id#:", node);
        Wearable.MessageApi.sendMessage(client, node, START_ACTIVITY_PATH, new byte[0]).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                if (!sendMessageResult.getStatus().isSuccess()) {
                    Log.e("GoogleApi", "Failed to send message with status code: "
                            + sendMessageResult.getStatus().getStatusCode());
                }
            }
        });
    }
    private void createWordSpinner(){
        String[] units = new String[15];
        for (int i = 0; i < 15; i ++){
            units[i] = Integer.toString(i + 1);
        }
        milesNumberPicker.setMaxValue(units.length - 1);
        milesNumberPicker.setMinValue(0);
        milesNumberPicker.setDisplayedValues(units);
    }

    private void setDividerColor(NumberPicker picker, int color) {

        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
