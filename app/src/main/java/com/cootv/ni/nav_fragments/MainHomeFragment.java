package com.cootv.ni.nav_fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cootv.ni.fragments.RadioFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.cootv.ni.R;
import com.cootv.ni.fragments.HomeFragment;
import com.cootv.ni.fragments.LiveTvFragment;
import com.cootv.ni.fragments.LiveTVUdpFragment;

public class MainHomeFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private ImageButton btnHome,btnLiveTv,btnRadio, btnudp;
    private Fragment fragment=null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       return inflater.inflate(R.layout.fragment_main_home, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnHome=view.findViewById(R.id.home);
        btnLiveTv=view.findViewById(R.id.live_tv);
        btnRadio=view.findViewById(R.id.live_radio);
        btnudp = view.findViewById(R.id.udp);

        btnLiveTv.setColorFilter(getActivity().getResources().getColor(R.color.grey_40));
        btnRadio.setColorFilter(getActivity().getResources().getColor(R.color.grey_40));
        btnHome.setColorFilter(getActivity().getResources().getColor(R.color.colorPrimary));
        btnudp.setColorFilter(getActivity().getResources().getColor(R.color.grey_40));

        btnRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment=new RadioFragment();
                loadFragment(fragment);

                btnRadio.setColorFilter(getActivity().getResources().getColor(R.color.colorPrimary));
                btnHome.setColorFilter(getActivity().getResources().getColor(R.color.grey_40));
                btnLiveTv.setColorFilter(getActivity().getResources().getColor(R.color.grey_40));
                btnudp.setColorFilter(getActivity().getResources().getColor(R.color.grey_40));
            }
        });

        btnLiveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment=new LiveTvFragment();
                loadFragment(fragment);

                btnLiveTv.setColorFilter(getActivity().getResources().getColor(R.color.colorPrimary));
                btnRadio.setColorFilter(getActivity().getResources().getColor(R.color.grey_40));
                btnHome.setColorFilter(getActivity().getResources().getColor(R.color.grey_40));
                btnudp.setColorFilter(getActivity().getResources().getColor(R.color.grey_40));
            }
        });
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment=new HomeFragment();
                loadFragment(fragment);

                btnRadio.setColorFilter(getActivity().getResources().getColor(R.color.grey_40));
                btnHome.setColorFilter(getActivity().getResources().getColor(R.color.colorPrimary));
                btnLiveTv.setColorFilter(getActivity().getResources().getColor(R.color.grey_40));
                btnudp.setColorFilter(getActivity().getResources().getColor(R.color.grey_40));
            }
        });

        btnudp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new LiveTVUdpFragment();
                loadFragment(fragment);

                btnRadio.setColorFilter(getActivity().getResources().getColor(R.color.grey_40));
                btnHome.setColorFilter(getActivity().getResources().getColor(R.color.grey_40));
                btnLiveTv.setColorFilter(getActivity().getResources().getColor(R.color.grey_40));
                btnudp.setColorFilter(getActivity().getResources().getColor(R.color.colorPrimary));
            }
        });

        loadFragment(new HomeFragment());

    }


    //----load fragment----------------------
    private boolean loadFragment(Fragment fragment){

        if (fragment!=null){

            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,fragment)
                    .commit();

            return true;
        }
        return false;

    }


}