package ca.ulaval.ima.mp;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProfilFragment extends Fragment {
    private static boolean isGpsEnabled;
    private static boolean isNetworkLocationEnabled;
    private String myPreferences = "myPrefs";
    private double longitude = 0;
    private double latitude = 0;

    public static ProfilFragment newInstance() {
        return new ProfilFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profil, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(myPreferences, Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("dark_mode", true)) {
            view.findViewById(R.id.profile_root).setBackgroundColor(Color.GRAY);
            view.findViewById(R.id.saveButton).setBackgroundColor(Color.GRAY);
        } else {
            view.findViewById(R.id.profile_root).setBackgroundColor(Color.parseColor("#CF2E2E"));
            view.findViewById(R.id.saveButton).setBackgroundColor(Color.parseColor("#CF2E2E"));
        }
        Log.d("ProfileFragment", "test");

    }
}
