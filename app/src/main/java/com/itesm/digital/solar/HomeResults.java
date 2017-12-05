package com.itesm.digital.solar;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class HomeResults extends AppCompatActivity {

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
                ClearActiveProject();
                finish();
            }
        });

        String ID_PROJECT = getIntent().getExtras().getString("ID_PROJECT");
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
}
