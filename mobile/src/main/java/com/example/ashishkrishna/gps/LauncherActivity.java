package com.example.ashishkrishna.gps;

import android.app.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.net.Uri;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import android.os.SystemClock;
import android.content.Intent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class LauncherActivity extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.

    public static String pathname;
    public static Uri filename;
    public static String mCurrPhotoPath;
    private static final String MESSAGE = "Hello Wear!";
    public Intent twtr;
    private GoogleApiClient client;
    public static final String BACK_ACTIVITY_PATH = "/start/ListenerService";
    private static final long CONNECTION_TIME_OUT_MS = 100;
    private String nodeId;
    LocationService FirstServicer;
    ServiceConnection mConnection;
    boolean binded;
    LocationManager location1;
    public double longitudedoub;
    public double latitudedoub;
    Intent alpha;
    Intent alpha1;
    Intent alpha2;
    long timeElapsed = 0;
    private Button settingButton;
    private Button mainActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        setupWidgets();
        initApi();
        settingButtonListener();
        mainActivityButtonListener();


        mConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
                FirstServicer = binder.getService();
                binded = true;

            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {

            }
        };
        if (binded) {
            Log.w("Bound", "Method is bound");
        } else {
            Log.w("Crash", "About to crash");
        }
        //updatePos();

    }

    private void settingButtonListener(){
        settingButton = (Button) findViewById(R.id.setting_button);
        settingButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent openSettingIntent = new Intent(LauncherActivity.this, SettingActivity.class);
                LauncherActivity.this.startActivity(openSettingIntent);

            }
        });
    }

    private void mainActivityButtonListener(){
        mainActivityButton = (Button) findViewById(R.id.main_activity_button);
        mainActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent openMainIntent = new Intent(LauncherActivity.this, MainActivity.class);
                LauncherActivity.this.startActivity(openMainIntent);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        Intent alpha = new Intent(LauncherActivity.this, LocationService.class);
        bindService(alpha, mConnection, Context.BIND_AUTO_CREATE);
        startService(alpha);
        Intent i =  new Intent(this, Listener.class);
        startService(i);
        setupWidgets();
    }


    /**
     * Initializes the GoogleApiClient and gets the Node ID of the connected device.
     */
    /*
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
        /* client = getGoogleApiClient(this);
        retrieveDeviceNode();
    }  */

    /**
     * Sets up the button for handling click events.
     */

    private void setupWidgets() {
        Log.w("Hitting", "Hitting");
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                        /*sendMessage(nodeId); */
                if (FirstServicer == null) {
                    Log.w("Null", "null");
                } else {
                    Log.w("NON", "nonnull");
                }
                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS); */
                        retrieveDeviceNode();
                       // client.disconnect();
              /*      }
                }).start(); */
                //updatePos();


               /* try {
                 //   Thread.currentThread().sleep(3000);
               // } catch (Exception g) {

               // } */
               /* new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendMessage(nodeId);
                    }
                }).start(); */

                alpha = new Intent(LauncherActivity.this, MapsActivity.class);
                Thread thrd1 = new Thread(new Runnable() {
                    @Override
                    public void run() {


                        startActivity(alpha);
                    }
                });
                thrd1.start();

            }
        });
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                //sendMessage(nodeId);
                if (FirstServicer == null) {
                    Log.w("Null", "null");
                } else {
                    Log.w("NON", "nonnull");
                }
                //updatePos();
               /* Thread v= new Thread(new Runnable() {
                    @Override
                    public void run() {
                        client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS); */
                        retrieveDeviceNode();
                    //    client.disconnect();
                  /*  }
                }); */

               // v.start();

                alpha1 = new Intent(LauncherActivity.this, DatabaseActivity.class);
                Thread thrd2 = new Thread(new Runnable() {
                    @Override
                    public void run() {


                        startActivity(alpha1);
                    }
                });
                thrd2.start();


            }
        });
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                //sendMessage(nodeId);
                if (FirstServicer == null) {
                    Log.w("Null", "null");
                } else {
                    Log.w("NON", "nonnull");
                }
                //updatePos();
               /* Thread r = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS); */
                        retrieveDeviceNode();
                        //sendMessage(nodeId);
                   /*     client.disconnect();
                    }
                });
                r.start(); */
                //sendMessage(nodeId);

                alpha2 = new Intent(LauncherActivity.this, ErrorListActivity.class);



                Thread thrd3 = new Thread(new Runnable() {
                    @Override
                    public void run() {


                        startActivity(alpha2);
                    }
                });
                thrd3.start();


            }
        });
    }

    public void updatePos() {
        location1 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        location1.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        Location locationset = location1.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        latitudedoub = locationset.getLatitude();
        longitudedoub = locationset.getLongitude();

        /*LocationListener l1 = FirstServicer.locationListener;
        TextView f1 = (TextView) findViewById(R.id.textView);
        f1.setText(String.valueOf(FirstServicer.getlat()));
        TextView f5 = (TextView) findViewById(R.id.textView2);
        f5.setText(String.valueOf(FirstServicer.getlong())); */
    }

 /*   private void sendMessage(String node) {
        Wearable.MessageApi.sendMessage(client, node, BACK_ACTIVITY_PATH, new byte[0]).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                if (!sendMessageResult.getStatus().isSuccess()) {
                    Log.e("GoogleApi", "Failed to send message with status code: "
                            + sendMessageResult.getStatus().getStatusCode());
                }

            }
        });
    } */

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
            Log.w("Latcalling:", String.valueOf(getlat()));
            Log.w("Longcallback:", String.valueOf(getlong()));


        }

        public double getlat() {
            return latitudedoub;
        }

        public double getlong() {
            return longitudedoub;
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

    /**
     * Returns a GoogleApiClient that can access the Wear API.
     *
     * @return A GoogleApiClient that can make calls to the Wear API
     */
   /* private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
    } */
    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service

        unbindService(mConnection);

    }

    /**
     * Connects to the GoogleApiClient and retrieves the connected device's Node ID. If there are
     * multiple connected devices, the first Node ID is returned.
     */
   /* private void retrieveDeviceNode() {
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
                Log.w(nodeId, "Node successfully retrieved");

                client.disconnect();
            }
        }).start();
    } */

    private void sendMessage(String node) {
        Log.w("I", "I");
        Log.w("Node Id#:", node);
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


    private void initApi() {
        Log.w("H", "H");
        client = getGoogleApiClient(this);
    }

    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(this)
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
                sendMessage(nodeId);
                client.disconnect();
            }
        }).start();
    }

    /**
     * Sends a message to the connected mobile device, telling it to show a Toast.
     */
}


