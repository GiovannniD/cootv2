package com.cootv.ni;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cootv.ni.utils.ApiResources;
import com.cootv.ni.utils.ToastMsg;
import com.cootv.ni.utils.VolleySingleton;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.UnsupportedEncodingException;

public class ComentarioActivity extends AppCompatActivity {

    private EditText etComentario;
    private Button btnEnviarComentario;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retroalimentacion);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comentarios");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //---analytics-----------
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "comentario_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Espere un momento");
        dialog.setCancelable(false);

        etComentario=findViewById(R.id.comentariox);
        btnEnviarComentario=findViewById(R.id.enviarcomentario);


        btnEnviarComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String equipo="Equipo: OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ") OS API Level: "+ android.os.Build.VERSION.SDK_INT +" Device: " + android.os.Build.DEVICE +" Model (and Product): " + android.os.Build.MODEL + " ("+ android.os.Build.PRODUCT + ")";
                if(etComentario.getText().toString().equals("")){
                    new ToastMsg(ComentarioActivity.this).toastIconError("Defina un comentario");
                }
                else {
                    String comentario_url = "&&comentario=";

                    String equipo_url = "&&equipo=";

                    String encodedStringEquipo= "";
                    String encodedStringComentario= "";
                    try{
                        encodedStringEquipo = URLEncoder.encode(equipo, "UTF-8");
                        encodedStringComentario = URLEncoder.encode(etComentario.getText().toString(), "UTF-8");
                    }catch (UnsupportedEncodingException ex){
                        ex.printStackTrace();
                    }

                    String baseUrl = new ApiResources().getComentario();

                    String encodedEquipo = encodedStringEquipo; // Encoding a query string

                    String completeUrl = baseUrl+comentario_url+encodedStringComentario+equipo_url+encodedEquipo;
                    //Log.e("url coment0",completeUrl);
                    registrarComentario(completeUrl);
                }
            }
        });
    }

    private void registrarComentario(String url){
        dialog.show();

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET,url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.cancel();
                try {
                  //  Log.e("SIGN UP RES:::::", String.valueOf(response));
                    if (response.getString("status").equals("success")){
                        new ToastMsg(ComentarioActivity.this).toastIconSuccess("Se ha enviado su comentario");
                        startActivity(new Intent(ComentarioActivity.this,MainActivity.class));
                        finish();
                    }else if (response.getString("status").equals("error")){
                        new ToastMsg(ComentarioActivity.this).toastIconError(response.getString("data"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.cancel();
               // Log.e("ERROR LOADING API:::::", String.valueOf(error));
                new ToastMsg(ComentarioActivity.this).toastIconError(getString(R.string.error_toast));
            }
        });

        new VolleySingleton(ComentarioActivity.this).addToRequestQueue(jsonObjectRequest);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
