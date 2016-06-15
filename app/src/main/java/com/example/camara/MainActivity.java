package com.example.camara;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.example.camara.utils.ImageUtils;
import com.example.camara.utils.Utils;
import com.zhuchudong.toollibrary.BitmapUtil;
import com.zhuchudong.toollibrary.L;
import com.zhuchudong.toollibrary.StatusBarUtil;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener {
    //宽度450
    private TimerTask task;

    private Timer timer;
    private Camera camera;
    private SurfaceHolder holder;
    private SurfaceView surface_camera;
    private SVDraw surface_tip;

    private Camera.PictureCallback currentCallBack;
    private OrientationEventListener mOrientationListener;
    private int screenOritation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initOrientationListener();

        holder = surface_camera.getHolder();
        holder.setKeepScreenOn(true);
        holder.addCallback(this);
//        holder.setFixedSize(1280,720);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


    }


    @Override
    protected void onStart() {
        super.onStart();

        switch (getIntent().getIntExtra("type", 1)) {
            case 1:
                findViewById(R.id.btn_linearlayout).setVisibility(View.GONE);

                currentCallBack = new FirstCallback();
                timer = new Timer();
                initSchedule();

                break;
            case 2:
                currentCallBack = new SecondCallback();
                findViewById(R.id.btn_linearlayout).setVisibility(View.VISIBLE);

                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        mOrientationListener.disable();
    }

    private void initView() {
        StatusBarUtil.setColor(MainActivity.this, getResources().getColor(R.color.colorPrimary));
        surface_camera = (SurfaceView) findViewById(R.id.surface_camera);
        surface_tip = (SVDraw) findViewById(R.id.surface_tip);
        findViewById(R.id.btn_takepicture).setOnClickListener(this);
        findViewById(R.id.btn_again).setOnClickListener(this);

    }

    private void initOrientationListener() {
        mOrientationListener = new OrientationEventListener(this,
                SensorManager.SENSOR_DELAY_NORMAL) {

            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation == -1) {
                } else if (orientation < 45 || orientation > 315) {
                    screenOritation = Constants.TOP;
                } else if (orientation < 135 && orientation > 45) {
                    screenOritation = Constants.LEFT;
                } else if (orientation < 225 && orientation > 135) {
                    screenOritation = Constants.BOTTOM;
                } else if (orientation < 315 && orientation > 225) {
                    screenOritation = Constants.RIGHT;
                } else {
                }
            }
        };

        if (mOrientationListener.canDetectOrientation() == true) {
            mOrientationListener.enable();
        } else {
            L.e("Cannot detect orientation");
            mOrientationListener.disable();
        }
    }

    public void initSchedule() {
        task = new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (camera != null) {
                            camera.takePicture(null, null, new FirstCallback());
                        }
                    }
                });
            }
        };
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(task, 2000, 2000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_takepicture:
                camera.takePicture(null, null, currentCallBack);
                break;
            case R.id.btn_again:
                camera.startPreview();
                break;
            default:
                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (camera == null) {
            camera = Camera.open();
            try {
                camera.setPreviewDisplay(holder);
                initCamera();
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        initCamera();
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    private void initCamera() {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPictureFormat(PixelFormat.JPEG);
//        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_STEADYPHOTO);
//        parameters.setPictureSize(640,480);
//        parameters.setPreviewSize(1024,768);
        List<Camera.Size> previewsizes = parameters.getSupportedPreviewSizes();
        List<Camera.Size> picturesizes = parameters.getSupportedPictureSizes();


        if (previewsizes != null && previewsizes.size() > 0) {
            Camera.Size maxSize = previewsizes.get(previewsizes.size() - 1);
            for (int i = 0; i < previewsizes.size() - 1; i++) {
                if ((previewsizes.get(i).width + previewsizes.get(i).height) > (maxSize.width + maxSize.height)) {
                    maxSize =previewsizes.get(i);
                }
            }
            parameters.setPreviewSize(maxSize.width, maxSize.height);
            parameters.setPictureSize(maxSize.width, maxSize.height);
            L.e("setpreview&picturesizes   " + maxSize.width + "   " + maxSize.height);

        }

        for (int i = 0; i < previewsizes.size(); i++) {
            L.i("previewsizes   " + previewsizes.get(i).width + "   " + previewsizes.get(i).height);
        }
        for (int i = 0; i < picturesizes.size(); i++) {
            L.i("picturesizes   " + picturesizes.get(i).width + "   " + picturesizes.get(i).height);
        }


//        int width = surface_camera.getWidth();
//        int height = surface_camera.getHeight();
//        L.i("surface_camera  " + width + "   " + height);
//        parameters.setPictureSize(width, height);

        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//连续对焦
        camera.setParameters(parameters);
        camera.setDisplayOrientation(90);
        camera.startPreview();
        camera.cancelAutoFocus();
    }


    private final class NewCallback implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Utils.savepicture(MainActivity.this, data);
        }
    }


    private final class FirstCallback implements Camera.PictureCallback {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.startPreview();


            byte[] compressDada = ImageUtils.processBitmapBytesSmaller2(data, Constants.requestWidth, screenOritation);

            Utils.savepicture(MainActivity.this,compressDada);
            new UploadImageTask(Constants.url, compressDada, surface_tip).execute();

        }
    }

    private final class SecondCallback implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.stopPreview();

            byte[] compressDada = ImageUtils.processBitmapBytesSmaller2(data, Constants.requestWidth, screenOritation);

            Utils.savepicture(MainActivity.this, compressDada);
            new UploadImageTask("http://192.168.1.133:4212/index/searcher", compressDada, surface_tip).execute();

        }
    }


}
