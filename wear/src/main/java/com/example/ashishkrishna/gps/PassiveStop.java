package com.example.ashishkrishna.gps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.TimeUnit;


public class PassiveStop extends Activity {

    ImageButton confirmButton;
    GoogleApiClient client;
    public String START_ACTIVITY_PATH = "/stoppassive";
    public int CONNECTION_TIME_OUT_MS = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initApi();
        setContentView(R.layout.activity_passive_stop);
        confirmButton = (ImageButton) findViewById(R.id.Passive_Stopper);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences p = getSharedPreferences("prefs", 0);
                boolean firstRun = p.getBoolean("passiveRunning", false);
                if (firstRun) {
                    SharedPreferences.Editor editing = p.edit();
                    editing.putBoolean("passiveRunning", false);
                    editing.commit();
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
                            Intent g1 = new Intent(PassiveStop.this, MainActivity.class);
                            startActivity(g1);
                        }
                    }).start();
                    finish();

                }

                }

                    });




                //finish();



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_passive_stop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initApi() {
        Log.w("H", "H");
        client = getGoogleApiClient(this);
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

    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
    }
}
