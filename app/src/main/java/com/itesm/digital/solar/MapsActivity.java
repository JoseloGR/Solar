package com.itesm.digital.solar;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NONE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN;

public class MapsActivity extends FragmentActivity implements AdapterView.OnItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private boolean firstPoint = true;
    private boolean deletePolygon = false;
    private boolean startAnother = false;

    private double latitude = 0;
    private double longitude = 0;

    private List<List<LatLng>> listPolygons = new ArrayList<List<LatLng>>();

    private Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mSpinner = (Spinner) findViewById(R.id.layers_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.layers_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(this);

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

        updateMapType();

        // Add a marker in Sydney and move the camera
        LatLng ITESM_CSF = new LatLng(19.359611, -99.257616);
        //mMap.addMarker(new MarkerOptions().position(ITESM_CSF).title("Marcador en CSF"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ITESM_CSF));

        // Instantiates a new Polyline object and adds points to define a rectangle
        PolygonOptions rectOptions = new PolygonOptions()
                .add(new LatLng(0, 0),
                        new LatLng(0, 0)).fillColor(Color.rgb(255, 204, 128)).strokeWidth(4);

        // Get back the mutable Polygon
        final Polygon polygon = mMap.addPolygon(rectOptions);
        final Polygon polygon2 = mMap.addPolygon(rectOptions);
        final Polygon polygon3 = mMap.addPolygon(rectOptions);
        final Polygon polygon4 = mMap.addPolygon(rectOptions);

        mMap.setOnMapClickListener( new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng latLng) {
                if(listPolygons.size() == 0) {
                    List<LatLng> path = polygon.getPoints();
                    if (startAnother) {
                        Log.d("size ", Integer.toString(path.size()));
                        if(latitude == 0){
                            latitude = path.get(0).latitude;
                            longitude = path.get(0).longitude;
                        }
                        if(path.size() < 4){
                            Toast.makeText(getApplicationContext(), "You need more number of vertices", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            listPolygons.add(polygon.getPoints());
                            firstPoint = true;
                        }
                        startAnother = false;
                    }
                    else if (firstPoint) {   //check the first
                        path.remove(0);
                        path.remove(0);
                        path.add(latLng);
                        path.add(latLng);
                        firstPoint = false;
                    }
                    else if (deletePolygon) {
                        int size = path.size();
                        for (int i = 0; i < size; i++) {
                            path.remove(0);
                        }
                        path.add(latLng);
                        path.add(latLng);
                        deletePolygon = false;
                    } else if (path.get(path.size() - 1).latitude - latLng.latitude >= -0.004 &&
                            path.get(path.size() - 1).latitude - latLng.latitude <= 0.004 &&
                            path.get(path.size() - 1).longitude - latLng.longitude >= -0.004 &&
                            path.get(path.size() - 1).longitude - latLng.longitude <= 0.004){
                        path.remove(path.size() - 1);
                        path.add(latLng);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Big Distances between Points", Toast.LENGTH_SHORT).show();
                    }
                    polygon.setPoints(path);
                    Log.d("path + ", path.toString());
                }
                if(listPolygons.size() == 1) {
                    List<LatLng> path = polygon2.getPoints();
                    if (startAnother) {
                        Log.d("size ", Integer.toString(path.size()));
                        if(path.size() < 4){
                            Toast.makeText(getApplicationContext(), "You need more number of vertices", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            listPolygons.add(polygon2.getPoints());
                            firstPoint = true;
                        }
                        startAnother = false;
                    }
                    else if (firstPoint) {   //check the first
                        path.remove(0);
                        path.remove(0);
                        path.add(latLng);
                        path.add(latLng);
                        firstPoint = false;
                    }
                    else if (deletePolygon) {
                        int size = path.size();
                        for (int i = 0; i < size; i++) {
                            path.remove(0);
                        }
                        path.add(latLng);
                        path.add(latLng);
                        deletePolygon = false;
                    } else if (path.get(path.size() - 1).latitude - latLng.latitude >= -0.004 &&
                            path.get(path.size() - 1).latitude - latLng.latitude <= 0.004 &&
                            path.get(path.size() - 1).longitude - latLng.longitude >= -0.004 &&
                            path.get(path.size() - 1).longitude - latLng.longitude <= 0.004){
                        path.remove(path.size() - 1);
                        path.add(latLng);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Big Distances between Points", Toast.LENGTH_SHORT).show();
                    }
                    polygon2.setPoints(path);
                    Log.d("path + ", path.toString());
                }
                if(listPolygons.size() == 2) {
                    List<LatLng> path = polygon3.getPoints();
                    if (startAnother) {
                        Log.d("size ", Integer.toString(path.size()));
                        if(path.size() < 4){
                            Toast.makeText(getApplicationContext(), "You need more number of vertices", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            listPolygons.add(polygon3.getPoints());
                            firstPoint = true;
                        }
                        startAnother = false;
                    }
                    else if (firstPoint) {   //check the first
                        path.remove(0);
                        path.remove(0);
                        path.add(latLng);
                        path.add(latLng);
                        firstPoint = false;
                    }
                    else if (deletePolygon) {
                        int size = path.size();
                        for (int i = 0; i < size; i++) {
                            path.remove(0);
                        }
                        path.add(latLng);
                        path.add(latLng);
                        deletePolygon = false;
                    } else if (path.get(path.size() - 1).latitude - latLng.latitude >= -0.004 &&
                            path.get(path.size() - 1).latitude - latLng.latitude <= 0.004 &&
                            path.get(path.size() - 1).longitude - latLng.longitude >= -0.004 &&
                            path.get(path.size() - 1).longitude - latLng.longitude <= 0.004){
                        path.remove(path.size() - 1);
                        path.add(latLng);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Big Distances between Points", Toast.LENGTH_SHORT).show();
                    }
                    polygon3.setPoints(path);
                    Log.d("path + ", path.toString());
                }
                if(listPolygons.size() == 3) {
                    List<LatLng> path = polygon4.getPoints();
                    if (startAnother) {
                        Toast.makeText(getApplicationContext(), "You can't add more areas", Toast.LENGTH_SHORT).show();
                        startAnother = false;
                    }
                    else if (firstPoint) {   //check the first
                        path.remove(0);
                        path.remove(0);
                        path.add(latLng);
                        path.add(latLng);
                        firstPoint = false;
                    }
                    else if (deletePolygon) {
                        int size = path.size();
                        for (int i = 0; i < size; i++) {
                            path.remove(0);
                        }
                        path.add(latLng);
                        path.add(latLng);
                        deletePolygon = false;
                    } else if (path.get(path.size() - 1).latitude - latLng.latitude >= -0.004 &&
                            path.get(path.size() - 1).latitude - latLng.latitude <= 0.004 &&
                            path.get(path.size() - 1).longitude - latLng.longitude >= -0.004 &&
                            path.get(path.size() - 1).longitude - latLng.longitude <= 0.004){
                        path.remove(path.size() - 1);
                        path.add(latLng);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Big Distances between Points", Toast.LENGTH_SHORT).show();
                    }
                    polygon4.setPoints(path);
                    Log.d("path + ", path.toString());
                }
            }
        });
    }

    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, "Map not ready", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /** Called when the Clear button is clicked. */
    public void onClearMap(View view) {
        if (!checkReady()) {
            return;
        }
        //mMap.clear();
        deletePolygon = true;
    }

    /** Called when the draw button is clicked. */
    public void drawAnother(View view) {
        if (!checkReady()) {
            return;
        }
        startAnother = true;
    }

    /** Called when the send button is clicked. */
    public void send(View view) {
        if (!checkReady()) {
            return;
        }
        Intent mainIntent = new Intent().setClass(MapsActivity.this, SubstationActivity.class);
        mainIntent.putExtra("latitude", latitude);
        mainIntent.putExtra("longitude", longitude);
        startActivity(mainIntent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        updateMapType();
    }

    private void updateMapType() {
        // No toast because this can also be called by the Android framework in onResume() at which
        // point mMap may not be ready yet.
        if (mMap == null) {
            return;
        }

        String layerName = ((String) mSpinner.getSelectedItem());
        if (layerName.equals(getString(R.string.normal))) {
            mMap.setMapType(MAP_TYPE_NORMAL);
        } else if (layerName.equals(getString(R.string.hybrid))) {
            mMap.setMapType(MAP_TYPE_HYBRID);


        } else if (layerName.equals(getString(R.string.satellite))) {
            mMap.setMapType(MAP_TYPE_SATELLITE);
        } else if (layerName.equals(getString(R.string.terrain))) {
            mMap.setMapType(MAP_TYPE_TERRAIN);
        } else if (layerName.equals(getString(R.string.none_map))) {
            mMap.setMapType(MAP_TYPE_NONE);
        } else {
            Log.i("LDA", "Error setting layer with name " + layerName);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing.
    }
}