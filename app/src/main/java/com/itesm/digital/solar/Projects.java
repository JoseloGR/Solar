package com.itesm.digital.solar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.itesm.digital.solar.Interfaces.RecyclerViewClickListener;
import com.itesm.digital.solar.Interfaces.RequestInterface;
import com.itesm.digital.solar.Models.DataAdapterProjects;
import com.itesm.digital.solar.Models.Project;
import com.itesm.digital.solar.Models.ResponseAllProjects;
import com.itesm.digital.solar.Models.SolarProject;
import com.itesm.digital.solar.Utils.GlobalVariables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static dji.midware.data.manager.P3.ServiceManager.getContext;

public class Projects extends AppCompatActivity {

    public String ACTIVE_USERNAME,ID_USER,TOKEN;

    private RecyclerView recyclerView;
    private ArrayList<Project> data;
    private DataAdapterProjects adapter;
    TextView msg;
    public SharedPreferences prefs;
    RecyclerViewClickListener listener;

    Retrofit.Builder builderR = new Retrofit.Builder()
            .baseUrl(GlobalVariables.API_BASE+GlobalVariables.API_VERSION)
            .addConverterFactory(GsonConverterFactory.create());

    Retrofit retrofit = builderR.build();

    RequestInterface connectInterface = retrofit.create(RequestInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Projects.this, Proyects.class);
                startActivity(intent);
                finish();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initValues();

    }

    private void initValues(){
        prefs = getSharedPreferences("AccessUser", Context.MODE_PRIVATE);
        ACTIVE_USERNAME = prefs.getString("User", null);
        ID_USER = prefs.getString("IdUser", null);
        TOKEN = prefs.getString("Token", null);

        //Initializing Views
        msg = (TextView) findViewById(R.id.msg_records);

        initViews();
    }

    private void initViews(){
        recyclerView = (RecyclerView)findViewById(R.id.recycler_history);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        loadDataProjects();
    }

    private void loadDataProjects(){

        Call<List<Project>> responseProjects = connectInterface.GetAllProjects(TOKEN,ID_USER);

        responseProjects.enqueue(new Callback<List<Project>>() {
            @Override
            public void onResponse(Call<List<Project>> call, Response<List<Project>> response) {
                //dialog.dismiss();
                int statusCode = response.code();

                if (statusCode==200){
                    msg.setVisibility(View.GONE);
                    List<Project> jsonResponse = response.body();
                    data = new ArrayList<>(jsonResponse);
                    adapter = new DataAdapterProjects(data, getApplicationContext());
                    recyclerView.setAdapter(adapter);

                }
                else{
                    Log.d("PROJECT",response.toString());
                    //msg.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<List<Project>> call, Throwable t) {
                Log.d("OnFail", t.getMessage());
                msg.setVisibility(View.VISIBLE);
            }
        });

    }

}
