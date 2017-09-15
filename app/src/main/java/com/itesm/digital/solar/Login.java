package com.itesm.digital.solar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    private EditText user, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user = (EditText) findViewById(R.id.user_text);
        password = (EditText) findViewById(R.id.password_text);

        Button btn_access = (Button) findViewById(R.id.login_button);
        Button btn_register = (Button) findViewById(R.id.register_button);

        btn_access.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.toString() == "hola" && password.toString() == "hola") {
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
                Intent mainIntent = new Intent().setClass(Login.this, Register.class);
                startActivity(mainIntent);
            }
        });
    }
}
