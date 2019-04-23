package ca.ulaval.ima.mp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;


public class HomeFragment extends Fragment {
    private String myPreferences = "myPrefs";


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SwitchCompat switch_compat = view.findViewById(R.id.switchCompat);
        Button logoutButton = view.findViewById(R.id.logout);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(myPreferences, Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("dark_mode", true)) {
            switch_compat.setChecked(true);
            sharedPreferences.edit().putBoolean("dark_mode", true).apply();
            view.findViewById(R.id.rootHomeFragment).setBackgroundColor(Color.GRAY);
            view.findViewById(R.id.option).setBackgroundColor(Color.GRAY);
            switch_compat.setBackgroundColor(Color.GRAY);
            view.findViewById(R.id.logout).setBackgroundColor(Color.GRAY);
        }else {
            switch_compat.setChecked(false);
            view.findViewById(R.id.rootHomeFragment).setBackgroundColor(Color.parseColor("#CF2E2E"));
            view.findViewById(R.id.option).setBackgroundColor(Color.parseColor("#CF2E2E"));
            switch_compat.setBackgroundColor(Color.parseColor("#CF2E2E"));
            view.findViewById(R.id.logout).setBackgroundColor(Color.parseColor("#CF2E2E"));
        }
        switch_compat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    Log.d("HomeFragment", "Switch Checked");
                    sharedPreferences.edit().putBoolean("dark_mode", true).apply();
                    view.findViewById(R.id.rootHomeFragment).setBackgroundColor(Color.GRAY);
                    view.findViewById(R.id.option).setBackgroundColor(Color.GRAY);
                    switch_compat.setBackgroundColor(Color.GRAY);
                    view.findViewById(R.id.logout).setBackgroundColor(Color.GRAY);
                } else {
                    Log.d("HomeFragment", "Switch not checked");
                    sharedPreferences.edit().putBoolean("dark_mode", false).apply();
                    view.findViewById(R.id.rootHomeFragment).setBackgroundColor(Color.parseColor("#CF2E2E"));
                    view.findViewById(R.id.option).setBackgroundColor(Color.parseColor("#CF2E2E"));
                    switch_compat.setBackgroundColor(Color.parseColor("#CF2E2E"));
                    view.findViewById(R.id.logout).setBackgroundColor(Color.parseColor("#CF2E2E"));
                }
            }
        });
        logoutButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), AuthentificationActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}