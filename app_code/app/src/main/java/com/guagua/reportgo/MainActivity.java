package com.guagua.reportgo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.text.Layout;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , API.Callback , View.OnClickListener{

    LinearLayout background ;
    CircleImageView avatar;
    TextView name , plateNum;
    Button b1,b2,b3;

    UserData userData = new UserData();
    static MainActivity instance;

    public static MainActivity getInstance(Context context) {
        return instance;
    }

    public UserData getUserData() {
        return userData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new PermissionTool().getAllPermission(this);

    }

    private void findView(View view){
        avatar = (CircleImageView)view.findViewById(R.id.avatar);
        name = (TextView)view.findViewById(R.id.name);
        plateNum = (TextView)view.findViewById(R.id.plate_num);
        b1 = (Button)findViewById(R.id.record);
        b2 = (Button)findViewById(R.id.upload);
        b3 = (Button)findViewById(R.id.notif);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void initDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        background = (LinearLayout)findViewById(R.id.background);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // Do whatever you want here
                Toast.makeText(MainActivity.this,"CLOSE" , Toast.LENGTH_SHORT).show();
                //background.setBackground(null);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Do whatever you want here
                Toast.makeText(MainActivity.this,"OPEN" , Toast.LENGTH_SHORT).show();
                //background.setBackground(BlurUtil.drawable_Blur(MainActivity.this ,View2Bitmap(background)));
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        if(headerView!=null){
            findView(headerView);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public Bitmap View2Bitmap(View v){

        v.setDrawingCacheEnabled(true);

        v.buildDrawingCache();

        Bitmap bm = v.getDrawingCache();

        return bm;
    }

    private void refreshUI(UserData userData){
        //Uri uri = Uri.parse(userData.getAvatar());
        API.getInstance(instance).getImageViaUrl(userData.getAvatar(),avatar);
        name.setText(userData.getName());
        plateNum.setText(userData.getPlateNum());
    }

    @Override
    public void apiDataReturned(String response , String flag) {
        if(flag.equals(API.FLAG_GET_USER_DATA)) {
            userData = new Gson().fromJson(response, UserData.class);
            refreshUI(userData);
        }
    }

    private void storeEmail(String email){
        SharedPreferences preference = instance.getPreferences(Activity.MODE_PRIVATE);
        preference.edit().putString("email", email).apply();
    }

    private String getEmail(){
        SharedPreferences preference = instance.getPreferences(Activity.MODE_PRIVATE);
        String email = preference.getString("email", "");
        return email;
    }

    @Override
    public void onClick(View v) {
        if(v==b1){

        }
        else if(v==b2){
            Intent intent = new Intent(MainActivity.this , ShotActivity.class);
            startActivity(intent);
        }
        else if(v==b3){

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    instance = this;
                    initDrawer();
                    String email = this.getIntent().getStringExtra("email");
                    if (email != null)
                        storeEmail(email);
                    else
                        email = getEmail();
                    API.getInstance(instance).getUserData(email, this);

                } else {
                    Toast.makeText(this,"請至應用程式設定裡給予指定權限",Toast.LENGTH_SHORT).show();
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
