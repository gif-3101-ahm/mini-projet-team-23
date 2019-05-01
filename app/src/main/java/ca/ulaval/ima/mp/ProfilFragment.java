package ca.ulaval.ima.mp;

import android.app.AlertDialog;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ProfilFragment extends Fragment {
    private static boolean isGpsEnabled;
    private static boolean isNetworkLocationEnabled;
    private String myPreferences = "myPrefs";
    private ProfilRequest profilRequest = new ProfilRequest();
    EditText editTextFirstnameProfilLayout;
    EditText editTextLastnameProfilLayout;
    EditText editTextEmailProfilLayout;
    Button buttonSaveProfilLayout;
    AlertDialog alertDialog;
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

        editTextFirstnameProfilLayout = view.findViewById(R.id.editTextFirstnameProfilLayout);
        editTextLastnameProfilLayout = view.findViewById(R.id.editTextLastnameProfilLayout);
        editTextEmailProfilLayout = view.findViewById(R.id.editTextEmailProfilLayout);
        buttonSaveProfilLayout = view.findViewById(R.id.buttonSaveProfilLayout);

        if (sharedPreferences.getBoolean("dark_mode", true)) {
            view.findViewById(R.id.profile_root).setBackgroundColor(Color.GRAY);
            view.findViewById(R.id.buttonSaveProfilLayout).setBackgroundColor(Color.GRAY);
        } else{
            view.findViewById(R.id.profile_root).setBackgroundColor(Color.parseColor("#CF2E2E"));
            view.findViewById(R.id.buttonSaveProfilLayout).setBackgroundColor(Color.parseColor("#CF2E2E"));
        }

        try {
            JSONObject jsonData = profilRequest.getUserProfile();

            editTextFirstnameProfilLayout.setText(jsonData.getString("firstname"));
            editTextLastnameProfilLayout.setText(jsonData.getString("lastname"));
            editTextEmailProfilLayout.setText(jsonData.getString("email"));

            buttonSaveProfilLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> errors = new ArrayList<String>();

                    if (editTextEmailProfilLayout.getText().toString().isEmpty()) {
                        errors.add("Email is empty");
                    }
                    if (editTextFirstnameProfilLayout.getText().toString().isEmpty()) {
                        errors.add("Firstname is empty");
                    }
                    if (editTextLastnameProfilLayout.getText().toString().isEmpty()) {
                        errors.add("Lastname is empty");
                    }

                    if (errors.size() > 0) {
                        alertDialog = new AlertDialog.Builder(getContext()).create();
                        alertDialog.setTitle("Errors");
                        String message = "Some errors :\n";
                        for (int i = 0; i < errors.size(); i++) {
                            message += errors.get(i);
                            if (i + 1 != errors.size())
                                message += "\n";
                        }
                        alertDialog.setMessage(message);
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    } else {
                        try {
                            profilRequest.putUserProfile(new JSONObject()
                                    .put("firstname", editTextFirstnameProfilLayout.getText().toString())
                                    .put("lastname", editTextLastnameProfilLayout.getText().toString())
                                    .put("email", editTextEmailProfilLayout.getText().toString())
                            );
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
