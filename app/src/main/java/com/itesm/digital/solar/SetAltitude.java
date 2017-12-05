package com.itesm.digital.solar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetAltitude extends AppCompatActivity {

    EditText altitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_altitude);

        altitude = (EditText) findViewById(R.id.altitude);
        Button setAltitude = (Button) findViewById(R.id.set_altitude);

        setAltitude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });
    }

    void validation(){
        float height = Float.valueOf(altitude.getText().toString());
        double feets;

        if(height < 5){
            Toast.makeText(this, "Altitude slow", Toast.LENGTH_SHORT).show();
        }
        else if(height > 45){
            Toast.makeText(this, "Altitude high", Toast.LENGTH_SHORT).show();
        }
        else{
            feets = height * 3.28084;
            SharedPreferences altitudeDrone = getSharedPreferences("altitudeDrone", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = altitudeDrone.edit();

            editor.putString("Altitude", String.valueOf(feets));
            editor.apply();

            Intent intent = new Intent(SetAltitude.this, MapsActivityCurrentPlace.class);
            startActivity(intent);
        }
    }
}
