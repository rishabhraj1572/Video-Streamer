package com.example.demo;

import static com.example.demo.OptionsActivity.CHANNEL_LINK;
import static com.example.demo.OptionsActivity.COOKIE;
import static com.exaple.demo.OptionsActivity.EXTRA_NAME;
import static com.example.demo.OptionsActivity.EXTRA_URL;
import static com.example.demo.OptionsActivity.LICENSE_URL;
import static com.example.demo.OptionsActivity.ORIGIN;
import static com.example.demo.OptionsActivity.REFERER;
import static com.example.demo.OptionsActivity.TYPE;
import static com.example.demo.OptionsActivity.USER_AGENT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback;
import com.google.android.exoplayer2.drm.LocalMediaDrmCallback;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.example.demo.dialog.TrackSelectionDialog;

import java.util.Map;


public class PlayerActivity extends AppCompatActivity {

    StyledPlayerView playerView;
    ExoPlayer exoPlayer;
    ImageView zoom;
    ImageView settings;
    private boolean isShowingTrackSelectionDialog;
    private DefaultTrackSelector trackSelector;
    PowerManager.WakeLock wakeLock;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Intent intent =getIntent();
        String channelImage=intent.getStringExtra(EXTRA_URL);
        String channelName=intent.getStringExtra(EXTRA_NAME);
        String channelLink=intent.getStringExtra(CHANNEL_LINK);
        String type =intent.getStringExtra(TYPE);
        String LicenseUrl= intent.getStringExtra(LICENSE_URL);
        String Origin = intent.getStringExtra(ORIGIN);
        String Referer = intent.getStringExtra(REFERER);
        String Cookie = intent.getStringExtra(COOKIE);
        String userAgent = intent.getStringExtra(USER_AGENT);

        playVideo(type,userAgent,channelLink,LicenseUrl,Origin,Referer,Cookie);

        TextView videoTitle=findViewById(R.id.videoTitle);
        videoTitle.setText(channelName);

        FullScreencall();
        vpnCheck();

        //for no screen off while playing the video
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Player:No Sleep");
        wakeLock.acquire(300*60*1000L /*300 minutes*/);
        //Back inside player
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView back=findViewById(R.id.Back_btn_img);
        back.setOnClickListener(view -> onBackPressed());

        //zoom button
        zoom=findViewById(R.id.img_full_scr);
        zoom.setOnClickListener(view->adjust_screen());

        //play pause
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView play = findViewById(R.id.play_vid);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView pause= findViewById(R.id.pause_vid);
        pause.setOnClickListener(view->{
            exoPlayer.play();
            pause.setVisibility(View.GONE);
            play.setVisibility(View.VISIBLE);
        });
        play.setOnClickListener(view->{
            exoPlayer.pause();
            play.setVisibility(View.GONE);
            pause.setVisibility(View.VISIBLE);
        });

        //setting button
        settings=findViewById(R.id.img_settings);
        settings.setOnClickListener(view->video_options());


    }

    private void playVideo(String type,String userAgent,String MediaURL,String LicenseURL,String origin,String referer,String cookie){
        Toast.makeText(this, ""+getIntent().getStringExtra(EXTRA_NAME), Toast.LENGTH_SHORT).show();
        if(type.equals("dash")){
            PlayerActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            playerView = findViewById(R.id.exoplayer);
            //Uri videourl = Uri.parse(str);

            DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(
                    userAgent,
                    null /* listener */,
                    DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                    DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                    true /* allowCrossProtocolRedirects */
            );

            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                    this,
                    null /* listener */,
                    httpDataSourceFactory
            );

            httpDataSourceFactory.setDefaultRequestProperties(Map.of("origin",origin));
            httpDataSourceFactory.setDefaultRequestProperties(Map.of("referer",referer));
            httpDataSourceFactory.setDefaultRequestProperties(Map.of("cookie",cookie));

            trackSelector = new DefaultTrackSelector(this);
            exoPlayer = new SimpleExoPlayer.Builder(PlayerActivity.this).setTrackSelector(trackSelector).build();
            playerView.setPlayer(exoPlayer);
            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this);
            SimpleExoPlayer player = new SimpleExoPlayer.Builder(this, renderersFactory).build();
            MediaSource mediaSource = buildMediaSource(this, MediaURL, LicenseURL, null,origin,referer,cookie,userAgent);

            exoPlayer.setMediaSource(mediaSource);
            exoPlayer.prepare();
            exoPlayer.play();
            playerView.setKeepScreenOn(true);
            //wakeLock.acquire(300*60*1000L /*300 minutes*/);
        }else if(type.equals("m3u8")){
            PlayerActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            playerView = findViewById(R.id.exoplayer);
            Uri videourl = Uri.parse(MediaURL);

            DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(
                    userAgent,
                    null /* listener */,
                    DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                    DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                    true /* allowCrossProtocolRedirects */
            );

            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                    this,
                    null /* listener */,
                    httpDataSourceFactory
            );

            httpDataSourceFactory.setDefaultRequestProperties(Map.of("origin",origin));
            httpDataSourceFactory.setDefaultRequestProperties(Map.of("referer",referer));
            httpDataSourceFactory.setDefaultRequestProperties(Map.of("cookie",cookie));

            trackSelector = new DefaultTrackSelector(this);
            exoPlayer = new SimpleExoPlayer.Builder(PlayerActivity.this).setTrackSelector(trackSelector).build();
            playerView.setPlayer(exoPlayer);
            //Toast.makeText(getApplicationContext(),link,Toast.LENGTH_SHORT).show();
            HlsMediaSource mediaItem = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videourl));
            exoPlayer.setMediaSource(mediaItem);
            exoPlayer.prepare();
            exoPlayer.play();
            playerView.setKeepScreenOn(true);
//            wakeLock.acquire(300*60*1000L /*300 minutes*/);
        }else if(type.equals("external")){
            String url = MediaURL;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            finish();
        }else{
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void video_options() {
        if (!isShowingTrackSelectionDialog
                && TrackSelectionDialog.willHaveContent(trackSelector)) {
            isShowingTrackSelectionDialog = true;
            TrackSelectionDialog trackSelectionDialog =
                    TrackSelectionDialog.createForTrackSelector(
                            trackSelector,
                            /* onDismissListener= */ dismissedDialog -> isShowingTrackSelectionDialog = false);
            trackSelectionDialog.show(getSupportFragmentManager(), /* tag= */ null);


        }
    }
    private void adjust_screen() {
        TextView text_1=findViewById(R.id.text_01);
        if(playerView.getResizeMode() == AspectRatioFrameLayout.RESIZE_MODE_FILL) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            zoom.setImageDrawable(getDrawable(R.drawable.ic_baseline_fullscreen_24));
            text_1.setText("Zoom");
        } else if(playerView.getResizeMode() == AspectRatioFrameLayout.RESIZE_MODE_ZOOM) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
            zoom.setImageDrawable(getDrawable(R.drawable.fit_screen));
            text_1.setText("Fit");
        } else if(playerView.getResizeMode() == AspectRatioFrameLayout.RESIZE_MODE_FIT) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
            zoom.setImageDrawable(getDrawable(R.drawable.ic_baseline_fullscreen_exit_24));
            text_1.setText("Fill");
        }
        else {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            zoom.setImageDrawable(getDrawable(R.drawable.ic_baseline_fullscreen_24));

        }
    }
    private void pausePlayer(){
        if(exoPlayer!= null){
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.getPlaybackState();
        }
    }
    private void startPlayer(){
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.getPlaybackState();
    }
    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer.clearVideoSurface();
            exoPlayer = null;
        }
    }
    Boolean isBackPressed = false;
    @Override
    public void onBackPressed() {
        isBackPressed = true;
        releasePlayer();
        finish();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(!isBackPressed) {
            pausePlayer();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire(300*60*1000L /*300 minutes*/);
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(exoPlayer!=null){
        playerView.setKeepScreenOn(false);
        }
        wakeLock.release();
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

    private MediaSource buildMediaSource(Context context, String videoUrl, String licenseServer, String licenseToken,String origin,String referer,String cookie,String userAgent) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, userAgent);
        return new DashMediaSource.Factory(
                new DefaultDashChunkSource.Factory(dataSourceFactory), dataSourceFactory)
                .setDrmSessionManager(buildDrmSessionManager(userAgent, licenseServer, licenseToken,origin,referer,cookie))
                .createMediaSource(new MediaItem.Builder().setUri(videoUrl).build());
    }

    private DefaultDrmSessionManager buildDrmSessionManager(String userAgent, String licenseUrl, String drmToken,String origin,String referer,String cookie) {
        String key = "11:11";
        if(licenseUrl.contains(":")){
            key =licenseUrl;
        }
        String keys[]=key.split(":");
        String keyString = "{ \"keys\":[ { \"kty\":\"oct\", \"k\":\""+convertToHex64(keys[1])+"\", \"kid\":\""+convertToHex64(keys[0])+"\" } ], \"type\":\"temporary\" }";
        LocalMediaDrmCallback drmCallback1 = new LocalMediaDrmCallback(keyString.getBytes());
        HttpMediaDrmCallback drmCallback = new HttpMediaDrmCallback(licenseUrl,
                new DefaultHttpDataSourceFactory(userAgent, null));
        drmCallback.setKeyRequestProperty("origin",origin);
        drmCallback.setKeyRequestProperty("referer",referer);
        drmCallback.setKeyRequestProperty("cookie",cookie);
        // Here the license token is attached to license request
        if (drmToken != null) {
            drmCallback.setKeyRequestProperty("X-AxDRM-Message", drmToken);
        }

        return new DefaultDrmSessionManager.Builder()
                .setUuidAndExoMediaDrmProvider(C.CLEARKEY_UUID, FrameworkMediaDrm.DEFAULT_PROVIDER)
                .build(drmCallback1);
    }
    public boolean isVPNEnabled() {
        Context mContext = PlayerActivity.this;
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
            releasePlayer();
        }
    }

    public String convertToHex64(String input){

        String hexString = input;

        // Convert hex string to byte array
        byte[] bytes = hexStringToByteArray(hexString);

        // Encode byte array as Base64 string
        String base64String1 = Base64.encodeToString(bytes, Base64.NO_WRAP);

        String base64String = base64String1.substring(0,base64String1.length()-2);
        return base64String;
    }
    public static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }
}
