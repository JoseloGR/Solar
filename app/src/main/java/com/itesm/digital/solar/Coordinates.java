package com.itesm.digital.solar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Coordinates extends AppCompatActivity {


    public static List<LatLng> prefav = new ArrayList<LatLng>();
    private double latiD;
    private double longiD;
    EditText lat;
    EditText lon;
    TextView last;
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinates);

        Button btn_add_new = (Button) findViewById(R.id.add_new);  //relaciona el objeto con el boton
        Button btn_send = (Button) findViewById(R.id.send);
        Button btn_delete = (Button) findViewById(R.id.delete_coordinate);

        lat = (EditText) findViewById(R.id.latitude);
        lon = (EditText) findViewById(R.id.longitude);
        last = (TextView) findViewById(R.id.last_cordinates);

        btn_add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addVertex();
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent().setClass(Coordinates.this, Coordinates.class);
                startActivity(mainIntent);
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCoordinate();
            }
        });

    }

    private void addVertex(){
        latiD = Double.parseDouble(lat.getText().toString());
        longiD = Double.parseDouble(lon.getText().toString());
        LatLng coordinate = new LatLng(latiD, longiD);
        prefav.add(coordinate);
        last.setText(lat.getText().toString() + ", " + lon.getText().toString());
        lat.setText("");
        lon.setText("");
        index++;
    }

    private void deleteCoordinate(){
        prefav.remove(index-1);
        last.setText("");
        lat.setText("");
        lon.setText("");
        index--;
        Toast.makeText(Coordinates.this,"Coordinate deleted",Toast.LENGTH_SHORT).show();
    }
}
