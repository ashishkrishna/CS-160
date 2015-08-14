package com.example.ashishkrishna.gps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
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
public class GenderActivity extends Activity {
    public GoogleApiClient client;
    public int CONNECTION_TIME_OUT_MS = 1000;
    private String nodeId;
    private String START_ACTIVITY_PATH = "/newentry";

    private ImageButton FemaleButton;
    private ImageButton MaleButton;
    private ImageButton GenderNeutralButton;
    private Handler mHandler;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender);

        mHandler = new Handler();
        initApi();

        FemaleButton = (ImageButton) findViewById(R.id.female_icon);
        initSelectionButton(FemaleButton);

        MaleButton = (ImageButton) findViewById(R.id.male_icon);
        initSelectionButton(MaleButton);

        GenderNeutralButton = (ImageButton) findViewById(R.id.gender_neutral_icon);
        initSelectionButton(GenderNeutralButton);
    }

    private void initSelectionButton(ImageButton IButton){
        IButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.female_icon:
                        FemaleButton.setImageResource(R.drawable.female_icon_clicked);
                        mHandler.postDelayed(new Runnable() {
                            public void run() {
                                FemaleButton.setImageResource(R.drawable.female_icon);
                            }
                        }, 2000);
                        break;
                    case R.id.male_icon:
                        MaleButton.setImageResource(R.drawable.male_icon_clicked);
                        mHandler.postDelayed(new Runnable() {
                            public void run() {
                                MaleButton.setImageResource(R.drawable.male_icon);
                            }
                        }, 2000);

                        break;
                    case R.id.gender_neutral_icon:
                        GenderNeutralButton.setImageResource(R.drawable.gender_neutral_icon_clicked);
                        mHandler.postDelayed(new Runnable() {
                            public void run() {
                                GenderNeutralButton.setImageResource(R.drawable.gender_neutral_icon);
                            }
                        }, 2000);
                        break;
                }
                if(v.getId() == R.id.female_icon) {
                    START_ACTIVITY_PATH = "/female";
                }

                if(v.getId() == R.id.male_icon) {
                    START_ACTIVITY_PATH = "/male";
                }
                if(v.getId() == R.id.gender_neutral_icon) {
                    START_ACTIVITY_PATH = "/genderneutral";
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                        sendMessage(DisconnectListenerService.bestNode);
                        client.disconnect();
                    }
                }).start();
                openCommentActivity();
            }
        });
    }


    private void openCommentActivity(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent openCommentIntent = new Intent(GenderActivity.this, CommentActivity.class);
                GenderActivity.this.startActivity(openCommentIntent);
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
