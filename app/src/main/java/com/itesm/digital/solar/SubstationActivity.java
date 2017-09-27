package com.itesm.digital.solar;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class SubstationActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private LatLng locationTerrain, locationSE;
    Marker subStationMarker;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_substation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onResetMap();
                Snackbar.make(view, "Subestaciones borradas", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationSE = new LatLng(0, 0);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);

        locationTerrain = new LatLng(19.359611, -99.257616);
        mMap.addMarker(new MarkerOptions()
                .position(locationTerrain)
                .title("Ubicación área")
                .snippet("Área seleccionada a evaluar"));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(locationTerrain)    // Sets the center of the map
                .zoom(16)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Toast.makeText(SubstationActivity.this,
                "Ubicación:\n" + latLng.latitude + " : " + latLng.longitude,
                Toast.LENGTH_LONG).show();

        if(locationSE.latitude==0) {
            //Add marker
            subStationMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Subestación eléctrica")
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            locationSE = new LatLng(latLng.latitude,latLng.longitude);
        }


    }

    @Override
    public void onMarkerDragStart(Marker marker) {}

    @Override
    public void onMarkerDrag(Marker marker) {}

    @Override
    public void onMarkerDragEnd(Marker marker) {
        locationSE = marker.getPosition();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        if (marker.getPosition()==locationSE){
            fab.setVisibility(View.VISIBLE);
        }

        return false;
    }

    private void onResetMap() {
        locationSE = new LatLng(0, 0);
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(locationTerrain)
                .title("Ubicación área")
                .snippet("Área seleccionada a evaluar"));
    }
}
