package com.itesm.digital.solar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.itesm.digital.solar.Interfaces.RequestInterface;
import com.itesm.digital.solar.Models.Coordinate;
import com.itesm.digital.solar.Models.RequestBlobstore;
import com.itesm.digital.solar.Models.ResponseBlobstore;
import com.itesm.digital.solar.Models.ResponseCoordinate;
import com.itesm.digital.solar.Utils.GlobalVariables;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import dji.common.camera.SettingsDefinitions;
import dji.common.camera.SystemState;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointAction;
import dji.common.mission.waypoint.WaypointActionType;
import dji.common.mission.waypoint.WaypointMission;
import dji.common.mission.waypoint.WaypointMissionDownloadEvent;
import dji.common.mission.waypoint.WaypointMissionExecutionEvent;
import dji.common.mission.waypoint.WaypointMissionFinishedAction;
import dji.common.mission.waypoint.WaypointMissionFlightPathMode;
import dji.common.mission.waypoint.WaypointMissionHeadingMode;
import dji.common.mission.waypoint.WaypointMissionState;
import dji.common.mission.waypoint.WaypointMissionUploadEvent;
import dji.common.useraccount.UserAccountState;
import dji.common.util.CommonCallbacks;
import dji.log.DJILog;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.sdk.camera.DownloadListener;
import dji.sdk.camera.MediaFile;
import dji.sdk.camera.MediaManager;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.flightcontroller.FlightController;
import dji.common.error.DJIError;
import dji.sdk.mission.timeline.TimelineElement;
import dji.sdk.mission.timeline.triggers.Trigger;
import dji.sdk.mission.timeline.triggers.TriggerEvent;
import dji.sdk.mission.waypoint.WaypointMissionOperator;
import dji.sdk.mission.waypoint.WaypointMissionOperatorListener;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.useraccount.UserAccountManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainDrone extends FragmentActivity implements View.OnClickListener, GoogleMap.OnMapClickListener, OnMapReadyCallback {

    protected static final String TAG = "GSDemoActivity";

    private GoogleMap gMap;

    private Button locate, add, clear;
    private Button config, upload, start, stop;

    private boolean isAdd = false;

    private double droneLocationLat = 181, droneLocationLng = 181;
    private final Map<Integer, Marker> mMarkers = new ConcurrentHashMap<Integer, Marker>();
    private Marker droneMarker = null;

    private float altitude = 100.0f;
    private float mSpeed = 10.0f;

    private List<Waypoint> waypointList = new ArrayList<>();

    //private List<LatLng> coordinatesList = new ArrayList<>();
    private List<LatLng> points = SubstationActivity.points;

    public static WaypointMission.Builder waypointMissionBuilder;
    private FlightController mFlightController;
    private WaypointMissionOperator instance;
    private WaypointMissionFinishedAction mFinishedAction = WaypointMissionFinishedAction.NO_ACTION;
    private WaypointMissionHeadingMode mHeadingMode = WaypointMissionHeadingMode.AUTO;

    private int iCheck = 0;

    private Handler handler;
    final File destDir = new File(Environment.getExternalStorageDirectory().getPath() + "/testing2/");
    public File imgn;

    private MediaManager mediaManager;
    //private FileListAdapter mListAdapter;
    private List<MediaFile> mediaFileList = new ArrayList<MediaFile>();
    //private ProgressDialog mDownloadDialog;
    //private ProgressDialog mLoadingDialog;
    private int currentProgress = -1;
    //private FetchMediaTaskScheduler scheduler;
    private MediaManager.FileListState currentFileListState = MediaManager.FileListState.UNKNOWN;

    //private WaypointAction takePhoto;
    private WaypointAction stayDownload;
    //public WaypointActionType photo;

    /*private TimelineElement timeLine;

    private Trigger trigger;

    private Trigger.Action shootPhoto;

    private Trigger.Listener tListener;*/

    MaterialDialog.Builder builder;
    MaterialDialog dialog;

    Retrofit.Builder builderR = new Retrofit.Builder()
            .baseUrl(GlobalVariables.API_BASE+GlobalVariables.API_VERSION)
            .addConverterFactory(GsonConverterFactory.create());

    Retrofit retrofit = builderR.build();

    RequestInterface connectInterface = retrofit.create(RequestInterface.class);

    public SharedPreferences prefs;

    public String TOKEN;
    public String ID_AREA;

    private int j=1;

    private ArrayList<Coordinate> data;

    @Override
    protected void onResume(){
        super.onResume();
        initFlightController();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        unregisterReceiver(mReceiver);
        removeListener();

        DJIDemoApplication.getCameraInstance().setMode(SettingsDefinitions.CameraMode.SHOOT_PHOTO, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError mError) {
                if (mError != null){
                    setResultToToast("Set Shoot Photo Mode Failed" + mError.getDescription());
                }
            }
        });

        if (mediaFileList != null) {
            mediaFileList.clear();
        }

        super.onDestroy();
    }

    /**
     * @Description : RETURN Button RESPONSE FUNCTION
     */
    public void onReturn(View view){
        Log.d(TAG, "onReturn");
        this.finish();
    }

    private void setResultToToast(final String string){
        MainDrone.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainDrone.this, string, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initUI() {

        locate = (Button) findViewById(R.id.locate);
        add = (Button) findViewById(R.id.add);
        clear = (Button) findViewById(R.id.clear);
        config = (Button) findViewById(R.id.config);
        upload = (Button) findViewById(R.id.upload);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);

        locate.setOnClickListener(this);
        add.setOnClickListener(this);
        clear.setOnClickListener(this);
        config.setOnClickListener(this);
        upload.setOnClickListener(this);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        // When the compile and target version is higher than 22, please request the
        // following permissions at runtime to ensure the
        // SDK work well.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.VIBRATE,
                            Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.WAKE_LOCK, Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SYSTEM_ALERT_WINDOW,
                            Manifest.permission.READ_PHONE_STATE,
                    }
                    , 1);
        }

        setContentView(R.layout.activity_main_drone);

        IntentFilter filter = new IntentFilter();
        filter.addAction(DJIDemoApplication.FLAG_CONNECTION_CHANGE);
        registerReceiver(mReceiver, filter);

        //takePhoto = new WaypointAction(WaypointActionType.START_TAKE_PHOTO, 0);
        stayDownload = new WaypointAction(WaypointActionType.STAY, 25000);

        /*tListener = new Trigger.Listener() {
            @Override
            public void onEvent(Trigger trigger, TriggerEvent triggerEvent, @Nullable DJIError djiError) {

            }
        };

        shootPhoto = new Trigger.Action() {
            @Override
            public void onCall() {
                switchCameraMode(SettingsDefinitions.CameraMode.SHOOT_PHOTO);

                captureAction();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initMediaManager();
                    }
                }, 5000);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        downloadFileByIndex(0);
                    }
                }, 10000);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switchCameraMode(SettingsDefinitions.CameraMode.SHOOT_PHOTO);
                    }
                }, 15000);

                //iCheck++;
            }
        };

        trigger = new Trigger() {
            @Override
            public boolean isActive() {
                return super.isActive();
            }

            @Override
            public void start() {
                super.start();
                //shootPhoto.onCall();
            }

            @Override
            public void stop() {
                super.stop();
            }

            @Override
            public void setAction(Action action) {
                super.setAction(action);
            }

            @Override
            public void addListener(Listener listener) {
                super.addListener(listener);
            }

            @Override
            public void removeListener(Listener listener) {
                super.removeListener(listener);
            }

            @Override
            public void removeAllListeners() {
                super.removeAllListeners();
            }

            @Override
            public void notifyListenersOfEvent(TriggerEvent triggerEvent, DJIError djiError) {
                super.notifyListenersOfEvent(triggerEvent, djiError);
            }
        };

        timeLine = new TimelineElement() {
            @Override
            public void run() {
                trigger.setAction(shootPhoto);
                trigger.addListener(tListener);
                trigger.start();
            }

            @Override
            public boolean isPausable() {
                return false;
            }

            @Override
            public void stop() {

            }

            @Override
            public DJIError checkValidity() {
                return null;
            }
        };

        timeLine.triggers();*/

        /*coordinatesList.add(new LatLng(19.358565, -99.259659));
        coordinatesList.add(new LatLng(19.358613, -99.259388));
        coordinatesList.add(new LatLng(19.358557, -99.259472));*/

        //createPoints();

        handler = new Handler();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        addListener();

        /*mReceivedVideoDataCallBack = new VideoFeeder.VideoDataCallback() {

            @Override
            public void onReceive(byte[] videoBuffer, int size) {
                if (mCodecManager != null) {
                    mCodecManager.sendDataToDecoder(videoBuffer, size);
                }
            }
        };*/

        Camera camera = DJIDemoApplication.getCameraInstance();

        if (camera != null) {

            camera.setSystemStateCallback(new SystemState.Callback() {
                @Override
                public void onUpdate(SystemState cameraSystemState) {
                    if (null != cameraSystemState) {

                        int recordTime = cameraSystemState.getCurrentVideoRecordingTimeInSeconds();
                        int minutes = (recordTime % 3600) / 60;
                        int seconds = recordTime % 60;

                        final String timeString = String.format("%02d:%02d", minutes, seconds);
                        final boolean isVideoRecording = cameraSystemState.isRecording();

                        MainDrone.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                //recordingTime.setText(timeString);

                                /*
                                 * Update recordingTime TextView visibility and mRecordBtn's check state
                                 */
                                /*if (isVideoRecording){
                                    recordingTime.setVisibility(View.VISIBLE);
                                }else
                                {
                                    recordingTime.setVisibility(View.INVISIBLE);
                                }*/
                            }
                        });
                    }
                }
            });

        }

        initUI();

        builder = new MaterialDialog.Builder(this)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0);

        dialog = builder.build();

        getCoordinate();

    }

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            onProductConnectionChange();
        }
    };

    private void onProductConnectionChange()
    {
        initFlightController();
        loginAccount();
    }

    private void loginAccount(){

        UserAccountManager.getInstance().logIntoDJIUserAccount(this,
                new CommonCallbacks.CompletionCallbackWith<UserAccountState>() {
                    @Override
                    public void onSuccess(final UserAccountState userAccountState) {
                        Log.e(TAG, "Login Success");
                    }
                    @Override
                    public void onFailure(DJIError error) {
                        setResultToToast("Login Error:"
                                + error.getDescription());
                    }
                });
    }

    private void initFlightController() {

        BaseProduct product = DJIDemoApplication.getProductInstance();
        if (product != null && product.isConnected()) {
            if (product instanceof Aircraft) {
                mFlightController = ((Aircraft) product).getFlightController();
            }
        }

        if (mFlightController != null) {
            mFlightController.setStateCallback(new FlightControllerState.Callback() {

                @Override
                public void onUpdate(FlightControllerState djiFlightControllerCurrentState) {
                    droneLocationLat = djiFlightControllerCurrentState.getAircraftLocation().getLatitude();
                    droneLocationLng = djiFlightControllerCurrentState.getAircraftLocation().getLongitude();

                    /*if (WaypointMissionExecutionEvent.getProgress().isWaypointReached) {
                        setResultToToast("Waypoint reached");
                    }*/

                    /*if (waypointMissionBuilder != null) {
                        if (droneLocationLat == waypointList.get(iCheck).coordinate.getLatitude() && droneLocationLng == waypointList.get(iCheck).coordinate.getLongitude())
                        {
                            switchCameraMode(SettingsDefinitions.CameraMode.SHOOT_PHOTO);

                            captureAction();

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    initMediaManager();
                                }
                            }, 5000);

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        downloadFileByIndex(0);
                                    }
                                }, 10000);

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        switchCameraMode(SettingsDefinitions.CameraMode.SHOOT_PHOTO);
                                    }
                                }, 15000);

                            iCheck++;
                        }
                    }*/

                    updateDroneLocation();
                }
            });
        }
    }

    //Add Listener for WaypointMissionOperator
    private void addListener() {
        if (getWaypointMissionOperator() != null){
            getWaypointMissionOperator().addListener(eventNotificationListener);
        }
    }

    private void removeListener() {
        if (getWaypointMissionOperator() != null) {
            getWaypointMissionOperator().removeListener(eventNotificationListener);
        }
    }

    private WaypointMissionOperatorListener eventNotificationListener = new WaypointMissionOperatorListener() {
        @Override
        public void onDownloadUpdate(WaypointMissionDownloadEvent downloadEvent) {

        }

        @Override
        public void onUploadUpdate(WaypointMissionUploadEvent uploadEvent) {

        }

        @Override
        public void onExecutionUpdate(WaypointMissionExecutionEvent executionEvent) {
            //setResultToToast(Integer.toString(executionEvent.getProgress().targetWaypointIndex));

            if (executionEvent.getProgress().isWaypointReached && executionEvent.getProgress().targetWaypointIndex == iCheck) {
                //setResultToToast("Waypoint reached");
                iCheck++;

                captureAction();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initMediaManager();
                    }
                }, 5000);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        downloadFileByIndex(0);
                    }
                }, 10000);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switchCameraMode(SettingsDefinitions.CameraMode.SHOOT_PHOTO);
                    }
                }, 15000);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imgn = lastFileModified(destDir.toString());

                        Bitmap bm = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(imgn.toString()),426,240,true);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 0, baos); //bm is the bitmap object
                        byte[] b = baos.toByteArray();

                        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                        sendPhoto(encodedImage);
                    }
                }, 20000);
            }
        }

        @Override
        public void onExecutionStart() {

        }

        @Override
        public void onExecutionFinish(@Nullable final DJIError error) {
            setResultToToast("Execution finished: " + (error == null ? "Success!" : error.getDescription()));
            iCheck = 0;
        }
    };

    public WaypointMissionOperator getWaypointMissionOperator() {
        if (instance == null) {
            instance = DJISDKManager.getInstance().getMissionControl().getWaypointMissionOperator();
        }
        return instance;
    }

    private void setUpMap() {
        gMap.setOnMapClickListener(this);// add the listener for click for amap object

    }

    @Override
    public void onMapClick(LatLng point) {
        createPoints();

        /*if (isAdd){
            markWaypoint(point);
            Waypoint mWaypoint = new Waypoint(point.latitude, point.longitude, altitude);
            //Add Waypoints to Waypoint arraylist;
            if (waypointMissionBuilder != null) {
                waypointList.add(mWaypoint);
                waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
            }else
            {
                waypointMissionBuilder = new WaypointMission.Builder();
                waypointList.add(mWaypoint);
                waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
            }
        }else{
            setResultToToast("Cannot Add Waypoint");
        }*/
    }

    public void createPoints() {
        isAdd=true;
        for (int i=1; i<data.size(); i++)
        {
            markWaypoint(new LatLng(Double.parseDouble(data.get(i).getPosition().getLat()),
                    Double.parseDouble(data.get(i).getPosition().getLng())));
            Waypoint mWaypoint = new Waypoint(Double.parseDouble(data.get(i).getPosition().getLat()),
                    Double.parseDouble(data.get(i).getPosition().getLng()), altitude);
            //Add Waypoints to Waypoint arraylist;
            //Log.d("Waypoint: ", mWaypoint.toString());
            if (waypointMissionBuilder != null) {
                waypointList.add(mWaypoint);
                waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
            }else
            {
                waypointMissionBuilder = new WaypointMission.Builder();
                waypointList.add(mWaypoint);
                waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
            }
        }
    }

    public static boolean checkGpsCoordination(double latitude, double longitude) {
        return (latitude > -90 && latitude < 90 && longitude > -180 && longitude < 180) && (latitude != 0f && longitude != 0f);
    }

    // Update the drone location based on states from MCU.
    private void updateDroneLocation(){

        LatLng pos = new LatLng(droneLocationLat, droneLocationLng);
        /*LatLng check = new LatLng(0,0);

        if (waypointMissionBuilder != null) {
            check = new LatLng(waypointList.get(iCheck).coordinate.getLatitude(), waypointList.get(iCheck).coordinate.getLatitude());
        }*/

        //Create MarkerOptions object
        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(pos);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.aircraft));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (droneMarker != null) {
                    droneMarker.remove();
                }

                if (checkGpsCoordination(droneLocationLat, droneLocationLng)) {
                    droneMarker = gMap.addMarker(markerOptions);
                }
            }
        });

        /*if (waypointMissionBuilder != null) {
            if (pos == check)
            {
                switchCameraMode(SettingsDefinitions.CameraMode.SHOOT_PHOTO);

                captureAction();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initMediaManager();
                    }
                }, 5000);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        downloadFileByIndex(0);
                    }
                }, 10000);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switchCameraMode(SettingsDefinitions.CameraMode.SHOOT_PHOTO);
                    }
                }, 15000);

                iCheck++;
            }
        }*/
    }

    private void switchCameraMode(SettingsDefinitions.CameraMode cameraMode){

        Camera camera = DJIDemoApplication.getCameraInstance();
        if (camera != null) {
            camera.setMode(cameraMode, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError error) {

                    if (error == null) {
                        setResultToToast("Switch Camera Mode Succeeded");
                    } else {
                        setResultToToast(error.getDescription());
                    }
                }
            });
        }
    }

    private void captureAction(){

        final Camera camera = DJIDemoApplication.getCameraInstance();
        if (camera != null) {

            SettingsDefinitions.ShootPhotoMode photoMode = SettingsDefinitions.ShootPhotoMode.SINGLE; // Set the camera capture mode as Single mode
            camera.setShootPhotoMode(photoMode, new CommonCallbacks.CompletionCallback(){
                @Override
                public void onResult(DJIError djiError) {
                    if (null == djiError) {
                        //handler.postDelayed(new Runnable() {
                        //@Override
                        //public void run() {
                        camera.startShootPhoto(new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError == null) {
                                    setResultToToast("take photo: success");
                                }
                                else {
                                    setResultToToast(djiError.getDescription());
                                }
                            }
                        });
                        //}
                        // }, 2000);
                    }
                }
            });
        }
    }

    private void initMediaManager() {
        if (DJIDemoApplication.getProductInstance() == null) {
            mediaFileList.clear();
            //mListAdapter.notifyDataSetChanged();
            DJILog.e(TAG, "Product disconnected");
            return;
        } else {
            if (null != DJIDemoApplication.getCameraInstance() && DJIDemoApplication.getCameraInstance().isMediaDownloadModeSupported()) {
                mediaManager = DJIDemoApplication.getCameraInstance().getMediaManager();
                if (null != mediaManager) {
                    mediaManager.addUpdateFileListStateListener(this.updateFileListStateListener);
                    //mediaManager.addMediaUpdatedVideoPlaybackStateListener(this.updatedVideoPlaybackStateListener);
                    DJIDemoApplication.getCameraInstance().setMode(SettingsDefinitions.CameraMode.MEDIA_DOWNLOAD, new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError error) {
                            if (error == null) {
                                DJILog.e(TAG, "Set cameraMode success");
                                //showProgressDialog();
                                getFileList();
                            } else {
                                setResultToToast("Set cameraMode failed");
                            }
                        }
                    });
                    /*if (mediaManager.isVideoPlaybackSupported()) {
                        DJILog.e(TAG, "Camera support video playback!");
                    } else {
                        setResultToToast("Camera does not support video playback!");
                    }*/
                    //scheduler = mediaManager.getScheduler();
                }

            } else if (null != DJIDemoApplication.getCameraInstance()
                    && !DJIDemoApplication.getCameraInstance().isMediaDownloadModeSupported()) {
                setResultToToast("Media Download Mode not Supported");
            }
        }
        return;
    }

    private void getFileList() {
        mediaManager = DJIDemoApplication.getCameraInstance().getMediaManager();
        if (mediaManager != null) {

            if ((currentFileListState == MediaManager.FileListState.SYNCING) || (currentFileListState == MediaManager.FileListState.DELETING)){
                DJILog.e(TAG, "Media Manager is busy.");
            }else{
                mediaManager.refreshFileList(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError error) {
                        if (null == error) {
                            //hideProgressDialog();

                            //Reset data
                            if (currentFileListState != MediaManager.FileListState.INCOMPLETE) {
                                mediaFileList.clear();
                                //lastClickViewIndex = -1;
                                //lastClickView = null;
                            }

                            mediaFileList = mediaManager.getFileListSnapshot();
                            Collections.sort(mediaFileList, new Comparator<MediaFile>() {
                                @Override
                                public int compare(MediaFile lhs, MediaFile rhs) {
                                    if (lhs.getTimeCreated() < rhs.getTimeCreated()) {
                                        return 1;
                                    } else if (lhs.getTimeCreated() > rhs.getTimeCreated()) {
                                        return -1;
                                    }
                                    return 0;
                                }
                            });
                            /*scheduler.resume(new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError error) {
                                    if (error == null) {
                                        //getThumbnails();
                                        //getPreviews();
                                    }
                                }
                            });*/
                        } else {
                            //hideProgressDialog();
                            setResultToToast("Get Media File List Failed:" + error.getDescription());
                        }
                    }
                });
            }
        }
    }

    private MediaManager.FileListStateListener updateFileListStateListener = new MediaManager.FileListStateListener() {
        @Override
        public void onFileListStateChange(MediaManager.FileListState state) {
            currentFileListState = state;
        }
    };

    private void downloadFileByIndex(final int index){
        /*if ((mediaFileList.get(index).getMediaType() == MediaFile.MediaType.PANORAMA)
                || (mediaFileList.get(index).getMediaType() == MediaFile.MediaType.SHALLOW_FOCUS)) {
            return;
        }*/

        try {
            mediaFileList.get(index).fetchFileData(destDir, null, new DownloadListener<String>() {
                @Override
                public void onFailure(DJIError error) {
                    //HideDownloadProgressDialog();
                    setResultToToast("Download File Failed" + error.getDescription());
                    currentProgress = -1;
                }

                @Override
                public void onProgress(long total, long current) {
                }

                @Override
                public void onRateUpdate(long total, long current, long persize) {
                    int tmpProgress = (int) (1.0 * current / total * 100);
                    if (tmpProgress != currentProgress) {
                        //mDownloadDialog.setProgress(tmpProgress);
                        currentProgress = tmpProgress;
                    }
                }

                @Override
                public void onStart() {
                    currentProgress = -1;
                    //ShowDownloadProgressDialog();
                }

                @Override
                public void onSuccess(String filePath) {
                    //HideDownloadProgressDialog();
                    setResultToToast("Download File Success" + ":" + filePath);
                    currentProgress = -1;
                }
            });

            //switchCameraMode(SettingsDefinitions.CameraMode.SHOOT_PHOTO);

        } catch(IndexOutOfBoundsException e) {
            setResultToToast(e.getMessage());
        }

    }

    public static File lastFileModified(String dir) {
        File fl = new File(dir);
        File[] files = fl.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isFile();
            }
        });
        long lastMod = Long.MIN_VALUE;
        File choice = null;
        for (File file : files) {
            if (file.lastModified() > lastMod) {
                choice = file;
                lastMod = file.lastModified();
            }
        }
        return choice;
    }

    private void markWaypoint(LatLng point){
        //Create MarkerOptions object
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        Marker marker = gMap.addMarker(markerOptions);
        mMarkers.put(mMarkers.size(), marker);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.locate:{
                updateDroneLocation();
                cameraUpdate(); // Locate the drone's place
                break;
            }
            case R.id.add:{
                enableDisableAdd();
                break;
            }
            case R.id.clear:{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gMap.clear();
                    }

                });
                waypointList.clear();
                waypointMissionBuilder.waypointList(waypointList);
                updateDroneLocation();
                break;
            }
            case R.id.config:{
                showSettingDialog();
                break;
            }
            case R.id.upload:{
                uploadWayPointMission();
                break;
            }
            case R.id.start:{
                startWaypointMission();
                break;
            }
            case R.id.stop:{
                stopWaypointMission();
                break;
            }
            default:
                break;
        }
    }

    private void cameraUpdate(){
        LatLng pos = new LatLng(droneLocationLat, droneLocationLng);
        float zoomlevel = (float) 18.0;
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(pos, zoomlevel);
        gMap.moveCamera(cu);

    }

    private void enableDisableAdd(){
        if (isAdd == false) {
            isAdd = true;
            add.setText("Exit");
        }else{
            isAdd = false;
            add.setText("Add");
        }
    }

    private void showSettingDialog(){
        LinearLayout wayPointSettings = (LinearLayout)getLayoutInflater().inflate(R.layout.dialog_waypointsetting, null);

        final TextView wpAltitude_TV = (TextView) wayPointSettings.findViewById(R.id.altitude);
        RadioGroup speed_RG = (RadioGroup) wayPointSettings.findViewById(R.id.speed);
        RadioGroup actionAfterFinished_RG = (RadioGroup) wayPointSettings.findViewById(R.id.actionAfterFinished);
        RadioGroup heading_RG = (RadioGroup) wayPointSettings.findViewById(R.id.heading);

        speed_RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.lowSpeed){
                    mSpeed = 3.0f;
                } else if (checkedId == R.id.MidSpeed){
                    mSpeed = 5.0f;
                } else if (checkedId == R.id.HighSpeed){
                    mSpeed = 10.0f;
                }
            }

        });

        actionAfterFinished_RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(TAG, "Select finish action");
                if (checkedId == R.id.finishNone){
                    mFinishedAction = WaypointMissionFinishedAction.NO_ACTION;
                } else if (checkedId == R.id.finishGoHome){
                    mFinishedAction = WaypointMissionFinishedAction.GO_HOME;
                } else if (checkedId == R.id.finishAutoLanding){
                    mFinishedAction = WaypointMissionFinishedAction.AUTO_LAND;
                } else if (checkedId == R.id.finishToFirst){
                    mFinishedAction = WaypointMissionFinishedAction.GO_FIRST_WAYPOINT;
                }
            }
        });

        heading_RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(TAG, "Select heading");

                if (checkedId == R.id.headingNext) {
                    mHeadingMode = WaypointMissionHeadingMode.AUTO;
                } else if (checkedId == R.id.headingInitDirec) {
                    mHeadingMode = WaypointMissionHeadingMode.USING_INITIAL_DIRECTION;
                } else if (checkedId == R.id.headingRC) {
                    mHeadingMode = WaypointMissionHeadingMode.CONTROL_BY_REMOTE_CONTROLLER;
                } else if (checkedId == R.id.headingWP) {
                    mHeadingMode = WaypointMissionHeadingMode.USING_WAYPOINT_HEADING;
                }
            }
        });

        new AlertDialog.Builder(this)
                .setTitle("")
                .setView(wayPointSettings)
                .setPositiveButton("Finish",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {

                        String altitudeString = wpAltitude_TV.getText().toString();
                        altitude = Integer.parseInt(nulltoIntegerDefalt(altitudeString));
                        Log.e(TAG,"altitude "+altitude);
                        Log.e(TAG,"speed "+mSpeed);
                        Log.e(TAG, "mFinishedAction "+mFinishedAction);
                        Log.e(TAG, "mHeadingMode "+mHeadingMode);
                        configWayPointMission();
                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }

                })
                .create()
                .show();
    }

    String nulltoIntegerDefalt(String value){
        if(!isIntValue(value)) value="0";
        return value;
    }

    boolean isIntValue(String val)
    {
        try {
            val=val.replace(" ","");
            Integer.parseInt(val);
        } catch (Exception e) {return false;}
        return true;
    }

    private void configWayPointMission(){

        if (waypointMissionBuilder == null){

            waypointMissionBuilder = new WaypointMission.Builder().finishedAction(mFinishedAction)
                    .headingMode(mHeadingMode)
                    .autoFlightSpeed(mSpeed)
                    .maxFlightSpeed(mSpeed)
                    .flightPathMode(WaypointMissionFlightPathMode.NORMAL);

        }else
        {
            waypointMissionBuilder.finishedAction(mFinishedAction)
                    .headingMode(mHeadingMode)
                    .autoFlightSpeed(mSpeed)
                    .maxFlightSpeed(mSpeed)
                    .flightPathMode(WaypointMissionFlightPathMode.NORMAL);

        }

        if (waypointMissionBuilder.getWaypointList().size() > 0){

            for (int i=0; i< waypointMissionBuilder.getWaypointList().size(); i++){
                waypointMissionBuilder.getWaypointList().get(i).altitude = altitude;
                //waypointMissionBuilder.getWaypointList().get(i).addAction(takePhoto);
                waypointMissionBuilder.getWaypointList().get(i).addAction(stayDownload);
            }

            setResultToToast("Set Waypoint attitude successfully");
        }

        DJIError error = getWaypointMissionOperator().loadMission(waypointMissionBuilder.build());
        if (error == null) {
            setResultToToast("loadWaypoint succeeded");
        } else {
            setResultToToast("loadWaypoint failed " + error.getDescription());
        }
    }

    private void uploadWayPointMission(){

        getWaypointMissionOperator().uploadMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError error) {
                if (error == null) {
                    setResultToToast("Mission upload successfully!");
                } else {
                    setResultToToast("Mission upload failed, error: " + error.getDescription() + " retrying...");
                    getWaypointMissionOperator().retryUploadMission(null);
                }

            }
        });

    }

    private void startWaypointMission(){

        //timeLine.run();
        if (getWaypointMissionOperator().getCurrentState() == WaypointMissionState.READY_TO_EXECUTE) {
            //setResultToToast("Ya funciona");
            getWaypointMissionOperator().startMission(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError error) {
                    setResultToToast("Mission Start: " + (error == null ? "Successfully" : error.getDescription()));
                }
            });
        }
        else {
            setResultToToast("Aun no");
        }
    }

    private void stopWaypointMission(){

        getWaypointMissionOperator().stopMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError error) {
                setResultToToast("Mission Stop: " + (error == null ? "Successfully" : error.getDescription()));
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (gMap == null) {
            gMap = googleMap;
            setUpMap();
        }

        LatLng shenzhen = new LatLng(22.5362, 113.9454);
        gMap.addMarker(new MarkerOptions().position(shenzhen).title("Marker in Shenzhen"));
        gMap.moveCamera(CameraUpdateFactory.newLatLng(shenzhen));
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

    public void sendPhoto(String img) {
        //String DATE="2017-10-12T17:22:31.316Z", NAME="Solar", ADDRESS="CSF TEC", COST="10", SURFACE="10", ID_USER="1";

        RequestBlobstore photoRegister = new RequestBlobstore();


        photoRegister.setImage(img);
        photoRegister.setCoordinateId(data.get(j).getId()); //Por ahora
        j++;

        Call<ResponseBlobstore> responsePhoto = connectInterface.RegisterPhoto(TOKEN, photoRegister);

        responsePhoto.enqueue(new Callback<ResponseBlobstore>() {
            @Override
            public void onResponse(Call<ResponseBlobstore> call, Response<ResponseBlobstore> response) {
                dialog.dismiss();
                int statusCode = response.code();
                ResponseBlobstore responseBody = response.body();
                if (statusCode==201 || statusCode==200){
                    //SuccessProject("Proyecto Solar", "Tu proyecto ha sido registrado exitosamente.");
                    setResultToToast("Image uploaded");
                    Log.d("SUCCESS",response.toString());
                }
                else{
                    showMessage("Proyecto Solar", "Hubo un problema al subir la imagen. Contacte al administrador.");
                    Log.d("PROJECT",response.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBlobstore> call, Throwable t) {
                dialog.dismiss();
                Log.d("OnFail", t.getMessage());
                showMessage("Error en la comunicación", "No es posible conectar con el servidor. Intente de nuevo por favor");
            }
        });
    }

    public void getCoordinate() {
        Call<List<Coordinate>> responseLimits = connectInterface.GetLimits(TOKEN,ID_AREA);

        responseLimits.enqueue(new Callback<List<Coordinate>>() {
            @Override
            public void onResponse(Call<List<Coordinate>> call, Response<List<Coordinate>> response) {
                int statusCode = response.code();

                if (statusCode==200){
                    //msg.setVisibility(View.GONE);
                    List<Coordinate> jsonResponse = response.body();
                    data = new ArrayList<>(jsonResponse);
                    Log.d("Coordinates:", data.get(2).getId());
                    //adapter = new DataAdapterProjects(data);
                    //recyclerView.setAdapter(adapter);
                }
                else{
                    Log.d("PROJECT",response.toString());
                    //msg.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Coordinate>> call, Throwable t) {

            }
        });
    }
}