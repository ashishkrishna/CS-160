package com.example.ashishkrishna.gps;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Rymico on 8/4/2015.
 */
public class BathroomActivity extends ActionBarActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    CustomViewPager mViewPager;
    private List<Map<String, String>> data = new ArrayList<Map<String, String>>();
    private SimpleAdapter adapter;

    private Context context;

    private ListView mListView;
    private Button reportButton, reviewButton;
    private String address, lat, lon, rating, ada, gender;
    public static View view;

    public DatabaseThreeService FirstServicer;
    public ServiceConnection mConnection;

    public boolean mCreate = false;
    public static ErrorListActivity error1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bathroom);
        //view = getCurrentFocus();
        /*Intent alpha = new Intent(this, DatabaseThreeService.class);
        startService(alpha);
        bindService(alpha, mConnection, Context.BIND_AUTO_CREATE);
        Intent intent = new Intent(BathroomActivity.this, DatabaseThreeService.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);*/
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.rgb(3, 155, 229));
        }
        //actionBar.setLogo(R.drawable.action_bar_logo_32px);
        //actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        context = getApplicationContext();
        adapter = new CustomAdapter(context,
                data,                             //a list of hashmaps
                R.layout.comments,             //the layout to use for each item
                new String[]{"Comment"},     //the array of keys
                new int[]{R.id.comment});    //array of view ids that should display the values (same order)
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            lat = extras.getString("Lat");
            lon = extras.getString("Lon");
            address = extras.getString("Address");
            rating = extras.getString("Rating");
            ada = extras.getString("Ada");
            gender = extras.getString("Gender");
        }

        Log.w("Latitude is", lat);
        Log.w("Longitude is", lon);

        //Log.w("Rating:", rating);
        //Log.w("Ada:", ada);
        //Log.w("Gender", gender);
        getSupportActionBar().setTitle(address);
        setAddress(address);
        setRating(rating);
        if (ada.equalsIgnoreCase("true")) {
            ((ImageView) findViewById(R.id.ada)).setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.bathroom_page_ada_32px, null));
        }

        if (gender.equalsIgnoreCase("genderneutral")) {
            ((ImageView) findViewById(R.id.gender)).setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.gn, null));
        } else if (gender.equalsIgnoreCase("male")) {
            ((ImageView) findViewById(R.id.gender)).setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.m, null));
        } else if (gender.equalsIgnoreCase("female")) {
            ((ImageView) findViewById(R.id.gender)).setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.fem, null));
        }
        mListView = (ListView) findViewById(R.id.comments);
        mListView.setAdapter(adapter);


        reportButton = (Button) findViewById(R.id.report);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent report = new Intent(context, MainActivity.class);
                report.putExtra("Tab", 2);
                report.putExtra("Lat", lat);
                report.putExtra("Lon", lon);
                report.putExtra("Address", address);
                report.putExtra("Rating", rating);
                report.putExtra("Ada", ada);
                report.putExtra("Gender", gender);
                startActivity(report);
            }
        });

        reviewButton = (Button) findViewById(R.id.review);
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent review = new Intent(context, CommentActivity.class);
                review.putExtra("Lat", lat);
                review.putExtra("Lon", lon);
                review.putExtra("Address", address);
                review.putExtra("Rating", rating);
                review.putExtra("Ada", ada);
                review.putExtra("Gender", gender);
                startActivity(review);
            }
        });

        buttonEffect(reportButton);
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                DatabaseThreeService.LocalBinder2 binder = (DatabaseThreeService.LocalBinder2) service;
                FirstServicer = binder.getService();
                mCreate = true;
                //binded = true;

                if (FirstServicer==null) {
                    Log.w("Nulled", "nulled");
                }
                else {
                    executing();
                }
                Log.w("mcreate", "trs");

            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                unbindService(mConnection);

            }
        };




        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        // Set up the ViewPager with the sections adapter.
        //mViewPager = (CustomViewPager) findViewById(R.id.pager);
        //mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        /*mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }*/






    }

    //button animations
    public static void buttonEffect(View button) {
        button.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(0xffBDBDBD, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService

        Intent intent = new Intent(BathroomActivity.this, DatabaseThreeService.class);
        //startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);



    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service

        unbindService(mConnection);

    }

    public void executing() {
        LoadTaskComments attempt11 = new LoadTaskComments();
        //FirstServicer.createnewLoadBathroom();
        /*
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                FirstServicer.attempt10.execute(lat, lon);

            }
        });
        t1.start();

         while(FirstServicer.rating == null && FirstServicer.ada == null) {
            try {
                Thread.currentThread().wait();
            }
            catch(InterruptedException h) {

            }
        } */


        attempt11.execute(lat, lon);


       /* while (FirstServicer.indicate != 1) {
            try {
                Thread.currentThread().wait();
            }
            catch(InterruptedException w) {
                w.printStackTrace();
            } */
        // }

        /*adapter = new CustomAdapter(context,
                data,                             //a list of hashmaps
                R.layout.comments,             //the layout to use for each item
                new String[]{"Comment"},     //the array of keys
                new int[]{R.id.comment});    //array of view ids that should display the values (same order)
        mListView = (ListView) findViewById(R.id.comments);
        mListView.setAdapter(adapter);*/
        //adapter.notifyDataSetChanged();
        //rating = FirstServicer.rating;
        //setRating(rating);
        //FirstServicer.longandlat(Double.parseDouble(lat), Double.parseDouble(lon));
        //address = FirstServicer.addressln;
        //((TextView) findViewById(R.id.address)).setText(FirstServicer.addressln);
        //Log.w("Address found", "  " + FirstServicer.addressln);

    }



    public void openSettingActivity(MenuItem item){
        Intent openSettingIntent = new Intent(BathroomActivity.this, SettingActivity.class);
        BathroomActivity.this.startActivity(openSettingIntent);

    }


    public class LoadTaskComments extends AsyncTask<String, Void, JSONArray> {

        protected JSONArray doInBackground(String... params) {
            Log.w("Params", params[0]+"   "+params[1]);
            String url = "http://teammynstur.pythonanywhere.com/comments/lat="+params[0]+"&lon="+params[1];
            HttpResponse response;
            HttpClient httpclient = new DefaultHttpClient();
            String responseString = "";
            Log.w("gets here", "gets here");

            try {
                response = httpclient.execute(new HttpGet(url));
                Log.w("in try", "in try");
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();
                    Log.w("Response string", responseString);

                } else {
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(response.getStatusLine().getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                //TODO Handle problems..
            } catch (IOException e) {
                //TODO Handle problems..
            }
            try {
                Log.w("2 gets", "2 gets");
                JSONArray messages = new JSONArray(responseString);
                return messages;
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }


        protected void onPostExecute(JSONArray itemsList) {
            Log.w("3 gets", "3 gets");
            data.clear();
            if (itemsList != null) {
                for (int i = 0; i < itemsList.length(); i++) {
                    try {
                        JSONArray current = itemsList.getJSONArray(i);
                        Log.w("4 gets", current.getString(2));
                        if (!(current.getString(2).equals(null))) {
                            Map<String, String> listItem = new HashMap<String, String>(2);
                            listItem.put("Comment", current.getString(2));
                            data.add(listItem);
                            //Log.w("Put", "put "+Integer.toString(i));
                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                //indicate = 1;

                Log.w("Got here", "Got here");
            }
            adapter.notifyDataSetChanged();
        }

    }

    public void setRating(String rating) {
        float ratingVal = Float.parseFloat(rating);
        View ratingBar = findViewById(R.id.ratingBar);
        ((RatingBar) ratingBar).setRating(ratingVal);
    }

    public void setAddress(String address) {
        TextView addressText = (TextView) findViewById(R.id.address);
        addressText.setText(address);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bathroom, menu);
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


}