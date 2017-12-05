package com.itesm.digital.solar;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.itesm.digital.solar.Interfaces.RequestInterface;
import com.itesm.digital.solar.Utils.GlobalVariables;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResultsWithObstacles extends AppCompatActivity {

    private XYPlot plot;
    public String ID_AREA, ID_PROJECT, TOKEN;

    Retrofit.Builder builderR = new Retrofit.Builder()
            .baseUrl(GlobalVariables.API_BASE+GlobalVariables.API_VERSION)
            .addConverterFactory(GsonConverterFactory.create());

    Retrofit retrofit = builderR.build();

    RequestInterface connectInterface = retrofit.create(RequestInterface.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_without_obstacles);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                ID_AREA = "";
                TOKEN = "";
            } else {
                ID_AREA = extras.getString("ID_AREA");
                TOKEN = extras.getString("TOKEN");
            }
        } else {
            ID_AREA = (String) savedInstanceState.getSerializable("ID_AREA");
            TOKEN = (String) savedInstanceState.getSerializable("TOKEN");
        }

        Log.d("AREAA", ID_AREA);
        Log.d("TOKEN", TOKEN);


        TextView roi = (TextView) findViewById(R.id.roi);
        TextView payback = (TextView) findViewById(R.id.payback);
        TextView earnings = (TextView) findViewById(R.id.earnings);
        TextView totalCost = (TextView) findViewById(R.id.total_cost);

        totalCost.setText("Total Cost xxx");

        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.plot);

        // create a couple arrays of y-values to plot:
        final Number[] domainLabels = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
        Number[] series1Numbers = {0, 5150614.87,5100609.03,5051088.71,5002048.86,4953485.14,4905393.00,4857767.61,4810604.78,4763900.05,4717648.70,4671846.16,4626488.39,4581571.02,4537089.81,4493040.55,4449418.75,4406220.67,4363441.91,4321078.41,4279126.12};
        //Number[] series2Numbers = {5, 2, 10, 5, 20, 10, 40, 20, 80, 40};

        // turn the above arrays into XYSeries':
        // (Y_VALS_ONLY means use the element index as the x value)
        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        /*XYSeries series2 = new SimpleXYSeries(
                Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");*/

        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
        LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.RED, Color.GREEN, Color.BLUE, null);

        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        /*series2Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));*/

        // add a new series' to the xyplot:
        plot.addSeries(series1, series1Format);
        //plot.addSeries(series2, series2Format);

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(domainLabels[i]);
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });
    }

}
