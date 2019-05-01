package ca.ulaval.ima.mp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import okhttp3.Response;

public class SignInFragment extends Fragment {

    TextView textViewSignUpLoginLayout;
    Button buttonLoginSignInLayout;
    EditText editTextEmailSignInLayout;
    EditText editTextPasswordSignInLayout;
    AuthentificationRequest authentificationRequest = new AuthentificationRequest();
    AlertDialog alertDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textViewSignUpLoginLayout = view.findViewById(R.id.textViewSignUpSignInLayout);
        buttonLoginSignInLayout = view.findViewById(R.id.buttonLoginSignInLayout);
        buttonLoginSignInLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextEmailSignInLayout = getView().findViewById(R.id.editTextEmailSignInLayout);
                editTextPasswordSignInLayout = getView().findViewById(R.id.editTextPasswordSignInLayout);

                try {
                    boolean isSignIn = authentificationRequest.postSignIn(new JSONObject()
                            .put("email", editTextEmailSignInLayout.getText().toString())
                            .put("password", editTextPasswordSignInLayout.getText().toString())
                            .put("returnSecureToken", true));

                    if (!isSignIn) {
                        alertDialog = new AlertDialog.Builder(getContext()).create();
                        alertDialog.setTitle("Errors");
                        alertDialog.setMessage("Wrong Email or password");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    } else {
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        startActivity(intent);
                    }

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        textViewSignUpLoginLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new SignUpFragment();
                FragmentManager fragmentManager = getFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

    }
}
