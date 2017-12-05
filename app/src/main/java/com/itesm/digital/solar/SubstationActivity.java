package com.itesm.digital.solar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
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
import android.widget.Button;
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
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.itesm.digital.solar.Interfaces.RequestInterface;
import com.itesm.digital.solar.Models.Center;
import com.itesm.digital.solar.Models.Position;
import com.itesm.digital.solar.Models.RequestArea;
import com.itesm.digital.solar.Models.RequestCoordinate;
import com.itesm.digital.solar.Models.RequestLimit;
import com.itesm.digital.solar.Models.RequestProject;
import com.itesm.digital.solar.Models.ResponseArea;
import com.itesm.digital.solar.Models.ResponseCoordinate;
import com.itesm.digital.solar.Models.ResponseLimit;
import com.itesm.digital.solar.Models.ResponseProject;
import com.itesm.digital.solar.Utils.GlobalVariables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public static List<LatLng> points = new ArrayList<LatLng>();

    public SharedPreferences prefs;
    public SharedPreferences prefs2;
    public String ACTIVE_USERNAME = "", ID_PROJECT="", ID_AREA="", ID_USER="",TOKEN="",NAME="",COST="",ADDRESS="Hola",DATE="2017-12-05T09:25:13.106Z",SURFACE="30";
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
                Snackbar.make(view, "Deleted Substations", Snackbar.LENGTH_LONG)
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
        latitude = Double.valueOf(prefs.getString("LATITUDE_USER", null));
        longitude = Double.valueOf(prefs.getString("LONGITUDE_USER", null));

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
        //Toast.makeText(SubstationActivity.this, "Ubicación:\n" + latLng.latitude + " : " + latLng.longitude, Toast.LENGTH_LONG).show();

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

        //SphericalUtil.computeArea(MapsActivityCurrentPlace.listPolygons.get(0));

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
                //dialog.dismiss();
                int statusCode = response.code();
                ResponseProject responseBody = response.body();
                if (statusCode==201 || statusCode==200){
                    ID_PROJECT = responseBody.getId().toString();
                    NextStepAreaRegister();
                    //SuccessProject("Proyecto Solar", "Tu proyecto ha sido registrado exitosamente.");
                }
                else{
                    //showMessage("Proyecto Solar", "El proyecto se encuentra generando.");
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

    private void SendDataArea(){

        RequestArea areaRegister = new RequestArea();
        Center center = new Center();
        center.setLat(String.valueOf(latitude));
        center.setLng(String.valueOf(longitude));
        areaRegister.setCenter(center);
        areaRegister.setAzimuth("0");
        areaRegister.setSolarRadiation("0");
        areaRegister.setProjectId(ID_PROJECT);
        areaRegister.setSurface("0");

        Call<ResponseArea> responseArea = connectInterface.RegisterArea(TOKEN, areaRegister, ID_PROJECT);

        responseArea.enqueue(new Callback<ResponseArea>() {
            @Override
            public void onResponse(Call<ResponseArea> call, Response<ResponseArea> response) {
                //dialog.dismiss();
                int statusCode = response.code();
                ResponseArea responseBody = response.body();
                if (statusCode==201 || statusCode==200){
                    ID_AREA = responseBody.getId().toString();
                    NextStepLimitRegister();
                    //SuccessProject("Proyecto Solar", "Tu proyecto ha sido registrado exitosamente.");
                }
                else{
                    //showMessage("Proyecto Solar", "Hubo un problema al crear el proyecto. Contacte al administrador.");
                    Log.d("PROJECT A",response.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseArea> call, Throwable t) {
                dialog.dismiss();
                Log.d("OnFail", t.getMessage());
                showMessage("Error en la comunicación", "No es posible conectar con el servidor. Intente de nuevo por favor");
            }
        });

        /*
        for (int i = 0; i < polygon.size(); i++){
            SendDataLimits(polygon.get(0));
        }
         */
    }

    public void SendDataLimits(){

        RequestLimit limitRegister = new RequestLimit();
        Position position = new Position();
        position.setLat(String.valueOf(latitude));
        position.setLng(String.valueOf(longitude));
        limitRegister.setPosition(position);
        limitRegister.setAltitude("0");
        limitRegister.setAreaId(ID_AREA);

        Call<ResponseLimit> responseLimit = connectInterface.RegisterLimits(TOKEN, limitRegister, ID_AREA);

        responseLimit.enqueue(new Callback<ResponseLimit>() {
            @Override
            public void onResponse(Call<ResponseLimit> call, Response<ResponseLimit> response) {
                dialog.dismiss();
                int statusCode = response.code();
                ResponseLimit responseBody = response.body();
                if (statusCode==201 || statusCode==200){
                    SuccessProject("Proyecto Solar", "Tu proyecto ha sido registrado exitosamente.");
                }
                else{
                    //showMessage("Proyecto Solar", "Hubo un problema al crear el proyecto. Contacte al administrador.");
                    Log.d("PROJECT L",response.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseLimit> call, Throwable t) {
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
            /*
                for(int i = 0; i < MapsActivityCurrentPlace.listPolygons.size(); i++){
                SendDataArea(MapsActivityCurrentPlace.listPolygons.get(i));
            }
             */
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
                        prefs2 = getSharedPreferences("altitudeDrone", Context.MODE_PRIVATE);
                        String stringAltitude = prefs2.getString("Altitude", null);
                        Double altitude = Double.valueOf(stringAltitude);

                        createRoute(MapsActivityCurrentPlace.listPolygons.get(0), altitude);

                        Intent intent = new Intent(SubstationActivity.this, HomeResults.class);

                        intent.putExtra("ID_PROJECT", ID_PROJECT);
                        intent.putExtra("ID_AREA", ID_AREA);
                        intent.putExtra("TOKEN", TOKEN);

                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }

    private LatLng findPoint(double x1, double y1, double angle, double w)
    {
        double y2;
        double x2;

        x2 = w * Math.cos(angle) + x1;
        y2 = w * Math.sin(angle) + y1;

        //y2  = y1  + ((w * Math.sin(angle)) / 6378137) * (180 / Math.PI);
        //x2 = x1 + ((w * Math.cos(angle)) / 6378137) * (180 / Math.PI) / Math.cos(y1 * Math.PI/180);

        /*Log.d("x1 = ", Double.toString(x1));
        Log.d("y1 = ", Double.toString(y1));
        Log.d("x2 = ", Double.toString(x2));
        Log.d("y2 = ", Double.toString(y2));*/

        return new LatLng(y2, x2);
    }

    private int findPoints2(LatLng p1, LatLng p2, double w, int i, double h)
    {
        double angle = calcAngle(p1.longitude, p2.longitude, p1.latitude, p2.latitude);
        LatLng temp;

        while(true)
        {
            if (points.size()<=1)
                break;

            temp = findPoint(points.get(i).longitude, points.get(i).latitude, angle, w);

            //Log.d("temp: ", temp.toString());

            if (angle >= 0 && angle < Math.PI/2 && temp.longitude >= p2.longitude
                    && temp.latitude >= p2.latitude) {
                break;
            }
            else if (angle >= Math.PI/2 && angle < Math.PI && temp.longitude < p2.longitude
                    && temp.latitude >= p2.latitude) {
                break;
            }
            else if (angle >= Math.PI && angle < 3*Math.PI/2 && temp.longitude < p2.longitude
                    && temp.latitude < p2.latitude) {
                break;
            }
            else if (angle >= 3*Math.PI/2 && angle < 2*Math.PI && temp.longitude >= p2.longitude
                    && temp.latitude < p2.latitude) {
                break;
            }

            points.add(i+1, temp);
            //sendCoordinate(temp.latitude, temp.longitude, h);
            i++;
            //w+=w;
        }

        return i;
    }

    private double calcAngle(double x1, double x2, double y1, double y2)
    {
        double hyp;
        double opp;
        //double sign=0;

        hyp = Math.sqrt(Math.pow(x2-x1,2) + Math.pow(y2-y1,2));
        opp = Math.sqrt(Math.pow(y2-y1,2));

        if (x2>=x1 && y2>=y1)
            return (Math.asin(opp/hyp));
        else if (x2<x1 && y2>=y1)
            return ((Math.PI)-(Math.asin(opp/hyp)));
        else if (x2<x1 && y2<y1)
            return ((3*Math.PI/2)-(Math.PI/2 - Math.asin(opp/hyp)));
        else
            return ((2*Math.PI)-(Math.asin(opp/hyp)));
    }

    private double calcSlope(double x1, double x2, double y1, double y2)
    {
        return (y2-y1)/(x2-x1);
    }

    private double calcB(double x, double y, double m)
    {
        return y-m*x;
    }

    private boolean findVertex(LatLng p1, LatLng p2, LatLng p3, double d, double h)
    {
        double ma = calcSlope(p1.longitude, p2.longitude, p1.latitude, p2.latitude);
        double mb = calcSlope(p2.longitude, p3.longitude, p2.latitude, p3.latitude);
        double ba = calcB(p2.longitude, p2.latitude, ma);
        double bb = calcB(p3.longitude, p3.latitude, mb);

        double angle1 = calcAngle(p1.longitude, p2.longitude, p1.latitude, p2.latitude);
        double angle2 = calcAngle(p2.longitude, p3.longitude, p2.latitude, p3.latitude);

        double da = d;
        double db = d;

        /*Log.d("p1: ", p1.toString());
        Log.d("p2: ", p2.toString());
        Log.d("p3: ", p3.toString());

        Log.d("angle1: ", Double.toString(Math.toDegrees(angle1)));
        Log.d("angle2: ", Double.toString(Math.toDegrees(angle2)));


        Log.d("ma: ", Double.toString(ma));
        Log.d("ba: ", Double.toString(ba));
        Log.d("mb: ", Double.toString(mb));
        Log.d("bb: ", Double.toString(bb));
        Log.d("", "");*/

        if (angle1 > Math.PI/2 && angle1 <= 3*Math.PI/2) {
            da=-da;
        }
        if (angle2 > Math.PI/2 && angle2 <= 3*Math.PI/2) {
            db=-db;
        }

        double x = (bb +db*Math.sqrt(1+Math.pow(mb,2)) - ba - da*Math.sqrt(1+Math.pow(ma,2)))/(ma-mb);
        double y = ma*x+ba+da*Math.sqrt(1+Math.pow(ma,2));

        points.add(new LatLng(y,x));
        //sendCoordinate(y, x, h);

        return true;
    }

    private LatLng rotate(double angle, double x, double y, double ox, double oy, int r)
    {
        /*Log.d("angle2 = " , Double.toString(angle));
        Log.d("ox = ", Double.toString(ox));
        Log.d("oy = ", Double.toString(oy));
        Log.d("x = ", Double.toString(x));
        Log.d("y = ", Double.toString(y));
        Log.d("r = ", Double.toString(r));*/

        double lon = (x+(x*r))*Math.cos(angle)-(y+(y*r))*Math.sin(angle)+ox;
        double lat = (x+(x*r))*Math.sin(angle)+(y+(y*r))*Math.cos(angle)+oy;

        //Log.d("lon: ", Double.toString(lon));
        //Log.d("lat: ", Double.toString(lat));

        return new LatLng(lat, lon);
    }

    private void createRoute(List<LatLng> listVertices, double height){
        double length;
        double width;
        double longitudeDron;
        double latitudeDron;
        double pending;
        double b;
        double angle;
        double angle2;
        double wt = 1;
        double wa;
        double rw = 0;
        int i = -1;
        int ii;
        int fi;
        int r=0;
        int cw=0;
        int dei=0;
        boolean added;

        double tempx=0;
        double tempy=0;

        List<Integer> deleteVertex = new ArrayList<Integer>();
        List<Double> angles = new ArrayList<Double>();

        //vertix.setLongitude(listVertices.get(0).longitude);
        //vertix.setLatitude(listVertices.get(0).latitude);
        //firstLocationDron.setLongitude(listVertices.get(0).longitude);
        //firstLocationDron.setLatitude(listVertices.get(0).latitude);
        longitudeDron = listVertices.get(0).longitude;
        latitudeDron = listVertices.get(0).latitude;

        height = 32.0;

        length = (1.8 * height * 0.3048) / 6378137 *180/Math.PI;
        width = (1.37 * height * 0.3048) / 6378137 *180/Math.PI;

        int li=0;
        int lf=0;

        //Log.d("New Vertex1: ", listVertices.toString());

        int sizeV=listVertices.size();

        //for (int k=0; k<8; k++)
        while (true)
        {
            for (int l=0; l<listVertices.size(); l++)
            {
                if (l-1<0) {
                    li=listVertices.size()-1;
                }
                else {
                    li=l-1;
                }
                if (l+1>=listVertices.size()) {
                    lf=0;
                }
                else {
                    lf=l+1;
                }

                /*angles.add(Math.atan2(listVertices.get(li).latitude
                        - listVertices.get(lf).latitude, listVertices.get(li).longitude
                        - listVertices.get(lf).longitude));*/

                angles.add(calcAngle(listVertices.get(l).longitude, listVertices.get(lf).longitude,
                        listVertices.get(l).latitude, listVertices.get(lf).latitude));

                //Log.d("angle1: ", Double.toString(angles.get(l)));

                added = findVertex(listVertices.get(li), listVertices.get(l),
                        listVertices.get(lf), length/2, height);

                if (added) {
                    i++;
                }
                else {
                    sizeV--;
                }

                /*angle = calcAngle(listVertices.get(l).longitude, points.get(i+1).longitude,
                        listVertices.get(l).latitude, points.get(i+1).latitude);

                Log.d("angle: ", Double.toString(Math.toDegrees(angle)));*/

                //i++;
            }

            //Log.d("sizeV: ", Integer.toString(sizeV));

            if (sizeV <= 1)
                break;

            i=i-listVertices.size()+1;

            //Log.d("i1: ", Integer.toString(i));

            listVertices.clear();

            for (int l=0; l<sizeV; l++)
            {
                //Log.d("i1: ", Integer.toString(i));

                listVertices.add(points.get(i));
                i++;
                //Log.d("i2: ", Integer.toString(i));

            }

            i=i-listVertices.size();

            //Log.d("i2: ", Integer.toString(i));
            int l2=0;

            for (int l=0; l<listVertices.size(); l++)
            {
                //Log.d("angle1: ", Double.toString(angles.get(l2)));

                if (l-1<0) {
                    li=listVertices.size()-1;
                }
                else {
                    li=l-1;
                }
                if (l+1>=listVertices.size()) {
                    lf=0;
                }
                else {
                    lf=l+1;
                }

                angle2 = calcAngle(listVertices.get(l).longitude, listVertices.get(lf).longitude,
                        listVertices.get(l).latitude, listVertices.get(lf).latitude);

                //Log.d("angle2: ", Double.toString(angle2));

                if (angle2 >= angles.get(l2) - 0.00000001 && angle2 <= angles.get(l2) + 0.00000001) {
                    i++;
                }
                else {
                    Log.d("hola: ", "funciona");

                    sizeV--;
                    points.remove(i);
                    listVertices.remove(l);
                    l--;
                }
                l2++;

            }

            Log.d("Vertices: ", listVertices.toString());

            if (sizeV <= 1)
                break;

            //Log.d(" "," ");

            i=points.size()-listVertices.size();



            //points.add(points.get(i));

            //i++;

            for (int l=0; l<listVertices.size(); l++)
            {
                //Log.d("i1: ", Integer.toString(i));
                ii=i;

                if (l + 1 >= listVertices.size()) {
                    i = findPoints2(listVertices.get(l), listVertices.get(0), width / 2, i, height);
                } else {
                    i = findPoints2(listVertices.get(l), listVertices.get(l+1), width / 2, i, height);
                }

                if (ii==i) {
                    sizeV--;
                    deleteVertex.add(l);
                }
                //Log.d("i2: ", Integer.toString(i));

                //Log.d("points: ", points.toString());

                i++;
            }

            if (listVertices.size() <= 2)
                break;

            //Log.d("points: ", points.toString());

            for (int l=0; l<deleteVertex.size(); l++)
            {
                //Log.d("hola: ", Integer.toString(deleteVertex.size()));
                if (deleteVertex.get(l)+1 > listVertices.size()) {
                    tempx=((listVertices.get(0).longitude
                            - listVertices.get(deleteVertex.get(l)).longitude) /2)
                            + listVertices.get(deleteVertex.get(l)).longitude;

                    tempy=((listVertices.get(deleteVertex.get(l)+1).latitude
                            - listVertices.get(deleteVertex.get(l)).latitude) /2)
                            + listVertices.get(deleteVertex.get(l)).latitude;
                }
                else {
                    tempx=((listVertices.get(deleteVertex.get(l)+1).longitude
                            - listVertices.get(deleteVertex.get(l)).longitude) /2)
                            + listVertices.get(deleteVertex.get(l)).longitude;

                    tempy=((listVertices.get(deleteVertex.get(l)+1).latitude
                            - listVertices.get(deleteVertex.get(l)).latitude) /2)
                            + listVertices.get(deleteVertex.get(l)).latitude;
                }

                /*Log.d("tempx = (", Double.toString(listVertices.get(deleteVertex.get(l)+1).longitude)
                        + " - " + Double.toString(listVertices.get(deleteVertex.get(l)).longitude)
                        + ") / 2 + " + Double.toString(listVertices.get(deleteVertex.get(l)).longitude));

                Log.d("tempy = (", Double.toString(listVertices.get(deleteVertex.get(l)+1).latitude)
                        + " - " + Double.toString(listVertices.get(deleteVertex.get(l)).latitude)
                        + ") / 2 + " + Double.toString(listVertices.get(deleteVertex.get(l)).latitude));*/

                //Log.d("tempx: ", Double.toString(tempx));
                //Log.d("tempy: ", Double.toString(tempy));

                listVertices.set(deleteVertex.get(l), new LatLng(tempy, tempx));
            }

            //Log.d("New Vertex1: ", listVertices.toString());

            for (int l=0; l<deleteVertex.size(); l++)
            {
                listVertices.remove(deleteVertex.get(l)+1-dei);
                dei++;
            }

            deleteVertex.clear();
            dei=0;

            i--;
            //Log.d("New Vertex2: ", listVertices.toString());

            if (listVertices.size() == 1) {
                points.add(listVertices.get(0));
                //sendCoordinate(listVertices.get(0).latitude, listVertices.get(0).longitude, height);
                break;
            }
            else if (listVertices.size() < 1)
                break;

            //if (sizeV<=3)
            //break;
        }

        Log.d("points: ", points.toString());

        for (int l=0; l<points.size(); l++)
        {
            sendCoordinate(points.get(l).latitude, points.get(l).longitude, height);
        }

        //Log.d("Salio: ", listVertices.toString());

        //return firstLocationDron;
    }

    public void sendCoordinate(double lat, double lon, double al) {
        //String DATE="2017-10-12T17:22:31.316Z", NAME="Solar", ADDRESS="CSF TEC", COST="10", SURFACE="10", ID_USER="1";

        RequestCoordinate coordinateRegister = new RequestCoordinate();
        Position pos = new Position();

        pos.setLat(Double.toString(lat));
        pos.setLng(Double.toString(lon));

        coordinateRegister.setPosition(pos);
        coordinateRegister.setAltitude(al);
        coordinateRegister.setAreaId(ID_AREA); //Por ahora
        coordinateRegister.setResultId(""); //Por ahora

        Call<ResponseCoordinate> responseCoordinate = connectInterface.RegisterCoordinate(TOKEN, coordinateRegister);

        responseCoordinate.enqueue(new Callback<ResponseCoordinate>() {
            @Override
            public void onResponse(Call<ResponseCoordinate> call, Response<ResponseCoordinate> response) {
                dialog.dismiss();
                int statusCode = response.code();
                ResponseCoordinate responseBody = response.body();
                if (statusCode==201 || statusCode==200){
                    //SuccessProject("Proyecto Solar", "Tu proyecto ha sido registrado exitosamente.");
                    Log.d("SUCCESS",response.toString());
                }
                else{
                    showMessage("Proyecto Solar", "Hubo un problema al crear la coordenada. Contacte al administrador.");
                    Log.d("PROJECT",response.toString());
                }

            }

            @Override
            public void onFailure(Call<ResponseCoordinate> call, Throwable t) {
                dialog.dismiss();
                Log.d("OnFail", t.getMessage());
                showMessage("Error en la comunicación", "No es posible conectar con el servidor. Intente de nuevo por favor");
            }
        });

        //Log.d("hola: ", "sale");

    }

    public void NextStepAreaRegister(){
        SendDataArea();
    }

    public void NextStepLimitRegister(){
        SendDataLimits();
    }
}
