package com.itesm.digital.solar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Proyects extends AppCompatActivity {

    public SharedPreferences prefs;
    public String ACTIVE_USERNAME = "", ID_USER="",TOKEN="",NAME="",COST="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proyects);

        ejecutar();

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

        initValues();
    }

    private void initValues() {

        prefs = getSharedPreferences("AccessUser", Context.MODE_PRIVATE);
        ACTIVE_USERNAME = prefs.getString("User", null);
        ID_USER = prefs.getString("IdUser", null);
        TOKEN = prefs.getString("Token", null);

        Log.d("TOKEN", TOKEN);

    }

    public void hilo(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void ejecutar(){
        time time = new time();
        time.execute();

    }

    public class time extends AsyncTask<Void,Integer,Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            for(int i=0;i<3;i++){
                hilo();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            Toast.makeText(Proyects.this,"hola",Toast.LENGTH_SHORT).show();
            ejecutar();
        }
    }
}
