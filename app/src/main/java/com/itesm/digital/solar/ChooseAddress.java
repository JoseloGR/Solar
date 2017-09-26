package com.itesm.digital.solar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseAddress extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_address);

        Button btn_current_position = (Button) findViewById(R.id.btn_select_current_position);  //relaciona el objeto con el boton
        Button btn_introduce_address = (Button) findViewById(R.id.btn_introduce_address);
        Button btn_show_general_map = (Button) findViewById(R.id.btn_show_general_map);

        btn_current_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent().setClass(ChooseAddress.this, MapsActivityCurrentPlace.class);
                startActivity(mainIntent);
            }
        });

        btn_show_general_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent().setClass(ChooseAddress.this, MapsActivity.class);
                startActivity(mainIntent);
            }
        });

    }

}
