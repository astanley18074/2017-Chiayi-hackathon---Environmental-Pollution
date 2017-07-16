package com.guagua.reportgo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements API.Callback{

    Button loginButton;
    CallbackManager callbackManager;
    String email;
    String plateNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(AccessToken.getCurrentAccessToken()!=null){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            setContentView(R.layout.activity_login);
            LoginManager.getInstance().getLoginBehavior();

            callbackManager = CallbackManager.Factory.create();
            initFBLogin();
        }

    }


    private void initFBLogin(){
        loginButton = (Button) findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDialog();
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            //登入成功
            @Override
            public void onSuccess(LoginResult loginResult) {

                final AccessToken accessToken = loginResult.getAccessToken();
                //send request and call graph api
                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            //當RESPONSE回來的時候
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                String username = "";
                                String avatar = "";
                                UserData userData = new UserData();

                                try {
                                    username = object.getString("name");
                                    avatar = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                    email = object.getString("email");

                                    userData.setAvatar(avatar);
                                    userData.setName(username);
                                    userData.setEmail(email);
                                    userData.setPlateNum(plateNum);
                                    userData.setToken(accessToken.getToken());

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                API.getInstance(LoginActivity.this).insertUserData(userData,LoginActivity.this);
                            }
                        });

                //包入你想要得到的資料 送出request
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,picture");
                request.setParameters(parameters);
                request.executeAsync();
            }

            //登入取消
            @Override
            public void onCancel() {
                // App code
                Log.w(LoginActivity.class.getName(), "Cancel login...");
            }

            //登入失敗
            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.w(LoginActivity.class.getName(), "An error occurred during Facebook login.");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.print(requestCode+" / "+resultCode+" / ");
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void apiDataReturned(String response , String flag) {
        if(flag.equals(API.FLAG_INSERT_USER_DATA)) {
            if (response.equals("200")) {
                Toast.makeText(LoginActivity.this, "登入成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            } else {
                Toast.makeText(LoginActivity.this, "登入失敗: " + response, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void inputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("請輸入車牌號碼");
        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                plateNum = input.getText().toString();
                if(plateNum!=null&&plateNum.length()>0)
                    LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email"));
                else
                    Toast.makeText(LoginActivity.this,"請輸入正確車牌",Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
