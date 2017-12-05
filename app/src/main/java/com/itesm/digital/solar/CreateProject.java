package com.itesm.digital.solar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
            attemptCreate();
            }
        });
    }

    private void attemptCreate() {

        // Reset errors.
        name.setError(null);
        address.setError(null);

        // Store values at the time of the login attempt.
        String nameValue = name.getText().toString();
        String costValue = address.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid project name
        if (TextUtils.isEmpty(nameValue)) {
            name.setError(getString(R.string.error_field_required));
            focusView = name;
            cancel = true;
        }

        // Check for a valid kW/h cost
        if (TextUtils.isEmpty(costValue)) {
            address.setError(getString(R.string.error_field_required));
            focusView = address;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            ContinueProcess();

        }
    }

    public void ContinueProcess(){

        SharedPreferences tokenUser = getSharedPreferences("AccessUser", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = tokenUser.edit();

        editor.putString("Name", name.getText().toString());
        editor.putString("Cost", address.getText().toString());
        editor.apply();

        Intent mainIntent = new Intent().setClass(CreateProject.this, ChooseAddress.class);
        startActivity(mainIntent);

    }
}
