package com.project.phoenixNotes.Activities;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.project.phoenixNotes.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ShowUserLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    boolean mLocationPermissionGranted = false;


    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int CONNECTION_RESOLUTION_REQUEST = 2;
    private GoogleApiClient googleApiClient;
    private Location mLastKnownLocation;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Location userLocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code = 99;
    boolean isEdit = false;
    String completeAddress = "";
    Geocoder geocoder;
    List<Address> addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_location);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            checkUserLocationPermission();
//        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng objLatLng = getIntent().getExtras().getParcelable("Latlng");

        if (objLatLng != null) {
            LatLng temp = new LatLng(objLatLng.latitude, objLatLng.longitude);
            mMap.addMarker(new MarkerOptions().position(temp)
                    .title(getAddressTitle(temp)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(temp, 15));

        }

    }

    public String getAddressTitle(LatLng location) {
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            completeAddress = completeAddress + addresses.get(0).getAddressLine(0) + " " + addresses.get(0).getLocality() + " " + addresses.get(0).getAdminArea() + " " + addresses.get(0).getCountryName();

        } catch (IOException e) {

        }
        return completeAddress;

    }
}


