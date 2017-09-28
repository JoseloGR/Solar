package com.itesm.digital.solar;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

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

    private GoogleMap mMap;
    private LatLng locationTerrain, locationSE;
    Marker subStationMarker;
    FloatingActionButton fab;
    RequestInterface projectInterface;
    public String TOKEN = "token_dummy";

    MaterialDialog.Builder builder;
    MaterialDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_substation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);

        locationTerrain = new LatLng(19.359611, -99.257616);
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

        OkHttpClient clientOk = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request authed = chain.request()
                                .newBuilder()
                                .addHeader("Authorization","Token "+ TOKEN)
                                .build();
                        return chain.proceed(authed);
                    }
                }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalVariables.API_BASE+GlobalVariables.API_VERSION)
                .addConverterFactory(GsonConverterFactory.create())
                .client(clientOk)
                .build();

        projectInterface = retrofit.create(RequestInterface.class);

        RequestProject projectRegister = new RequestProject();
        projectRegister.setName("Proyecto Dummy");
        projectRegister.setAddress("Dirección complementaria");
        projectRegister.setCost("10");
        projectRegister.setDate("2017-09-28T17:14:26.378Z");
        projectRegister.setSurface(locationSE.toString());

        Call<ResponseProject> responseRegister = projectInterface.RegisterProject(projectRegister);

        responseRegister.enqueue(new Callback<ResponseProject>() {
            @Override
            public void onResponse(Call<ResponseProject> call, Response<ResponseProject> response) {
                dialog.dismiss();
                int statusCode = response.code();
                ResponseProject responseBody = response.body();
                if (statusCode==201){
                    showMessage("Proyecto Solar", "Tu proyecto ha sido registrado exitosamente.");
                }
                else
                    showMessage("Proyecto Solar", "Hubo un problema al crear el proyecto. Contacte al administrador.");

            }

            @Override
            public void onFailure(Call<ResponseProject> call, Throwable t) {
                dialog.dismiss();
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
            SendDataProject();
        }else{
            showMessage("Error en la comunicación", "Asegúrate de tener conexión a internet");
        }
    }

}
