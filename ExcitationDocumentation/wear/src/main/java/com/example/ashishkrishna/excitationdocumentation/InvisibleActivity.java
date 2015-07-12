package com.example.ashishkrishna.excitationdocumentation;

/**
 * Created by ashishkrishna on 7/9/15.
 */
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import android.content.Intent;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.MessageApi;
import android.util.Log;
import com.google.android.gms.common.api.ResultCallback;
import java.util.List;
import java.util.concurrent.TimeUnit;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;
public class InvisibleActivity extends Activity {
    private static final long CONNECTION_TIME_OUT_MS = 1000;
    private static final String MESSAGE = "Hello from Wear!";
    public static final String START_ACTIVITY_PATH = "/start/ExcitationDocumentationActivity";
    private GoogleApiClient client;
    private String nodeId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(001);
        stopService(new Intent(this, SensorService.class));


        Log.w("Hits the Invisible", "Hitting this section");

                initApi();



    }


   private void initApi() {
        client = getGoogleApiClient(this);
        retrieveDeviceNode();
    }






    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
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
                Log.w("A", "A");
                Log.w("B", nodeId);
                sendMessage(nodeId);
                client.disconnect();

            }

        }).start();


    }




    private void sendMessage(String node) {
            Wearable.MessageApi.sendMessage(client, node, START_ACTIVITY_PATH, new byte[0]).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                @Override
                public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                    if (!sendMessageResult.getStatus().isSuccess()) {
                        Log.e("GoogleApi", "Failed to send message with status code: "
                                + sendMessageResult.getStatus().getStatusCode());
                    }
                    Log.w("E", "I");
                }
            });



    }

    }




