package com.guagua.reportgo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by a63098233 on 2016/12/21.
 */
public class PermissionTool {

    private final int REQUEST_LOCATION = 0;
    private final int REQUEST_CODE_READ_PHONE_STATE = 1;
    private final int REQUEST_ALL = 2;
    private final int REQUEST_READ_AND_WRITE_EXTERNAL_STORAGE = 3;

    private Context context;



    public boolean getAllPermission(Context context){
        int writePermission = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        int readPermission = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);

        int writeStoragePermission = ActivityCompat.checkSelfPermission(context , Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readStoragePermission = ActivityCompat.checkSelfPermission(context , Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED
                ||writeStoragePermission != PackageManager.PERMISSION_GRANTED || readStoragePermission != PackageManager.PERMISSION_GRANTED ) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    (Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION ,Manifest.permission.ACCESS_COARSE_LOCATION ,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_ALL);
            return false;
        } else {
            return true;
        }
    }


}
