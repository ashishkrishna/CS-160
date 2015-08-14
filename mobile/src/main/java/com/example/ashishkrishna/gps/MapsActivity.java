package com.example.ashishkrishna.gps;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public LocationManager location1;
    public double latitudedoub;
    public double longitudedoub;
    public LatLng Position;
    public Marker myPos;
    public boolean ismarkerclicked;
    public Geocoder geocoder;
    public List<Address> addresses;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        UiSettings zooming = mMap.getUiSettings();
        zooming.setZoomControlsEnabled(true);
        ismarkerclicked = false;
       /* mMap.getMapAsync(this);  */
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(37.8758, -122.257))
                    .title("Cloyne Bathroom"));
            Log.w("PRINT WHY ISNT PRINT", "yo??????!!!!!");
            Log.w("Not null", "No error");
        }
        else {
            Log.w("Erroring here", "Error");
        }
       // setUpMapIfNeeded();
        updatePos();

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(MapsActivity.this, BathroomActivity.class);
                startActivity(intent);

            }
        });


    }

    @Override
    public void onMapReady(GoogleMap map) {

    }



    public void updatePos() {

        location1 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
            geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
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

//            Log.w("PRIdfs", "hi!!!!!!!!!");
//            myPos = mMap.addMarker(new MarkerOptions().alpha(0).position(Position)
//                    .title(straddress));
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Position, 15));



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
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
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
