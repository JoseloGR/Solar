package com.itesm.digital.solar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.itesm.digital.solar.Interfaces.RequestInterface;
import com.itesm.digital.solar.Models.Project;
import com.itesm.digital.solar.Models.ResponseArea;
import com.itesm.digital.solar.Models.ResponseDataArea;
import com.itesm.digital.solar.Models.ResponseProject;
import com.itesm.digital.solar.Utils.GlobalVariables;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeResults extends AppCompatActivity {

    String ID_PROJECT="", ID_AREA="", TOKEN="", NOMBRE="",ID_USER="";
    Toolbar toolbar;
    public SharedPreferences prefs;

    Retrofit.Builder builderR = new Retrofit.Builder()
            .baseUrl(GlobalVariables.API_BASE+GlobalVariables.API_VERSION)
            .addConverterFactory(GsonConverterFactory.create());

    Retrofit retrofit = builderR.build();

    RequestInterface connectInterface = retrofit.create(RequestInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_results);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayShowTitleEnabled(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backFlow();
            }
        });

        CardView cardDrone = (CardView) findViewById(R.id.dronecardId);
        cardDrone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StartConnectionDrone();
            }
        });

        CardView cardROI = (CardView) findViewById(R.id.roicardId);
        cardROI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartROIProcess();
            }
        });

        ID_PROJECT = getIntent().getExtras().getString("ID_PROJECT");
        ID_AREA = getIntent().getExtras().getString("ID_AREA");
        //TOKEN = getIntent().getExtras().getString("TOKEN");
        SetActiveProject(ID_PROJECT, ID_AREA);

        initValues();
    }

    private void initValues(){
        prefs = getSharedPreferences("AccessUser", Context.MODE_PRIVATE);
        ID_USER = prefs.getString("IdUser", null);
        TOKEN = prefs.getString("Token", null);

        loadDataAreaProject();
        loadDataProject();
    }

    public void SetActiveProject(String ID_P, String ID_A){
        SharedPreferences project = getSharedPreferences("ActiveProject", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = project.edit();

        editor.putString("ID_PROJECT", ID_P);
        editor.putString("ID_AREA", ID_A);
        editor.apply();
    }

    public void ClearActiveProject(){
        SharedPreferences project = getSharedPreferences("ActiveProject", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = project.edit();
        editor.clear();
        editor.commit();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        backFlow();
    }

    public void backFlow(){
        ClearActiveProject();
        Intent intent = new Intent(HomeResults.this, Projects.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

    private void loadDataAreaProject(){

        Call<List<ResponseDataArea>> responseProjects = connectInterface.GetAreaProject(TOKEN,ID_PROJECT);

        responseProjects.enqueue(new Callback<List<ResponseDataArea>>() {
            @Override
            public void onResponse(Call<List<ResponseDataArea>> call, Response<List<ResponseDataArea>> response) {
                int statusCode = response.code();

                Log.d("SUCCESS AREA", response.toString());

                if (statusCode==200){
                    List<ResponseDataArea> jsonResponse = response.body();

                    ArrayList<ResponseDataArea> data = new ArrayList<>(jsonResponse);

                    SharedPreferences project = getSharedPreferences("ActiveProject", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = project.edit();

                    if(data.size()!=0){
                        editor.putString("ID_AREA", data.get(0).getId().toString());
                    }

                    editor.apply();
                }
                else{
                    Log.d("PROJECT",response.toString());
                }

            }

            @Override
            public void onFailure(Call<List<ResponseDataArea>> call, Throwable t) {
                Log.d("OnFail", t.getMessage());
            }
        });

    }

    private void loadDataProject(){

        Call<ResponseProject> responseProjects = connectInterface.GetDataProject(TOKEN,ID_PROJECT);

        responseProjects.enqueue(new Callback<ResponseProject>() {
            @Override
            public void onResponse(Call<ResponseProject> call, Response<ResponseProject> response) {
                int statusCode = response.code();

                Log.d("SUCCESS PROJECT", response.toString());

                if (statusCode==200){
                    ResponseProject jsonResponse = response.body();

                    SharedPreferences project = getSharedPreferences("ActiveProject", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = project.edit();
                    editor.putString("NOMBRE", jsonResponse.getName().toString());
                    editor.apply();

                    NOMBRE = jsonResponse.getName().toString();

                    //changeNameToolbar(jsonResponse.getName().toString());
                }
                else{
                    Log.d("PROJECT",response.toString());
                }

            }

            @Override
            public void onFailure(Call<ResponseProject> call, Throwable t) {
                Log.d("OnFail", t.getMessage());
            }
        });

    }

    public void StartConnectionDrone(){
        SharedPreferences prefs = getSharedPreferences("AccessUser", Context.MODE_PRIVATE);
        TOKEN = prefs.getString("Token", null);

        SharedPreferences prefsProject = getSharedPreferences("ActiveProject", Context.MODE_PRIVATE);
        ID_AREA = prefsProject.getString("ID_AREA", null);

        Intent intent = new Intent(HomeResults.this, MainDrone.class);
        intent.putExtra("ID_PROJECT", ID_PROJECT);
        intent.putExtra("ID_AREA", ID_AREA);
        intent.putExtra("TOKEN", TOKEN);
        startActivity(intent);
        //finish();
    }

    private void changeNameToolbar(String NOMBRE){
        getSupportActionBar().setTitle(NOMBRE);
    }

    private void StartROIProcess(){

        SharedPreferences prefs = getSharedPreferences("AccessUser", Context.MODE_PRIVATE);
        TOKEN = prefs.getString("Token", null);

        SharedPreferences prefsProject = getSharedPreferences("ActiveProject", Context.MODE_PRIVATE);
        ID_AREA = prefsProject.getString("ID_AREA", null);

        Intent intent = new Intent(HomeResults.this, ResultsWithObstacles.class);
        intent.putExtra("ID_PROJECT", ID_PROJECT);
        intent.putExtra("ID_AREA", ID_AREA);
        intent.putExtra("TOKEN", TOKEN);
        startActivity(intent);

    }
}
