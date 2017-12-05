package com.itesm.digital.solar;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.itesm.digital.solar.Utils.Validations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Coordinates extends AppCompatActivity {

    public static List<LatLng> prefav = new ArrayList<LatLng>();
    private double latiD;
    private double longiD;
    EditText lat;
    EditText lon;
    private String latitude;
    private String longitude;
    private String last;
    int index = 0;
    private String items;
    private boolean firstVertex = true;
    ArrayAdapter<String> adapter;
    ArrayList<String> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinates);
        itemList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.txtview, itemList);

        ListView listV = (ListView) findViewById(R.id.list);
        listV.setAdapter(adapter);
        Button btn_add_new = (Button) findViewById(R.id.add_new);
        Button btn_send = (Button) findViewById(R.id.send);
        lat = (EditText) findViewById(R.id.latitude);
        lon = (EditText) findViewById(R.id.longitude);

        btn_add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addVertex();
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createArea();
            }
        });

    }

    private void addVertex(){

        latitude = lat.getText().toString();
        longitude = lon.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(latitude)) {
            lat.setError(getString(R.string.error_emp_lat));
            focusView = lat;
            cancel = true;
        }else if(TextUtils.isEmpty(longitude)){
            lon.setError(getString(R.string.error_emp_lon));
            focusView = lon;
            cancel = true;
        }else {
            latiD = Double.parseDouble(latitude);
            latitude = String.format("%.6f", latiD);
            latiD = Double.parseDouble(latitude);
            longiD = Double.parseDouble(longitude);
            longitude = String.format("%.6f", longiD);
            longiD = Double.parseDouble(longitude);
            if(longiD >= -180 && longiD <= 180 && latiD >= -90 && latiD <= 90){
                if(firstVertex){
                    LatLng coordinate = new LatLng(latiD, longiD);
                    prefav.add(coordinate);
                    index++;
                    last = (index + ". Lat: " + latitude + " Lon: " + longitude);
                    // add new item to arraylist
                    itemList.add(last);
                    // notify listview of data changed
                    adapter.notifyDataSetChanged();
                    lat.setText("");
                    lon.setText("");
                    firstVertex = false;
                }
                else if(prefav.get(prefav.size() - 1).latitude - latiD >= -0.001 &&
                        prefav.get(prefav.size() - 1).latitude - latiD <= 0.001 &&
                        prefav.get(prefav.size() - 1).longitude - longiD >= -0.001 &&
                        prefav.get(prefav.size() - 1).longitude - longiD <= 0.001){
                    LatLng coordinate = new LatLng(latiD, longiD);
                    prefav.add(coordinate);
                    index++;
                    last = (index + ". Lat: " + latitude + " Lon: " + longitude);
                    // add new item to arraylist
                    itemList.add(last);
                    // notify listview of data changed
                    adapter.notifyDataSetChanged();
                    lat.setText("");
                    lon.setText("");
                }
                else{
                    Toast.makeText(this, "Distances too large between points", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "Values out of the range", Toast.LENGTH_SHORT).show();
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }

    }

    private void createArea(){
        if(prefav.size() >= 3){
            MapsActivityCurrentPlace.listPolygons.add(prefav);
            Intent mainIntent = new Intent().setClass(Coordinates.this, AreaCoordinates.class);
            startActivity(mainIntent);
        }
        else{
            Toast.makeText(this, "You need more amount of vertices", Toast.LENGTH_SHORT).show();
        }
    }
}
