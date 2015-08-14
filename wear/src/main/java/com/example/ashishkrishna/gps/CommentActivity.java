package com.example.ashishkrishna.gps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.os.Handler;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by qinbian on 7/30/15.
 */
public class CommentActivity extends Activity {
    public GoogleApiClient client;
    public int CONNECTION_TIME_OUT_MS = 1000;
    private String nodeId;
    private String START_ACTIVITY_PATH = "/newentry";


    private ImageButton SkipButton;
    private ImageButton VoiceButton;
    private ImageButton PhoneButton;
    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        mHandler = new Handler();
        initApi();

        SkipButton = (ImageButton) findViewById(R.id.skip_icon);
        initSkipButton(SkipButton);

        VoiceButton = (ImageButton) findViewById(R.id.mic_icon);
        initMicButton(VoiceButton);

        PhoneButton = (ImageButton) findViewById(R.id.phone_icon);
        initPhoneButton(PhoneButton);
    }

    //Add listener to skip button
    private void initSkipButton(ImageButton IButton){
        IButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkipButton.setImageResource(R.drawable.cross_circle_clicked);

                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        SkipButton.setImageResource(R.drawable.cross_circle);
                    }
                }, 2000);
                if (v.getId() == R.id.skip_icon) {
                    START_ACTIVITY_PATH = "/skip";
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                        sendMessage(DisconnectListenerService.bestNode);
                        client.disconnect();
                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        openNewActivity(CommentActivity.this, LogResultActivity.class);
                    }
                }).start();

            }
        });
    }

    //Add listener to mic button
    private void initMicButton(ImageButton IButton){
        IButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoiceButton.setImageResource(R.drawable.mic_clicked);
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        VoiceButton.setImageResource(R.drawable.mic);
                    }
                }, 2000);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        openNewActivity(CommentActivity.this, MicListeningActivity.class);
                    }
                }).start();


            }
        });
    }

    private void initPhoneButton(ImageButton IButton){
        IButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneButton.setImageResource(R.drawable.phone_share_icon_clicked);
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        PhoneButton.setImageResource(R.drawable.phone_share_icon);
                    }
                }, 2000);


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent openPhoneIntent = new Intent(CommentActivity.this, ConfirmationActivity.class);
                        openPhoneIntent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
                        openPhoneIntent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                                getString(R.string.phone_opened));
                        startActivity(openPhoneIntent);
                    }
                }).start();

            }
        });
    }

    //Open New Activity
    private void openNewActivity(Context from, Class<?> to){

        Intent openNewIntent = new Intent(from, to);
        from.startActivity(openNewIntent);
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
