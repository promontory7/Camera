package com.example.camara.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/5/8.
 */
public class Utils {
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    final static String TAG = "Utils";

    public static boolean isScreenOriatationPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public static boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    public static File getOutputMediaFile(Context context, int type) {
        File mediaStorageDir = null;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            mediaStorageDir = new File(context.getExternalCacheDir(), "MyCamera");

            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.e(TAG, "创建文件目录出错");
                    return null;
                } else {
                    Log.e(TAG, "文件目录创建成功：" + mediaStorageDir);
                }
            }
        } else {
            Log.e(TAG, "没有内存卡??");
        }


        String timeStamp = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMAGE" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VIDEO" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public static String savepicture(Context context, byte[] bytes) {
        File pictureFile = Utils.getOutputMediaFile(context, Utils.MEDIA_TYPE_IMAGE);
        if (!pictureFile.exists()) {
            try {
                pictureFile.createNewFile();
                Log.i(TAG, "图片文件创建成功");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "图片文件创建失败");
            }
        }
        if (pictureFile == null) {
            Log.e(TAG, "图片文件为空");
            return null;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);

            fos.write(bytes);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pictureFile.getAbsolutePath();
    }
}
