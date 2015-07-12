package com.example.ashishkrishna.excitationdocumentation;

/**
 * Created by ashishkrishna on 7/5/15.
 */
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import android.content.Intent;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import android.util.Log;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ListenerService extends WearableListenerService {

    String nodeId;
    public static final String START_ACTIVITY_PATH = "/start/ExcitationDocumentationActivity";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent) {
            String action = intent.getAction();

        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        nodeId = messageEvent.getSourceNodeId();
        showToast(messageEvent.getPath());
        if(messageEvent.getPath().equals(START_ACTIVITY_PATH)){
            Intent intent = new Intent(this , ExcitationDocumentationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
    }



    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


}