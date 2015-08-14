package com.example.ashishkrishna.gps;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationListener;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.wearable.view.DismissOverlayView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;

/**
 * Sample that shows how to set up a basic Google Map on Android Wear.
 */
public class MapsActivity extends FragmentActivity  {

    private static final LatLng SYDNEY = new LatLng(-33.85704, 151.21522);
    public static String lat;
    public static String lon;

    /**
     * Overlay that shows a short help text when first launched. It also provides an option to
     * exit the app.
     */
    private DismissOverlayView mDismissOverlay;

    /**
     * The map. It is initialized when the map has been fully loaded and is ready to be used.
     *
     * @see #(com.google.android.gms.maps.GoogleMap)
     */
    private GoogleMap mMap;

    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        // Set the layout. It only contains a SupportMapFragment and a DismissOverlay.
        setContentView(R.layout.activity_maps);
        new Thread(new Runnable() {
           @Override
            public void run() {
               Intent intent = new Intent(Intent.ACTION_VIEW);
               intent.setData(Uri.parse("google.navigation:///?q="+lat+","+lon+"&mode=w"));
               startActivity(intent);
               }
           }).start();

        finish();





        // Retrieve the containers for the root of the layout and the map. Margins will need to be
        // set on them to account for the system window insets.
       /* final FrameLayout topFrameLayout = (FrameLayout) MapsActivity.this.findViewById(R.id.root_container);
        final FrameLayout mapFrameLayout = (FrameLayout) MapsActivity.this.findViewById(R.id.map_container);
        //LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //inflater.inflate(R.layout.activity_maps, topFrameLayout, false);
        //inflater.inflate(R.layout.activity_maps, mapFrameLayout, false);

        // Set the system view insets on the containers when they become available.
        topFrameLayout.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                // Call through to super implementation and apply insets
                insets = topFrameLayout.onApplyWindowInsets(insets);

                FrameLayout.LayoutParams params =
                        (FrameLayout.LayoutParams) mapFrameLayout.getLayoutParams();

                // Add Wearable insets to FrameLayout container holding map as margins
                params.setMargins(
                        insets.getSystemWindowInsetLeft(),
                        insets.getSystemWindowInsetTop(),
                        insets.getSystemWindowInsetRight(),
                        insets.getSystemWindowInsetBottom());
                mapFrameLayout.setLayoutParams(params);

                return insets;
            }
        });

        // Obtain the DismissOverlayView and display the intro help text.
        mDismissOverlay = (DismissOverlayView) findViewById(R.id.dismiss_overlay);
        mDismissOverlay.setIntroText(R.string.intro_text);
        mDismissOverlay.showIntroIfNecessary(); */

        // Obtain the MapFragment and set the async listener to be notified when the map is ready.
     /*   SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this); */

    }

  /*  @Override
    public void onMapReady(GoogleMap googleMap) {
        // Map is ready to be used.
        mMap = googleMap;

        // Set the long click listener as a way to exit the map.
        mMap.setOnMapLongClickListener(this);

        // Add a marker with a title that is shown in its info window.
        mMap.addMarker(new MarkerOptions().position(SYDNEY)
                .title("Sydney Opera House"));

        // Move the camera to show the marker.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 10));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        // Display the dismiss overlay with a button to exit this activity.
        mDismissOverlay.show();
    } */
}

