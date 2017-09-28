package com.itesm.digital.solar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Proyects extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proyects);

        Button btn_projects = (Button) findViewById(R.id.projects);
        Button btn_create = (Button) findViewById(R.id.generate_project);

        /*btn_projects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent().setClass(Proyects.this, Register.class);
                startActivity(mainIntent);
            }
        });*/

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent().setClass(Proyects.this, CreateProject.class);
                startActivity(mainIntent);
            }
        });
    }
}
