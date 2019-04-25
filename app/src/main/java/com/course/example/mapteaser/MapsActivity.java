/*
This application will open a GoogleMap centered at Bentley University.
It will enable tracking of the Android device. It will then move the map and marker
to the location of the device on a periodic basis, or it can update when a given
distance from the last location is reached.

Be sure to use Settings to grant app needed permissions

To update position using the emulator, use the emulator extended controls.
 */

package com.course.example.mapteaser;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class  MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final LatLng BENTLEY = new LatLng(42.3889167, -71.2208033);
    private static final float zoom = 14.0f;
    private LocationManager  locManager;
    private LocationListener locListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker at Bentley University.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker at Bentley and move the camera
        mMap.addMarker(new MarkerOptions()
                .position(BENTLEY)
                .title("Bentley University")
                .snippet("Population: 5,000")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

       mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BENTLEY, zoom));

        //choose map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        //mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //set listener on marker click
        mMap.setOnMarkerClickListener(
                new GoogleMap.OnMarkerClickListener() {

                    public boolean onMarkerClick(Marker m) {
                        String title = m.getTitle();
                        String snip = m.getSnippet();
                        Toast.makeText(getApplicationContext(), title + "\n" + snip, Toast.LENGTH_LONG).show();
                        return true;
                    }
                }
        );

        //set listener on map long tap
        mMap.setOnMapLongClickListener(
                new GoogleMap.OnMapLongClickListener() {
                    public void onMapLongClick(LatLng point) {
                        Toast.makeText(getApplicationContext(), "Long Tap", Toast.LENGTH_LONG).show();
                    }
                }
        );

        //enable map tracking of current location
        try {
            mMap.setMyLocationEnabled(true);


        // Use the LocationManager class to obtain GPS locations
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locListener = new MyLocationListener();

        //Register for location updates using the named provider, and a pending intent.
        //10 second minimum interval between updates, 0 meters minimum distance between updates
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 100,
                locListener);

        } catch(SecurityException e) {
            Toast.makeText(this, "Security Exception - setup", Toast.LENGTH_LONG).show();
        }
    }

//inner class used to create object that will receive Location update callbacks
public class MyLocationListener implements LocationListener {

    @Override
    public void onLocationChanged(Location loc) {

        double latitude = loc.getLatitude();
        double longitude = loc.getLongitude();

        String Text = "My current location is: " +
                "Latitude = " + latitude + "Longitude = " + longitude;

        Toast.makeText(getApplicationContext(), Text, Toast.LENGTH_LONG)
                .show();

        LatLng position = new LatLng(latitude, longitude);

        mMap.clear();

        mMap.addMarker(new MarkerOptions()
                .position(position)
                .title("User Location")
                .snippet("")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, zoom));
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getApplicationContext(), "Gps Disabled",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getApplicationContext(),
                "Gps Enabled", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

}// End MyLocationListener

    //stop updates
    public void onStop() {
        super.onStop();
        try {
            locManager.removeUpdates(locListener);
        } catch(SecurityException e) {Toast.makeText(this,
                "Security Exception - stop", Toast.LENGTH_LONG).show();}
    }

}
