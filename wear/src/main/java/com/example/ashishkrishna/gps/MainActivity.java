package com.example.ashishkrishna.gps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    private TextView modeDisplay;
    private ImageButton activeButton;
    private ImageButton passiveButton;
    private ImageButton logButton;
    private Handler mHandler;
    private String activeString;
    private String passiveString;
    private String logString;

    private TextView mTextView;
    public GoogleApiClient client;
    public int CONNECTION_TIME_OUT_MS = 1000;
    private String nodeId;
    private String START_ACTIVITY_PATH = "/newentry";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_main);

        activeString = "Active";
        passiveString = "Passive";
        logString = "Log";

        initApi();

        mHandler = new Handler();
        modeDisplay = (TextView) findViewById(R.id.mode_display);

        addListenerOnLog();
        addListenerOnActive();
        addListenerOnPassive();
    }

    private void addListenerOnLog() {
        logButton = (ImageButton) findViewById(R.id.log_mode_icon);
        logButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Ashish's part
                logButton.setImageResource(R.drawable.log_mode_icon_clicked);
                modeDisplay.setText(logString);
                START_ACTIVITY_PATH = "/newentry";

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                        sendMessage(DisconnectListenerService.bestNode);
                        client.disconnect();
                    }
                }).start();


                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        logButton.setImageResource(R.drawable.log_mode_icon);
                        modeDisplay.setText("");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Intent openRatingIntent = new Intent(MainActivity.this, RatingActivity.class);
                                MainActivity.this.startActivity(openRatingIntent);
                            }
                        }).start();
                    }
                }, 1500);
            }
        });
    }



    private void addListenerOnActive() {
        activeButton = (ImageButton) findViewById(R.id.active_mode_icon);
        activeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                START_ACTIVITY_PATH = "/onactive";

                activeButton.setImageResource(R.drawable.active_mode_icon_clicked);
                modeDisplay.setText(activeString);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activeButton.setImageResource(R.drawable.active_mode_icon);
                        modeDisplay.setText("");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                                sendMessage(DisconnectListenerService.bestNode);
                                client.disconnect();

                                //for demo purpose
                                // Intent activeIntent = new Intent(MainActivity.this, ActiveResultActivity.class);
                                //  MainActivity.this.startActivity(activeIntent);
                            }
                        }).start();
                    }

                }, 1500);
            }
        });
    }



                //new Thread(new Runnable() {
                  //  @Override
                    //public void run() {
                      //  Intent openActiveIntent = new Intent(MainActivity.this, ActiveActivity.class);
                        //startActivity(openActiveIntent);
                        //MainActivity.this.retrieveDeviceNode();'
                    //}
                //}).start();
                //retrieveDeviceNode();
                // activeButton.setImageResource(R.drawable.active_mode_icon_clicked);
                // if (modeDisplay.getText().toString().equals(activeString)) {
                /*  mHandler.postDelayed(new Runnable() {
                        public void run() {
                            activeButton.setImageResource(R.drawable.active_mode_icon);
                        }
                    }, 2000); */
                   /* new Thread(new Runnable() {
                        @Override
                    public void run() {
                            Intent openActiveIntent = new Intent(MainActivity.this, ActiveActivity.class);
                            startActivity(openActiveIntent);
                      //  }

                    }).start(); */


                //  } else {
                //  modeDisplay.setText(activeString);
                //  mHandler.postDelayed(new Runnable() {
                //   public void run() {
                //     modeDisplay.setText("");
                // }


    private void addListenerOnPassive() {
        passiveButton = (ImageButton) findViewById(R.id.passive_mode_icon);
        passiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences("prefs", 0);
                boolean firstRun = settings.getBoolean("passiveRunning", false);
                if (!firstRun) {

                    passiveButton.setImageResource(R.drawable.passive_mode_icon_clicked);
                    modeDisplay.setText(passiveString);


                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            passiveButton.setImageResource(R.drawable.passive_mode_icon);
                            modeDisplay.setText("");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent g1 = new Intent(MainActivity.this, PassiveUnitActivity.class);
                                    startActivity(g1);
                                }
                            }).start();
                        }
                    }, 1500);


                } else {
                    passiveButton.setImageResource(R.drawable.passive_mode_icon_clicked);
                    modeDisplay.getText().toString().equals(passiveString);
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            passiveButton.setImageResource(R.drawable.passive_mode_icon);
                            modeDisplay.setText("");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent g1 = new Intent(MainActivity.this, PassiveStop.class);
                                    startActivity(g1);
                                }
                            }).start();
                        }
                    }, 1500);

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







    /*private void retrieveDeviceNode() {
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
