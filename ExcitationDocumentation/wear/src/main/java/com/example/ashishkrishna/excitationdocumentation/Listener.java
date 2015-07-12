package com.example.ashishkrishna.excitationdocumentation;

import android.app.Notification;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.*;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import android.content.Intent;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;


/**
 * Created by ashishkrishna on 7/5/15.
 */
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import android.content.Intent;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.MessageApi;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import android.util.Log;

public class Listener extends WearableListenerService {
    public static final String BACK_ACTIVITY_PATH = "/start/ListenerService";
    public static final String END_ALL = "/end/endmyapplication";
    private static final long CONNECTION_TIME_OUT_MS = 1000;
    private GoogleApiClient client;
    String nodeId;

   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
       super.onStartCommand(intent, flags, startId);
       startForeground(android.os.Process.myPid(), new Notification());
       Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
       initApi();
       return START_STICKY;
   }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Log.w("Message received", messageEvent.getPath());

        nodeId = messageEvent.getSourceNodeId();
        if (messageEvent.getPath().equals(BACK_ACTIVITY_PATH)) {
            Log.w("BackPATH", "BACKPATH");
            Intent intent = new Intent(this, SensorService.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(intent);


        }
        else if (messageEvent.getPath() == END_ALL) {
            String dummy = messageEvent.getPath();
            if (dummy.substring(0) == "h") {
                Log.w("HTTP", "http");
                try {
                   System.runFinalizersOnExit(true);

                }
                catch (Exception f) {
                    return;
                }



            }

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



    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Log.w("url:", urldisplay);
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            return bmp;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}

