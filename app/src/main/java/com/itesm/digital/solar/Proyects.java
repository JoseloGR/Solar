package com.itesm.digital.solar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.itesm.digital.solar.Models.RequestCoordinate;
import com.itesm.digital.solar.Models.RequestProject;
import com.itesm.digital.solar.Models.ResponseCoordinate;
import com.itesm.digital.solar.Models.ResponseProject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Proyects extends AppCompatActivity {

    public SharedPreferences prefs;
    public String ACTIVE_USERNAME = "", ID_USER="",TOKEN="",NAME="",COST="";

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

        initValues();
    }

    private void initValues() {

        prefs = getSharedPreferences("AccessUser", Context.MODE_PRIVATE);
        ACTIVE_USERNAME = prefs.getString("User", null);
        ID_USER = prefs.getString("IdUser", null);
        TOKEN = prefs.getString("Token", null);

        Log.d("TOKEN", TOKEN);

    }

    private void SendDataProject(){

        double ALTITUDE = 2.0;

        RequestCoordinate sendCoordinate = new RequestCoordinate();
        sendCoordinate.setAltitude(ALTITUDE);

        projectRegister.setName(NAME);
        projectRegister.setAddress(ADDRESS);
        projectRegister.setCost(COST);
        projectRegister.setDate(DATE);
        projectRegister.setSurface(SURFACE);
        projectRegister.setUserId(ID_USER);

        Call<ResponseProject> responseRegister = connectInterface.RegisterProject(TOKEN, projectRegister);

        responseRegister.enqueue(new Callback<ResponseProject>() {
            @Override
            public void onResponse(Call<ResponseProject> call, Response<ResponseProject> response) {
                dialog.dismiss();
                int statusCode = response.code();
                ResponseProject responseBody = response.body();
                if (statusCode==201 || statusCode==200){
                    SuccessProject("Proyecto Solar", "Tu proyecto ha sido registrado exitosamente.");
                }
                else{
                    showMessage("Proyecto Solar", "Hubo un problema al crear el proyecto. Contacte al administrador.");
                    Log.d("PROJECT",response.toString());
                }

            }

            @Override
            public void onFailure(Call<ResponseProject> call, Throwable t) {
                dialog.dismiss();
                Log.d("OnFail", t.getMessage());
                showMessage("Error en la comunicación", "No es posible conectar con el servidor. Intente de nuevo por favor");
            }
        });


    }
}
