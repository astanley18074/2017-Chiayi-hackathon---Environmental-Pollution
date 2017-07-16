package com.guagua.reportgo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.guagua.reportgo.helpers.DocumentHelper;
import com.guagua.reportgo.imgurmodel.ImageResponse;
import com.guagua.reportgo.imgurmodel.Upload;
import com.guagua.reportgo.services.UploadService;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ShotActivity extends AppCompatActivity implements View.OnClickListener , API.Callback ,Callback<ImageResponse> {

    ImageView camera ;
    Button  date ;
    Button send ;
    EditText address ;
    Report report = new Report();
    String ChooseDate;

    public final int SELECT_PICTURE_REQUEST_CODE = 0x01;
    public static Uri outputFileUri;

    ShotActivity getInstance(){
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shot);
        findView();

    }

    void findView(){
        camera = (ImageView)findViewById(R.id.camera);
        date = (Button)findViewById(R.id.date);
        send = (Button)findViewById(R.id.send);
        address = (EditText) findViewById(R.id.address);
        camera.setOnClickListener(this);
        date.setOnClickListener(this);
        send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==camera){
            openImageIntent();
        }
        else if(v==date){
            setAlert();
        }
        else if(v==send){
            createUpload(outputFileUri);
        }
    }

    private void openImageIntent() {

        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "ReportGO" + File.separator + "ReportPhoto" + File.separator);
        root.mkdirs();
        final String fname = "reportPic";
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getInstance().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(sdImageMainDirectory));
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        getInstance().startActivityForResult(chooserIntent, SELECT_PICTURE_REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE_REQUEST_CODE) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;
                if (isCamera) {
                    selectedImageUri = outputFileUri;
                    //API.getInstance().saveShopAvatar(MainActivity.getAccountInfo().getShopID(), selectedImageUri, this);

                } else {
                    selectedImageUri = data == null ? null : data.getData();
                    outputFileUri = selectedImageUri;
                    //API.getInstance().saveShopAvatar(MainActivity.getAccountInfo().getShopID(), selectedImageUri, this);
                }

                try {
                    Bitmap image = Util.ResizeImageUri(getInstance(),outputFileUri);

                    camera.setImageBitmap(image);
                    //free memory
                    image = null;
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void apiDataReturned(String response, String flag) {
        if(flag.equals(API.FLAG_INSERT_REPORT)){
            if(response.equals("200")){
                Toast.makeText(ShotActivity.this, "檢舉成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void createUpload(Uri uri) {

        String filePath = DocumentHelper.getPath(this, uri);
        //Safety check to prevent null pointer exception
        if (filePath == null || filePath.isEmpty()) return;
        File image = new File(filePath);

        Upload upload = new Upload();
        upload.image = image;

        new UploadService(this).Execute(upload,ShotActivity.this);
    }

    @Override
    public void success(ImageResponse imageResponse, Response response) {
        report.setPic(imageResponse.data.link);
        report.setAddress(address.getText().toString());
        report.setDate(date.getText().toString());
    }

    @Override
    public void failure(RetrofitError error) {
        Toast.makeText(this,"上傳失敗",Toast.LENGTH_SHORT);
    }

    private void setAlert(){
        final String Title = "選擇日期";

        //-----------取得 Layout reference----------
        LayoutInflater inflater = LayoutInflater.from(getInstance());
        final View view = inflater.inflate(R.layout.calendar_alert, null);
        CalendarView calendar = (CalendarView)view.findViewById(R.id.calendarView);
        ChooseDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getDate());
        Calendar day=Calendar.getInstance();

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String m = "" + (month + 1);
                if (m.length() == 1) m = "0" + m;
                String d = "" + dayOfMonth;
                if (d.length() == 1) d = "0" + d;
                ChooseDate = year + "-" + m + "-" + d;


            }
        });
        //-----------產生視窗--------
        AlertDialog.Builder builder = new AlertDialog.Builder(getInstance());
        builder.setTitle(Title);
        builder.setView(view);
        builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                date.setText(ChooseDate);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();

    }
}
