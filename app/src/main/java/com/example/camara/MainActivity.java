package com.example.camara;

import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.example.camara.utils.Constants;
import com.example.camara.utils.ImageUtils;
import com.example.camara.utils.Utils;
import com.zhuchudong.toollibrary.StatusBarUtil;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener {
    //宽度450
    TimerTask task;

    private Timer timer;
    Camera camera;
    SurfaceHolder holder;
    SurfaceView surface_camera;
    SVDraw surface_tip;

    Camera.PictureCallback currentCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        holder = surface_camera.getHolder();
        holder.setKeepScreenOn(true);
        holder.addCallback(this);
        holder.setFixedSize(1280,720);
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
    }

    private void initView() {
        StatusBarUtil.setColor(MainActivity.this, getResources().getColor(R.color.colorPrimary));
        surface_camera = (SurfaceView) findViewById(R.id.surface_camera);
        surface_tip = (SVDraw) findViewById(R.id.surface_tip);
        findViewById(R.id.btn_takepicture).setOnClickListener(this);
        findViewById(R.id.btn_again).setOnClickListener(this);

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
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//连续对焦
        camera.setParameters(parameters);
        camera.setDisplayOrientation(90);
        camera.startPreview();
        camera.cancelAutoFocus();
    }


    private final class FirstCallback implements Camera.PictureCallback {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.startPreview();
            byte[] compressDada = ImageUtils.processBitmapBytesSmaller(data, Constants.requestWidth);

            Utils.savepicture(MainActivity.this, compressDada);

            new UploadImageTask(Constants.url, compressDada, surface_tip).execute();

        }
    }

    private final class SecondCallback implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.stopPreview();
            byte[] compressDada = ImageUtils.processBitmapBytesSmaller(data, 450);

//            Utils.savepicture(MainActivity.this, compressDada);
            new UploadImageTask("http://192.168.1.133:4212/index/searcher", compressDada, surface_tip).execute();

        }
    }

}
