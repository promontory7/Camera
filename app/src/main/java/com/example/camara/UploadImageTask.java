package com.example.camara;

import android.os.AsyncTask;
import android.util.Log;
import android.view.SurfaceView;

import com.example.camara.utils.Constants;
import com.example.camara.utils.ImageUtils;
import com.zhuchudong.toollibrary.L;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/7.
 */
public class UploadImageTask extends AsyncTask {
    SVDraw surfaceView;
    HttpClient httpClient;
    HttpResponse responseHttpClient;
    HttpEntity entity;
    HttpPost httpPost;
    String url;
    byte[] data;

    float xscare = 0;
    float yscare = 0;

    public UploadImageTask(String url, byte[] data, SVDraw surfaceView) {
        this.surfaceView = surfaceView;
        this.url = url;
        this.data = data;
        try {
            httpClient = new DefaultHttpClient();
            httpPost = new HttpPost(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initScare();
    }

    public void initScare() {
        xscare = (float) surfaceView.getWidth() / 450;
        yscare = surfaceView.getHeight()/ Constants.height;
        L.e("yscare" + yscare + "    xscare" + xscare);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        ByteArrayEntity byteArrayEntity = new ByteArrayEntity(data);
        httpPost.setEntity(byteArrayEntity);
        try {
            responseHttpClient = httpClient.execute(httpPost);
            entity = responseHttpClient.getEntity();

            if (entity != null) {
                JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity));
                if (jsonObject != null && jsonObject.has("bounding_rects")) {
                    JSONArray locations = jsonObject.optJSONArray("bounding_rects");
                    ArrayList<LocationBean> locationList = new ArrayList();
                    if (locations != null && locations.length() > 0) {
                        for (int i = 0; i < locations.length(); i++) {
                            JSONObject locationJson = locations.optJSONObject(i);
                            LocationBean locationBean = new LocationBean();
                            if (xscare == 0||yscare==0) {
                                initScare();
                            }
                            locationBean.setX((int) (locationJson.optInt("x") * xscare));
                            locationBean.setY((int) (locationJson.optInt("y") * yscare));
                            locationBean.setWidth((int) (locationJson.optInt("width") * xscare));
                            locationBean.setHeight((int) (locationJson.optInt("height") * yscare));

                            locationList.add(locationBean);
                        }
                    }
                    return locationList;
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
//        surfaceView.clearDraw();
        if (o == null) return;
        ArrayList<LocationBean> locationBeanArrayList = (ArrayList<LocationBean>) o;
        if (locationBeanArrayList == null) return;
        Log.e("size", "位置数量:" + locationBeanArrayList.size());
        if (locationBeanArrayList != null && locationBeanArrayList.size() > 0) {
            for (int i = 0; i < locationBeanArrayList.size(); i++) {
                LocationBean locationBean = locationBeanArrayList.get(i);
                Log.e("locationBean  " + i + "   ,", locationBean.toString());
            }
        }
        surfaceView.drawlocation(locationBeanArrayList);
    }
}
