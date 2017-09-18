package com.itesm.digital.solar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        address = (EditText) findViewById(R.id.address_client);

        Button btn_create = (Button) findViewById(R.id.create_project); //relaciona el objeto con el boton de create


    }
}
