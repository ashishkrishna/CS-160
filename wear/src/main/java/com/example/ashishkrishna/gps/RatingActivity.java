package com.example.ashishkrishna.gps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by qinbian on 7/29/15.
 */
public class RatingActivity extends Activity {
    private RatingBar ratingBar;
    private TextView textRatingValue;
    private float ratingResult;
    public GoogleApiClient client;
    public int CONNECTION_TIME_OUT_MS = 1000;
    private String nodeId;
    private String START_ACTIVITY_PATH = "/newentry";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

//        RatingBar rbar = (RatingBar) findViewById(R.id.ratingBar5);
//        LayerDrawable stars = (LayerDrawable) rbar.getProgressDrawable();
//        stars.getDrawable(2).setColorFilter(Color.parseColor("#F50057"), PorterDuff.Mode.SRC_ATOP);

        initApi();
        addListeneronRatingBar();


    }


    //Add com.example.ashishkrishna.gps.Listener when user rates
    private void addListeneronRatingBar() {
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingBar.setRating(rating);
                ratingResult = ratingBar.getRating();
                START_ACTIVITY_PATH = Float.toString(ratingResult);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                        sendMessage(DisconnectListenerService.bestNode);
                        client.disconnect();
                    }
                }).start();
                openADA();
            }

        });
    }

    //Switch to ADA_Activity
    private void openADA(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent openADAIntent = new Intent(RatingActivity.this, ADA_Activity.class);
                startActivity(openADAIntent);
            }
        }).start();

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

   /* private void retrieveDeviceNode() {
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
        }).start(); */
    //}
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
}
