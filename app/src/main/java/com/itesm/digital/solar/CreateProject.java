package com.itesm.digital.solar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateProject extends AppCompatActivity {

    private EditText name;   //guarda el nombre del proyecto
    private EditText address;  //guarda la dirección del cliente

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        name = (EditText) findViewById(R.id.name_project);
        address = (EditText) findViewById(R.id.cost_kwh_project);

        Button btn_create = (Button) findViewById(R.id.create_project); //relaciona el objeto con el boton de create

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lleva a la pantalla de registro

                SharedPreferences tokenUser = getSharedPreferences("AccessUser", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = tokenUser.edit();

                editor.putString("Name", name.getText().toString());
                editor.putString("Cost", address.getText().toString());
                editor.apply();

                Intent mainIntent = new Intent().setClass(CreateProject.this, ChooseAddress.class);
                startActivity(mainIntent);
            }
        });
    }
}
