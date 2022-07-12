package com.cootv.ni.utils;

import com.cootv.ni.R;

public class ApiResources {

    public static String adStatus="0",adMobBannerId="null", adMobInterstitialId="null", adMobPublisherId="null";


    String URL = MyAppClass.getContext().getString(R.string.api_url);

    String API_SECRECT_KEY = "api_secret_key="+MyAppClass.getContext().getString(R.string.api_key);


    String slider = URL+"get_slider?"+API_SECRECT_KEY;
    String get_featured_tv = URL+"get_featured_tv_channel?"+API_SECRECT_KEY;
    String get_featured_tv_nac = URL+"get_featured_tv_channel_nac?"+API_SECRECT_KEY;
    String get_featured_tv_ext = URL+"get_featured_tv_channel_ext?"+API_SECRECT_KEY;
    String get_live_tv = URL+"get_featured_tv_channel?"+API_SECRECT_KEY+"&&page=";
    String get_live_tv_ext = URL+"get_featured_tv_channel_ext?"+API_SECRECT_KEY+"&&page=";
    String get_live_tv_nac = URL+"get_featured_tv_channel_nac?"+API_SECRECT_KEY+"&&page=";


    String details = URL+"get_single_details?"+API_SECRECT_KEY;

    String comentar = URL+"agregar_comentario?"+API_SECRECT_KEY;

    String termsURL = MyAppClass.getContext().getString(R.string.terms_url);

    String terms_full =  URL+"get_terms_full?"+API_SECRECT_KEY;


    String adDetails = URL+"get_ads?"+API_SECRECT_KEY;

    public String getAdDetails() {
        return adDetails;
    }


    public String getTermsURL() {
        return termsURL;
    }

    public String getTerms_full() {
        return terms_full;
    }

    public String getComentario() {
        return comentar;
    }

    public String getDetails() {
        return details;
    }

    public String getSlider() {
        return slider;
    }

    public String getGet_live_tv() {
        return get_live_tv;
    }

    public String getGet_live_tv_nac() {
        return get_live_tv_nac;
    }

    public String getGet_live_tv_ext() {
        return get_live_tv_ext;
    }

    public String getGet_featured_tv_nac() {
        return get_featured_tv_nac;
    }

    public String getGet_featured_tv_ext() {
        return get_featured_tv_ext;
    }

}
