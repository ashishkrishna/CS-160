package com.example.ashishkrishna.gps;

/**
 * Created by ashishkrishna on 7/31/15.
 */
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import android.content.Intent;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Listener extends WearableListenerService {


    ServiceConnection mConnection;
    ServiceConnection mConnect2;
    DatabaseService FirstServicer;
    DatabaseTwoService SecondServer;
    public LocationManager location1;
    public static double longitudedoub;
    public static double latitudedoub;
    public static  String BACK_ACTIVITY_PATH = "/start/ListenerService";
    private static final long CONNECTION_TIME_OUT_MS = 100;
    private String nodeId;
    private GoogleApiClient client;
    public int a;
    public static final String START_ACTIVITY_PATH = "/newentry";
    Timer timer = new Timer();
    Timer timer2 = new Timer();
    String[] tauzet;
    public static double lat;
    public static double lon;
    public static double autoseqlat;
    public static double autoseqlon;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w("Listener Service Started:", "Mobile");
        updatePos();
        if (null != intent) {
            String action = intent.getAction();

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
        mConnect2 = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service2) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                DatabaseTwoService.LocalBinder binder2 = (DatabaseTwoService.LocalBinder) service2;
                SecondServer = binder2.getService();
                //binded = true;

            }

            @Override
            public void onServiceDisconnected(ComponentName arg2) {
                unbindService(mConnect2);

            }
        };
        initApi();
        //Intent alpha = new Intent(this, DatabaseService.class);
        //bindService(alpha, mConnection, Context.BIND_AUTO_CREATE);

        //Intent alpha = new Intent(this, DatabaseService.class);
        //bindService(alpha, mConnection, Context.BIND_AUTO_CREATE);
        return super.onStartCommand(intent, flags, startId);
    }



    public void updatePos() {
        location1 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        location1.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        Location locationset = location1.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        latitudedoub = locationset.getLatitude();
        longitudedoub = locationset.getLongitude();
    }
    public final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double longitude;
            double latitude;
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            Log.w("Long:", String.valueOf(longitude));
            Log.w("Lat:", String.valueOf(latitude));
            longitudedoub = longitude;
            latitudedoub = latitude;



        }
        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    };

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.w("H", "H");
        nodeId = messageEvent.getSourceNodeId();
        showToast(messageEvent.getPath());
        if (messageEvent.getPath().equals(START_ACTIVITY_PATH)) {
            Intent alpha = new Intent(this, DatabaseService.class);
            startService(alpha);
            bindService(alpha, mConnection, Context.BIND_AUTO_CREATE);
            Intent alpha2 = new Intent(this, DatabaseTwoService.class);
            startService(alpha2);
            bindService(alpha2, mConnect2, Context.BIND_AUTO_CREATE);
            /*Intent intent = new Intent(this, DatabaseActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent); */

        }

        else if (messageEvent.getPath().equals("/onactive")) {
            Log.w("Hits", "The Listener");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent alpha = new Intent(Listener.this, DatabaseService.class);
                    ComponentName alphabravo = startService(alpha);
                    bindService(new Intent(Listener.this, DatabaseService.class), mConnection, Context.BIND_AUTO_CREATE);
                }
            }).start();
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException f) {

            }
            FirstServicer.createnewLoad();
            //FirstServicer.createjson();
            String str123[] = FirstServicer.reading();
            if(str123[2].equals("gender neutral")) {
                str123[2] = "genderneutral";
            }
            Log.w("These are the values", str123[0]);
            Log.w("These are the values", str123[1]);
            Log.w("These are the values", str123[2]);
            Log.w("These are the values", str123[3]);

            if (str123[0] == "male") {
                Log.w("True", "This is here");
                if (str123[3] == "true") {
                    FirstServicer.attempt10.execute(Double.toString(FirstServicer.latitudedoub), Double.toString(FirstServicer.longitudedoub), "male", "true");
                    /*  new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int j = 0; j < FirstServicer.vals.length; j++) {
                                BACK_ACTIVITY_PATH = FirstServicer.vals[j];
                                retrieveDeviceNode();
                            }
                        }
                    }).start(); */
                } else {
                    FirstServicer.attempt10.execute(Double.toString(FirstServicer.latitudedoub), Double.toString(FirstServicer.longitudedoub), "male", "false");

                   /*  new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int j = 0; j < FirstServicer.vals.length; j++) {
                                BACK_ACTIVITY_PATH = FirstServicer.vals[j];
                                retrieveDeviceNode();
                            }
                        }
                    }).start(); */


                }


            }
            if (str123[1] == "female") {
                if (str123[3] == "true") {
                    FirstServicer.attempt11.execute(Double.toString(FirstServicer.latitudedoub), Double.toString(FirstServicer.longitudedoub), "female", "true");
/*  new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int j = 0; j < FirstServicer.vals.length; j++) {
                                BACK_ACTIVITY_PATH = FirstServicer.vals[j];
                                retrieveDeviceNode();
                            }
                        }
                    }).start(); */


                } else {
                    FirstServicer.attempt11.execute(Double.toString(FirstServicer.latitudedoub), Double.toString(FirstServicer.longitudedoub), "female", "false");

                   /*  new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int j = 0; j < FirstServicer.vals.length; j++) {
                                BACK_ACTIVITY_PATH = FirstServicer.vals[j];
                                retrieveDeviceNode();
                            }
                        }
                    }).start(); */

                }
            }

            if (str123[2] == "genderneutral") {
                if (str123[3] == "true") {
                    FirstServicer.attempt12.execute(Double.toString(FirstServicer.latitudedoub), Double.toString(FirstServicer.longitudedoub), "genderneutral", "true");
                    //unbindService(mConnection);
                    Log.w("PING", "LISTEN");
                    /*  new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int j = 0; j < FirstServicer.vals.length; j++) {
                                BACK_ACTIVITY_PATH = FirstServicer.vals[j];
                                retrieveDeviceNode();
                            }
                        }
                    }).start(); */


                } else {
                    FirstServicer.attempt12.execute(Double.toString(FirstServicer.latitudedoub), Double.toString(FirstServicer.longitudedoub), "genderneutral", "false");
                    //unbindService(mConnection);
                    //Log.w("NUMVALS: ", Integer.toString(FirstServicer.vals.length));
                    //Log.w("Value1:", FirstServicer.vals[0]);


                     /* new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int j = 0; j < FirstServicer.vals.length; j++) {
                                BACK_ACTIVITY_PATH = FirstServicer.vals[j];
                                retrieveDeviceNode();
                            }
                        }
                    }).start(); */




                }
            }
        }
         if(messageEvent.getPath().length() > 9) {
            if (messageEvent.getPath().substring(0, 10).equals("/onpassive")) {
                Log.w("Passive:", " Start this");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent alpha = new Intent(Listener.this, DatabaseTwoService.class);
                        ComponentName alphabravo = startService(alpha);
                        bindService(new Intent(Listener.this, DatabaseTwoService.class), mConnect2, Context.BIND_AUTO_CREATE);
                    }
                }).start();
                try {
                    Thread.currentThread().sleep(1000);
                } catch (InterruptedException f) {

                }

                SecondServer.createnewLoad();
                String str140[] = SecondServer.reading();
                Log.w("These are the values", str140[0]);
                Log.w("These are the values", str140[1]);
                Log.w("These are the values", str140[2]);
                Log.w("These are the values", str140[3]);
                if(str140[2].equals("gender neutral")) {{
                    str140[2] = "genderneutral";
                }}
                a = Integer.parseInt(messageEvent.getPath().substring(10, messageEvent.getPath().length()));

                if (str140[0].equals("male")) {
                    Log.w("True", "This is here");
                    if (str140[3].equals("true")) {

                        timer = new Timer();
                        timer.schedule(new AutoSequence(), 0, 150000);

/*  new Thread(new Runnable() {
 @Override
 public void run() {
 for (int j = 0; j < FirstServicer.vals.length; j++) {
 BACK_ACTIVITY_PATH = FirstServicer.vals[j];
 retrieveDeviceNode();
 }
 }
 }).start(); */
                    } else {
                        timer = new Timer();
                        timer.schedule(new AutoSequence(), 0, 150000);


/*  new Thread(new Runnable() {
 @Override
 public void run() {
 for (int j = 0; j < FirstServicer.vals.length; j++) {
 BACK_ACTIVITY_PATH = FirstServicer.vals[j];
 retrieveDeviceNode();
 }
 }
 }).start(); */


                    }


                }
                if (str140[1].equals("female")) {
                    if (str140[3].equals("true")) {
                        timer = new Timer();
                        timer.schedule(new AutoSequence(), 0, 150000);
/*  new Thread(new Runnable() {
 @Override
 public void run() {
 for (int j = 0; j < FirstServicer.vals.length; j++) {
 BACK_ACTIVITY_PATH = FirstServicer.vals[j];
 retrieveDeviceNode();
 }
 }
 }).start(); */


                    } else {
                        timer = new Timer();
                        timer.schedule(new AutoSequence(), 0, 150000);


/*  new Thread(new Runnable() {
 @Override
 public void run() {
 for (int j = 0; j < FirstServicer.vals.length; j++) {
 BACK_ACTIVITY_PATH = FirstServicer.vals[j];
 retrieveDeviceNode();
 }
 }
 }).start(); */

                    }
                }

                if (str140[2].equals("genderneutral")) {
                    if (str140[3].equals("true")) {
                        timer = new Timer();
                        timer.schedule(new AutoSequence(), 0, 150000);

//unbindService(mConnection);
                        Log.w("PING", "LISTEN");
/*  new Thread(new Runnable() {
 @Override
 public void run() {
 for (int j = 0; j < FirstServicer.vals.length; j++) {
 BACK_ACTIVITY_PATH = FirstServicer.vals[j];
 retrieveDeviceNode();
 }
 }
 }).start(); */


                    } else {
                        timer = new Timer();
                        timer.schedule(new AutoSequence(), 0, 150000);
//unbindService(mConnection);
//Log.w("NUMVALS: ", Integer.toString(FirstServicer.vals.length));
//Log.w("Value1:", FirstServicer.vals[0]);


/* new Thread(new Runnable() {
 @Override
 public void run() {
 for (int j = 0; j < FirstServicer.vals.length; j++) {
 BACK_ACTIVITY_PATH = FirstServicer.vals[j];
 retrieveDeviceNode();
 }
 }
 }).start(); */


                    }
                }


            }  if (messageEvent.getPath().equals("/stoppassive")) {
                timer.cancel();
            }


            }



           /* try {
                Thread.currentThread().sleep(4000);
            } catch (InterruptedException d) {

            } */
        // async.interrupt();

        //JSONArray parser = new JSONArray(FirstServicer.argumes);
        // Log.w(FirstServicer.argumes, "This comes from Listening");

        if(messageEvent.getPath().equals("/newentry")) {


        }

        if(messageEvent.getPath().equals("/skip")) {
            unbindService(mConnection);
        }
        if (messageEvent.getPath().equals("1.0") || messageEvent.getPath().equals("2.0") || messageEvent.getPath().equals("3.0") || messageEvent.getPath().equals("4.0") || messageEvent.getPath().equals("5.0")) {
            if (messageEvent.getPath().equals("1.0")) {
                FirstServicer.rating = 1;

            }
            if (messageEvent.getPath().equals("2.0")) {
                FirstServicer.rating = 2;

            }
            if (messageEvent.getPath().equals("3.0")) {
                FirstServicer.rating = 3;

            }
            if (messageEvent.getPath().equals("4.0")) {
                FirstServicer.rating = 4;

            }
            if (messageEvent.getPath().equals("5.0")) {
                FirstServicer.rating = 5;

            }


        }

        if (messageEvent.getPath().equals("true")) {
            FirstServicer.adaaccess = "true";
        }
        if (messageEvent.getPath().equals("false")) {
            FirstServicer.adaaccess = "false";
        }

        if (messageEvent.getPath().equals("/male")) {
            FirstServicer.gender = "male";
            FirstServicer.attempt4.execute(Double.toString(FirstServicer.latitudedoub), Double.toString(FirstServicer.longitudedoub), Integer.toString(FirstServicer.rating), FirstServicer.adaaccess, FirstServicer.gender);

        }

        if (messageEvent.getPath().equals("/female")) {
            FirstServicer.gender = "female";
            FirstServicer.attempt4.execute(Double.toString(FirstServicer.latitudedoub), Double.toString(FirstServicer.longitudedoub), Integer.toString(FirstServicer.rating), FirstServicer.adaaccess, FirstServicer.gender);

        }

        if (messageEvent.getPath().equals("/genderneutral")) {
            FirstServicer.gender = "genderneutral";
            FirstServicer.attempt4.execute(Double.toString(FirstServicer.latitudedoub), Double.toString(FirstServicer.longitudedoub), Integer.toString(FirstServicer.rating), FirstServicer.adaaccess, FirstServicer.gender);

        }

        else if ( messageEvent.getPath().equals("finishthetimerautoseq")) {
            timer2.cancel();
        }

    }

    private void initApi() {
        Log.w("H", "H");
        client = getGoogleApiClient(getApplicationContext());
    }


    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(getApplicationContext())
                .addApi(Wearable.API)
                .build();
    }

    private void retrieveDeviceNode() {
        Log.w("G", "G");
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
                //nodeId.equals(null))
                sendMessage(nodeId);


                client.disconnect();
            }
        }).start();
    }
    private void sendMessage(String node) {
        Log.w("I", "I");
        // Log.w("Node Id#:", node);
        Wearable.MessageApi.sendMessage(client, node, BACK_ACTIVITY_PATH, new byte[0]).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                if (!sendMessageResult.getStatus().isSuccess()) {
                    Log.e("GoogleApi", "Failed to send message with status code: "
                            + sendMessageResult.getStatus().getStatusCode());
                }
            }
        });
    }

    public static void getLatLongFromAddress(String youraddress) {
        String uri = "http://maps.google.com/maps/api/geocode/json?address=" +
                youraddress + "&sensor=false";
        HttpGet httpGet = new HttpGet(uri);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());

            Listener.lon = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            Listener.lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

            Log.d("latitude", "" + lat);
            Log.d("longitude", "" + lon);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    class AutoSequenceTimer extends TimerTask {


        @Override
        public void run() {
            updatePos();
            if (Math.abs(autoseqlat-latitudedoub) < 0.001 && Math.abs(autoseqlon - longitudedoub) < 0.001) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BACK_ACTIVITY_PATH = "reacheddest";
                        client.connect();
                        sendMessage(DisconnectListenerService.bestNode);
                        client.disconnect();
                    }
                }).start();


            }

        }
    }

    class AutoSequence extends TimerTask {
        @Override
        public void run() {
            SecondServer.createnewLoad();
            Log.w("AutoSequence", "HERE");
            String str140[] = SecondServer.reading();
            Log.w("These are the values", str140[0]);
            Log.w("These are the values", str140[1]);
            Log.w("These are the values", str140[2]);
            if (str140[2].equals("gender neutral")) {
                str140[2] = "genderneutral";
            }
            Log.w("These are the values", str140[3]);

            if (str140[0].equals("male")) {
                Log.w("True", "This is here");
                if (str140[3].equals("true")) {
                    SecondServer.attempt10.execute(Double.toString(SecondServer.latitudedoub), Double.toString(SecondServer.longitudedoub), "male", "true", Integer.toString(a));
                } else {
                    SecondServer.attempt10.execute(Double.toString(SecondServer.latitudedoub), Double.toString(SecondServer.longitudedoub), "male", "false", Integer.toString(a));


                }


            }
            if (str140[1].equals("female")) {
                if (str140[3].equals("true")) {
                    SecondServer.attempt10.execute(Double.toString(SecondServer.latitudedoub), Double.toString(SecondServer.longitudedoub), "female", "true", Integer.toString(a));


                } else {
                    SecondServer.attempt10.execute(Double.toString(SecondServer.latitudedoub), Double.toString(SecondServer.longitudedoub), "female", "false", Integer.toString(a));


                }
            }

            if (str140[2].equals("genderneutral")) {
                if (str140[3].equals("true")) {
                    SecondServer.attempt10.execute(Double.toString(SecondServer.latitudedoub), Double.toString(SecondServer.longitudedoub), "genderneutral", "true", Integer.toString(a));



                } else {
                    SecondServer.attempt10.execute(Double.toString(SecondServer.latitudedoub), Double.toString(SecondServer.longitudedoub), "genderneutral", "false", Integer.toString(a));




                }
            }










        }

        }
    }






