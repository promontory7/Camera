package com.example.camara.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.example.camara.MainActivity;
import com.zhuchudong.toollibrary.BitmapUtil;
import com.zhuchudong.toollibrary.L;

import java.io.ByteArrayOutputStream;

/**
 * Created by Administrator on 2016/5/9.
 */
public class ImageUtils {

    //计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight, boolean isportrait) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;


        final int heightRatio = Math.round((float) height / (float) reqHeight);
        final int widthRatio = Math.round((float) width / (float) reqWidth);

        if (isportrait) {
            inSampleSize = heightRatio;
        } else {
            inSampleSize = widthRatio;
        }

        if (inSampleSize <= 0) {
            inSampleSize = 1;
        }

        L.e("height：" + height + "width：" + width + "   reqHeight" + reqHeight + "     inSampleSize" + inSampleSize);
        return inSampleSize;
    }

    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static byte[] processBitmapBytesSmaller(byte[] data, int width, boolean isportrait) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        options.inSampleSize = calculateInSampleSize(options, width, width, isportrait);
        options.inJustDecodeBounds = false;

        Bitmap smallBitmap = null;
        if (isportrait) {
            smallBitmap = adjustPhotoRotation(BitmapFactory.decodeByteArray(data, 0, data.length, options), 90);
        } else {
            smallBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        double sacle = ((double) smallBitmap.getWidth()) / width;
        int height = (int) (((double) smallBitmap.getHeight()) / sacle);
        Constants.height = height;
        Bitmap lastBitmap = Bitmap.createScaledBitmap(smallBitmap, width, height, true);
        L.e("最后上传的图片：  height : " + lastBitmap.getHeight() + "    width :" + lastBitmap.getWidth());
        lastBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
        return bos.toByteArray();
    }

    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static byte[] processBitmapBytesSmaller2(byte[] data, int width, boolean isportrait) {
        Bitmap beginBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        Bitmap normalBitmap = BitmapUtil.rotate(beginBitmap, 90);
        Bitmap compressBitmap =BitmapUtil.compressImage(normalBitmap,200);
        Bitmap completeBitmap = BitmapUtil.scalewidth(compressBitmap, width);
        L.e("completeBitmap  "+completeBitmap.getWidth()+"   "+completeBitmap.getHeight());
        return BitmapUtil.getBytesFromBitmap(completeBitmap);
    }

    public static Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {
        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        try {
            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
            return bm1;
        } catch (OutOfMemoryError ex) {
        }

        return null;
    }
}
