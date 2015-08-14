package com.example.ashishkrishna.gps;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ActiveActivity extends Activity implements WearableListView.ClickListener {

    public static WearableListView mListView;
    public static ArrayList<String> listItems;
    public static ArrayList<String> listcoords;
    public static int position;
    public GoogleApiClient client;
    public String START_ACTIVITY_PATH;
    public int CONNECTION_TIME_OUT_MS = 1000;
    String b = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activere);
        initApi();
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
       /* stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mListView = (WearableListView) stub.findViewById(R.id.sample_list_view);

            }
        }); */
        mListView = (WearableListView) findViewById((R.id.sample_list_view));
        mListView.setAdapter(new MyAdapter(ActiveActivity.this));
        mListView.setClickListener(ActiveActivity.this);
        int i = 0;
        listItems = new ArrayList<String>();
        listcoords = new ArrayList<String>();
        while (i < Listener.itemizer.length) {

            String pc1 = Listener.itemizer[i];
            String[] d = pc1.split("Lat:");

            String d0 = d[0];
            String d1 = d[1];
            Log.w("Part1", d0);
            Log.w("Part2", d1);
            listItems.add(d0);
            listcoords.add(d1);
            i++;
        }



    }



    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        Log.w("HelloListView", "You clicked Item: " + id + " at position:" + position);

    }





    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        Log.v("Communication met", "Item clicked here");
        int i = viewHolder.getAdapterPosition();
        String a = listcoords.get(i);
        String[] b = a.split("Lon:");
        String l1 = b[0];
        String l2 = b[1];
        l1.replaceAll("\\s+","");
        l2.replaceAll("\\s+","");
        MapsActivity.lat = l1;
        MapsActivity.lon = l2;
        Intent gamma = new Intent(this, MapsActivity.class);
        startActivity(gamma);


        // startActivity(b1);
    }


    @Override
    public void onTopEmptyRegionClick() {

    }

    private class MyAdapter extends WearableListView.Adapter {
        private final LayoutInflater mInflater;

        private MyAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new WearableListView.ViewHolder(
                    mInflater.inflate(R.layout.row_simple_layout, null));
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
            String alpha = listItems.get(position).toString();
            String[] d = alpha.split("Rating:");
            TextView view = (TextView) holder.itemView.findViewById(R.id.textView);
           view.setText(d[0]);
            holder.itemView.setTag(position);
            ImageButton vw1 = (ImageButton) holder.itemView.findViewById(R.id.imgvw);
            d[1] = d[1].replaceAll("\\s+","");
            Log.w("This", d[1]);
            if(d[1].equals("1")) {
                vw1.setImageResource(R.drawable.active_soda_rating);
            }
            if(d[1].equals("2")) {
                vw1.setImageResource(R.drawable.two_stars);
            }
            if(d[1].equals("3")) {
                vw1.setImageResource(R.drawable.three_stars);
            }
            if(d[1].equals("4")) {
                vw1.setImageResource(R.drawable.four_stars);
            }
            if(d[1].equals("5")) {
                vw1.setImageResource(R.drawable.active_hearst_rating);
            }
            holder.itemView.setTag(position);

        }

        @Override
        public int getItemCount() {
            return Listener.itemizer.length;
        }
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


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
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