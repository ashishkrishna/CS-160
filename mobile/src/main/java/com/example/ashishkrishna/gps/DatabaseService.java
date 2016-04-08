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
import android.provider.Telephony;
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

public class DatabaseService extends Service {
    public LocationManager location1;
    public static double longitudedoub;
    public static double latitudedoub;
    public static double lat;
    public static double lon;
    public static String lat1;
    public static String lon1;
    public int rating;
    public String adaaccess;
    public String gender;
    public String commerror;
    PostTask attempt4;
    PostTaskSecond attempt8;
    public LoadTask attempt10 = new LoadTask();
    public LoadTask attempt11 = new LoadTask();
    public LoadTask attempt12 = new LoadTask();
    public String argumes;
    public String addressln;
    public DatabaseService myself;
    private final IBinder mBinder = new DatabaseService.LocalBinder();
    public static  String BACK_ACTIVITY_PATH = "/start/ListenerService";
    private static final long CONNECTION_TIME_OUT_MS = 100;
    private String nodeId;
    private GoogleApiClient client;
    public static String prev;
    public static String name1;
    public static Queue<String> vals  = new LinkedList<String>();
    public ForkJoinPool pool = new ForkJoinPool();
    public static Map<String, String> listItem;
    public static ArrayList<String> Addresscontainer;
    public static String streetaddress;
    public DatabaseService() {
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
        updatePos();
        initApi();
        client.connect();
        attempt4 = new PostTask();
        myself = this;
        return START_STICKY;
    }

    public void createnewLog() {
        attempt4 = new PostTask();
    }

    public void createnewComment() {
        attempt8 = new PostTaskSecond();
    }

    public void createnewLoad() {
        attempt10 = new LoadTask();
        attempt11 = new LoadTask();
        attempt12 = new LoadTask();
        sendMessage3();
    }

    public void createnewLoadMobile() {
        attempt10 = new LoadTask();
        attempt11 = new LoadTask();
        attempt12 = new LoadTask();
    }

    /*public void createjson() {
        argumes = new JSONArray();
    } */

    public void updatePos() {
        location1 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        location1.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        Location locationset = location1.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        latitudedoub = locationset.getLatitude();
        longitudedoub = locationset.getLongitude();
    }

    public String longandlat(double lat, double lon) {
        Geocoder geocoder = new Geocoder(DatabaseService.this, Locale.getDefault());
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
            return streetaddress;

        }
        catch (Exception e) {
            streetaddress = "Berkeley, CA";
            Log.w("Cannot get address", "Something went wrong");
            return "";
        }
    }

    public static void getLatLongFromAddress(String youraddress) {
        String uri = "http://maps.google.com/maps/api/geocode/json?address=" +
                youraddress + "&sensor=false";
        HttpGet httpGet = new HttpGet(uri);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());

            DatabaseActivity.lon = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            DatabaseActivity.lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

            Log.d("latitude", "" + lat);
            Log.d("longitude", "" + lon);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double longitude;
            double latitude;
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            Log.w("Long:", String.valueOf(longitude));
            Log.w("Lat:", String.valueOf(latitude));
            longitudedoub = longitude;
            latitudedoub = latitude;



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

    public String[] reading() {
        String[] toreturn = new String[4];
        toreturn[0] = "None";
        toreturn[1] = "None";
        toreturn[2] = "None";
        toreturn[3] = "false";
        FileInputStream y;
        try {
            y = this.openFileInput("settingsgps.txt");
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
    public class LocalBinder extends Binder {
        DatabaseService getService() {
            // Return this instance of LocalService so clients can call public methods
            return DatabaseService.this;
        }
    }
    public class PostTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String url = "http://teammynstur.pythonanywhere.com/messages";

            HttpResponse response;
            HttpClient httpclient = new DefaultHttpClient();
            try {

                HttpPost post = new HttpPost(url);
                List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
                postParameters.add(new BasicNameValuePair("lat", params[0]));
                postParameters.add(new BasicNameValuePair("lon", params[1]));
                postParameters.add(new BasicNameValuePair("rating", params[2]));
                postParameters.add(new BasicNameValuePair("ada", params[3]));
                postParameters.add(new BasicNameValuePair("gender", params[4]));


                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParameters);
                post.setEntity(entity);
                Log.w("Writing", "It down");
                response = httpclient.execute(post);

            } catch (ClientProtocolException e) {
                //TODO Handle problems..
            } catch (IOException e) {
                //TODO Handle problems..
            }

            return null;
        }

        @Override
        protected void onPostExecute(String arg0) {
            Log.w("Done", "Fin");
            //reload(null);

        }

    }
    public class PostTaskSecond extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String url = "http://teammynstur.pythonanywhere.com/comments";

            HttpResponse response;
            HttpClient httpclient = new DefaultHttpClient();
            try {

                HttpPost post = new HttpPost(url);
                List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
                postParameters.add(new BasicNameValuePair("lat", params[0]));
                postParameters.add(new BasicNameValuePair("lon", params[1]));
                postParameters.add(new BasicNameValuePair("commnerr", params[2]));


                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParameters);
                post.setEntity(entity);
                Log.w("Writing", "It down");
                response = httpclient.execute(post);

            } catch (ClientProtocolException e) {
                //TODO Handle problems..
            } catch (IOException e) {
                //TODO Handle problems..
            }

            return null;
        }

        @Override
        protected void onPostExecute(String arg0) {
            Log.w("Done", "Fin");
            //reload(null);

        }

    }
    /* private class GetTask extends AsyncTask<String, Void, String> {

     } */
    public class LoadTask extends AsyncTask<String, Void, JSONArray> {

        protected JSONArray doInBackground(String...params) {
            String url2 = "http://teammynstur.pythonanywhere.com/messages/currlat=" + params[0]+"&currlon="+params[1]+"&setgender="+params[2]+"&setada="+params[3];
            Log.w("URL:", url2);
            HttpResponse response;
            HttpClient httpclient = new DefaultHttpClient();
            String responseString = "";


            try {
                response = httpclient.execute(new HttpGet(url2));
                if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();
                    //Log.w("Params1", responseString);

                } else{
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

                JSONArray messages = new JSONArray(responseString);

                Log.w("response", responseString);
                //argumes = responseString;
                // Log.w(argumes, "This is from Database");

                //Log.w("Params", messages.toString());
                return messages;
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(JSONArray itemsList) {
            // data.clear();
            Log.w("Lenght of JSONArray:", Integer.toString(itemsList.length()));
            for (int i = 0; i < itemsList.length(); i++) {
                try {

                    JSONArray current = itemsList.getJSONArray(i);
                    listItem = new HashMap<String, String>(4);

                    listItem.put("lat", current.getString(0));
                    listItem.put("lon", current.getString(1));
                    listItem.put("rating", current.getString(2));
                    listItem.put("ada", current.getString(3));
                    listItem.put("gender", current.getString(4));
                    Double latitudedoub = Double.parseDouble(listItem.get("lat"));
                    Double longitudedoub = Double.parseDouble(listItem.get("lon"));
                    Geocoder geocoder = new Geocoder(DatabaseService.this, Locale.getDefault());
                    String streetaddress;
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitudedoub, longitudedoub, 1);
                        //String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        String addressln = addresses.get(0).getAddressLine(0);
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
                        DatabaseService.streetaddress = " Rating:" + listItem.get("rating") + " Lat:" + listItem.get("lat") + " Lon:" + listItem.get("lon");
                        //streetaddress = "alpha1";
                        PlacesTask aleph = new PlacesTask();
                        String container = aleph.execute(listItem.get("lat"), listItem.get("lon")).get();
                        container = container + " Rating:" + listItem.get("rating") + " Gender:" + listItem.get("gender") + " ADA:" + listItem.get("ada")+ " Lat:" + listItem.get("lat") + " Lon:" + listItem.get("lon");
                        vals.add(container);


                        //BACK_ACTIVITY_PATH = streetaddress;
                        //vals.add(streetaddress);
                        //Log.w("Address", streetaddress);
                        //retrieveDeviceNode();

                    /*new Thread(new Runnable() {
                        @Override
                        public void run() {
                            retrieveDeviceNode();
                        }
                    }).start(); */

                    }
                    catch (Exception e) {
                        streetaddress = "Berkeley, CA";
                        Log.w("Cannot get address", "Something went wrong");
                        e.printStackTrace();
                    }
                    //TextView t1 = (TextView) findViewById(R.id.textView);
                    //TextView t2 = (TextView) findViewById(R.id.textView3);
                    //TextView t3 = (TextView) findViewById(R.id.textView4);
                    //TextView t4 = (TextView) findViewById(R.id.textView5);
                    //t1.setText(listItem.get("num"));
                    // t2.setText(listItem.get("name"));
                    //t3.setText(straddress);
                    // t4.setText(listItem.get("rating"));
                   /* String address = listItem.get("address");
                    String[] addressparts = address.split(" ");
                    String expr = "";
                    for (int v = 0; v<addressparts.length; v++) {
                        expr = expr + addressparts[v];
                    }
                    getLatLongFromAddress(expr); */
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            prntadds();



            //adapter.notifyDataSetChanged();
        }


    }


    public void prntadds() {
        Log.w("This is length Queue:", Integer.toString(vals.size()));
        //String addr = vals.remove();
        //if(addr == null) {
        // Log.w("Null", "nulled vals");
        // return;
        //}

        sendMessage2();

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

    private void retrieveDeviceNode(String mssg) {
        final String messag = mssg;
        Log.w("G", mssg);
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.blockingConnect(30, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(client).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    nodeId = nodes.get(0).getId();

                }
                //nodeId.equals(null))
                sendMessage(nodeId, messag);


                client.disconnect();
            }
        }).start();
    }

    private void sendMessage2() {
        client.connect();
        Log.v("myApp", "OnConnected entered");

        String WEARABLE_DATA_PATH = "/wearable_data";

        // Create a DataMap object and send it to the data layer
        int i = 0;
        DataMap dataMap = new DataMap();
        while (!vals.isEmpty()) {

            dataMap.putString("Address"+Integer.toString(i), vals.remove());
            i++;
        }
        //Requires a new thread to avoid blocking the UI
        new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap).start();
    }

    private void sendMessage3() {
        client.connect();
        Log.v("myApp", "OnConnected entered");

        String WEARABLE_DATA_PATH = "/wearable_data";

        // Create a DataMap object and send it to the data layer
        int i = 0;
        DataMap dataMap = new DataMap();
        dataMap.putInt("cautionandmem", 34);

        //Requires a new thread to avoid blocking the UI
        new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap).start();
    }
    @Override
    public void onDestroy() {
        if (null != client && client.isConnected()) {
            client.disconnect();
        }
        super.onDestroy();
    }

    private void sendMessage(String node, String mssg) {
        //Log.w("I", mssg);
        // Log.w("Node Id#:", node);
        Wearable.MessageApi.sendMessage(client, node, mssg, new byte[0]).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                if (!sendMessageResult.getStatus().isSuccess()) {
                    Log.e("GoogleApi", "Failed to send message with status code: "
                            + sendMessageResult.getStatus().getStatusCode());
                }
            }
        });
    }
    class SendToDataLayerThread extends Thread {
        String path;
        DataMap dataMap;

        // Constructor for sending data objects to the data layer
        SendToDataLayerThread(String p, DataMap data) {
            path = p;
            dataMap = data;
        }

        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(client).await();
            for (Node node : nodes.getNodes()) {

                // Construct a DataRequest and send over the data layer
                PutDataMapRequest putDMR = PutDataMapRequest.create(path);
                putDMR.getDataMap().putAll(dataMap);
                PutDataRequest request = putDMR.asPutDataRequest();
                DataApi.DataItemResult result = Wearable.DataApi.putDataItem(client,request).await();
                if (result.getStatus().isSuccess()) {
                    Log.v("myTag", "DataMap: " + dataMap + " sent to: " + node.getDisplayName());
                } else {
                    // Log an error
                    Log.v("myTag", "ERROR: failed to send DataMap");
                }
            }
        }
    }

    public class PlacesTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {
            String url = "https://maps.googleapis.com/maps/api/place/search/json?key=AIzaSyCN95maHJZw-xD4dLahrlbRuntEXqSz28k&location=" + params[0] + "," + params[1]+"&radius=25&sensor=false&types=establishment";
            HttpResponse response;
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet placesGet = new HttpGet(url);
            String responseString = "";
            StringBuilder stringBuilder = new StringBuilder();
            try {
                response = httpclient.execute(new HttpGet(url));
                HttpEntity entity = response.getEntity();
                InputStream stream = entity.getContent();
                int b;
                while ((b = stream.read()) != -1) {
                    stringBuilder.append((char) b);
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String returnme = "";
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = new JSONObject(stringBuilder.toString());

                returnme = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                        .getString("name");
                return returnme;

            } catch (JSONException e) {
                e.printStackTrace();
            }




//argumes = responseString;
// Log.w(argumes, "This is from Database");

//Log.w("Params", messages.toString());
                return " ";


        }

        protected void onPostExecute(JSONArray itemsList) {
// data.clear();


        }
    }

}
