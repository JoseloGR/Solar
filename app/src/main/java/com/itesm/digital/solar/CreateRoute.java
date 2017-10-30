package com.itesm.digital.solar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NONE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN;

public class CreateRoute extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnMapReadyCallback {

    private static final String TAG = MapsActivityCurrentPlace.class.getSimpleName();

    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;

    public static List<List<LatLng>> listPolygons = new ArrayList<List<LatLng>>();
    private List<LatLng> last = new ArrayList<LatLng>();
    public static List<LatLng> points = new ArrayList<LatLng>();

    private Spinner mSpinner;

    //set if the user is near of the area selected
    private boolean nearArea = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mSpinner = (Spinner) findViewById(R.id.layers_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.layers_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Sets up the options menu.
     * @param menu The options menu.
     * @return Boolean.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.current_place_menu, menu);
        return true;
    }

    /**
     * Handles a click on the menu option to get a place.
     * @param item The menu item to handle.
     * @return Boolean.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_get_place) {
            showCurrentPlace();
        }
        return true;
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

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

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        setCenter(MapsActivityCurrentPlace.listPolygons.get(0));

        // Instantiates a new Polyline object and adds points to define a rectangle
        PolygonOptions rectOptions = new PolygonOptions()
                .add(new LatLng(0, 0),
                        new LatLng(0, 0)).fillColor(Color.rgb(255, 204, 128)).strokeWidth(4);

        // Get back the mutable Polygon
        final Polygon polygon = mMap.addPolygon(rectOptions);
        //Sets the points of this polygon
        polygon.setPoints(MapsActivityCurrentPlace.listPolygons.get(0));

        createRoute(10, MapsActivityCurrentPlace.listPolygons.get(0));
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

    private Location findLongitudeDron(List<LatLng> listVertices, double height){
        Location firstLocationDron = new Location("");
        Location vertix = new Location("");
        double length;
        double width;
        double longitudeDron;
        double latitudeDron;
        double pending;
        double b;
        double angle;
        double wt = 1;
        double wa;
        double rw = 0;
        int i = -1;
        int r=0;
        int cw=0;

        vertix.setLongitude(listVertices.get(0).longitude);
        vertix.setLatitude(listVertices.get(0).latitude);
        firstLocationDron.setLongitude(listVertices.get(0).longitude);
        firstLocationDron.setLatitude(listVertices.get(0).latitude);
        longitudeDron = listVertices.get(0).longitude;
        latitudeDron = listVertices.get(0).latitude;

        height = 23.0;

        length = (1.8 * height * 0.3048) / 6378137 *180/Math.PI;
        width = (1.37 * height * 0.3048) / 6378137 *180/Math.PI;
        //width = 0.5;
        //length = 1;

        /*pending = (listVertices.get(1).longitude - listVertices.get(0).longitude) / (listVertices.get(1).latitude - listVertices.get(0).latitude);
        b = (-pending * listVertices.get(0).latitude) + listVertices.get(0).longitude;*/

        /*angle = calcAngle(listVertices.get(0).longitude, listVertices.get(1).longitude,
                listVertices.get(0).latitude, listVertices.get(1).latitude);*/



        /*List<LatLng> test = new ArrayList<LatLng>();

        test.add(new LatLng(1,-2));
        test.add(new LatLng(2,-5));
        test.add(new LatLng(6,-6));
        test.add(new LatLng(7,-3));*/

        //test.add(new LatLng(6,-10));

        //LatLng center = setCenter(test);

        //Log.d("center: ", center.toString());

        int j=0;
        int j2=0;

        //listVertices.remove(listVertices.size()-1);

        //Log.d("vertices: ", listVertices.toString());

        for (int k=0; k<4; k++)
        //while (cw < listVertices.size())
        {
            //Log.d("j", Integer.toString(j));
            //if (j+1 >= test.size()) {
            if (j+1 >= listVertices.size()) {
                j2 = 0;
            }
            else
                j2 = j+1;

            angle = calcAngle(listVertices.get(j).longitude, listVertices.get(j2).longitude,
                    listVertices.get(j).latitude, listVertices.get(j2).latitude);

            /*angle = calcAngle(test.get(j).longitude, test.get(j2).longitude,
                    test.get(j).latitude, test.get(j2).latitude);*/

            wt = (Math.sqrt(Math.pow(listVertices.get(j2).longitude-listVertices.get(j).longitude,2)
                    + Math.pow(listVertices.get(j2).latitude-listVertices.get(j).latitude,2))) - width -rw;

            /*wt = (Math.sqrt(Math.pow(test.get(j2).longitude-test.get(j).longitude,2)
                    + Math.pow(test.get(j2).latitude-test.get(j).latitude,2))) - width -rw;*/

            if (wt<0) {
                cw++;
            }
            else {
                cw = 0;

                Log.d("angle: ", Double.toString(angle*180/Math.PI));
                /*Log.d("length: ", Double.toString(length));
                Log.d("width: ", Double.toString(width));
                Log.d("wt: ", Double.toString(wt));
                Log.d("rw: ", Double.toString(rw));*/

                wa = 0;

                //i=0;

                //points.add(new LatLng(listVertices.get(0).latitude, listVertices.get(0).longitude));
                points.add(rotate(angle, length / 2, length / 2, listVertices.get(j).longitude, listVertices.get(j).latitude, r));

                /*if (angle > Math.PI/2 && angle <= Math.PI) {
                    points.add(rotate(angle, length / 2, -length / 2, test.get(j).longitude, test.get(j).latitude, r));
                    i++;
                }

                else {*/
                //points.add(rotate(angle, length / 2, length / 2, test.get(j).longitude, test.get(j).latitude, r));
                i++;
                //}

                //for (int l=1; l<test.size(); l++)
                for (int l=1; l<listVertices.size(); l++)
                {
                    points.add(rotate(angle, -length / 2, length / 2, listVertices.get(l).longitude, listVertices.get(l).latitude, r));
                    i++;
                    //if (l+1 >= test.size())
                    if (l+1 >= listVertices.size())
                    {
                        /*angle = calcAngle(test.get(l).longitude, test.get(0).longitude,
                                test.get(l).latitude, test.get(0).latitude);*/

                        angle = calcAngle(listVertices.get(l).longitude, listVertices.get(0).longitude,
                                listVertices.get(l).latitude, listVertices.get(0).latitude);
                    }
                    else
                    {
                        /*angle = calcAngle(test.get(l).longitude, test.get(l + 1).longitude,
                                test.get(l).latitude, test.get(l + 1).latitude);*/

                        angle = calcAngle(listVertices.get(l).longitude, listVertices.get(l + 1).longitude,
                                listVertices.get(l).latitude, listVertices.get(l + 1).latitude);
                    }

                    Log.d("angle: ", Double.toString(angle*180/Math.PI));

                    /*if (test.get(l).longitude < test.get(l).longitude) {
                        if (test.get(l).latitude < test.get(l).latitude){

                        }
                        else {

                        }
                    }*/
                    //points.add(rotate(angle, -length / 2, length / 2, test.get(l).longitude, test.get(l).latitude, r));
                    // i++;
                }

                //points.add(points.get(i-test.size()+1));
                points.add(points.get(i-listVertices.size()+1));
                i++;

                //if (j == test.size() - 1) {
                //if (j == listVertices.size() - 1) {
                r++;
                //}

                /*while (wa<wt)
                {
                    //Log.d("i: ", Integer.toString(i));
                    points.add(findPoint(points.get(i).longitude, points.get(i).latitude,
                            angle, width));

                    /*if (i>0 && angle <= Math.PI/2 && points.get(i).latitude < points.get(i-1).latitude)
                        points.remove(i);
                    else if (angle > Math.PI/2 && angle <= Math.PI && points.get(i).longitude > points.get(i-1).longitude)
                        points.remove(i);
                    else if (angle > Math.PI && angle <= 3*Math.PI/2 && points.get(i).latitude > points.get(i-1).latitude)
                        points.remove(i);
                    else if (angle > 3*Math.PI/2 && points.get(i).longitude < points.get(i-1).longitude)
                        points.remove(i);
                    else {
                        i++;
                        wa += width;
                    //}
                }

                points.remove(i);
                i--;*/
            }

            Log.d("points: ", points.toString());
            //j++;

            //if (j >= test.size()) {
            //if (j >= listVertices.size()) {
            j = 0;
            rw += length;
            //}

        }

        PolygonOptions rectOptions2 = new PolygonOptions()
                .add(new LatLng(0, 0),
                        new LatLng(0, 0)).fillColor(Color.rgb(255, 204, 128)).strokeWidth(10);

        // Get back the mutable Polygon
        final Polygon polygon2 = mMap.addPolygon(rectOptions2);
        //Sets the points of this polygon
        polygon2.setPoints(points);

        //longitudeDron = (pending * listVertices.get(0).latitude) + b;

        /*while(vertix.distanceTo(firstLocationDron) <= (width / 2)){
            if(listVertices.get(0).latitude - listVertices.get(1).latitude > 0){
                latitudeDron -= 0.00001;
            }
            else{
                latitudeDron += 0.00001;
            }
            longitudeDron = (pending * latitudeDron) + b;
            firstLocationDron.setLongitude(longitudeDron);
            firstLocationDron.setLatitude(latitudeDron);
            Log.v("Position dron", firstLocationDron.toString());
        }*/

        /*while(vertix.distanceTo(firstLocationDron) <= (length / 2) + (width / 2)){
            if(listVertices.get(0).latitude - listVertices.get(1).latitude > 0){
                latitudeDron -= 0.00001;
            }
            else{
                latitudeDron += 0.00001;
            }
            firstLocationDron.setLatitude(latitudeDron);
            Log.v("Latitude dron", firstLocationDron.toString());
        }*/
        return firstLocationDron;
    }

    private void createRoute(double height, List<LatLng> listVertices){
        Log.v("Distancia", String.valueOf(findLongitudeDron(listVertices, height)));
    }

    private LatLng setCenter(List<LatLng> first){
        double lowestX = 0;
        double lowestY = 0;
        double highestY = 0;
        double highestX = 0;
        double centerX = 0;
        double centerY = 0;

        //List<LatLng> first = MapsActivityCurrentPlace.listPolygons.get(0);
        //List<LatLng> first = MapsActivityCurrentPlace.listPolygons.get(0);

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
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    private void showCurrentPlace() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission") final
            Task<PlaceLikelihoodBufferResponse> placeResult =
                    mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener
                    (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                                // Set the count, handling cases where less than 5 entries are returned.
                                int count;
                                if (likelyPlaces.getCount() < M_MAX_ENTRIES) {
                                    count = likelyPlaces.getCount();
                                } else {
                                    count = M_MAX_ENTRIES;
                                }

                                int i = 0;
                                mLikelyPlaceNames = new String[count];
                                mLikelyPlaceAddresses = new String[count];
                                mLikelyPlaceAttributions = new String[count];
                                mLikelyPlaceLatLngs = new LatLng[count];

                                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                    // Build a list of likely places to show the user.
                                    mLikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
                                    mLikelyPlaceAddresses[i] = (String) placeLikelihood.getPlace()
                                            .getAddress();
                                    mLikelyPlaceAttributions[i] = (String) placeLikelihood.getPlace()
                                            .getAttributions();
                                    mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                                    i++;
                                    if (i > (count - 1)) {
                                        break;
                                    }
                                }

                                // Release the place likelihood buffer, to avoid memory leaks.
                                likelyPlaces.release();

                                // Show a dialog offering the user the list of likely places, and add a
                                // marker at the selected place.
                                openPlacesDialog();

                            } else {
                                Log.e(TAG, "Exception: %s", task.getException());
                            }
                        }
                    });
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocation)
                    .snippet(getString(R.string.default_info_snippet)));

            // Prompt the user for permission.
            getLocationPermission();
        }
    }

    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */
    private void openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The "which" argument contains the position of the selected item.
                LatLng markerLatLng = mLikelyPlaceLatLngs[which];
                String markerSnippet = mLikelyPlaceAddresses[which];
                if (mLikelyPlaceAttributions[which] != null) {
                    markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
                }

                // Add a marker for the selected place, with an info window
                // showing information about that place.
                mMap.addMarker(new MarkerOptions()
                        .title(mLikelyPlaceNames[which])
                        .position(markerLatLng)
                        .snippet(markerSnippet));

                // Position the map's camera at the location of the marker.
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                        DEFAULT_ZOOM));
            }
        };

        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.pick_place)
                .setItems(mLikelyPlaceNames, listener)
                .show();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, "Map not ready", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        updateMapType();
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

    private void verifyCercany(){
        //verify if the distance between actual location and area selected is too big
        Location vertices = new Location("");
        int numberVertices = MapsActivityCurrentPlace.listPolygons.get(0).size();
        float distance = 0;
        for(int i = 0; i < numberVertices; i++) {
            vertices.setLatitude(MapsActivityCurrentPlace.listPolygons.get(0).get(i).latitude);
            vertices.setLongitude(MapsActivityCurrentPlace.listPolygons.get(0).get(i).longitude);
            //distance = mLastKnownLocation.distanceTo(vertices);
            //Log.v("Localizacion", String.valueOf(actualLatitude) + " " + String.valueOf(actualLongitude));
            //Log.v("Distancia", "Punto " +  String.valueOf(i + 1) + " distancia " + String.valueOf(distance));
            /*if(distance < 1.0000000E7){
                nearArea = true;
                Log.v("Area cerca", String.valueOf(nearArea));
            }*/
        }
    }
}
