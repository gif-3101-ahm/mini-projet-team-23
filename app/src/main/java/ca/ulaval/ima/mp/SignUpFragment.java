package ca.ulaval.ima.mp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class SignUpFragment extends Fragment {

    EditText editTextEmailSignUpLayout;
    EditText editTextFirstnameSignUpLayout;
    EditText editTextLastnameSignUpLayout;
    EditText editTextPasswordSignUpLayout;
    EditText editTextPasswordConfirmSignUpLayout;
    Button buttonSignUpSignUpLayout;
    TextView textViewLoginSignUpLayout;
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
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonSignUpSignUpLayout = getView().findViewById(R.id.buttonSignUpSignUpLayout);

        buttonSignUpSignUpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> errors = new ArrayList<String>();

                editTextEmailSignUpLayout = getView().findViewById(R.id.editTextEmailSignUpLayout);
                editTextFirstnameSignUpLayout = getView().findViewById(R.id.editTextFirstnameSignUpLayout);
                editTextLastnameSignUpLayout = getView().findViewById(R.id.editTextLastnameSignUpLayout);
                editTextPasswordSignUpLayout = getView().findViewById(R.id.editTextPasswordSignUpLayout);
                editTextPasswordConfirmSignUpLayout = getView().findViewById(R.id.editTextPasswordConfirmSignUpLayout);

                if (editTextEmailSignUpLayout.getText().toString().isEmpty()) {
                    errors.add("Email is empty");
                }
                if (editTextFirstnameSignUpLayout.getText().toString().isEmpty()) {
                    errors.add("Firstname is empty");
                }
                if (editTextLastnameSignUpLayout.getText().toString().isEmpty()) {
                    errors.add("Lastname is empty");
                }

                if (!editTextPasswordSignUpLayout.getText().toString().equals(editTextPasswordConfirmSignUpLayout.getText().toString())) {
                    errors.add("Password must be confirmed");
                }

                if (editTextPasswordSignUpLayout.getText().toString().isEmpty()) {
                    errors.add("Password is empty");
                }
                if (editTextPasswordConfirmSignUpLayout.getText().toString().isEmpty()) {
                    errors.add("Password Confirm is empty");
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
                        boolean isSignIn = authentificationRequest.postSignUp(new JSONObject()
                                .put("email", editTextEmailSignUpLayout.getText().toString())
                                .put("password", editTextPasswordSignUpLayout.getText().toString())
                                .put("firstname", editTextFirstnameSignUpLayout.getText().toString())
                                .put("lastname", editTextLastnameSignUpLayout.getText().toString())
                                .put("returnSecureToken", true)
                        );

                        if (!isSignIn) {
                            alertDialog = new AlertDialog.Builder(getContext()).create();
                            alertDialog.setTitle("Errors");
                            alertDialog.setMessage("Something wrong");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
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

        textViewLoginSignUpLayout = getView().findViewById(R.id.textViewLoginSignUpLayout);

        textViewLoginSignUpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }
}
