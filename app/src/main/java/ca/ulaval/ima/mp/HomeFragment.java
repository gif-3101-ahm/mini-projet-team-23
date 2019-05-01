package ca.ulaval.ima.mp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.concurrent.ExecutionException;


public class HomeFragment extends Fragment {
    private static boolean isGpsEnabled;
    private static boolean isNetworkLocationEnabled;
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
        TextView weatherText = view.findViewById(R.id.weatherText);
        TextView tempMinText = view.findViewById(R.id.tempMin);
        TextView tempMaxText = view.findViewById(R.id.tempMax);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(myPreferences, Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("dark_mode", true)) {
            switch_compat.setChecked(true);
            sharedPreferences.edit().putBoolean("dark_mode", true).apply();
            view.findViewById(R.id.rootHomeFragment).setBackgroundColor(Color.GRAY);
            view.findViewById(R.id.option).setBackgroundColor(Color.GRAY);
            switch_compat.setBackgroundColor(Color.GRAY);
            view.findViewById(R.id.logout).setBackgroundColor(Color.GRAY);
        } else {
            switch_compat.setChecked(false);
            view.findViewById(R.id.rootHomeFragment).setBackgroundColor(Color.parseColor("#CF2E2E"));
            view.findViewById(R.id.option).setBackgroundColor(Color.parseColor("#CF2E2E"));
            switch_compat.setBackgroundColor(Color.parseColor("#CF2E2E"));
            view.findViewById(R.id.logout).setBackgroundColor(Color.parseColor("#CF2E2E"));
        }
        switch_compat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
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
        Location location = getLocationWithCheckNetworkAndGPS(getContext());
        try {
            MyResponse response = MyRequest.getSimple("https://api.openweathermap.org/data/2.5/weather?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&units=metric&appid=fc356b04a955dca4fea7632703c17a97");
            String responseString = response.getBody();
            JSONArray Jarray = new JSONObject(responseString).getJSONArray("weather");
            String desc = Jarray.getJSONObject(0).getString("description");
            JSONObject Jobject = new JSONObject(responseString).getJSONObject("main");
            String tempMin = Jobject.getString("temp_min");
            String tempMax = Jobject.getString("temp_max");
            String temp = Jobject.getString("temp");
            weatherText.setText(desc + ", " + temp + "° on average");
            tempMinText.setText("Minimum temperature : " + tempMin + "°");
            tempMaxText.setText("Maximum temperature : " + tempMax + "°");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static Location getLocationWithCheckNetworkAndGPS(Context mContext) {
        LocationManager lm = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        assert lm != null;
        isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkLocationEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location networkLoacation = null, gpsLocation = null, finalLoc = null;
        if (isGpsEnabled)
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return null;
            }
        gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (isNetworkLocationEnabled)
            networkLoacation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (gpsLocation != null && networkLoacation != null) {

            return gpsLocation.getAccuracy() > networkLoacation.getAccuracy() ? (finalLoc = networkLoacation) : (finalLoc = gpsLocation);

        } else {
            if (gpsLocation != null) {
                return finalLoc = gpsLocation;
            } else if (networkLoacation != null) {
                return finalLoc = networkLoacation;
            }
        }
        return finalLoc;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}