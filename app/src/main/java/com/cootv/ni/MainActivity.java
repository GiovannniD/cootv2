package com.cootv.ni;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.cootv.ni.adapters.LiveTvHomeAdapter;
import com.cootv.ni.fragments.HomeFragment;
import com.cootv.ni.fragments.RadioFragment;
import com.cootv.ni.models.CommonModels;
import com.cootv.ni.utils.ApiResources;
import com.cootv.ni.utils.PopUpAds;
import com.cootv.ni.utils.ToastMsg;
import com.cootv.ni.utils.VolleySingleton;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.cootv.ni.adapters.NavigationAdapter;
import com.cootv.ni.fragments.LiveTvFragment;
import com.cootv.ni.models.NavigationModel;

//import com.cootv.ni.nav_fragments.FavoriteFragment;

import com.cootv.ni.nav_fragments.MainHomeFragment;
import com.cootv.ni.utils.SpacingItemDecoration;
import com.cootv.ni.utils.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.cootv.ni.utils.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

interface PermissionResultListener {
    public void onPermissionResult(int requestCode,
                                   String permissions[], int[] grantResults);
}

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener/*, BottomNavigationView.OnNavigationItemSelectedListener*/  {

    private PermissionResultListener mPermissionResultListener;

    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;

    private RecyclerView recyclerView;
    private NavigationAdapter mAdapter;
    private List<NavigationModel> list =new ArrayList<>();
    private NavigationView navigationView;
    private String[] navItemImage;

    private String numero_uno = "0",numero_dos = "0";

    private String[] navItemName2;
    private String[] navItemImage2;
    private boolean status=false;

    private Toast toastMessage;

    private SharedPreferences preferences;
    private FirebaseAnalytics mFirebaseAnalytics;

    //private BottomNavigationView bottomnavigation;

    private Location mlocation;

    int PERMISSION_ID = 44;
    private Boolean isrequestlocation = false;

    private InterstitialAd mInterstitialAd;

    private List<CommonModels> listTv =new ArrayList<>();
    private VolleySingleton singleton;
    private ApiResources apiResources;

    private int keycount = 0;
    private String pressedkey = "";
    private CountDownTimer timo;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.i("KeyEvent 1", event.toString());
        if (event.getAction() == KeyEvent.ACTION_UP) {
            Log.i("KeyEvent 2", String.valueOf(event.getKeyCode()));
            if(event.getKeyCode() >= 7 && event.getKeyCode()<=16){
                int character = event.getKeyCode() - 7;
                Log.i("KeyEvent 3", String.valueOf(character));

                channel_number(character);
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void goChannel()
    {
        if (pressedkey.charAt(0) == '0')
        {
            pressedkey = pressedkey.replaceFirst("0", "");
        }

        for(CommonModels list:listTv)
        {
            if (list.getTitle().equals(String.valueOf(pressedkey)))
            {
                Intent intent=new Intent(getApplicationContext(), DetailsActivity.class);
                intent.putExtra("vType",list.getVideoType());
                intent.putExtra("id",list.getId());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getApplicationContext().startActivity(intent);
                break;
            }
        }
        keycount = 0;
        pressedkey = "";
    }

    private void channel_number(int number) {
        System.out.println(number);
        keycount++;
        pressedkey += String.valueOf(number);
        if (keycount <= 1) {
            toastMessage = Toast.makeText(this, pressedkey, Toast.LENGTH_LONG);
            toastMessage.show();
            timo = new CountDownTimer(3000, 1000) {
                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    System.out.println("fin del timer");
                    goChannel();

                }
            }.start();
        } else {
            System.out.println("metio otro numero");
            timo.cancel();
            goChannel();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //bottomnavigation = (BottomNavigationView)findViewById(R.id.custom_bottom_navigation);
        //bottomnavigation.setOnNavigationItemSelectedListener(this);

        //---analytics-----------
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "main_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        //----dark mode----------
        preferences=getSharedPreferences("push",MODE_PRIVATE);

        //----init---------------------------
        navigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);


        //----navDrawer------------------------
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        navigationView.setNavigationItemSelectedListener(this);


        //----fetch array------------
        String[] navItemName = getResources().getStringArray(R.array.nav_item_name);
        navItemImage=getResources().getStringArray(R.array.nav_item_image);
        navItemImage2=getResources().getStringArray(R.array.nav_item_image_2);


        navItemName2=getResources().getStringArray(R.array.nav_item_name_2);

        //----navigation view items---------------------
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 15), true));
        recyclerView.setHasFixedSize(true);


        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        status = prefs.getBoolean("status",false);

        if (status){
            for (int i = 0; i< navItemName.length; i++){
                NavigationModel models =new NavigationModel(navItemImage[i], navItemName[i]);
                list.add(models);
            }
        }else {
            for (int i=0;i<navItemName2.length;i++){
                NavigationModel models =new NavigationModel(navItemImage2[i],navItemName2[i]);
                list.add(models);
            }
        }

        //set data and list adapter
        mAdapter = new NavigationAdapter(this, list);
        recyclerView.setAdapter(mAdapter);

        final NavigationAdapter.OriginalViewHolder[] viewHolder = {null};

        apiResources=new ApiResources();
        mlocation = new Location(MainActivity.this);
        singleton=new VolleySingleton(MainActivity.this);

        /*getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);*/
        getFeaturedTVNac();

        mAdapter.setOnItemClickListener(new NavigationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, NavigationModel obj, int position, NavigationAdapter.OriginalViewHolder holder) {

              //  Log.e("POSITION OF NAV:::", String.valueOf(position));

                //----action for click items nav---------------------

                if (position==0){
                    loadFragment(new MainHomeFragment());
                }
                else if (position==1){
                    loadFragment(new LiveTvFragment());
                }
                else if (position==2){
                    loadFragment(new RadioFragment());
                }
                else  if (position==3){
                    Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
                    startActivity(intent);
                } else if (position==4){
                    Intent intent=new Intent(MainActivity.this,ComentarioActivity.class);
                    startActivity(intent);
                }
                else if (position == 5)
                {
                    mDrawerLayout.closeDrawers();

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Verificar cobertura móvil Cootel")
                            .setMessage("Para validar la cobertura debe tener activado el GPS y dar permiso de utilizar la ubicación a CooTV, desea realizar esta acción?")
                            .setIcon(R.drawable.ic_info_black_24dp)
                            .setPositiveButton("Si", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (isLocationEnabled()) {
                                        if (checkPermissions()) {
                                            LoadAdMob();
                                            mlocation.fn_getlocation();
                                        } else {
                                            requestPermissions();
                                        }
                                    }
                                    else {
                                        isrequestlocation = true;
                                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        startActivity(intent);
                                    }
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();
                }
                else if (position == 6)
                {
                    ShareCompat.IntentBuilder.from(MainActivity.this)
                            .setType("text/plain")
                            .setChooserTitle("CooTV")
                            .setText("http://play.google.com/store/apps/details?id=" + getPackageName())
                            .startChooser();
                }

                //----behaviour of bg nav items-----------------
                if (!obj.getTitle().equals("Settings") && !obj.getTitle().equals("Login") && !obj.getTitle().equals("Sign Out")){

                    if (preferences.getBoolean("dark",false)){
                        mAdapter.chanColor(viewHolder[0],position,R.color.nav_bg);
                    }else {
                        mAdapter.chanColor(viewHolder[0],position,R.color.white);
                    }


                    holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    holder.name.setTextColor(getResources().getColor(R.color.white));
                    viewHolder[0] =holder;
                }

                mDrawerLayout.closeDrawers();
            }
        });

        //----external method call--------------
        loadFragment(new MainHomeFragment());
    }

    @Override
    public void onResume()
    {
        Log.i("onResume", "entrando en onResume");
        super.onResume();
        if (isrequestlocation)
        {
            Log.i("onResume", "entrando a solicitar ubicación en onResume");
            if (checkPermissions() && isLocationEnabled()) {
                Log.i("onResume", "entrando despues de checkPermissions a solicitar ubicación en onResume");
                LoadAdMob();
                mlocation.fn_getlocation();
            }
            else {
                requestPermissions();
            }
            isrequestlocation = false;
        }
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID)
        {
            LoadAdMob();
            mlocation.fn_getlocation();
        }
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        return true;
    }

    private boolean loadFragment(Fragment fragment){

        if (fragment!=null){

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,fragment)
                    .commit();

            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawers();
        }else {

            new AlertDialog.Builder(MainActivity.this).setMessage("Realmente deseas salir de CooTV ?")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            System.exit(0);
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).create().show();

        }
    }

    //----nav menu item click---------------
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // set item as selected to persist highlight

        /*if (menuItem.isChecked())
            menuItem.setChecked(false);

        switch (menuItem.getItemId())
        {
            case R.id.action_home:
                menuItem.setChecked(true);
                loadFragment(new HomeFragment());
                break;
            case R.id.action_chanels:
                menuItem.setChecked(true);
                loadFragment(new LiveTvFragment());
                break;
            case R.id.action_radios:
                menuItem.setChecked(true);
                loadFragment(new RadioFragment());
                break;
        }*/

        menuItem.setChecked(true); // eliminar esta linea al momento de probar de nuevo con el BottonNavigationView
        mDrawerLayout.closeDrawers();

        return true;
    }

    public void LoadAdMob()
    {
        if (ApiResources.adStatus.equals("1")){
            Log.i("LoadAdMob", "Respuesta de carga de anuncio "+ApiResources.adStatus);
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
                }
                @Override
                public void onAdClosed() {
                    System.out.println("se cerró el anuncio");
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

        }
    }

    private void getFeaturedTVNac(){

        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, apiResources.getGet_featured_tv_nac(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0;i<response.length();i++){
                    try {
                        JSONObject jsonObject=response.getJSONObject(i);
                        CommonModels models =new CommonModels();
                        models.setImageUrl(jsonObject.getString("poster_url"));
                        models.setTitle(jsonObject.getString("tv_name").replaceAll("[^\\d.]", ""));
                        models.setVideoType("tv");
                        models.setId(jsonObject.getString("live_tv_id"));
                        listTv.add(models);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        singleton.addToRequestQueue(jsonArrayRequest);

    }
}
