package com.guagua.reportgo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by a63098233 on 2017/7/14.
 */

public class API {

    private static API instance;
    private Context context;
    private static RequestQueue requestQueue;
    private static boolean isShowingDialog;
    private static String URL = "http://140.136.149.147:8080";

    public static API getInstance(Context context) {

        if (instance == null){
            instance = new API();
            instance.requestQueue = Volley.newRequestQueue(context);
        }

        if(context!=null)
            if(!isConnected(context)&&!isShowingDialog){
                isShowingDialog = true;
                ShowMsgDialog(context,"請檢查您的網路連線是否正常");
            }

        return instance;
    }

    public static final String FLAG_INSERT_USER_DATA = "insertUserData";
    public static final String FLAG_GET_USER_DATA = "getUserData";
    public static final String FLAG_GET_IMAGE_VIA_URL = "getImageViaUrl";
    public static final String FLAG_INSERT_REPORT_DATA = "insertReportData";
    public static final String FLAG_GET_REPORT_DATA = "getReportData";

    public interface Callback{
        void apiDataReturned(String response , String flag);
    }

    public void insertUserData(final UserData data , final Callback callback){
        String url = URL+"/report_go/insertUserData.php";
        final Response.Listener<String> responseListener;
        final Response.ErrorListener errorListener;
        final StringRequest request;

        responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.apiDataReturned(response , FLAG_INSERT_USER_DATA);
            }
        };

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.apiDataReturned(error.toString(),FLAG_INSERT_USER_DATA);
            }
        };

        request = new StringRequest(Request.Method.POST, url, responseListener, errorListener){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Gson gson = new Gson();

                String json = gson.toJson(data);

                Map<String, String> param = new HashMap<String, String>();
                param.put("user_data", json);
                return param;

            }
        };

        requestQueue.add(request);
    }


    public void getUserData(final String email, final Callback callback){
        String url = URL + "/report_go/getUserData.php?email="+email;
        Response.Listener<String> responseListener;
        Response.ErrorListener errorListener;
        StringRequest request;

        responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    callback.apiDataReturned(response,FLAG_GET_USER_DATA);
                }catch(Exception e){
                    callback.apiDataReturned(e.toString(),FLAG_GET_USER_DATA);
                }
            }
        };

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.apiDataReturned(error.toString(),FLAG_GET_USER_DATA);
            }
        };

        request = new StringRequest(Request.Method.GET, url, responseListener, errorListener);

        requestQueue.add(request);
    }

    public void insertReportData(final Report data , final Callback callback){
        String url = URL+"/report_go/insertReportData.php";
        final Response.Listener<String> responseListener;
        final Response.ErrorListener errorListener;
        final StringRequest request;

        responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.apiDataReturned(response , FLAG_INSERT_REPORT_DATA);
            }
        };

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.apiDataReturned(error.toString(),FLAG_INSERT_REPORT_DATA);
            }
        };

        request = new StringRequest(Request.Method.POST, url, responseListener, errorListener){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Gson gson = new Gson();

                String json = gson.toJson(data);

                Map<String, String> param = new HashMap<String, String>();
                param.put("report_data", json);
                return param;
            }
        };

        requestQueue.add(request);
    }


    public void getImageViaUrl(final String url, final ImageView targetView ){

        ImageRequest request = new ImageRequest(
                url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        if(targetView!=null)
                            targetView.setImageBitmap(response);
                    }
                },
                0,
                0,
                ImageView.ScaleType.FIT_START,
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.w(getClass().getName(),"Image load failed!");
                    }
                });

        requestQueue.add(request);
    }

    private static void ShowMsgDialog(final Context context, String Msg) {
        AlertDialog.Builder MyAlertDialog = new AlertDialog.Builder(context);
        MyAlertDialog.setTitle("網路錯誤");
        MyAlertDialog.setMessage(Msg);
        DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //如果不做任何事情 就會直接關閉 對話方塊
                Activity activity;
                if(context!=null) {
                    activity = (Activity) context;
                    activity.onBackPressed();
                }
                dialog.dismiss();
                isShowingDialog = false;
            }
        };
        MyAlertDialog.setNeutralButton("確定",OkClick );
        MyAlertDialog.show();
    }

    private static boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
}
