package com.example.ashishkrishna.gps;

/**
 * Created by ashishkrishna on 7/30/15.
 */
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DatabaseActivity extends Activity {
    List<Map<String, String>> data = new ArrayList<Map<String, String>>();
    SimpleAdapter adapter;
    String[] test1 = new String[4];
    PostTask attempt1 = new PostTask();
    LoadTask attempt2 = new LoadTask();
    PostTaskSecond attemp4 = new PostTaskSecond();
    public static double lat;
    public static double lon;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.databaselayout);

        /*adapter = new SimpleAdapter(this,
                data,							 //a list of hashmaps
                R.layout.messaging, 			 //the layout to use for each item
                new String[] {"Num", "Name", "Address", "Rating" }, 	 //the array of keys
                new int[] {R.id.num, R.id.nameText, R.id.addressText, R.id.rating });	//array of view ids that should display the values (same order)
        ListView messageList = (ListView)findViewById(R.id.currentmessages);
        messageList.setAdapter(adapter);
        messageList.setLongClickable(true); */

        test1[0] = "1";
        test1[1] = "Ashish's House";
        test1[2] = "2539 Benvenue Avenue, Berkeley, CA 94704";
        test1[3] = "Four stars";
        attempt2.execute("5");
        //attempt1.execute(test1[0], test1[1], test1[2], test1[3]);
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                        /*sendMessage(nodeId); */

                EditText text1 = (EditText) findViewById(R.id.editText);
                test1[0] = text1.getText().toString();

                EditText text3 = (EditText) findViewById(R.id.editText3);
                test1[2] = text3.getText().toString();

                String[] addressparts = test1[2].split(" ");
                String expr = "";
                for (int i = 0; i<addressparts.length; i++) {
                    expr = expr + addressparts[i];
                }
                getLatLongFromAddress(expr);
                attemp4.execute(Double.toString(lat), Double.toString(lon), test1[0]);


            }
        });

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
    public void reload(View v) {
        LoadTask task = new LoadTask();
        task.execute();
    }

    private class PostTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String url = "http://teammynstur.pythonanywhere.com/messages";

            HttpResponse response;
            HttpClient httpclient = new DefaultHttpClient();
            try {

                HttpPost post = new HttpPost(url);
                List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
                postParameters.add(new BasicNameValuePair("num", params[0]));
                postParameters.add(new BasicNameValuePair("name", params[1]));
                postParameters.add(new BasicNameValuePair("lat", params[2]));
                postParameters.add(new BasicNameValuePair("lon", params[3]));
                postParameters.add(new BasicNameValuePair("rating", params[4]));


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
            String url = "http://teammynstur.pythonanywhere.com/messages/" + params[0];
            Log.w("URL:", url);
            HttpResponse response;
            HttpClient httpclient = new DefaultHttpClient();
            String responseString = "";

            try {
                response = httpclient.execute(new HttpGet(url));
                if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();
                    Log.w("Params1", responseString);

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
                Log.w("Params", messages.toString());
                return messages;
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(JSONArray itemsList) {
           // data.clear();
            //for (int i = 0; i < itemsList.length(); i++) {
                try {
                   // JSONArray current = itemsList.getJSONArray(i);
                    Map<String, String> listItem = new HashMap<String, String>(4);

                    listItem.put("lat", itemsList.getString(0));
                    listItem.put("lon", itemsList.getString(1));
                    listItem.put("rating", itemsList.getString(2));
                    listItem.put("ada", itemsList.getString(3));
                    listItem.put("gender", itemsList.getString(4));
                    Double latitudedoub = Double.parseDouble(listItem.get("lat"));
                    Double longitudedoub = Double.parseDouble(listItem.get("lon"));
                    Geocoder geocoder = new Geocoder(DatabaseActivity.this, Locale.getDefault());
                    String straddress;
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
                        straddress =  addressln + " " + city + ", " + state + "  " + country + "  " + postalCode;

                    }
                    catch (Exception e) {
                        straddress = "Berkeley, CA";
                        Log.w("Cannot get address", "Something went wrong");
                    }
                    //TextView t1 = (TextView) findViewById(R.id.textView);
                    //TextView t2 = (TextView) findViewById(R.id.textView3);
                    //TextView t3 = (TextView) findViewById(R.id.textView4);
                    //TextView t4 = (TextView) findViewById(R.id.textView5);
                    //t1.setText(listItem.get("num"));
                   // t2.setText(listItem.get("name"));
                    //t3.setText(straddress);
                   // t4.setText(listItem.get("rating"));
                    /*String address = listItem.get("address");
                    String[] addressparts = address.split(" ");
                    String expr = "";
                    for (int i = 0; i<addressparts.length; i++) {
                        expr = expr + addressparts[i];
                    }
                    getLatLongFromAddress(expr); */
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
           // }
            //adapter.notifyDataSetChanged();
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
}
