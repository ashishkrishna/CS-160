package com.example.ashishkrishna.gps;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    CustomViewPager mViewPager;
    public static DatabaseService FirstServicer;
    public ServiceConnection mConnection;
    public static int x = 1;
    private static String address, lat, lon, rating, ada, gender;

    public static ErrorListActivity error1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.rgb(3, 155, 229));
        }
        //getWindow().setStatusBarColor(Color.rgb(3, 155, 229));

        //actionBar.setLogo(R.drawable.action_bar_logo_32px);
        //actionBar.setDisplayUseLogoEnabled(true);
        //actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.

        mConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                DatabaseService.LocalBinder binder = (DatabaseService.LocalBinder) service;
                FirstServicer = binder.getService();
                //binded = true;

            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                unbindService(mConnection);

            }
        };

        Intent alpha = new Intent(this, DatabaseService.class);
        ComponentName alphabravo = startService(alpha);
        bindService(new Intent(this, DatabaseService.class), mConnection, Context.BIND_AUTO_CREATE);
        //startService(alpha);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (CustomViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
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
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            int tabSelect = extras.getInt("Tab");
            lat = extras.getString("Lat");
            lon = extras.getString("Lon");
            address = extras.getString("Address");
            rating = extras.getString("Rating");
            ada = extras.getString("Ada");
            gender = extras.getString("Gender");
            mViewPager.setCurrentItem(tabSelect);
        }



    }

    public void openSettingActivity(MenuItem item){
        Intent openSettingIntent = new Intent(MainActivity.this, SettingActivity.class);
        MainActivity.this.startActivity(openSettingIntent);

    }


    @Override
    public void onStart() {
        super.onStart();

        Intent i =  new Intent(this, Listener.class);
        startService(i);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onDestroy() {
        unbindService(mConnection);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent beta = new Intent(MainActivity.this, MainActivity.class);
                startActivity(beta);
            }
        }).start();
        this.finish();
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new SuggestedFragment();
                    break;
                case 1:
                    fragment = new MapsFragment();
                    break;
                case 2:
                    fragment = new ReportFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }


    public static class SuggestedFragment extends Fragment {

        private List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        private SimpleAdapter adapter;
        //private GoogleApiClient client;
        //private String nodeId;
        //private String BACK_ACTIVITY_PATH = "/start/ListenerService";
        //private int CONNECTION_TIME_OUT_MS = 1000;
        private LoadTask attempt10, attempt11, attempt12;
        private Context context;
        private String currlat, currlon;


        /**
         * The fragment's ListView/GridView.
         */
        private ListView mListView;

        /**
         * The Adapter which will be used to populate the ListView/GridView with
         * Views.
         */

        /**
         * Mandatory empty constructor for the fragment manager to instantiate the
         * fragment (e.g. upon screen orientation changes).
         */
        public SuggestedFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //initApi();
            //retrieveDeviceNode();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_favorites_list, container, false);
            context = getActivity().getApplicationContext();


            // Set the adapter
            adapter = new CustomAdapter(context,
                    data,                             //a list of hashmaps
                    R.layout.favorites,             //the layout to use for each item
                    new String[]{"Lat", "Lon", "Address", "Rating", "Ada", "Gender"},     //the array of keys
                    new int[]{R.id.lat, R.id.lon, R.id.address, R.id.ratingBar, R.id.ada, R.id.gender});    //array of view ids that should display the values (same order)

            CustomAdapter.ViewBinder viewBinder = new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data, String textRepresentation) {
                    if (view.getId() == R.id.address) {
                        String[] latandlon = data.toString().split(",");
                        FirstServicer.longandlat(Double.parseDouble(latandlon[0]), Double.parseDouble(latandlon[1]));
                        ((TextView) view).setText(FirstServicer.addressln);
                        return true;
                    }
                    if (view.getId() == R.id.ratingBar) {
                        float rating = Float.parseFloat(data.toString());
                        ((RatingBar) view).setRating(rating);
                        return true;
                    }
                    if (view.getId() == R.id.ada) {
                        if (data.toString().equalsIgnoreCase("true")) {
                            ((ImageView) view).setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.bathroom_page_ada_32px, null));
                            return true;
                        } else {
                            return true;
                        }
                    }
                    if (view.getId() == R.id.gender) {
                        if (data.toString().equalsIgnoreCase("genderneutral")) {
                            ((ImageView) view).setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.gn, null));
                            return true;
                        } else if (data.toString().equalsIgnoreCase("male")) {
                            ((ImageView) view).setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.m, null));
                            return true;
                        } else if (data.toString().equalsIgnoreCase("female")) {
                            ((ImageView) view).setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.fem, null));
                            return true;
                        } else {
                            return true;
                        }
                    }
                    return false;
                }
            };
            adapter.setViewBinder(viewBinder);

            mListView = (ListView) view.findViewById(R.id.favorites);
            mListView.setAdapter(adapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(context, BathroomActivity.class);
                    //String lat = ((TextView) view.findViewById(R.id.lat)).getText().toString();
                    //String lon = ((TextView) view.findViewById(R.id.lon)).getText().toString();
                    HashMap selected = (HashMap) adapter.getItem(position);
                    String lat = selected.get("Lat").toString();
                    String lon = selected.get("Lon").toString();
                    String address = selected.get("Address").toString();
                    String rating = selected.get("Rating").toString();
                    String ada = selected.get("Ada").toString();
                    String gender = selected.get("Gender").toString();
                    intent.putExtra("Lat", lat);
                    intent.putExtra("Lon", lon);
                    intent.putExtra("Address", address);
                    intent.putExtra("Rating", rating);
                    intent.putExtra("Ada", ada);
                    intent.putExtra("Gender", gender);
                    startActivity(intent);
                }
            });

            loading();
            //String read[] = reading();
            //attempt1 = new LoadTask();
            //attempt1.execute(read[0],read[1]);

            //FirstServicer.createnewLoadMobile();

            // Set OnItemClickListener so we can be notified on item clicks
            //mListView.setOnItemClickListener(this);

            return view;
        }

        public void loading() {
            attempt10 = new LoadTask();
            attempt11 = new LoadTask();
            attempt12 = new LoadTask();
            //FirstServicer.createjson();
            //attempt10 = new LoadTask();
            String str123[] = reading();
            //Log.w("These are the values", str123[0]);
            //Log.w("These are the values", str123[1]);
            //Log.w("These are the values", str123[2]);
            //Log.w("These are the values", str123[3]);

            if (str123[0] == "male") {
                Log.w("True", "This is here");
                if (str123[3] == "true") {
                    attempt10.execute("male", "true");
                    /*  new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int j = 0; j < FirstServicer.vals.length; j++) {
                                BACK_ACTIVITY_PATH = FirstServicer.vals[j];
                                retrieveDeviceNode();
                            }
                        }
                    }).start(); */
                } else {
                    attempt10.execute("male", "false");

                   /*  new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int j = 0; j < FirstServicer.vals.length; j++) {
                                BACK_ACTIVITY_PATH = FirstServicer.vals[j];
                                retrieveDeviceNode();
                            }
                        }
                    }).start(); */


                }


            }
            if (str123[1] == "female") {
                if (str123[3] == "true") {
                    attempt11.execute("female", "true");
/*  new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int j = 0; j < FirstServicer.vals.length; j++) {
                                BACK_ACTIVITY_PATH = FirstServicer.vals[j];
                                retrieveDeviceNode();
                            }
                        }
                    }).start(); */


                } else {
                    attempt11.execute("female", "false");

                   /*  new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int j = 0; j < FirstServicer.vals.length; j++) {
                                BACK_ACTIVITY_PATH = FirstServicer.vals[j];
                                retrieveDeviceNode();
                            }
                        }
                    }).start(); */

                }
            }

            if (str123[2] == "genderneutral") {
                if (str123[3] == "true") {
                    attempt12.execute("genderneutral", "true");
                    //unbindService(mConnection);
                    //Log.w("PING", "LISTEN");
                    /*  new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int j = 0; j < FirstServicer.vals.length; j++) {
                                BACK_ACTIVITY_PATH = FirstServicer.vals[j];
                                retrieveDeviceNode();
                            }
                        }
                    }).start(); */


                } else {
                    attempt12.execute("genderneutral", "false");
                    //unbindService(mConnection);
                    //Log.w("NUMVALS: ", Integer.toString(FirstServicer.vals.length));
                    //Log.w("Value1:", FirstServicer.vals[0]);


                     /* new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int j = 0; j < FirstServicer.vals.length; j++) {
                                BACK_ACTIVITY_PATH = FirstServicer.vals[j];
                                retrieveDeviceNode();
                            }
                        }
                    }).start(); */


                }
            }
        }
        public String[] reading(){
            String[] toreturn = new String[4];
            toreturn[0] = "None";
            toreturn[1] = "None";
            toreturn[2] = "None";
            toreturn[3] = "false";
            FileInputStream y;
            try {
                y = context.openFileInput("settingsgps.txt");
            }
            catch(FileNotFoundException n) {
                n.printStackTrace();
                return toreturn;
            }
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(y));
            String Read;
            try {
                if (y != null) {
                    while ((Read = reader.readLine()) != null) {
                        buffer.append(Read + "\n");
                    }
                    String s1 = buffer.toString();
                    if (s1.charAt(0) == 'Y') {
                        toreturn[0] = "male";
                    }
                    if (s1.charAt(1) == 'Y') {
                        toreturn[1] = "female";
                    }
                    if (s1.charAt(2) == 'Y') {
                        toreturn[2] = "genderneutral";
                    }
                    if (s1.charAt(3) == 'Y') {
                        toreturn[3] = "true";
                    }


                }
            }
            catch (IOException h) {
                h.printStackTrace();
            }
            return toreturn;
        }

        public void reload(View v) {
            LoadTask task = new LoadTask();
            task.execute();
        }


        private class LoadTask extends AsyncTask<String, Void, JSONArray> {

            protected JSONArray doInBackground(String... params) {
                String url = "http://teammynstur.pythonanywhere.com/mssgs1/setgender="+params[0]+"&setada="+params[1];
                Log.w("URL", url);
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
                for (int i = 0; i < itemsList.length(); i++) {
                    try {
                        JSONArray current = itemsList.getJSONArray(i);
                        Map<String, String> listItem = new HashMap<String, String>(2);
                        listItem.put("Lat", current.getString(0));
                        listItem.put("Lon", current.getString(1));
                        listItem.put("Address", current.getString(0)+","+current.getString(1));
                        listItem.put("Rating", current.getString(2));
                        listItem.put("Ada", current.getString(3));
                        listItem.put("Gender", current.getString(4));
                        data.add(listItem);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
                //Log.w("Got here", "Got here");
            }

        }
    }

    public static class ReportFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.

         private static final String ARG_SECTION_NUMBER = "section_number";


         * Returns a new instance of this fragment for the given section
         * number.

         public static ErrorReportFragment newInstance(int sectionNumber) {
         ErrorReportFragment fragment = new ErrorReportFragment();
         Bundle args = new Bundle();
         args.putInt(ARG_SECTION_NUMBER, sectionNumber);
         fragment.setArguments(args);
         return fragment;
         } */


        public ReportFragment(){
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
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View errorReport = inflater.inflate(R.layout.fragment_error_report, container, false);

            //start modifications
            //set restroom name in top line
            TextView restroomName = (TextView) errorReport.findViewById(R.id.nameOfRestroom);
            String passed_in_name = "Sutardja Dai 3rd Floor";
            restroomName.setText("Reporting " + passed_in_name);

            //onclick to make textfield visible for commenting
            View.OnClickListener enterText = new View.OnClickListener(){
                public void onClick(View v) {
                    EditText textEntry = (EditText) errorReport.findViewById(R.id.editText);
                    textEntry.setVisibility(View.VISIBLE);
                    textEntry.setTextColor(getResources().getColor(R.color.textFieldColor));
                }
            };

            //onclick to go back
            View.OnClickListener goBack = new View.OnClickListener(){
                public void onClick(View v) {
                    //load the previous fragment
                    Intent back = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                    if (address != null) {
                        back = new Intent(getActivity().getApplicationContext(), BathroomActivity.class);
                        back.putExtra("Lat", lat);
                        back.putExtra("Lon", lon);
                        back.putExtra("Address", address);
                        back.putExtra("Rating", rating);
                        back.putExtra("Ada", ada);
                        back.putExtra("Gender", gender);
                    }
                    startActivity(back);
                }
            };

            //onclick to log the report
            View.OnClickListener submitOC;


            //set the text field & button onclicks
            RadioButton rb = (RadioButton) errorReport.findViewById(R.id.otherText);
            rb.setOnClickListener(enterText);
            Button back = (Button) errorReport.findViewById(R.id.back);
            back.setOnClickListener(goBack);
            Button submit = (Button) errorReport.findViewById(R.id.submit);
            submit.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    //get the text entered

                    //if it exists, log this
                    //if it's empty, send 'DNE' message
                    EditText textEntry = (EditText) errorReport.findViewById(R.id.editText);
                    if (textEntry.getText().toString().isEmpty()) {
                        FirstServicer.createnewComment();
                        FirstServicer.commerror = "DNE";
                        FirstServicer.attempt8.execute(Double.toString(FirstServicer.latitudedoub), Double.toString(FirstServicer.longitudedoub), FirstServicer.commerror);
                        //getActivity().getSupportFragmentManager().popBackStack();
                        getActivity().onBackPressed();





                    }

                    else {
                        FirstServicer.createnewComment();
                        FirstServicer.commerror = textEntry.getText().toString();
                        FirstServicer.attempt8.execute(Double.toString(FirstServicer.latitudedoub), Double.toString(FirstServicer.longitudedoub), FirstServicer.commerror);
                        //getActivity().getSupportFragmentManager().popBackStack();
                        getActivity().onBackPressed();
                    }


                }

            });
            buttonEffect(back);
            buttonEffect(submit);

            return errorReport;
        }

    }
    public static class MapsFragment extends Fragment implements OnMapReadyCallback {
        private GoogleMap mMap; // Might be null if Google Play services APK is not available.
        public LocationManager location1;
        public double latitudedoub;
        public double longitudedoub;
        public LatLng Position;
        public Marker myPos;
        public boolean ismarkerclicked;
        public List<Address> addresses;
        public Geocoder geocoder;

        public MapsFragment(){
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.activity_maps, container, false);

            //setContentView(R.layout.activity_maps);
            //mMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            mMap = ((SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            UiSettings zooming = mMap.getUiSettings();
            zooming.setZoomControlsEnabled(true);
            ismarkerclicked = false;
       /* mMap.getMapAsync(this);  */
            if (mMap != null) {
                Log.w("Not null", "No error");
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(37.875112, -122.258354))
                        .title("Sutardja Dai 3rd Floor")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(37.874760, -122.258480))
                        .title("Sutardja Dai 1st Floor")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(37.874624, -122.257308))
                        .title("Hearst Mining 3rd Floor")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(37.875742, -122.258612))
                        .title("Soda Hall 3rd Floor")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(37.875485, -122.259143))
                        .title("Etcheverry 2nd Floor")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(37.874912, -122.259105))
                        .title("Blum Hall")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(37.874403, -122.259058))
                        .title("O'Brien Hall")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));



            }
            else {
                Log.w("Erroring here", "Error");
            }
            // setUpMapIfNeeded();
            updatePos();
            return view;


        }

        @Override
        public void onMapReady(GoogleMap map) {


        }

        public void updatePos() {
            location1 = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            location1.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
            Location locationset = location1.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            latitudedoub = locationset.getLatitude();
            longitudedoub = locationset.getLongitude();
            mMap.setMyLocationEnabled(true);
        }

        public final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String straddress;
                double longitude;
                double latitude;
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                Log.w("Long:", String.valueOf(longitude));
                Log.w("Lat:", String.valueOf(latitude));
                mMap.setMyLocationEnabled(true);
                longitudedoub = longitude;
                latitudedoub = latitude;
                Log.w("Latcalling:", String.valueOf(getlat()));
                Log.w("Longcallback:", String.valueOf(getlong()));
                Position = new LatLng(latitudedoub, longitudedoub);
                geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(latitudedoub, longitudedoub, 1);
                    //String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    if (city == null) {
                        city = " ";
                    }
                    String state = addresses.get(0).getAdminArea();
                    if (state == null) {
                        state = " ";
                    }
                    String country = addresses.get(0).getCountryName();
                    if(country == null) {
                        country = " ";
                    }
                    String postalCode = addresses.get(0).getPostalCode();
                    if (postalCode == null) {
                        postalCode = " ";
                    }
                    straddress =  city + ", " + state + "  " + country + "  " + postalCode;

                }
                catch (Exception e) {
                    straddress = "Berkeley, CA";
                    Log.w("Cannot get address", "Something went wrong");
                }
                if (myPos != null) {
                    myPos.remove();
                }
           /* mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker markone) {
                    if (ismarkerclicked == true) {
                        markone.hideInfoWindow();
                        ismarkerclicked = false;
                        Log.w("True", "False");
                        return false;
                    }
                    else {
                        markone.showInfoWindow();
                        ismarkerclicked = true;
                        Log.w("False", "True");
                        return true;
                    }
                }

            }); */

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                    @Override
                    public void onInfoWindowClick(Marker marker) {


                        marker.hideInfoWindow();
                    }
                });

                myPos = mMap.addMarker(new MarkerOptions().alpha(0).position(Position)
                        .title(straddress));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Position, 15));



            }

            public double getlat() {
                return latitudedoub;
            }

            public double getlong() {
                return longitudedoub;
            }
            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStatusChanged(String provider,
                                        int status, Bundle extras) {
                // TODO Auto-generated method stub
            }
        };

        @Override
        public void onResume() {
            super.onResume();
            setUpMapIfNeeded();
        }

        /**
         * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
         * installed) and the map has not already been instantiated.. This will ensure that we only ever
         * call {@link #setUpMap()} once when {@link #mMap} is not null.
         * <p/>
         * If it isn't installed {@link SupportMapFragment} (and
         * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
         * install/update the Google Play services APK on their device.
         * <p/>
         * A user can return to this FragmentActivity after following the prompt and correctly
         * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
         * have been completely destroyed during this process (it is likely that it would only be
         * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
         * method in {@link #onResume()} to guarantee that it will be called.
         */
        private void setUpMapIfNeeded() {
            // Do a null check to confirm that we have not already instantiated the map.
            if (mMap == null) {
                Log.w("Erroring", "Errors");
                // Try to obtain the map from the SupportMapFragment.
                mMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map))
                        .getMap();
            }
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                Log.w("Hits", "hits");
                setUpMap();
            }
            else {
                Log.w("Erroring", "Errors");
            }

        }

        /**
         * This is where we can add markers or lines, add listeners or move the camera. In this case, we
         * just add a marker near Africa.
         * <p/>
         * This should only be called once and when we are sure that {@link #mMap} is not null.
         */
        private void setUpMap() {
            // mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        }
    }
}