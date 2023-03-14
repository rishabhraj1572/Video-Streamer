package com.example.demo;

import static com.example.demo.R.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_SCREEN_TIME_OUT = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        FullScreencall();
        Splash();
        vpnCheck();

    }

    private void Splash() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String url = AppConfig.startURL;
                Request request = new Request.Builder()
                        .url(url)
                        //.addHeader("","")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                String myResponse = response.body().string();
                                if(myResponse.equals("1")){
                                start();
                                }else if(myResponse.equals("2")){
                                    maintainance();
                                }
                            }else{
                                update();
                            }
                        }
                    });


                // Intent is used to switch from one activity to another.
//                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
//                startActivity(intent); // invoke the SecondActivity.
//                finish(); // the current activity will get finished.
            }
        }, SPLASH_SCREEN_TIME_OUT);
    }


    private void maintainance() {
        Intent intent=new Intent(this, MaintenanceActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    public void start(){
            Intent intent = new Intent(this, OptionsActivity.class);
            startActivity(intent);
            finishAffinity();

    }
    public void update(){
            Intent intent = new Intent(this, UpdateActivity.class);
            startActivity(intent);
            finishAffinity();

        //Toast.makeText(getApplicationContext(),"Text Changed Successfully",Toast.LENGTH_SHORT).show();

    }
    public void onBackPressed() {
        finishAffinity();
    }
    public void FullScreencall() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
    public boolean isVPNEnabled() {
        Context mContext = MainActivity.this;
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
        } else {
            return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_VPN).isConnectedOrConnecting();
        }
    }
    public void vpnCheck(){
        if(isVPNEnabled()==true){
            finishAffinity();
        }
    }

}


