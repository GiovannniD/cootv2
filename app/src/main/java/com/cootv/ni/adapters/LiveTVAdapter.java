package com.cootv.ni.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.OkHttpClient;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cootv.ni.DetailsActivity;
import com.cootv.ni.R;
import com.cootv.ni.models.CommonModels;
import com.cootv.ni.utils.TLSSocketFactory;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class LiveTVAdapter extends RecyclerView.Adapter<LiveTVAdapter.OriginalViewHolder> {

    private List<CommonModels> items = new ArrayList<>();
    private Context ctx;


    public LiveTVAdapter(Context context, List<CommonModels> items) {
        this.items = items;
        ctx = context;
    }


    @Override
    public LiveTVAdapter.OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LiveTVAdapter.OriginalViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_live_tv, parent, false);
        vh = new LiveTVAdapter.OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(LiveTVAdapter.OriginalViewHolder holder, final int position) {

        final CommonModels obj = items.get(position);

        holder.name.setText(obj.getTitle());

        //Picasso.get().load(obj.getImageUrl()).into(holder.image);

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

            Picasso p=new Picasso.Builder(ctx)
                    .downloader(new OkHttp3Downloader(ok))
                    .build();
            p.load(obj.getImageUrl()).into(holder.image);
        }catch (KeyManagementException e){

        }catch (NoSuchAlgorithmException e){

        }


        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ctx, DetailsActivity.class);
                intent.putExtra("vType",obj.getVideoType());
                intent.putExtra("id",obj.getId());
                ctx.startActivity(intent);
            }
        });


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

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView name;
        public TextView counter;
        public View lyt_parent;


        public OriginalViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.image);
            name = v.findViewById(R.id.name);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

}