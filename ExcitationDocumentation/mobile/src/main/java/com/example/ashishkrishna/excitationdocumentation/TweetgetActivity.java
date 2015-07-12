package com.example.ashishkrishna.excitationdocumentation;

/**
 * Created by ashishkrishna on 7/9/15.
 *
 */
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import android.app.Notification;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import com.twitter.sdk.android.core.TwitterCore;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;


public class TweetgetActivity extends MainActivity {
    public static Bitmap bmpsending;
    private GoogleApiClient client;
    public static String url1;
    public String nodeId;
    protected int CONNECTION_TIME_OUT_MS;
    String EXIT_WEAR = "/end/endmyapplication";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitterget);
        initApi();
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        Log.w("Starting Tweetget", "Starting this Activity");
        SearchService st1 = twitterApiClient.getSearchService();
        Intent composer = new Intent(new TweetComposer.Builder(this)
                .text("#cs160excited.")
                .image(Uri.parse(MainActivity.mCurrPhotoPath)).createIntent());
        startActivityForResult(composer, 100);






        st1.tweets("#cs160excited", null, null, null, "recent", 100, null, null, null, true, new Callback<Search>() {

            @Override
            public void success(Result<Search> result) {
                final List<Tweet> tweets = result.data.tweets;
                for (Tweet twt1 : tweets) {
                    if (twt1.entities.media == null) {
                        continue;
                    } else if (twt1.entities.media.isEmpty()) {
                        continue;
                    } else {
                        TweetgetActivity.url1 = twt1.entities.media.get(0).mediaUrl;
                        EXIT_WEAR = TweetgetActivity.url1;
                        if (nodeId == null) {
                            Log.w("not null", "not empty");
                        }
                        //sendMessage(nodeId);
                        Log.w("urlobtain:", TweetgetActivity.url1);
                        break;
                    }
                }
                try {

                    try {
                        Thread.currentThread().wait(4000);
                    } catch (Exception r) {

                    }

                    TweetgetActivity.bmpsending = new DownloadImageTask((ImageView) findViewById(R.id.imageView12)).execute(TweetgetActivity.url1).get(1000, TimeUnit.MILLISECONDS);
                } catch (Exception f) {

                }

                try {
                    Log.w("Joined", "Back as usual");
                } catch (Exception m) {

                }

                if (TweetgetActivity.bmpsending != null) {
                    Notification notificationBuilder =


                            new Notification.Builder(TweetgetActivity.this)
                                    .setSmallIcon(android.R.drawable.ic_menu_camera)
                                    .setContentTitle("Someone Else!")
                                    .setContentText("Someone else was excited about this!").extend(new Notification.WearableExtender().setBackground(TweetgetActivity.bmpsending)).build();
                    NotificationManagerCompat notificationManager1 =
                            NotificationManagerCompat.from(TweetgetActivity.this);


                    int notifId = 001;
                    notificationManager1.notify(notifId, notificationBuilder);
                    Log.w("urlwritten:", "url1");
                } else {
                    Log.w("Problem", "Connection Troubles");
                }


            }


            @Override
            public void failure(TwitterException e) {
                Log.w("This failed", "failure");

            }
        });

        Button btncl = (Button) findViewById(R.id.button3);
        btncl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                sendMessage(nodeId);
                try {
                    Thread.currentThread().wait(2000);
                } catch (Exception y) {

                }
                // System.runFinalizersOnExit(true);
                // System.exit(0);
                //android.os.Process.killProcess(android.os.Process.myPid());
            }
        });

    }
    private void initApi() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                Log.w("START", "starting");
                retrieveDeviceNode();
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

        retrieveDeviceNode();
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

                client.disconnect();

            }

        }).start();


    }




    private void sendMessage(String node) {
        Wearable.MessageApi.sendMessage(client, node, EXIT_WEAR, new byte[0]).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // To Handle Camera Result


        }



        private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
           ImageView bmImage;

            public DownloadImageTask(ImageView bmImage) {
                this.bmImage = bmImage;
            }

            protected Bitmap doInBackground(String... urls) {
                String urldisplay = urls[0];
                Log.w("url:", urldisplay);
                TweetgetActivity.bmpsending = null;
                try {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    TweetgetActivity.bmpsending = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }

                return TweetgetActivity.bmpsending;
            }

            protected void onPostExecute(Bitmap result) {
                bmImage.setImageBitmap(result);
            }
        }


    }


