package com.itesm.digital.solar;

import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.itesm.digital.solar.Interfaces.RecyclerViewClickListener;
import com.itesm.digital.solar.Interfaces.RequestInterface;
import com.itesm.digital.solar.Models.Alternatives;
import com.itesm.digital.solar.Models.DataAdapterAlternatives;
import com.itesm.digital.solar.Models.RequestCreateAlternatives;
import com.itesm.digital.solar.Models.ResponseCreateAlternatives;
import com.itesm.digital.solar.Utils.GlobalVariables;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResultsWithObstacles extends AppCompatActivity implements RecyclerViewClickListener {

    SlidingUpPanelLayout slidingUpPanelLayout;
    public String ID_AREA, ID_PROJECT, TOKEN;
    TextView roi, payback, earnings, totalCost;

    MaterialDialog.Builder builder;
    MaterialDialog dialog;

    private RecyclerView recyclerView;
    private ArrayList<Alternatives> data;
    private DataAdapterAlternatives adapter;

    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS).build();

    Retrofit.Builder builderR = new Retrofit.Builder()
            .baseUrl(GlobalVariables.API_BASE+GlobalVariables.API_VERSION)
            .client(client)
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

        ButterKnife.bind(this);

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
                ID_PROJECT = "";
            } else {
                ID_AREA = extras.getString("ID_AREA");
                TOKEN = extras.getString("TOKEN");
                ID_PROJECT = extras.getString("ID_PROJECT");
            }
        } else {
            ID_AREA = (String) savedInstanceState.getSerializable("ID_AREA");
            TOKEN = (String) savedInstanceState.getSerializable("TOKEN");
            ID_PROJECT = (String) savedInstanceState.getSerializable("ID_PROJECT");
        }

        Log.d("AREAA", ID_AREA);
        Log.d("TOKEN", TOKEN);
        Log.d("PROJECT", ID_PROJECT);


        roi = (TextView) findViewById(R.id.roi);
        payback = (TextView) findViewById(R.id.payback);
        earnings = (TextView) findViewById(R.id.earnings);
        totalCost = (TextView) findViewById(R.id.total_cost);

        builder = new MaterialDialog.Builder(this)
                .title(R.string.processing)
                .content(R.string.please_wait_pro)
                .progress(true, 0);

        dialog = builder.build();
        initViews();
    }

    private void initViews(){
        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_alternatives);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        showProgress(true);
        GenerateResults();
    }

    private void GenerateResults(){

        RequestCreateAlternatives requestProject = new RequestCreateAlternatives();
        requestProject.setAreaID(Integer.parseInt(ID_AREA));
        Call<ResponseCreateAlternatives> responseProjects = connectInterface.CreateAlternative(TOKEN,requestProject);

        responseProjects.enqueue(new Callback<ResponseCreateAlternatives>() {
            @Override
            public void onResponse(Call<ResponseCreateAlternatives> call, Response<ResponseCreateAlternatives> response) {
                int statusCode = response.code();
                Log.d("CREATE ALT", response.toString());

                if (statusCode==200){
                    ResponseCreateAlternatives jsonResponse = response.body();
                    Log.d("STATUS", jsonResponse.toString());

                    loadDataAlternatives();
                }
                else{
                    Log.d("PROJECT",response.toString());
                }

            }

            @Override
            public void onFailure(Call<ResponseCreateAlternatives> call, Throwable t) {
                Log.d("OnFail", t.getMessage());
                showProgress(false);
            }
        });

    }

    private void loadDataAlternatives(){

        Call<List<List<Alternatives>>> responseAlternatives = connectInterface.GetAlternatives(TOKEN,ID_PROJECT);

        responseAlternatives.enqueue(new Callback<List<List<Alternatives>>>() {
            @Override
            public void onResponse(Call<List<List<Alternatives>>> call, Response<List<List<Alternatives>>> response) {
                int statusCode = response.code();
                showProgress(false);
                if (statusCode==200){
                    List<List<Alternatives>> jsonResponse = response.body();
                    data = new ArrayList<>(jsonResponse.get(0));
                    Log.d("ALL ALT", data.toString());

                    updateAdapter();

                }
                else{
                    Log.d("PROJECT",response.toString());
                }

            }

            @Override
            public void onFailure(Call<List<List<Alternatives>>> call, Throwable t) {
                Log.d("OnFail", t.getMessage());
                showProgress(false);
            }
        });

    }

    private void showProgress(boolean show) {
        if (show)
            dialog.show();
        else
            dialog.dismiss();
    }

    private void updateAdapter(){
        adapter = new DataAdapterAlternatives(data, getApplicationContext());
        recyclerView.setAdapter(adapter);
        adapter.setClickListener(this);
    }

    @Override
    public void onClick(View view, int position) {
        SetAlternative(data.get(position));
    }

    public void SetAlternative(Alternatives alternative){
        Log.d("Data", alternative.getSavings().toString());

        roi.setText(alternative.getRoi());
        payback.setText(alternative.getPayback());
        earnings.setText(alternative.getGanancias());
        totalCost.setText(alternative.getCostoInstalacion());
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }
}
