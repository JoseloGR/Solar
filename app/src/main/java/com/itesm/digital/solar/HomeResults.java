package com.itesm.digital.solar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.itesm.digital.solar.Models.Project;

public class HomeResults extends AppCompatActivity {

    String ID_PROJECT="", ID_AREA="1", TOKEN="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_results);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                Intent intent = new Intent(HomeResults.this, ConnectionActivity.class);
                intent.putExtra("ID_PROJECT", ID_PROJECT);
                intent.putExtra("ID_AREA", ID_AREA);
                intent.putExtra("TOKEN", TOKEN);
                startActivity(intent);
                finish();
            }
        });

        ID_PROJECT = getIntent().getExtras().getString("ID_PROJECT");
        ID_AREA = getIntent().getExtras().getString("ID_AREA");
        TOKEN = getIntent().getExtras().getString("TOKEN");
        SetActiveProject(ID_PROJECT);
    }

    public void SetActiveProject(String ID_P){
        SharedPreferences project = getSharedPreferences("ActiveProject", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = project.edit();

        editor.putString("ID_PROJECT", ID_P);
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
        startActivity(intent);
        finish();

    }
}
