package com.cootv.ni;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import androidx.annotation.RequiresApi;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SizeF;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;

import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.cootv.ni.utils.SwipeGestureListener;
import com.cootv.ni.utils.TutoShowcase;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.cootv.ni.adapters.HomePageAdapter;
import com.cootv.ni.adapters.LiveTvHomeAdapter;
import com.cootv.ni.adapters.ServerApater;
import com.cootv.ni.models.CommonModels;
import com.cootv.ni.utils.ApiResources;
import com.cootv.ni.utils.BannerAds;
import com.cootv.ni.utils.PopUpAds;
import com.cootv.ni.utils.ToastMsg;
import com.cootv.ni.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.graphics.BitmapFactory;
import android.widget.Toast;


public class DetailsActivity extends AppCompatActivity {

    private TextView tvName,tvRelease,tvDes,tvRelated;


    private RecyclerView rvServer,rvRelated;

    public static RelativeLayout lPlay;


    private ServerApater serverAdapter;
    private HomePageAdapter relatedAdapter;
    private LiveTvHomeAdapter relatedTvAdapter;

    private List<CommonModels> listRelated =new ArrayList<>();
    private List<CommonModels> listTV =new ArrayList<>();

    public static LinearLayout llBottom,llBottomParent;

    private SwipeRefreshLayout swipeRefreshLayout;

    private String type="",id="", _serverType="";
    private boolean isLoaded;
    private ImageView imgAddFav;
    private Integer  adcount=0;
    public static ImageView imgBack;

    private String V_URL = "";
    private String stream_label_rta = "",tipo_stream="";
    public static WebView webView;
    public static ProgressBar progressBar;
    private boolean isFav = false;


    private ShimmerFrameLayout shimmerFrameLayout;

    private RelativeLayout adView;
    private InterstitialAd mInterstitialAd;
    private LinearLayout download_text;


    public static SimpleExoPlayer player;
    public static PlayerView simpleExoPlayerView;

    public static ImageView imgFull;

    public static boolean isPlaying,isFullScr;
    public static View playerLayout;
    private int keycount=0;
    private String pressedkey="";
    private int playerHeight;
    public static boolean isVideo=true;
    private FirebaseAnalytics mFirebaseAnalytics;
    public static MediaSource mediaSource=null;
    private AlertDialog alertDialog;
    private AudioManager audioManager;
    private Toast toastMessage;
    private CountDownTimer timo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //---analytics-----------
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        adView=findViewById(R.id.adView);
        llBottom=findViewById(R.id.llbottom);
        tvDes=findViewById(R.id.tv_details);
        tvRelease=findViewById(R.id.tv_release_date);
        tvName=findViewById(R.id.text_name);
        swipeRefreshLayout=findViewById(R.id.swipe_layout);
        imgAddFav=findViewById(R.id.add_fav);
        imgBack=findViewById(R.id.img_back);
        webView=findViewById(R.id.webView);
        progressBar=findViewById(R.id.progressBar);
        llBottomParent=findViewById(R.id.llbottomparent);
        lPlay=findViewById(R.id.play);
        rvRelated=findViewById(R.id.rv_related);
        tvRelated=findViewById(R.id.tv_related);
        shimmerFrameLayout=findViewById(R.id.shimmer_view_container);
        simpleExoPlayerView = findViewById(R.id.video_view);
        playerLayout=findViewById(R.id.player_layout);
        imgFull=findViewById(R.id.img_full_scr);
        rvServer=findViewById(R.id.rv_server_list);
        download_text = findViewById(R.id.download_text);
        shimmerFrameLayout.startShimmer();


        playerHeight = lPlay.getLayoutParams().height;


        progressBar.setMax(100); // 100 maximum value for the progress value
        progressBar.setProgress(50);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        type = getIntent().getStringExtra("vType");
        id = getIntent().getStringExtra("id");
        tipo_stream = getIntent().getStringExtra("stream_from");

        final SharedPreferences preferences=getSharedPreferences("user",MODE_PRIVATE);

        if (preferences.getBoolean("status",false)){
            imgAddFav.setVisibility(VISIBLE);
        }else {
            imgAddFav.setVisibility(GONE);
        }
        isLoaded=false;
        imgFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isFullScr){
                    isFullScr=false;
                    swipeRefreshLayout.setVisibility(VISIBLE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                    if (isVideo){
                        lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, playerHeight));

                    }else {
                        lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, playerHeight));
                    }

                }else {

                    isFullScr = true;
                    swipeRefreshLayout.setVisibility(GONE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                    if (isVideo) {
                        lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

                    } else {
                        lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

                    }


                }

            }
        });
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        setupGestures();

        if (!isNetworkAvailable()){
            new ToastMsg(DetailsActivity.this).toastIconError(getString(R.string.no_internet));
        }

        initGetData();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                initGetData();
            }
        });

        loadAd();

    }
    private void showIndications(){
        final SharedPreferences realPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        System.out.println("notificaciones: ");
        System.out.println(realPreferences.getBoolean("not_again", false));
        if(!realPreferences.getBoolean("not_again", false)){

            TutoShowcase.from(this)
                    .setContentView(R.layout.tutorial)
                    .onClickContentView(R.id.notagain, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            realPreferences.edit().putBoolean("not_again", true).commit();
                        }
                    })
                    .onClickContentView(R.id.repeat_tutorial, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            TutoShowcase.from(DetailsActivity.this).dismiss();
                            showIndications();
                        }
                    })
                    .withDismissView(R.id.leave_toturial)
                    .setFitsSystemWindows(true)
                    .on(R.id.tutorial_horizontal_hand_left, R.id.chan_change_back)

                    .displaySwipableLeft()
                    .delayed(2200)
                    .animated(true)
                    .duration(5000)
                    .on(R.id.tutorial_horizontal_hand_right, R.id.chan_change_forward)
                    .displaySwipableRight()
                    .delayed(7500)
                    .duration(5000)
                    .animated(true)
                    .on(R.id.tutorial_down_hand_left, R.id.brillo_info)
                    .displayScrollable()
                    .delayed(13500)
                    .duration(5000)
                    .animated(true)
                    .on(R.id.tutorial_down_hand_right, R.id.volumne_ctrl_info)
                    .displayScrollable()
                    .delayed(19500)
                    .duration(5000)
                    .animated(true)
                    .on(R.id.leave_toturial, R.id.last_message)
                    .showViewAnimated()
                    .delayed(25000)
                    .show();
        }
    }
    private void loadAd(){

        System.out.println("entra anuncio");
        System.out.println(ApiResources.adStatus);
        if (ApiResources.adStatus.equals("1")){

            BannerAds.ShowBannerAds(this, adView);
            PopUpAds.ShowInterstitialAds(this);

            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(ApiResources.adMobInterstitialId);
            mInterstitialAd.loadAd(new AdRequest.Builder().build());


            mInterstitialAd.setAdListener(new AdListener(){
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();

                    Random rand = new Random();
                    int i = rand.nextInt(10)+1;

                    if (i%2==0){
                        mInterstitialAd.show();
                    }
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    System.out.println("No se mostró el anuncio");
                    System.out.println(isLoaded);
                    if(!isLoaded){
                        isLoaded=true;
                        showIndications();
                    }

                }
                @Override
                public void onAdClosed() {
                    System.out.println("se cerró el anuncio");
                    if(!isLoaded){
                        isLoaded=true;
                        showIndications();
                    }
                    super.onAdClosed();
                }
                @Override
                public void onAdClicked() {
                    System.out.println("se clickeó el anuncio");
                }
                @Override
                public void onAdLeftApplication() {
                    System.out.println("se fue de la app");
                }

            });

        }else{
            if(!isLoaded){
                isLoaded=true;
                showIndications();
            }
        }
    }

    private void initGetData(){

        if (!type.equals("tv")){


            //----related rv----------
            relatedAdapter=new HomePageAdapter(this,listRelated);
            rvRelated.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            rvRelated.setHasFixedSize(true);
            rvRelated.setAdapter(relatedAdapter);

            if (type.equals("tvseries")){

                rvRelated.removeAllViews();
                listRelated.clear();
                rvServer.removeAllViews();
            }else {

                rvServer.removeAllViews();
                listTV.clear();
                rvRelated.removeAllViews();
                listRelated.clear();

                //---server adapter----
                serverAdapter=new ServerApater(this,listTV);
                rvServer.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
                rvServer.setHasFixedSize(true);
                rvServer.setAdapter(serverAdapter);


                getData(type,id);

                final ServerApater.OriginalViewHolder[] viewHolder = {null};
                serverAdapter.setOnItemClickListener(new ServerApater.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, CommonModels obj, int position, ServerApater.OriginalViewHolder holder) {
                        iniMoviePlayer(obj.getStremURL(),obj.getServerType(),DetailsActivity.this);

                        serverAdapter.chanColor(viewHolder[0],position);
                        holder.name.setTextColor(getResources().getColor(R.color.colorPrimary));
                        viewHolder[0] =holder;
                    }
                });
            }

        }else {

            tvRelated.setText("Relacionados :");

            rvServer.removeAllViews();
            listTV.clear();
            rvRelated.removeAllViews();
            listRelated.clear();

            //----related rv----------
            relatedTvAdapter=new LiveTvHomeAdapter(this,listRelated);
            rvRelated.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            rvRelated.setHasFixedSize(true);
            rvRelated.setAdapter(relatedTvAdapter);



            imgAddFav.setVisibility(GONE);

            serverAdapter=new ServerApater(this,listTV);
            rvServer.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            rvServer.setHasFixedSize(true);
            rvServer.setAdapter(serverAdapter);
            getTvData(type,id);
            llBottom.setVisibility(GONE);

            final ServerApater.OriginalViewHolder[] viewHolder = {null};
            serverAdapter.setOnItemClickListener(new ServerApater.OnItemClickListener() {
                @Override
                public void onItemClick(View view, CommonModels obj, int position, ServerApater.OriginalViewHolder holder) {
                    iniMoviePlayer(obj.getStremURL(),obj.getServerType(),DetailsActivity.this);

                    serverAdapter.chanColor(viewHolder[0],position);
                    holder.name.setTextColor(getResources().getColor(R.color.colorPrimary));
                    viewHolder[0] =holder;
                }
            });


        }
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        System.out.println(event);
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
            adjustVolume(false);
        }
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
            adjustVolume(true);
        }
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            ch_channel(false);
        }
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            ch_channel(true);
        }
        if(event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() >= 7 && event.getKeyCode()<=16){
            System.out.println(event.getKeyCode()-7);
            channel_number( event.getKeyCode()-7);
        }
        return super.dispatchKeyEvent(event);
    }


    private void initWeb(String s){

        if (isPlaying){
            player.release();

        }

        progressBar.setVisibility(GONE);

        webView.loadUrl(s);
        webView.setVisibility(VISIBLE);
        playerLayout.setVisibility(GONE);

    }


    public void iniMoviePlayer(String url,String type,Context context){

        if (type.equals("embed") || type.equals("vimeo") || type.equals("gdrive")){
            isVideo=false;
            initWeb(url);
        }else {
            isVideo=true;
            initVideoPlayer(url,context,type);
        }
    }




    public void initVideoPlayer(String url,Context context,String type){

        progressBar.setVisibility(VISIBLE);

        if (player!=null){
            System.out.println("entro en player");
            player.stop();
            player.release();

        }



        webView.setVisibility(GONE);
        playerLayout.setVisibility(VISIBLE);

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new
                AdaptiveTrackSelection.Factory(bandwidthMeter);


        TrackSelector trackSelector = new
                DefaultTrackSelector(videoTrackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        player.setPlayWhenReady(true);

        simpleExoPlayerView.setPlayer(player);
        simpleExoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo));
        simpleExoPlayerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                // Log.e("Visibil", String.valueOf(visibility));
                if (visibility==0){
                    imgBack.setVisibility(VISIBLE);
                    imgFull.setVisibility(VISIBLE);
                }else {
                    imgBack.setVisibility(GONE);
                    imgFull.setVisibility(GONE);
                }
            }
        });

        Uri uri = Uri.parse(url);
        if (type.equals("hls")){
            mediaSource = hlsMediaSource(uri,context);


        }else if (type.equals("youtube")){
        }
        else if (type.equals("youtube-live")){
        }
        else if (type.equals("rtmp")){
            mediaSource=rtmpMediaSource(uri);
        }else {
            mediaSource=mediaSource(uri,context);

        }

        player.prepare(mediaSource, true, true);

        player.addListener(new Player.DefaultEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                if (playWhenReady && playbackState == Player.STATE_READY) {
                    System.out.println("ya no esta esperando");
                    isPlaying=true;
                    progressBar.setVisibility(View.GONE);

                }
                else if (playbackState==Player.STATE_READY){
                    System.out.println("se jodio");
                    progressBar.setVisibility(View.GONE);
                    isPlaying=false;
                }
                else if (playbackState==Player.STATE_BUFFERING) {
                    isPlaying=false;
                    progressBar.setVisibility(VISIBLE);
                    System.out.println("esta esperando");

                } else {
                    // player paused in any state
                    isPlaying=false;
                }

            }
        });

    }


    private MediaSource rtmpMediaSource(Uri uri){

        MediaSource videoSource = null;



        RtmpDataSourceFactory dataSourceFactory = new RtmpDataSourceFactory();
        videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);


        return  videoSource;

    }


    private MediaSource hlsMediaSource(Uri uri,Context context){


        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "cootv"), bandwidthMeter);

        MediaSource videoSource = new HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);


        return videoSource;


    }


    private MediaSource mediaSource(Uri uri,Context context){
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("cootv")).
                createMediaSource(uri);

    }

    private void getTvData(String vtype,String vId){


        String type = "&&type="+vtype;
        String id = "&id="+vId;
        String url = new ApiResources().getDetails()+type+id;

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(GONE);

                try {
                    tvName.setText(response.getString("tv_name"));
                    tvDes.setText(response.getString("description"));
                    V_URL=response.getString("stream_url");
                    stream_label_rta = response.getString("stream_label");
                    _serverType = response.getString("stream_from");
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, response.getString("tv_name"));
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "canal");
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                    CommonModels model=new CommonModels();
                    model.setTitle(stream_label_rta);
                    model.setStremURL(V_URL);
                    model.setServerType(response.getString("stream_from"));
                    listTV.add(model);

                    JSONArray jsonArray = response.getJSONArray("all_tv_channel");
                    for (int i=0;i<jsonArray.length();i++){

                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        CommonModels models =new CommonModels();
                        models.setImageUrl(jsonObject.getString("poster_url"));
                        models.setTitle(jsonObject.getString("tv_name"));
                        models.setVideoType("tv");
                        models.setId(jsonObject.getString("live_tv_id"));
                        listRelated.add(models);

                    }
                    relatedTvAdapter.notifyDataSetChanged();



                    JSONArray serverArray = response.getJSONArray("additional_media_source");
                    for (int i = 0;i<serverArray.length();i++){
                        JSONObject jsonObject=serverArray.getJSONObject(i);

                        CommonModels models=new CommonModels();
                        models.setTitle(jsonObject.getString("label"));
                        models.setStremURL(jsonObject.getString("url"));
                        models.setServerType(jsonObject.getString("source"));


                        listTV.add(models);
                    }
                    serverAdapter.notifyDataSetChanged();
                    System.out.println(isFullScr);
                    if(isFullScr){
                        System.out.println("entra");
                        iniMoviePlayer(V_URL , _serverType,DetailsActivity.this);
                    }

                }catch (Exception e){

                }finally {

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        new VolleySingleton(DetailsActivity.this).addToRequestQueue(jsonObjectRequest);

    }


    private void getData(String vtype,String vId){


        String type = "&&type="+vtype;
        String id = "&id="+vId;


        String url = new ApiResources().getDetails()+type+id;

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(GONE);
                swipeRefreshLayout.setRefreshing(false);
                try {
                    int download_check = response.getInt("enable_download");
                    if (download_check==1){
                        download_text.setVisibility(VISIBLE);
                    }

                    tvName.setText(response.getString("title"));
                    tvRelease.setText("Release On "+response.getString("release"));
                    tvDes.setText(response.getString("description"));


                    //----server---------------
                    JSONArray serverArray = response.getJSONArray("videos");
                    for (int i = 0;i<serverArray.length();i++){
                        JSONObject jsonObject=serverArray.getJSONObject(i);

                        CommonModels models=new CommonModels();
                        models.setTitle(jsonObject.getString("label"));
                        models.setStremURL(jsonObject.getString("file_url"));
                        models.setServerType(jsonObject.getString("file_type"));


                        if (jsonObject.getString("file_type").equals("mp4")){
                            V_URL=jsonObject.getString("file_url");
                        }

                        listTV.add(models);
                    }
                    serverAdapter.notifyDataSetChanged();

                    //----realted post---------------
                    JSONArray relatedArray = response.getJSONArray("related_movie");
                    for (int i = 0;i<relatedArray.length();i++){
                        JSONObject jsonObject=relatedArray.getJSONObject(i);

                        CommonModels models=new CommonModels();
                        models.setTitle(jsonObject.getString("title"));
                        models.setImageUrl(jsonObject.getString("thumbnail_url"));
                        models.setId(jsonObject.getString("videos_id"));
                        models.setVideoType("movie");

                        listRelated.add(models);
                    }
                    System.out.println(isFullScr);
                    if(isFullScr){
                        System.out.println("entra");
                        iniMoviePlayer(V_URL , _serverType,DetailsActivity.this);
                    }
                    relatedAdapter.notifyDataSetChanged();

                }catch (Exception e){

                }finally {

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        new VolleySingleton(DetailsActivity.this).addToRequestQueue(jsonObjectRequest);


    }

    private void removeFromFav(String url){

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    if (response.getString("status").equals("success")){
                        isFav=false;
                        new ToastMsg(DetailsActivity.this).toastIconSuccess(response.getString("message"));
                        imgAddFav.setBackgroundResource(R.drawable.outline_favorite_border_24);
                    }else {
                        isFav=true;
                        new ToastMsg(DetailsActivity.this).toastIconError(response.getString("message"));
                        imgAddFav.setBackgroundResource(R.drawable.outline_favorite_24);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                new ToastMsg(DetailsActivity.this).toastIconError(getString(R.string.fetch_error));
            }
        });

        new VolleySingleton(DetailsActivity.this).addToRequestQueue(jsonObjectRequest);

    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (isPlaying && player!=null){
            player.setPlayWhenReady(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        releasePlayer();
        super.onBackPressed();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(player!=null){
            player.setPlayWhenReady(true);
        }

    }

    public void releasePlayer() {

        if (player != null) {
            player.setPlayWhenReady(true);
            player.stop();
            player.release();
            player = null;
            simpleExoPlayerView.setPlayer(null);
            simpleExoPlayerView = null;
            System.out.println("releasePlayer");
        }
    }
    private void setupGestures()
    {
        //check if swipe gestures are enables

        //get configuration values needed in swipe handler (avoid looking up values constantly)
        final float brightnessAdjustStep =  0.02f;
        final float hardSwipeThreshold = 37.0f;
        final boolean hardSwipeEnable = false;

        //init and set listener
        // playerView.setOnTouchListener(new SwipeGestureListener(TOUCH_DECAY_TIME, SWIPE_THRESHOLD_N, FLING_THRESHOLD_N,
        // new RectF(0, 20, 0, 75))
        int swipeFlingThreshold = 5;
        int touchDecayTime = 1500;


        playerLayout.setOnTouchListener(new SwipeGestureListener(touchDecayTime, swipeFlingThreshold, swipeFlingThreshold,
                new RectF(0,  20, 0, 75)){

            @Override
            public void onHorizontalSwipe(float deltaX, PointF swipeStart, PointF swipeEnd, PointF firstContact, SizeF screenSize)
            {
                System.out.println(deltaX);
                System.out.println(swipeStart);
                System.out.println(swipeEnd);
                if(deltaX > 0)
                    ch_channel(false);
                else
                    ch_channel(true);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onVerticalSwipe(float deltaY, PointF swipeStart, PointF swipeEnd, PointF firstContact, SizeF screenSize)
            {
                //check which screen size the swipe originated from
                if (firstContact.x > (screenSize.getWidth() / 2))
                {
                    //swipe on right site of screen, adjust volume
                    if (deltaY > 0.0)
                    {
                        //swipe up, increase volume
                        adjustVolume(true);
                    }
                    else
                    {
                        //swipe down, decrease volume
                        adjustVolume(false);
                    }
                }
                else
                {
                    //swipe on left site of screen, adjust brightness
                    if (deltaY > 0)
                    {
                        //swipe up, increase brightness
                        adjustScreenBrightness(brightnessAdjustStep, false);
                    }
                    else
                    {
                        //swipe down, decrease brightness:
                        //check if "hard" swipe, override hard swipe if not enabled
                        boolean hardSwipe = hardSwipeEnable || Math.abs(deltaY) > hardSwipeThreshold;


                        //allow setting brightness to 0 when hard swiping (but ONLY then)
                        adjustScreenBrightness(-brightnessAdjustStep, hardSwipe);
                    }
                }
            }

            @Override
            public void onHorizontalFling(float deltaX, PointF flingStart, PointF flingEnd, SizeF screenSize)
            {


            }

            @Override
            public void onVerticalFling(float deltaY, PointF flingStart, PointF flingEnd, SizeF screenSize)
            {
            }

            @Override
            public void onNoSwipeClick(View view, PointF clickPos, SizeF screenSize)
            {
                //forward click to player controls
                super.onNoSwipeClick(view, clickPos, screenSize);
                simpleExoPlayerView.performClick();
                System.out.println("toquetea");

            }
        });
    }


    private  void  ch_channel(boolean up){
        int siz = listRelated.size();
        for ( int z=0 ; z < siz; z++) {
            CommonModels data = listRelated.get(z);
            if(data.id.equalsIgnoreCase(id)){

                if( up ){
                    if(z == (siz-1)){
                        CommonModels data2 = listRelated.get(0);
                        id=data2.id;
                        type=data2.videoType;
                        _serverType=data2.getServerType();
                        if(adcount == 5){
                            adcount=0;
                            loadAd();
                        }else { adcount++;}
                        initGetData();
                        break;
                    }else{
                        CommonModels data2 = listRelated.get(z+1);
                        id=data2.id;
                        type=data2.videoType;
                        _serverType=data2.getServerType();
                        if(adcount == 5){
                            adcount=0;
                            loadAd();
                        }else { adcount++;}
                        initGetData();
                        break;
                    }

                }else{
                    if(z == 0){
                        CommonModels data2 = listRelated.get(siz-1);
                        id=data2.id;
                        type=data2.videoType;
                        _serverType=data2.getServerType();
                        if(adcount == 5){
                            adcount=0;
                            loadAd();
                        }else { adcount++;}
                        initGetData();
                        break;
                    }else {
                        CommonModels data2 = listRelated.get(z - 1);
                        id = data2.id;
                        type = data2.videoType;
                        _serverType = data2.getServerType();
                        if(adcount == 5){
                            adcount=0;
                            loadAd();
                        }else { adcount++;}
                        initGetData();
                        break;
                    }
                }
            }
        }
    }
    /**
     * Adjust the screen brightness
     *
     * @param adjust    the amount to adjust the brightness by. (range of brightness is 0.0 to 1.0)
     * @param allowZero if set to true, setting the brightness to zero (=device default/auto) is allowed. Otherwise, minimum brightness is clamped to 0.01
     */
    private void adjustScreenBrightness(float adjust, boolean allowZero)
    {
        try {
            toastMessage.cancel();
        }catch ( Exception e ){
            System.out.println(e);
        }
        //get window attributes
        WindowManager.LayoutParams windowAttributes = getWindow().getAttributes();

        //check if brightness is already zero (overrides allowZero)
        boolean alreadyZero = windowAttributes.screenBrightness == 0.0f;

        //modify screen brightness attribute withing range
        //allow setting it to zero if allowZero is set or the value was previously zero too
        System.out.println("Brillo: ");
        System.out.println( windowAttributes.screenBrightness);
        windowAttributes.screenBrightness = Math.min(Math.max(windowAttributes.screenBrightness + adjust, 0.01f), 1f);

        //set changed window attributes
        getWindow().setAttributes(windowAttributes);

        //show info text for brightness
        String brightnessStr = "Brillo: " + ((int) Math.floor(windowAttributes.screenBrightness * 100)) + "%";
       /* if (windowAttributes.screenBrightness == 0)
        {
            brightnessStr = getString(R.string.info_brightness_auto);
        }

        showInfoText(getString(R.string.info_brightness_change), brightnessStr);*/
        toastMessage= Toast.makeText(this, brightnessStr , Toast.LENGTH_LONG);
        toastMessage.show();

    }

    /**
     * Adjust the media volume by one volume step
     *
     * @param raise should the volume be raises (=true) or lowered (=false) by one step?
     */
    private void adjustVolume(boolean raise)
    {
        //check audioManager
        try {
            toastMessage.cancel();
        }catch ( Exception e ){
            System.out.println(e);
        }


        if (audioManager == null)
        {
            System.out.println("audio issues");
            return;
        }

        //adjusts volume without ui
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, (raise ? AudioManager.ADJUST_RAISE : AudioManager.ADJUST_LOWER), 0);

        //show info text for volume:
        //get volume + range
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int minVolume = 0;
        if (Util.SDK_INT > 28)
        {
            //get minimum stream volume if above api28
            minVolume = audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC);
        }

        //calculate volume in percent
        int volumePercent = (int) Math.floor(((float) currentVolume - (float) minVolume) / ((float) maxVolume - (float) minVolume) * 100);

        //show info text
        //showInfoText(getString(R.string.info_volume_change), volumePercent);

        toastMessage= Toast.makeText(this, "Volumen: "+String.valueOf(currentVolume) , Toast.LENGTH_LONG);
        toastMessage.show();
    }

    private void channel_number(int number){
        System.out.println(number);
        keycount++;
        pressedkey+= String.valueOf(number);
        if(keycount <= 1){
            toastMessage = Toast.makeText(this, pressedkey.length() == 1 ? pressedkey+"-":pressedkey, Toast.LENGTH_LONG);
            toastMessage.show();
            timo =  new CountDownTimer(3000, 1000) {
                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    System.out.println("fin del timer");
                    getnewChannel();

                }
            }.start();
        }else
        {
            System.out.println("metio otro numero");
            timo.cancel();
            getnewChannel();
        }


    }
    private void getnewChannel(){
        System.out.println("empieza la comprobacion");
        int siz = listRelated.size();
        if (pressedkey.charAt(0) == '0')
            pressedkey = pressedkey.replaceFirst("0", "");
        // if (pressedkey.charAt(0) == '0' && pressedkey.length() > 1) {
        System.out.println("empieza el loop");
        for (int z = 0; z < siz; z++) {
            CommonModels data = listRelated.get(z);
            System.out.println(data.getTitle());
            System.out.println(data.getTitle().replaceAll("[^\\d.]", ""));
            if (data.getTitle().replaceAll("[^\\d.]", "").equalsIgnoreCase(pressedkey)) {
                System.out.println("lo encontró");
                id = data.id;
                type = data.videoType;
                _serverType = data.getServerType();
                if (adcount == 5) {
                    adcount = 0;
                    loadAd();
                } else {
                    adcount++;
                }
                pressedkey="";
                keycount=0;
                initGetData();
                break;
            }
        }
        // }else{
        pressedkey="";
        keycount=0;
        //   }
    }

}
