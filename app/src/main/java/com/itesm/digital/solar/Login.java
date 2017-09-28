package com.itesm.digital.solar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    private EditText user;    //guarda el usuario a ingresar
    private EditText password;  //guarda la contraseña introducida por el usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user = (EditText) findViewById(R.id.user_text);  //recibe lo que el usuario ingreso
        password = (EditText) findViewById(R.id.password_text); //recibe lo que el usuario ingreso

        Button btn_access = (Button) findViewById(R.id.login_button);  //relaciona el objeto con el boton
        Button btn_register = (Button) findViewById(R.id.register_button);

        btn_access.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {    //activa un supervisor para quién aprete el boton
                //usuario dummy para ingresar
                if (user.getText().toString().equals("hola")  && password.getText().toString().equals("hola")) {
                    Intent mainIntent = new Intent().setClass(Login.this, Proyects.class);
                    startActivity(mainIntent);
                    Toast.makeText(getApplicationContext(), "Access Granted", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Access Denied", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lleva a la pantalla de registro
                Intent mainIntent = new Intent().setClass(Login.this, Register.class);
                startActivity(mainIntent);
            }
        });
    }
}
