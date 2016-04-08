package com.example.ashishkrishna.gps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
public class ADA_Activity extends Activity {

    private Handler mHandler;
    private ImageButton ADAYesButton;
    private ImageButton ADANoButton;
    public GoogleApiClient client;
    public int CONNECTION_TIME_OUT_MS = 1000;
    private String nodeId;
    private String START_ACTIVITY_PATH = "/newentry";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ada);

        mHandler = new Handler();

        initApi();

        ADAYesButton = (ImageButton) findViewById(R.id.ADA_yes);
        initSelectionButton(ADAYesButton);

        ADANoButton = (ImageButton) findViewById(R.id.ADA_no);
        initSelectionButton(ADANoButton);

    }

    //Set up listener for selection buttons
    private void initSelectionButton(ImageButton IButton){

        IButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.ADA_yes:
                        ADAYesButton.setImageResource(R.drawable.check_circle_clicked);
                        mHandler.postDelayed(new Runnable() {
                            public void run() {
                                ADAYesButton.setImageResource(R.drawable.check_circle);
                            }
                        }, 2000);
                        break;

                    case R.id.ADA_no:
                        ADANoButton.setImageResource(R.drawable.cross_circle_clicked);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ADANoButton.setImageResource(R.drawable.cross_circle);
                            }
                        }, 2000);
                        break;
                }

                if (v.getId() == R.id.ADA_no) {
                    START_ACTIVITY_PATH = "false";
                }
                else {
                    START_ACTIVITY_PATH = "true";
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                        sendMessage(DisconnectListenerService.bestNode);
                        client.disconnect();
                    }
                }).start();
                openGenderActivity();
            }
        });
    }


    //Open Gender Activity
    private void openGenderActivity(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent openGenderIntent = new Intent(ADA_Activity.this, GenderActivity.class);
                ADA_Activity.this.startActivity(openGenderIntent);
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
