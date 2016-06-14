package com.example.camara;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.zhuchudong.toollibrary.L;

import java.util.ArrayList;


/**
 * Created by Administrator on 2016/5/6.
 */
public class SVDraw extends SurfaceView implements SurfaceHolder.Callback {
    public SurfaceHolder mSurfaceHolder;
    private int mWidth;
    private int mHeight;
    private ArrayList<LocationBean> currentLocatios;

    public SVDraw(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        setZOrderOnTop(true);
    }

    public SVDraw(Context context) {
        super(context);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                L.i("点击了位置 "+event.getX()+"  "+event.getY());
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void drawlocation(ArrayList<LocationBean> locationBeanArrayList) {
        Canvas canvas = mSurfaceHolder.lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            Paint mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setColor(Color.GREEN);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(4);
            if (locationBeanArrayList != null && locationBeanArrayList.size() > 0) {
                for (int i = 0; i < locationBeanArrayList.size(); i++) {
                    LocationBean locationBean = locationBeanArrayList.get(i);
                    canvas.drawRect(locationBean.getX(), locationBean.getY(), locationBean.getX() + locationBean.getWidth()
                            , locationBean.getY() + locationBean.getHeight(), mPaint);
                }
            }
            mSurfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public void clearDraw() {
        Canvas canvas = mSurfaceHolder.lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mSurfaceHolder.unlockCanvasAndPost(canvas);
        }

    }
}
