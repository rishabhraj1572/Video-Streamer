package com.example.demo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

public class OptionsActivity extends AppCompatActivity implements ChannelAdapter.OnItemClickListener {
    public static final String EXTRA_URL="channelImg";
    public static final String EXTRA_NAME="channelName";
    public static final String CHANNEL_LINK = "channelLink";
    public static final String TYPE = "type";
    public static final String LICENSE_URL = "license";
    public static final String ORIGIN = "origin";
    public static final String REFERER = "referer";
    public static final String COOKIE = "cookie";
    public static final String USER_AGENT = "useragent";

    private RecyclerView mRecycleView;
    private ChannelAdapter mChannelAdapter;
    private ArrayList<ChannelItem> mChannelList;
    private RequestQueue mRequestQueue;

    ProgressDialog progressBar;
    private String jsonURL;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        FullScreencall();
        CheckForUpdate();
        vpnCheck();
        progressBar=new ProgressDialog(this);
        progressBar.show();
        progressBar.setMessage("Loading");
        progressBar.setCancelable(false);

        mRecycleView=findViewById(R.id.recyclerView);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshLayout=findViewById(R.id.swipeView);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mChannelList.clear();
                urlFetch();
                progressBar.show();

            }
        });


        mChannelList=new ArrayList<>();
        mRequestQueue= Volley.newRequestQueue(this);
        urlFetch();


    }

    private void urlFetch() {
        OkHttpClient client = new OkHttpClient();
        String url = AppConfig.jsonURLFetch;
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                //.addHeader("","")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    String myResponse = response.body().string();
                    jsonURL=myResponse;
                    parseJSON();
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.dismiss();
                }
            }
        });

    }

    private void CheckForUpdate() {
        OkHttpClient client = new OkHttpClient();
        String url = AppConfig.startURL;
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                //.addHeader("","")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    //Do Nothing ... Continue
                }else{
                    update();
                }
            }

            private void update() {
                Intent intent = new Intent(OptionsActivity.this, UpdateActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });
    }

    private void parseJSON() {
        String url = jsonURL;


        JsonObjectRequest request =new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Toast.makeText(OptionsActivity_T.this, ""+response, Toast.LENGTH_SHORT).show();
                            JSONArray jsonArray = response.getJSONArray("channels");
                            for(int i=0; i<jsonArray.length();i++){
                                JSONObject item = jsonArray.getJSONObject(i);

                                String channelName = item.getString("name");
                                String channelImg = item.getString("image");
                                String channelLink = item.getString("link");
                                String type =item.getString("type");
                                String LicenseUrl=item.getString("licence_url");
                                String origin=item.getString("origin");
                                String referer = item.getString("referer");
                                String cookie=item.getString("cookie");
                                String userAgent=item.getString("useragent");



                                mChannelList.add(new ChannelItem(channelImg,channelName,channelLink,type,LicenseUrl,origin,referer,cookie,userAgent));
                            }

                            mChannelAdapter = new ChannelAdapter(OptionsActivity.this,mChannelList);
                            mRecycleView.setAdapter(mChannelAdapter);
                            progressBar.dismiss();
                            mChannelAdapter.setOnItemClickListener(OptionsActivity.this);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        });

        mRequestQueue.add(request);
//        swipeRefreshLayout.setRefreshing(false);
//        progressBar.hide();

    }

    @Override
    public void onItemClick(int position) {
        Intent playIntent=new Intent(this,PlayerActivity.class);
        ChannelItem clickedItem=mChannelList.get(position);

        playIntent.putExtra(EXTRA_NAME,clickedItem.getChannelName());
        playIntent.putExtra(EXTRA_URL,clickedItem.getImageUrl());
        playIntent.putExtra(CHANNEL_LINK,clickedItem.getChannelLink());
        playIntent.putExtra(TYPE,clickedItem.getType());
        playIntent.putExtra(LICENSE_URL,clickedItem.getLicenseUrl());
        playIntent.putExtra(ORIGIN,clickedItem.getOrigin());
        playIntent.putExtra(REFERER,clickedItem.getReferer());
        playIntent.putExtra(COOKIE,clickedItem.getCookie());
        playIntent.putExtra(USER_AGENT,clickedItem.getUserAgent());



        startActivity(playIntent);

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

    public void onBackPressed(){
        finishAffinity();
    }
    public boolean isVPNEnabled() {
        Context mContext = OptionsActivity.this;
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
