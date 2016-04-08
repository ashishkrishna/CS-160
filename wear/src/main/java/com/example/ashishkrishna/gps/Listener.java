package com.example.ashishkrishna.gps; /**
 * Created by ashishkrishna on 7/28/15.
 */
import android.app.Notification;
import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.*;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import android.content.Intent;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;


public class Listener extends WearableListenerService {
    public static final String BACK_ACTIVITY_PATH = "/start/ListenerService";
    public static final String END_ALL = "/end/endmyapplication";
    public static String PATH_PHONE = "tau";
    private static final long CONNECTION_TIME_OUT_MS = 1000;
    private GoogleApiClient client;
    String nodeId;
    public static DataMap dataMap;
    public static String[] itemizer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startForeground(android.os.Process.myPid(), new Notification());
        //Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        initApi();
        setupVoiceTranscription();
        if (dataMap != null) {
            Log.w("DataMap", " "+dataMap);
        }
        else {
            Log.w("nulled", "DataMap");
        }
        return START_STICKY;
    }

    private static final String WEARABLE_DATA_PATH = "/wearable_data";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {


        for (DataEvent event : dataEvents) {

            // Check the data type

            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // Check the data path
                Log.w("data", "rs");
                String path = event.getDataItem().getUri().getPath();
               if (path.equals("/wearable_datum")) {
                    Log.w("Changed", "change recleared to empty");
                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                   Intent hx = new Intent(Listener.this, ActiveActivity.class);
                   hx.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   startActivity(hx);
                   return;

                }

                if (path.equals(WEARABLE_DATA_PATH)) {

                    Log.w("H", "T");


                }

                dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                Log.v("myTag", "DataMap received on watch: " + dataMap);
                ActiveActivity.listItems = new ArrayList<String>();
                Listener.itemizer = new String[dataMap.size()];
                int i = 0;
                while (i < dataMap.size()) {
                    String plchld1 = "Address" + Integer.toString(i);
                    String plchd2 = dataMap.get(plchld1);
                    if (plchd2 == null) {
                        Log.w("Nulled", "over");
                        return;
                    }
                    Log.w("Log:", plchld1);
                    Log.w("Log:", plchd2);
                    Listener.itemizer[i] = plchd2;
                    Log.w("Logger", Listener.itemizer[i]);
                    i++;


                }


            }
            ActiveResultActivity.NUM_PAGES = Listener.itemizer.length;
            Intent hx = new Intent(Listener.this, ActiveResultActivity.class);
            hx.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(hx);

        }
    }


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Log.w("Message received", messageEvent.getPath());

        nodeId = messageEvent.getSourceNodeId();
        if (messageEvent.getPath().equals(BACK_ACTIVITY_PATH)) {
            Log.w("BackPATH", "BACKPATH");
            new Thread(new Runnable()  {
                @Override
                public void run() {
                    Intent intent = new Intent(Listener.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }).start();




        } if (messageEvent.getPath() == END_ALL) {
            String dummy = messageEvent.getPath();
            if (dummy.substring(0) == "h") {
                Log.w("HTTP", "http");
                try {
                    System.runFinalizersOnExit(true);

                } catch (Exception f) {
                    return;
                }


            }

        } else if (messageEvent.getPath() == "Same Type") {
            Log.w("A:", messageEvent.getPath());
            Intent intent1 = new Intent(Listener.this, ActiveActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);

        }
        else if (messageEvent.getPath().substring(0, 6).equals("Alert!")) {
            Log.w("Hits", "Alerting");
            String[] e = messageEvent.getPath().split("Alert!: ");
            String[] f = e[1].split("Rating: ");
            String[] g = f[1].split("Lat: ");
            String[] h = g[1].split("Lon: ");
            h[0].replaceAll("\\s+","");
            h[1].replaceAll("\\s+","");
            String coords = "Coord1: " + h[0] + " Coord2: " + h[1];
            PATH_PHONE = coords;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.blockingConnect();
                    sendMessage(DisconnectListenerService.bestNode, PATH_PHONE);
                    client.disconnect();

                }
            }).start();
            MapsActivity.lat = h[0];
            MapsActivity.lon = h[1];
            int notificationId = 001;
            // Build intent for notification content
            Intent viewIntent = new Intent(this, MapsActivity.class);
            viewIntent.putExtra("open_error", true);
            PendingIntent viewPendingIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.watch_icon)
                            .setContentTitle("Bathroom Found!")
                            .setContentText(f[0] + "has a close bathroom fitting your needs! Swipe left to go here!")
                            .setVibrate(new long[]{10000, 1000})
                            .setContentIntent(viewPendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_MAX).extend((new NotificationCompat.WearableExtender()));
            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(this);
            notificationManager.notify(notificationId, notificationBuilder.build());


        }
        else if (messageEvent.getPath().equals("reacheddest")) {
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.watch_icon)
                            .setContentTitle("Arrived at Bathroom!")
                            .setContentText("You have arrived at your destination!")
                            .setVibrate(new long[]{10000, 1000})
                            .setPriority(NotificationCompat.PRIORITY_MAX).extend((new NotificationCompat.WearableExtender()));
            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(this);
            notificationManager.notify(001, notificationBuilder.build());
            PATH_PHONE = "finishthetimerautoseq";
            new Thread (new Runnable()  {
                @Override
                public void run() {
                    client.connect();
                    sendMessage(DisconnectListenerService.bestNode,PATH_PHONE);
                    client.disconnect();
                }
            }).start();
        }
        else {

        }
    }




    private void initApi() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                Log.w("START", "starting");
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.w("ERROR", "Error");
            }
        }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionresult) {
                Log.w("COMMERROR", "Failed to connect");

            }
        }).addApi(Wearable.API).build();
        client.connect();

    }

    private void setupVoiceTranscription() {


        CapabilityApi.CapabilityListener capabilityListener =
                new CapabilityApi.CapabilityListener() {
                    @Override
                    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
                        Log.w("On capability", "This has now changed");
                    }
                };

        Wearable.CapabilityApi.addCapabilityListener(
                client,
                capabilityListener,
                "do_stuff");
    }

    private void sendMessage(String node, String mssg) {
        Log.w("I", "I");
        Log.w("Node Id#:", node);
        Wearable.MessageApi.sendMessage(client, node, mssg, new byte[0]).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                if (!sendMessageResult.getStatus().isSuccess()) {
                    Log.e("GoogleApi", "Failed to send message with status code: "
                            + sendMessageResult.getStatus().getStatusCode());
                }
            }
        });
    }


}

