package com.cootv.ni;

import android.graphics.Bitmap;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cootv.ni.models.CommonModels;
import com.cootv.ni.utils.Volley;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.cootv.ni.utils.ApiResources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TermsActivity extends AppCompatActivity {


    //private WebView webView;
    private TextView webView;
    //private ProgressBar progressBar;
    private String terminos="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Terminos y condiciones de uso");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //---analytics-----------
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "terms_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        //progressBar=findViewById(R.id.progressBar);
        webView=findViewById(R.id.webView);
       // webView.setWebViewClient(new niWebViewClient());
       // webView.setWebChromeClient(new WebChromeClient());
        //webView.loadUrl(new ApiResources().getTermsURL());
        //webView.loadData(new ApiResources().getTerms_full(), "text/plain", "UTF-8");
        //Log.e("HEREEEEE","CARGANDOOOOOO");
        //Log.e("BEFORE JSON",terminos);
        getTerms(new ApiResources().getTerms_full());
        //new ApiResources().getTerms_full();



    }

    private void getTerms(String url){

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject=response.getJSONObject("terms");

                    terminos = jsonObject.getString("terms");
                    //Log.e("JSON",response.getString("terms"));
                    //terminos = response.getString("terms");
                   // Log.e("TERMINOS",terminos);

                    //webView.loadDataWithBaseURL(null,terminos, "text/html; charset=UTF-8", null,null);
                    //webView.setMovementMethod(new ScrollingMovementMethod());
                    webView.setText(terminos);

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

        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);

    }

    public class niWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
            //progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //Page load finished
            super.onPageFinished(view, url);
            //progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            //progressBar.setVisibility(View.GONE);
        }
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
