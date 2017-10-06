package com.itesm.digital.solar;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.itesm.digital.solar.Interfaces.RequestInterface;
import com.itesm.digital.solar.Models.RequestProject;
import com.itesm.digital.solar.Models.ResponseProject;
import com.itesm.digital.solar.Utils.GlobalVariables;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NONE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN;

public class SelectStartPointDron extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    Marker subStationMarker;
    private int counterMarkers = 0;

    private Spinner mSpinner;

    FloatingActionButton fab;

    MaterialDialog.Builder builder;
    MaterialDialog dialog;

    public SharedPreferences prefs;
    public String ACTIVE_USERNAME = "", ID_USER="",TOKEN="",NAME="",COST="",ADDRESS="Complemento a la ubicación",DATE="2017-10-03T20:28:07.174Z",SURFACE="30";

    RequestInterface projectInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_select_start_point_dron);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSpinner = (Spinner) findViewById(R.id.layers_spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.layers_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onResetMap();
                Snackbar.make(view, "Puntos borrados", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initUI();
    }

    private void initUI(){
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

        Log.d("TOKEN", TOKEN);

    }

    private LatLng setCenter(){
        double lowestX = 0;
        double lowestY = 0;
        double highestY = 0;
        double highestX = 0;
        double centerX = 0;
        double centerY = 0;

        List<LatLng> first = MapsActivityCurrentPlace.listPolygons.get(0);

        //set the lowest values of X and Y
        for (int i = 0; i < first.size(); i++){
            if (i == 0){
                lowestX = first.get(0).longitude;
                lowestY = first.get(0).latitude;
            }
            if (lowestX > first.get(i).longitude){
                lowestX = first.get(i).longitude;
            }
            if (lowestY > first.get(i).latitude){
                lowestY = first.get(i).latitude;
            }
        }

        //set the highest values of X and Y
        for (int i = 0; i < first.size(); i++){
            if (i == 0){
                highestX = first.get(0).longitude;
                highestY = first.get(0).latitude;
            }
            if (highestX < first.get(i).longitude){
                highestX = first.get(i).longitude;
            }
            if (highestY < first.get(i).latitude){
                highestY = first.get(i).latitude;
            }
        }

        centerX = lowestX + ((highestX - lowestX) / 2);
        centerY = lowestY + ((highestY - lowestY) / 2);

        LatLng center = new LatLng(centerY, centerX);

        Log.d("center ", center.toString());

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 19f));

        return center;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setOnMapLongClickListener(this);

        updateMapType();

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.map), false);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        setCenter();

        // Instantiates a new Polyline object and adds points to define a rectangle
        PolygonOptions rectOptions = new PolygonOptions()
                .add(new LatLng(0, 0),
                        new LatLng(0, 0)).fillColor(Color.rgb(255, 204, 128)).strokeWidth(4);

        // Get back the mutable Polygon
        final Polygon polygon = mMap.addPolygon(rectOptions);
        //Sets the points of this polygon
        polygon.setPoints(MapsActivityCurrentPlace.listPolygons.get(0));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if(counterMarkers == 0) {
            Toast.makeText(SelectStartPointDron.this,
                    "Ubicación de partida:\n" + latLng.latitude + " : " + latLng.longitude,
                    Toast.LENGTH_LONG).show();

            //Add marker
            subStationMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Punto de partida")
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            counterMarkers++;
        }
        else if(counterMarkers == 1) {
            Toast.makeText(SelectStartPointDron.this,
                    "Ubicación de aterrizaje:\n" + latLng.latitude + " : " + latLng.longitude,
                    Toast.LENGTH_LONG).show();

            //Add marker
            subStationMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Punto de aterrizaje")
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            counterMarkers++;
        }
        else{
            Toast.makeText(SelectStartPointDron.this, "You can't add more points",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {}

    @Override
    public void onMarkerDrag(Marker marker) {}

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        updateMapType();
    }

    private void onResetMap() {
        mMap.clear();
    }

    private void updateMapType() {
        // No toast because this can also be called by the Android framework in onResume() at which
        // point mMap may not be ready yet.
        if (mMap == null) {
            return;
        }

        String layerName = ((String) mSpinner.getSelectedItem());
        if (layerName.equals(getString(R.string.normal))) {
            mMap.setMapType(MAP_TYPE_NORMAL);
        } else if (layerName.equals(getString(R.string.hybrid))) {
            mMap.setMapType(MAP_TYPE_HYBRID);


        } else if (layerName.equals(getString(R.string.satellite))) {
            mMap.setMapType(MAP_TYPE_SATELLITE);
        } else if (layerName.equals(getString(R.string.terrain))) {
            mMap.setMapType(MAP_TYPE_TERRAIN);
        } else if (layerName.equals(getString(R.string.none_map))) {
            mMap.setMapType(MAP_TYPE_NONE);
        } else {
            Log.i("LDA", "Error setting layer with name " + layerName);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing.
    }

    private void SendDataProject(){

        OkHttpClient clientOk = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request authed = chain.request()
                                .newBuilder()
                                .addHeader("Authorization","Bearer "+ TOKEN)
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
        projectRegister.setName(Base64.encodeToString(NAME.getBytes(), Base64.NO_WRAP));
        projectRegister.setAddress(Base64.encodeToString(ADDRESS.getBytes(), Base64.NO_WRAP));
        projectRegister.setCost(Base64.encodeToString(COST.getBytes(), Base64.NO_WRAP));//Integer.valueOf(COST));
        projectRegister.setDate(Base64.encodeToString(DATE.getBytes(), Base64.NO_WRAP));
        projectRegister.setSurface(Base64.encodeToString(SURFACE.getBytes(), Base64.NO_WRAP));//locationSE.toString());
        //Base64.encodeToString(NAME.getBytes(), Base64.NO_WRAP)

        Call<ResponseProject> responseRegister = projectInterface.RegisterProject(projectRegister);

        responseRegister.enqueue(new Callback<ResponseProject>() {
            @Override
            public void onResponse(Call<ResponseProject> call, Response<ResponseProject> response) {
                dialog.dismiss();
                int statusCode = response.code();
                ResponseProject responseBody = response.body();
                if (statusCode==201 || statusCode==200){
                    showMessage("Proyecto Solar", "Tu proyecto ha sido registrado exitosamente.");
                }
                else{
                    showMessage("Proyecto Solar", "Hubo un problema al crear el proyecto. Contacte al administrador.");
                    Log.d("PROJECT",response.toString());
                }

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
            dialog.show();
            SendDataProject();
        }else{
            showMessage("Error en la comunicación", "Asegúrate de tener conexión a internet");
        }
    }
}
