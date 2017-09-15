package com.itesm.digital.solar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends AppCompatActivity {

    private EditText name, lastname, mail, password, repassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = (EditText) findViewById(R.id.name_user);
        lastname = (EditText) findViewById(R.id.lastname_user);
        mail = (EditText) findViewById(R.id.mail_user);
        password = (EditText) findViewById(R.id.new_password_user);
        repassword = (EditText) findViewById(R.id.repassword_user);

        Button btn_add = (Button) findViewById(R.id.add_user);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password.toString() == repassword.toString()) {
                    Toast.makeText(getApplicationContext(), "Check your mail", Toast.LENGTH_SHORT).show();
                    Intent mainIntent = new Intent().setClass(Register.this, Login.class);
                    startActivity(mainIntent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Password doesn't match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
