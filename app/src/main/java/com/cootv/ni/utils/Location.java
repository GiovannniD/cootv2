package com.cootv.ni.utils;

import android.Manifest;
import androidx.appcompat.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cootv.ni.R;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.LOCATION_SERVICE;

public class Location implements LocationListener {

    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    Double latitude,longitude;
    LocationManager locationManager;
    android.location.Location location;
    Context mcontext;

    ProgressDialog progress;

    public Location(Context mcontexto)
    {
        mcontext = mcontexto;
    }

    public void fn_getlocation(){

        locationManager = (LocationManager)  mcontext.getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnable && !isNetworkEnable){
        }else {

            if (isNetworkEnable){
                location = null;
                if (ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {

                            Log.i("latitude", location.getLatitude() + "");
                            Log.i("longitude", location.getLongitude() + "");

                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            try {
                                SendLocation(latitude,longitude);
                            } catch (Exception e) {
                                Log.i("error", e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            else if (isGPSEnable){
                location = null;
                if (ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            Log.i("latitude", location.getLatitude() + "");
                            Log.i("longitude", location.getLongitude() + "");
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            try {
                                SendLocation(latitude,longitude);
                            } catch (Exception e) {
                                Log.i("error", e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

    }

    @Override
    public void onLocationChanged(android.location.Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

   public void SendLocation(Double Lat, Double Long)
   {
       progress = ProgressDialog.show(mcontext, "CooTV", "Validando cobertura móvil, favor espere un momento...", true);
       progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

       JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, "http://192.168.128.26/NOC_ticketv2/distance.php?latitud="+Lat+"&longitud="+Long, null, new Response.Listener<JSONObject>() {
           @Override
           public void onResponse(JSONObject response) {

               try {
                   Log.i("SendLocation", response.toString());
                   JSONObject jsonObject=response;
                   if (jsonObject.getString("Response").equals("SI")){
                       progress.dismiss();

                       AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                       builder.setTitle("CooTV");
                       builder.setMessage("Verificación correcta, su ubicación actual cuenta con cobertura móvil Cootel!!!");
                       builder.setIcon(R.drawable.ic_check_circle_black_24dp);
                       builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               dialog.dismiss();
                           }
                       });
                       builder.show();

                   }
                   else if (jsonObject.getString("Response").equals("NO")) {
                       progress.dismiss();

                       AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                       builder.setTitle("CooTV");
                       builder.setMessage("Su ubicación actual no cuenta con cobertura móvil Cootel!!!");
                       builder.setIcon(R.drawable.ic_check_circle_black_24dp);
                       builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               dialog.dismiss();
                           }
                       });
                       builder.show();
                   }
                   else {
                       progress.dismiss();

                       AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                       builder.setTitle("CooTV");
                       builder.setMessage("Servicio temporalmente no disponible!!!");
                       builder.setIcon(R.drawable.ic_warning_black_24dp);
                       builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               dialog.dismiss();
                           }
                       });
                       builder.show();
                   }

               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
               progress.dismiss();
               Log.e("onErrorResponse", error.toString());

               AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
               builder.setTitle("CooTV");
               builder.setMessage("Error validando cobertura, favor intente nuevamente!!!");
               builder.setIcon(R.drawable.ic_error_black_24dp);
               builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                   }
               });
               builder.show();
           }
       });

       Volley.newRequestQueue(mcontext).add(jsonObjectRequest);
   }
}
