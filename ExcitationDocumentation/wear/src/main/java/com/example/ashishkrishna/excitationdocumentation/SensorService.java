package com.example.ashishkrishna.excitationdocumentation;

/**
 * Created by ashishkrishna on 7/5/15.
 */

import android.app.Activity;
import android.hardware.Sensor;
import android.content.Context;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.app.Service;
import android.os.IBinder;
import android.os.Process;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.ConnectionResult;
import android.content.BroadcastReceiver;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.MessageApi;
import android.support.v4.app.NotificationCompat;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import android.app.PendingIntent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import java.lang.Object;
import android.view.View;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.widget.Toast;
import android.R.drawable;
import android.app.Notification;

import com.google.android.gms.wearable.WearableListenerService;

import java.lang.Math;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class SensorService extends Service implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    public Intent intent;


    private static final long CONNECTION_TIME_OUT_MS = 1000;
    private GoogleApiClient client;
    public static final String START_ACTIVITY_PATH = "/start/ExcitationDocumentationActivity";

    private String nodeId;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startForeground(Process.myPid(), new Notification());
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        initApi();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);


    }

    @Override
    public IBinder onBind(Intent context) {
        return null;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
        stopForeground(true);
        Toast.makeText(this, "Service Ended", Toast.LENGTH_LONG).show();

    }

    private void initApi() {
        client = getGoogleApiClient(this);
        retrieveDeviceNode();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }



    private void sendMessage(String node) {
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


    @Override
    public void onSensorChanged(SensorEvent event) {
        if ((Math.abs(event.values[0]) >= 9.8) || (Math.abs(event.values[1]) >= 9.8 || (Math.abs(event.values[2]) >= 9.8))) {




            new Thread(new Runnable() {

                @Override
                public void run() {
                    Intent int1 = new Intent(SensorService.this, InvisibleActivity.class);
                    int1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent pendingintent1 = PendingIntent.getActivity(SensorService.this,0,int1, PendingIntent.FLAG_UPDATE_CURRENT );

                    NotificationCompat.Builder notificationBuilder =


                            new NotificationCompat.Builder(SensorService.this)
                                    .setSmallIcon(android.R.drawable.ic_menu_camera)
                                    .setContentTitle("You seem excited!")
                                    .setContentText("Document this excitement").setContentIntent(pendingintent1).addAction(android.R.drawable.ic_menu_camera,"TAKE PHOTO", pendingintent1  );

                    Notification notification1 = notificationBuilder.build();
                    notification1.flags = Notification.FLAG_ONGOING_EVENT;
                    NotificationManagerCompat notificationManager1 =
                            NotificationManagerCompat.from(SensorService.this);


                    int notifId = 001;
                    notificationManager1.notify(notifId, notification1);

                }
            }).start();
            try {

            } catch (Exception f) {
                Log.w("HIT HERE", "Ends to here");
            }





        }

    }


    private void retrieveDeviceNode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(client).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    nodeId = nodes.get(0).getId();
                }
                client.disconnect();
            }
        }).start();
    }

    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
    }



}

