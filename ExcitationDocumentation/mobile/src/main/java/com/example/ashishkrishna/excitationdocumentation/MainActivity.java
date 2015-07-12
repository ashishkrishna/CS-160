package com.example.ashishkrishna.excitationdocumentation;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.net.Uri;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import android.content.Intent;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "0EOQmHD2btsjEQNHEkhcaG3eY";
    private static final String TWITTER_SECRET = "GKCkfbHIo8s8jGMgyrV6xTDeSTVecQhIJ1kZBz2eN4RuKClYZp";

    public static String pathname;
    public static Uri filename;
    public static String mCurrPhotoPath;
    private static final long CONNECTION_TIME_OUT_MS = 100;
    private static final String MESSAGE = "Hello Wear!";
    public Intent twtr;
    private GoogleApiClient client;
    public static final String BACK_ACTIVITY_PATH = "/start/ListenerService";
    private String nodeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.main_activity);
        Intent listening = new Intent(this, ListenerService.class);
        startService(listening);
        setupWidgets();
        initApi();



    }

    /**
     * Initializes the GoogleApiClient and gets the Node ID of the connected device.
     */
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
        /* client = getGoogleApiClient(this); */
        retrieveDeviceNode();
    }

    /**
     * Sets up the button for handling click events.
     */
    private void setupWidgets() {
        findViewById(R.id.bton_toast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                        sendMessage(nodeId);
                        client.disconnect();
                    }
                }).start();
                sendMessage(nodeId);
                twtr = new Intent(MainActivity.this, TwitterActivity.class);
                Thread twtrd = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(twtr);

                    }
                });
                twtrd.start();


            }
        });
    }

    private void sendMessage(String node) {
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
    /**
     * Returns a GoogleApiClient that can access the Wear API.
     * @param context
     * @return A GoogleApiClient that can make calls to the Wear API
     */
   /* private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
    } */

    /**
     * Connects to the GoogleApiClient and retrieves the connected device's Node ID. If there are
     * multiple connected devices, the first Node ID is returned.
     */
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

    /**
     * Sends a message to the connected mobile device, telling it to show a Toast.
     */

    }



