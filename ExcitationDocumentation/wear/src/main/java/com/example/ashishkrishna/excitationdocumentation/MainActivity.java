package com.example.ashishkrishna.excitationdocumentation;





import android.app.Activity;
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

public class MainActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, Listener.class));
        startService(new Intent(this, SensorService.class));
        this.finish();



    }
}

