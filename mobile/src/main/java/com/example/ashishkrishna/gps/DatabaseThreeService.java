package com.example.ashishkrishna.gps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import org.apache.http.HttpEntity;
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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ForkJoinPool;

public class DatabaseThreeService extends Service {
    public LocationManager location1;
    public double longitudedoub;
    public double latitudedoub;
    public static double lat;
    public static double lon;
    public String rating;
    public String ada;
    public String gender;
    public String addressln;
    public String commerror;
    public LoadTaskBathroom attempt10 = new LoadTaskBathroom();
    public LoadTaskComments attempt11 = new LoadTaskComments();
    public List<Map<String, String>> data = new ArrayList<Map<String, String>>();
    public Map<String, String> listItem = new HashMap<String, String>(2);
    public String argumes;
    public DatabaseThreeService myself;
    private final IBinder mBinder = new DatabaseThreeService.LocalBinder2();
    public static  String BACK_ACTIVITY_PATH = "/start/ListenerService";
    private static final long CONNECTION_TIME_OUT_MS = 100;
    private String nodeId;
    public static int indicate;
    private GoogleApiClient client;
    public static String prev;
    public static Queue<String> vals  = new LinkedList<String>();
    public DatabaseThreeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
        // TODO: Return the communication channel to the service.

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //updatePos();
        super.onStartCommand(intent, flags, startId);
        myself = this;
        initApi();
        client.connect();
        return START_STICKY;
    }


    public void createnewLoadBathroom() {
        attempt10 = new LoadTaskBathroom();

    }

    public void createnewLoadComment() {
        attempt11 = new LoadTaskComments();
    }

    /*public void createjson() {
        argumes = new JSONArray();
    } */


    public String longandlat(double lat, double lon) {
        Geocoder geocoder = new Geocoder(DatabaseThreeService.this, Locale.getDefault());
        String streetaddress;
        Log.w("Latitude, Longitude", Double.toString(lat) + Double.toString(lon));
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            //String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            addressln = addresses.get(0).getAddressLine(0);
            if (addressln == null) {
                addressln = " ";
            }
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
            streetaddress =  addressln + " " + city + ", " + state + "  " + country + "  " + postalCode;
            BACK_ACTIVITY_PATH = streetaddress;
            vals.add(streetaddress);
            return streetaddress;

        }
        catch (Exception e) {
            streetaddress = "Berkeley, CA";
            Log.w("Cannot get address", "Something went wrong");
            return "";
        }
    }



    public class LocalBinder2 extends Binder {
        DatabaseThreeService getService() {
            // Return this instance of LocalService so clients can call public methods
            return DatabaseThreeService.this;
        }
    }
    public class LoadTaskBathroom extends AsyncTask<String, Void, JSONArray> {

        protected JSONArray doInBackground(String... params) {
            String url = "http://teammynstur.pythonanywhere.com/mssgs/lat="+params[0]+"&lon="+params[1];
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
            if (itemsList != null) {
                try {
                    JSONArray current = itemsList.getJSONArray(0);
                    //Map<String, String> listItem = new HashMap<String, String>(2);
                    rating = current.getString(2);;
                    ada = current.getString(3);
                    Log.w("Here is the ada", ada);
                    gender = current.getString(4);
                    Log.w("Here is the gender", gender);
                    //Log.w("Rating:", current.getString(2));
                    //Log.w("Ada:", current.getString(3));
                    //Log.w("Gender", current.getString(4));
                    Log.w("Current", current.toString());
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            //adapter.notifyDataSetChanged();
            Log.w("Got here", "Got here");
        }

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
                            listItem.put("Comment "+Integer.toString(i), current.getString(2));
                            data.add(listItem);
                            //Log.w("Put", "put "+Integer.toString(i));
                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                indicate = 1;

                Log.w("Got here", "Got here");
            }
            //adapter.notifyDataSetChanged();
        }

    }
    private void initApi() {
        Log.w("H", "H");
        client = getGoogleApiClient(getApplicationContext());
    }


    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(getApplicationContext())
                .addApi(Wearable.API)
                .build();
    }


}

