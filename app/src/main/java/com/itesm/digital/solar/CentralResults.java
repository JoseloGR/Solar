package com.itesm.digital.solar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;

public class CentralResults extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.central_results);

        String ID_PROJECT = getIntent().getExtras().getString("ID_PROJECT");

        Button btn_area_with_obstacles = (Button) findViewById(R.id.btn_area_with_obstacles);  //relaciona el objeto con el boton
        Button btn_area_without_obstacles = (Button) findViewById(R.id.btn_area_without_obstacles);
        Button btn_alternatives = (Button) findViewById(R.id.btn_alternatives);

        btn_area_with_obstacles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent().setClass(CentralResults.this, ResultsWithObstacles.class);
                startActivity(mainIntent);
            }
        });

        btn_area_without_obstacles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent().setClass(CentralResults.this, ResultsWithoutObstacles.class);
                startActivity(mainIntent);
            }
        });

        Log.d("Project Selected", ID_PROJECT);
    }

}
