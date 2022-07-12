package com.cootv.ni.fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cootv.ni.utils.NoSSLv3Compat;
import com.cootv.ni.utils.TLSSocketFactory;
import com.cootv.ni.utils.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdRequest;
import com.ixidev.gdpr.GDPRChecker;
import com.cootv.ni.ItemTVActivity;
import com.cootv.ni.R;
import com.cootv.ni.adapters.HomePageAdapter;
import com.cootv.ni.adapters.LiveTvHomeAdapter;
import com.cootv.ni.models.CommonModels;
import com.cootv.ni.utils.ApiResources;
import com.cootv.ni.utils.BannerAds;
import com.cootv.ni.utils.NetworkInst;
import com.cootv.ni.utils.VolleySingleton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class HomeFragment extends Fragment {

    ViewPager viewPager;
    CirclePageIndicator indicator;

    private List<CommonModels> listSlider=new ArrayList<>();

    private Timer timer;

    private ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView recyclerViewTv,recyclerViewTvExt;
    private LiveTvHomeAdapter adapterTv;
    private LiveTvHomeAdapter adapterTvExt;
    private List<CommonModels> listTv =new ArrayList<>();
    private List<CommonModels> listTvExt =new ArrayList<>();
    private ApiResources apiResources;
    private Button btnMoreTvNac,btnMoreTvExt;

    private int checkPass =0;

    private SliderAdapter sliderAdapter;

    private VolleySingleton singleton;
    private TextView tvNoItem;
    private CoordinatorLayout coordinatorLayout;
    //private SwipeRefreshLayout swipeRefreshLayout;
    private ScrollView scrollView;

    private RelativeLayout adView;

    private String numero_uno = "0",numero_dos = "0";
    private Toast toastMessage;

    private View sliderLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home,null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(getResources().getString(R.string.app_title));
        apiResources=new ApiResources();

        singleton=new VolleySingleton(getActivity());

        adView=view.findViewById(R.id.adView);
        btnMoreTvNac=view.findViewById(R.id.btn_more_tv_nac);
        btnMoreTvExt=view.findViewById(R.id.btn_more_tv_ext);
        shimmerFrameLayout=view.findViewById(R.id.shimmer_view_container);
        viewPager=view.findViewById(R.id.viewPager);
        indicator=view.findViewById(R.id.indicator);
        tvNoItem=view.findViewById(R.id.tv_noitem);
        coordinatorLayout=view.findViewById(R.id.coordinator_lyt);
        scrollView=view.findViewById(R.id.scrollView);
        sliderLayout=view.findViewById(R.id.slider_layout);

        sliderAdapter=new SliderAdapter(getActivity(),listSlider);
        viewPager.setAdapter(sliderAdapter);
        indicator.setViewPager(viewPager);


        //----btn click-------------
        btnClick();


        //----featured tv recycler view-----------------
        recyclerViewTv = view.findViewById(R.id.recyclerViewTv);
        recyclerViewTv.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        recyclerViewTv.setHasFixedSize(true);

        recyclerViewTv.setFocusable(true);
        recyclerViewTv.setNestedScrollingEnabled(false);
        adapterTv = new LiveTvHomeAdapter(getContext(), listTv);
        recyclerViewTv.setAdapter(adapterTv);

        //----featured tv recycler view-----------------
        recyclerViewTvExt = view.findViewById(R.id.recyclerViewTvExt);
        recyclerViewTvExt.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        recyclerViewTvExt.setHasFixedSize(true);
        recyclerViewTvExt.setNestedScrollingEnabled(false);
        adapterTvExt = new LiveTvHomeAdapter(getContext(), listTvExt);
        recyclerViewTvExt.setAdapter(adapterTvExt);
        recyclerViewTv.scrollToPosition(0);

        shimmerFrameLayout.startShimmer();


        if (new NetworkInst(getContext()).isNetworkAvailable()){

                getFeaturedTVNac();
                getFeaturedTVExt();
                getSlider(apiResources.getSlider());


        }else {
            tvNoItem.setText(getString(R.string.no_internet));
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            coordinatorLayout.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        }

        getAdDetails(new ApiResources().getAdDetails());


    }

    private void loadAd(){

        if (ApiResources.adStatus.equals("1")){
            BannerAds.ShowBannerAds(getContext(), adView);
        }
    }

    private void btnClick(){

        btnMoreTvNac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),ItemTVActivity.class);
                intent.putExtra("url",apiResources.getGet_live_tv_nac());
                intent.putExtra("title","Canales");
                getActivity().startActivity(intent);
            }
        });

        btnMoreTvExt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),ItemTVActivity.class);
                intent.putExtra("url",apiResources.getGet_live_tv_ext());
                intent.putExtra("title","Estaciones de Radio");
                getActivity().startActivity(intent);
            }
        });

    }



    private void getAdDetails(String url){

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject jsonObject=response.getJSONObject("admob");

                    ApiResources.adStatus = jsonObject.getString("status");
                    ApiResources.adMobBannerId = jsonObject.getString("admob_banner_ads_id");
                    ApiResources.adMobInterstitialId = jsonObject.getString("admob_interstitial_ads_id");
                    ApiResources.adMobPublisherId = jsonObject.getString("admob_app_id");

                    new GDPRChecker()
                            .withContext(getContext())
                            .withPrivacyUrl(getString(R.string.terms_url)) // your privacy url
                            .withPublisherIds(ApiResources.adMobPublisherId) // your admob account Publisher id
                            .check();

                    loadAd();


                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);


    }


    private void getSlider(String url){

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    //swipeRefreshLayout.setRefreshing(false);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                    coordinatorLayout.setVisibility(View.GONE);


                    if (response.getString("slider_type").equals("disable")){
                        sliderLayout.setVisibility(View.GONE);
                    }

                    else if (response.getString("slider_type").equals("movie")){

                        JSONArray jsonArray=response.getJSONArray("data");
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject=jsonArray.getJSONObject(i);
                            CommonModels models =new CommonModels();
                            models.setImageUrl(jsonObject.getString("poster_url"));
                            models.setTitle(jsonObject.getString("title"));
                            models.setVideoType("movie");
                            models.setId(jsonObject.getString("videos_id"));

                            listSlider.add(models);
                        }

                    }else {
                        JSONArray jsonArray=response.getJSONArray("data");
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject=jsonArray.getJSONObject(i);
                            CommonModels models =new CommonModels();
                            models.setImageUrl(jsonObject.getString("image_link"));
                            models.setTitle(jsonObject.getString("title"));
                            models.setVideoType("image");
                            listSlider.add(models);

                        }
                    }

                    sliderAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                coordinatorLayout.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);

            }
        });

        Volley.newRequestQueue(getActivity()).add(jsonObjectRequest);


    }


    private void getFeaturedTVNac(){

        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, apiResources.getGet_featured_tv_nac(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
               // swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                for (int i=0;i<response.length();i++){
                    try {
                        JSONObject jsonObject=response.getJSONObject(i);
                        CommonModels models =new CommonModels();
                        models.setImageUrl(jsonObject.getString("poster_url"));
                        models.setTitle(jsonObject.getString("tv_name"));
                        models.setVideoType("tv");
                        models.setId(jsonObject.getString("live_tv_id"));
                        listTv.add(models);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapterTv.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        singleton.addToRequestQueue(jsonArrayRequest);

    }

    private void getFeaturedTVExt(){

        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, apiResources.getGet_featured_tv_ext(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
               // swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                for (int i=0;i<response.length();i++){
                    try {
                        JSONObject jsonObject=response.getJSONObject(i);
                        CommonModels models =new CommonModels();
                        models.setImageUrl(jsonObject.getString("poster_url"));
                        models.setTitle(jsonObject.getString("tv_name"));
                        models.setVideoType("tv");
                        models.setId(jsonObject.getString("live_tv_id"));
                        listTvExt.add(models);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapterTvExt.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        singleton.addToRequestQueue(jsonArrayRequest);

    }

    @Override
    public void onStart() {
        super.onStart();
        shimmerFrameLayout.startShimmer();
    }

    @Override
    public void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmer();
        //timer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //----adapter for slider-------------
    public class SliderAdapter extends PagerAdapter {

        private Context context;
        private List<CommonModels> list=new ArrayList<>();

        public SliderAdapter(Context context, List<CommonModels> list) {
            this.context = context;
            this.list=list;
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_slider, null);

            View lyt_parent = view.findViewById(R.id.lyt_parent);

            final CommonModels models=list.get(position);

            TextView textView = view.findViewById(R.id.textView);

            textView.setText(models.getTitle());

            ImageView imageView=view.findViewById(R.id.imageview);

            try {

            SSLContext sslContext=SSLContext.getInstance("TLS");
            sslContext.init(null, null, null);
            SSLSocketFactory noSSLv3Factory;

            if (Build.VERSION.SDK_INT<=Build.VERSION_CODES.KITKAT) {
                noSSLv3Factory=new TLSSocketFactory(sslContext.getSocketFactory());
            }
            else {
                noSSLv3Factory=sslContext.getSocketFactory();
            }

            OkHttpClient.Builder okb=new OkHttpClient.Builder()
                    .sslSocketFactory(noSSLv3Factory, provideX509TrustManager());
            OkHttpClient ok=okb.build();

            Picasso p=new Picasso.Builder(getActivity())
                    .downloader(new OkHttp3Downloader(ok))
                    .build();
                p.load(models.getImageUrl()).fit().centerCrop().into(imageView);
            }catch (KeyManagementException e){

            }catch (NoSuchAlgorithmException e){

            }


            ViewPager viewPager = (ViewPager) container;
            viewPager.addView(view, 0);

            return view;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ViewPager viewPager = (ViewPager) container;
            View view = (View) object;
            viewPager.removeView(view);
        }
    }

    public X509TrustManager provideX509TrustManager() {
        try {
            TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            factory.init((KeyStore) null);
            TrustManager[] trustManagers = factory.getTrustManagers();
            return (X509TrustManager) trustManagers[0];
        }
        catch (NoSuchAlgorithmException exception) {
            Log.e(getClass().getSimpleName(), "not trust manager available", exception);
        }
        catch (KeyStoreException exception) {
            Log.e(getClass().getSimpleName(), "not trust manager available", exception);
        }

        return null;
    }

}
