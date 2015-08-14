package com.example.ashishkrishna.gps;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import android.util.Log;
import android.content.Context;
import android.content.ContentResolver;
import android.provider.Settings;



import android.os.IBinder;


/**
 * Created by ashishkrishna on 7/28/15.
 */
public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient mGoogleApiClient;
    LocationManager location1;
    LocationListener locationlisten1;
    double longitudedoub;
    double latitudedoub;
    private final IBinder mBinder = new LocalBinder();
    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.w("Location Service", "location service");
       /* ContentResolver contentResolver = getBaseContext()
                .getContentResolver();
        boolean gpsStatus = Settings.Secure
                .isLocationProviderEnabled(contentResolver,
                        LocationManager.GPS_PROVIDER);
        if (gpsStatus != true ) {
            Log.w("GPS not enabled!", "ERROR");
        }
        else {
            Log.w("GPS is enabled", "PROCEEDING");
        } */
        location1 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        location1.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        Location locationset = location1.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        latitudedoub = locationset.getLatitude();
        longitudedoub = locationset.getLongitude();

            Log.w(String.valueOf(latitudedoub), "lat");
            Log.w(String.valueOf(longitudedoub), "long");

        return START_STICKY;
    }





    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
                Log.w("Connected", "Connection Status");
    }

  /*  @Override
    public IBinder onBind(Intent context) {
        return null;

    } */

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
            Log.w("Latcalling:", String.valueOf(getlat()));
            Log.w("Longcallback:", String.valueOf(getlong()));



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

    public class LocalBinder extends Binder {
        LocationService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LocationService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /** method for clients */

    public double getlat() {
        return latitudedoub;
    }

    public double getlong() {
        return longitudedoub;
    }

}
