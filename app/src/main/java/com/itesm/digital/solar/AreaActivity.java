package com.itesm.digital.solar;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.List;

public class AreaActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng csf = new LatLng(19.358572, -99.259516);
        mMap.addMarker(new MarkerOptions().position(csf).title("Marker in CSF"));
        //mMap.setMinZoomPreference(6.0f);
        //mMap.setMaxZoomPreference(14.0f);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(csf));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());

        // Instantiates a new Polyline object and adds points to define a rectangle
        PolygonOptions rectOptions = new PolygonOptions()
                .add(new LatLng(37.35, -122.0),
                        new LatLng(37.45, -122.0),
                        new LatLng(37.45, -122.2),
                        new LatLng(37.35, -122.2),
                        new LatLng(37.35, -122.0)).fillColor(Color.BLUE);

        // Get back the mutable Polygon
        final Polygon polygon = mMap.addPolygon(rectOptions);
        mMap.setOnMapClickListener( new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng latLng) {
                List<LatLng> path = polygon.getPoints();
                path.remove(path.size()-1);
                path.add(latLng);
                polygon.setPoints(path);
                Log.d("path + ",path.toString());
            }
        });
    }
}
