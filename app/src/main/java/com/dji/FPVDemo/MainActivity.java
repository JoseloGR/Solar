package com.dji.FPVDemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.TextureView.SurfaceTextureListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dji.common.camera.SettingsDefinitions;
import dji.common.camera.SystemState;
import dji.common.error.DJIError;
import dji.common.product.Model;
import dji.common.useraccount.UserAccountState;
import dji.common.util.CommonCallbacks;
import dji.log.DJILog;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.sdk.camera.DownloadListener;
import dji.sdk.camera.FetchMediaTaskScheduler;
import dji.sdk.camera.MediaFile;
import dji.sdk.camera.MediaManager;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;
import dji.sdk.useraccount.UserAccountManager;

public class MainActivity extends Activity implements SurfaceTextureListener,OnClickListener{

    private static final String TAG = MainActivity.class.getName();
    protected VideoFeeder.VideoDataCallback mReceivedVideoDataCallBack = null;

    // Codec for video live view
    protected DJICodecManager mCodecManager = null;

    protected TextureView mVideoSurface = null;
    private Button mCaptureBtn, mShootPhotoModeBtn, mRecordVideoModeBtn;
    private ToggleButton mRecordBtn;
    private TextView recordingTime, datePhoto;

    private Handler handler;

    //private int lastClickViewIndex =-1;
    //private View lastClickView;
    //private FileListAdapter mListAdapter;

    final File file = new File(Environment.getExternalStorageDirectory().getPath() + "/hola2/");

    private MediaManager mediaManager;
    private List<MediaFile> mediaFileList = new ArrayList<MediaFile>();
    //private ProgressDialog mDownloadDialog;
    //private ProgressDialog mLoadingDialog;
    private int currentProgress = -1;
    //private FetchMediaTaskScheduler scheduler;
    //private MediaManager.FileListState currentFileListState = MediaManager.FileListState.UNKNOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();

        file.mkdir();

        initUI();

        // The callback for receiving the raw H264 video data for camera live view
        mReceivedVideoDataCallBack = new VideoFeeder.VideoDataCallback() {

            @Override
            public void onReceive(byte[] videoBuffer, int size) {
                if (mCodecManager != null) {
                    mCodecManager.sendDataToDecoder(videoBuffer, size);
                }
            }
        };

        Camera camera = FPVDemoApplication.getCameraInstance();

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

                        MainActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                recordingTime.setText(timeString);

                                /*
                                 * Update recordingTime TextView visibility and mRecordBtn's check state
                                 */
                                if (isVideoRecording){
                                    recordingTime.setVisibility(View.VISIBLE);
                                }else
                                {
                                    recordingTime.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    }
                }
            });

        }

    }

    protected void onProductChange() {
        initPreviewer();
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
                        showToast("Login Error:"
                                + error.getDescription());
                    }
                });
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
        initPreviewer();
        //initMediaManager();
        onProductChange();

        if(mVideoSurface == null) {
            Log.e(TAG, "mVideoSurface is null");
        }
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        uninitPreviewer();
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

    public void onReturn(View view){
        Log.e(TAG, "onReturn");
        this.finish();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        uninitPreviewer();

        /*lastClickView = null;
        if (mediaManager != null) {
            mediaManager.stop(null);
            //mMediaManager.removeMediaUpdatedVideoPlaybackStateListener(updatedVideoPlaybackStateListener);
            mediaManager.exitMediaDownloading();
        }*/

        FPVDemoApplication.getCameraInstance().setMode(SettingsDefinitions.CameraMode.SHOOT_PHOTO, new CommonCallbacks.CompletionCallback() {
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

        super.onDestroy();
    }

    private void initUI() {
        // init mVideoSurface
        mVideoSurface = (TextureView)findViewById(R.id.video_previewer_surface);

        recordingTime = (TextView) findViewById(R.id.timer);
        mCaptureBtn = (Button) findViewById(R.id.btn_capture);
        mRecordBtn = (ToggleButton) findViewById(R.id.btn_record);
        mShootPhotoModeBtn = (Button) findViewById(R.id.btn_shoot_photo_mode);
        mRecordVideoModeBtn = (Button) findViewById(R.id.btn_record_video_mode);

        //mListAdapter = new FileListAdapter();
        //listView.setAdapter(mListAdapter);

        if (null != mVideoSurface) {
            mVideoSurface.setSurfaceTextureListener(this);
        }

        mCaptureBtn.setOnClickListener(this);
        mRecordBtn.setOnClickListener(this);
        mShootPhotoModeBtn.setOnClickListener(this);
        mRecordVideoModeBtn.setOnClickListener(this);

        recordingTime.setVisibility(View.INVISIBLE);

        mRecordBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startRecord();
                } else {
                    stopRecord();
                }
            }
        });

        /*mDownloadDialog = new ProgressDialog(MainActivity.this);
        mDownloadDialog.setTitle("Downloading file");
        mDownloadDialog.setIcon(android.R.drawable.ic_dialog_info);
        mDownloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDownloadDialog.setCanceledOnTouchOutside(false);
        mDownloadDialog.setCancelable(true);
        mDownloadDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (mediaManager != null) {
                    mediaManager.exitMediaDownloading();
                }
            }
        });

        mLoadingDialog = new ProgressDialog(MainActivity.this);
        mLoadingDialog.setMessage("Please wait");
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setCancelable(false);*/
    }

    private void initPreviewer() {

        BaseProduct product = FPVDemoApplication.getProductInstance();

        if (product == null || !product.isConnected()) {
            showToast(getString(R.string.disconnected));
        } else {
            if (null != mVideoSurface) {
                mVideoSurface.setSurfaceTextureListener(this);
            }
            if (!product.getModel().equals(Model.UNKNOWN_AIRCRAFT)) {
                VideoFeeder.getInstance().getPrimaryVideoFeed().setCallback(mReceivedVideoDataCallBack);
            }
        }
    }

    private void uninitPreviewer() {
        Camera camera = FPVDemoApplication.getCameraInstance();
        if (camera != null){
            // Reset the callback
            VideoFeeder.getInstance().getPrimaryVideoFeed().setCallback(null);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.e(TAG, "onSurfaceTextureAvailable");
        if (mCodecManager == null) {
            mCodecManager = new DJICodecManager(this, surface, width, height);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.e(TAG, "onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.e(TAG,"onSurfaceTextureDestroyed");
        if (mCodecManager != null) {
            mCodecManager.cleanSurface();
            mCodecManager = null;
        }

        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*private class ItemHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail_img;
        TextView file_name;
        TextView file_type;
        TextView file_size;
        TextView file_time;

        public ItemHolder(View itemView) {
            super(itemView);
            this.thumbnail_img = (ImageView) itemView.findViewById(R.id.filethumbnail);
            this.file_name = (TextView) itemView.findViewById(R.id.filename);
            this.file_type = (TextView) itemView.findViewById(R.id.filetype);
            this.file_size = (TextView) itemView.findViewById(R.id.fileSize);
            this.file_time = (TextView) itemView.findViewById(R.id.filetime);
        }
    }*/

    /*private class FileListAdapter {
        @Override
        public int getItemCount() {
            if (mediaFileList != null) {
                return mediaFileList.size();
            }
            return 0;
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_info_item, parent, false);
            return new ItemHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemHolder mItemHolder, final int index) {

            final MediaFile mediaFile = mediaFileList.get(index);
            if (mediaFile != null) {
                if (mediaFile.getMediaType() != MediaFile.MediaType.MOV && mediaFile.getMediaType() != MediaFile.MediaType.MP4) {
                    mItemHolder.file_time.setVisibility(View.GONE);
                } else {
                    mItemHolder.file_time.setVisibility(View.VISIBLE);
                    mItemHolder.file_time.setText(mediaFile.getDurationInSeconds() + " s");
                }
                mItemHolder.file_name.setText(mediaFile.getFileName());
                mItemHolder.file_type.setText(mediaFile.getMediaType().name());
                mItemHolder.file_size.setText(mediaFile.getFileSize() + " Bytes");
                mItemHolder.thumbnail_img.setImageBitmap(mediaFile.getThumbnail());
                mItemHolder.thumbnail_img.setOnClickListener(ImgOnClickListener);
                mItemHolder.thumbnail_img.setTag(mediaFile);
                mItemHolder.itemView.setTag(index);

                if (lastClickViewIndex == index) {
                    mItemHolder.itemView.setSelected(true);
                } else {
                    mItemHolder.itemView.setSelected(false);
                }
                mItemHolder.itemView.setOnClickListener(itemViewOnClickListener);

            }
        }
    }*/

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_capture:{
                captureAction();
                //downloadFileByIndex(0);
                break;
            }
            case R.id.btn_shoot_photo_mode:{
                switchCameraMode(SettingsDefinitions.CameraMode.SHOOT_PHOTO);
                break;
            }
            case R.id.btn_record_video_mode:{
                switchCameraMode(SettingsDefinitions.CameraMode.RECORD_VIDEO);
                break;
            }
            default:
                break;
        }
    }

    private void switchCameraMode(SettingsDefinitions.CameraMode cameraMode){

        Camera camera = FPVDemoApplication.getCameraInstance();
        if (camera != null) {
            camera.setMode(cameraMode, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError error) {

                    if (error == null) {
                        showToast("Switch Camera Mode Succeeded");
                    } else {
                        showToast(error.getDescription());
                    }
                }
            });
            }
    }

    /*private void showProgressDialog() {
        if (mLoadingDialog != null) {
            runOnUiThread(new Runnable() {
                public void run() {
                    mLoadingDialog.show();
                }
            });
        }
    }

    private void ShowDownloadProgressDialog() {
        if (mDownloadDialog != null) {
            runOnUiThread(new Runnable() {
                public void run() {
                    mDownloadDialog.incrementProgressBy(-mDownloadDialog.getProgress());
                    mDownloadDialog.show();
                }
            });
        }
    }

    private void HideDownloadProgressDialog() {

        if (null != mDownloadDialog && mDownloadDialog.isShowing()) {
            runOnUiThread(new Runnable() {
                public void run() {
                    mDownloadDialog.dismiss();
                }
            });
        }
    }*/

    private void setResultToToast(final String result) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*private void initMediaManager() {
        if (FPVDemoApplication.getProductInstance() == null) {
            mediaFileList.clear();
            //mListAdapter.notifyDataSetChanged();
            DJILog.e(TAG, "Product disconnected");
            return;
        } else {
            if (null != FPVDemoApplication.getCameraInstance() && FPVDemoApplication.getCameraInstance().isMediaDownloadModeSupported()) {
                mediaManager = FPVDemoApplication.getCameraInstance().getMediaManager();
                if (null != mediaManager) {
                    //mediaManager.addUpdateFileListStateListener(this.updateFileListStateListener);
                    //mediaManager.addMediaUpdatedVideoPlaybackStateListener(this.updatedVideoPlaybackStateListener);
                    FPVDemoApplication.getCameraInstance().setMode(SettingsDefinitions.CameraMode.MEDIA_DOWNLOAD, new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError error) {
                            if (error == null) {
                                DJILog.e(TAG, "Set cameraMode success");
                                //showProgressDialog();
                                //getFileList();
                            } else {
                                setResultToToast("Set cameraMode failed");
                            }
                        }
                    });
                    /*if (mMediaManager.isVideoPlaybackSupported()) {
                        DJILog.e(TAG, "Camera support video playback!");
                    } else {
                        setResultToToast("Camera does not support video playback!");
                    }
                    //scheduler = mediaManager.getScheduler();
                }

            } else if (null != FPVDemoApplication.getCameraInstance()
                    && !FPVDemoApplication.getCameraInstance().isMediaDownloadModeSupported()) {
                setResultToToast("Media Download Mode not Supported");
            }
        }
    }*/

    /*private void getFileList() {
        mediaManager = FPVDemoApplication.getCameraInstance().getMediaManager();
        if (mediaManager != null) {

            if ((currentFileListState == MediaManager.FileListState.SYNCING) || (currentFileListState == MediaManager.FileListState.DELETING)){
                DJILog.e(TAG, "Media Manager is busy.");
            }else{
                mediaManager.refreshFileList(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError error) {
                        if (null == error) {
                            hideProgressDialog();

                            //Reset data
                            if (currentFileListState != MediaManager.FileListState.INCOMPLETE) {
                                mediaFileList.clear();
                                lastClickViewIndex = -1;
                                lastClickView = null;
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
                            scheduler.resume(new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError error) {
                                    if (error == null) {
                                        getThumbnails();
                                        getPreviews();
                                    }
                                }
                            });
                        } else {
                            hideProgressDialog();
                            setResultToToast("Get Media File List Failed:" + error.getDescription());
                        }
                    }
                });
            }
        }
    }*/

    /*private MediaManager.FileListStateListener updateFileListStateListener = new MediaManager.FileListStateListener() {
        @Override
        public void onFileListStateChange(MediaManager.FileListState state) {
            currentFileListState = state;
        }
    };*/

    // Method for taking photo
    private void captureAction(){

        //file = new File(Environment.getExternalStorageDirectory().getPath() + "/Dji_Sdk_Test/");
        //file = new File(getFilesDir(), "hola");

        final Camera camera = FPVDemoApplication.getCameraInstance();
        if (camera != null) {

            SettingsDefinitions.ShootPhotoMode photoMode = SettingsDefinitions.ShootPhotoMode.SINGLE; // Set the camera capture mode as Single mode
            camera.setShootPhotoMode(photoMode, new CommonCallbacks.CompletionCallback(){
                    @Override
                    public void onResult(DJIError djiError) {
                        if (null == djiError) {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    camera.startShootPhoto(new CommonCallbacks.CompletionCallback() {
                                        @Override
                                        public void onResult(DJIError djiError) {
                                            if (djiError == null) {
                                                showToast("take photo: success");
                                                downloadFileByIndex(0);
                                                //media.fetchFileData(file, "fotoDron", new DownloadHandler<String>());
                                            } else {
                                                showToast(djiError.getDescription());
                                            }
                                        }
                                    });
                                }
                            }, 2000);
                            //media.fetchFileData(file, "fotoDron", new DownloadHandler<String>());

                        }
                    }
            });

            //downloadFileByIndex(0);
        }
        //media.fetchFileData(file, "fotoDron", new DownloadHandler<String>());
    }

    private void downloadFileByIndex(final int index){
        /*if ((mediaFileList.get(index).getMediaType() == MediaFile.MediaType.PANORAMA)
                || (mediaFileList.get(index).getMediaType() == MediaFile.MediaType.SHALLOW_FOCUS)) {
            return;
        }*/

        //String[] array = {"WRITE_EXTERNAL_STORAGE", "READ_EXTERNAL_STORAGE"};

        //requestPermissions(array, 28);

        //int a = checkPermission(array[0], "com.dji.FPVDemo");

        //int b = checkPermission(array[0], 28, 2);

        //int[] permissions = {a, checkPermission (array[1], "com.dji.FPVDemo")}

        //onRequestPermissionsResult(28, array, permissions);

        /*Camera camera = FPVDemoApplication.getCameraInstance();

        camera.setMode(SettingsDefinitions.CameraMode.MEDIA_DOWNLOAD, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError error) {

                if (error == null) {
                    showToast("Switch Camera Mode Succeeded");
                } else {
                    showToast(error.getDescription());
                }
            }
        });*/

        if (FPVDemoApplication.getProductInstance() == null) {
            mediaFileList.clear();
            //mListAdapter.notifyDataSetChanged();
            DJILog.e(TAG, "Product disconnected");
            return;
        } else {
            setResultToToast("Product found");
            if (null != FPVDemoApplication.getCameraInstance() && FPVDemoApplication.getCameraInstance().isMediaDownloadModeSupported()) {
                mediaManager = FPVDemoApplication.getCameraInstance().getMediaManager();
                if (null != mediaManager) {
                    //mediaManager.addUpdateFileListStateListener(this.updateFileListStateListener);
                    //mediaManager.addMediaUpdatedVideoPlaybackStateListener(this.updatedVideoPlaybackStateListener);
                    FPVDemoApplication.getCameraInstance().setMode(SettingsDefinitions.CameraMode.MEDIA_DOWNLOAD, new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError error) {
                            if (error == null) {
                                //DJILog.e(TAG, "Set cameraMode success");
                                setResultToToast("Set cameraMode success");
                                //showProgressDialog();
                                //getFileList();
                            } else {
                                setResultToToast("Set cameraMode failed");
                                setResultToToast(error.getDescription());
                            }
                        }
                    });
                    /*if (mMediaManager.isVideoPlaybackSupported()) {
                        DJILog.e(TAG, "Camera support video playback!");
                    } else {
                        setResultToToast("Camera does not support video playback!");
                    }*/
                    //scheduler = mediaManager.getScheduler();
                }

            } else if (null != FPVDemoApplication.getCameraInstance()
                    && !FPVDemoApplication.getCameraInstance().isMediaDownloadModeSupported()) {
                setResultToToast("Media Download Mode not Supported");
            }
        }

        mediaFileList = mediaManager.getFileListSnapshot();
        /*Collections.sort(mediaFileList, new Comparator<MediaFile>() {
            @Override
            public int compare(MediaFile lhs, MediaFile rhs) {
                if (lhs.getTimeCreated() < rhs.getTimeCreated()) {
                    return 1;
                } else if (lhs.getTimeCreated() > rhs.getTimeCreated()) {
                    return -1;
                }
                return 0;
            }
        });*/

        try {
            mediaFileList.get(index).getDateCreated();
            setResultToToast("Funciona");
        } catch (IndexOutOfBoundsException e) {
            setResultToToast(e.getMessage());
        }

        //datePhoto.setText(mediaFileList.get(index).getDateCreated());
        //datePhoto.setVisibility(View.VISIBLE);

        /*mediaFileList.get(index).fetchFileData(file, null, new DownloadListener<String>() {
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
        });*/
    }

    // Method for starting recording
    private void startRecord(){

        final Camera camera = FPVDemoApplication.getCameraInstance();
        if (camera != null) {
            camera.startRecordVideo(new CommonCallbacks.CompletionCallback(){
                @Override
                public void onResult(DJIError djiError)
                {
                    if (djiError == null) {
                        showToast("Record video: success");
                    }else {
                        showToast(djiError.getDescription());
                    }
                }
            }); // Execute the startRecordVideo API
        }
    }

    // Method for stopping recording
    private void stopRecord(){

        Camera camera = FPVDemoApplication.getCameraInstance();
        if (camera != null) {
            camera.stopRecordVideo(new CommonCallbacks.CompletionCallback(){

                @Override
                public void onResult(DJIError djiError)
                {
                    if(djiError == null) {
                        showToast("Stop recording: success");
                    }else {
                        showToast(djiError.getDescription());
                    }
                }
            }); // Execute the stopRecordVideo API
        }

    }
}
