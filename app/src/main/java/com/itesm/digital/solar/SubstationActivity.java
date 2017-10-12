package com.itesm.digital.solar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.itesm.digital.solar.Interfaces.RequestInterface;
import com.itesm.digital.solar.Models.RequestProject;
import com.itesm.digital.solar.Models.ResponseProject;
import com.itesm.digital.solar.Utils.GlobalVariables;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SubstationActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMarkerClickListener {

    private double latitude;
    private double longitude;

    private GoogleMap mMap;
    private LatLng locationTerrain, locationSE;
    Marker subStationMarker;
    FloatingActionButton fab;
    RequestInterface projectInterface;

    MaterialDialog.Builder builder;
    MaterialDialog dialog;

    public SharedPreferences prefs;
    public String ACTIVE_USERNAME = "", ID_USER="",TOKEN="",NAME="",COST="",ADDRESS="Complemento a la ubicación",DATE="2017-10-10T17:45:13.106Z",SURFACE="30";
    public int COST_VALUE=10, AREA_VALUE=20;

    Retrofit.Builder builderR = new Retrofit.Builder()
            .baseUrl(GlobalVariables.API_BASE+GlobalVariables.API_VERSION)
            .addConverterFactory(GsonConverterFactory.create());

    Retrofit retrofit = builderR.build();

    RequestInterface connectInterface = retrofit.create(RequestInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_substation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                latitude = 0;
                longitude = 0;
            } else {
                latitude = extras.getDouble("latitude");
                longitude = extras.getDouble("longitude");
            }
        } else {
            latitude = (double) savedInstanceState.getSerializable("latitude");
            longitude = (double) savedInstanceState.getSerializable("longitude");
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onResetMap();
                Snackbar.make(view, "Subestaciones borradas", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initUI();
    }

    private void initUI(){
        locationSE = new LatLng(0, 0);

        builder = new MaterialDialog.Builder(this)
                .title("Solar")
                .content("Por favor espere un momento...")
                .progress(true, 0);

        dialog = builder.build();

        prefs = getSharedPreferences("AccessUser", Context.MODE_PRIVATE);
        ACTIVE_USERNAME = prefs.getString("User", null);
        ID_USER = prefs.getString("IdUser", null);
        TOKEN = prefs.getString("Token", null);
        NAME = prefs.getString("Name", null);
        COST = prefs.getString("Cost", null);

        Log.d("TOKEN SUB", TOKEN);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);

        locationTerrain = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions()
                .position(locationTerrain)
                .title("Ubicación área")
                .snippet("Área seleccionada a evaluar"));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(locationTerrain)    // Sets the center of the map
                .zoom(16)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Toast.makeText(SubstationActivity.this,
                "Ubicación:\n" + latLng.latitude + " : " + latLng.longitude,
                Toast.LENGTH_LONG).show();

        if(locationSE.latitude==0) {
            //Add marker
            subStationMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Subestación eléctrica")
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            locationSE = new LatLng(latLng.latitude,latLng.longitude);
        }


    }

    @Override
    public void onMarkerDragStart(Marker marker) {}

    @Override
    public void onMarkerDrag(Marker marker) {}

    @Override
    public void onMarkerDragEnd(Marker marker) {
        locationSE = marker.getPosition();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        if (marker.getPosition()==locationSE){
            fab.setVisibility(View.VISIBLE);
        }

        return false;
    }

    private void onResetMap() {
        locationSE = new LatLng(0, 0);
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(locationTerrain)
                .title("Ubicación área")
                .snippet("Área seleccionada a evaluar"));
    }

    private void SendDataProject(){

        RequestProject projectRegister = new RequestProject();
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

    public boolean isOnline () {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

    private void showMessage(String title, String message){

        if(message.isEmpty())
            message = "Tuvimos un problema con la conexión, inténtalo de nuevo por favor";
        new MaterialDialog.Builder(this)
                .title(title)
                .content(message)
                .positiveText("Ok")
                .show();
    }

    public void uploadProject(View v){

        if(isOnline()){
            dialog.show();
            SendDataProject();
        }else{
            showMessage("Error en la comunicación", "Asegúrate de tener conexión a internet");
        }
    }

    public void SuccessProject(String title, String message){
        new MaterialDialog.Builder(this)
                .title(title)
                .content(message)
                .positiveText("Ok")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(SubstationActivity.this, Proyects.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }
}
