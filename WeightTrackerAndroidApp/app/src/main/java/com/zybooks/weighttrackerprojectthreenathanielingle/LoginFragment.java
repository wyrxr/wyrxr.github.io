package com.zybooks.weighttrackerprojectthreenathanielingle;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class LoginFragment extends Fragment {
    private EditText mEnteredUsername;
    private EditText mEnteredPassword;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        // get preferences to see if the user is currently logged in
        sharedPreferences = requireActivity().getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        mEnteredUsername = rootView.findViewById(R.id.username);
        mEnteredPassword = rootView.findViewById(R.id.password);

        Button mLoginButton = rootView.findViewById(R.id.login);
        Button mRegisterButton = rootView.findViewById(R.id.register);

        LoginDatabaseHelper db = new LoginDatabaseHelper(getContext());

        mLoginButton.setOnClickListener(view -> {
            String username = mEnteredUsername.getText().toString();
            String password = mEnteredPassword.getText().toString();
            try {
                if (db.checkLogin(username, password)) {
                    // Log in to existing account
                    editor.putBoolean("logged_in", true);
                    editor.apply();
                    Navigation.findNavController(view).navigate(R.id.list_fragment);
                } else {
                    Toast.makeText(getContext(), "Username or password are incorrect.", Toast.LENGTH_SHORT).show();
                }
            } catch (NoSuchAlgorithmException e) {
                // Since the Algorithm is hardcoded, we should never get this error.
            }
        });

        mRegisterButton.setOnClickListener(view -> {
            String username = mEnteredUsername.getText().toString();
            String password = mEnteredPassword.getText().toString();
            try {
                if (db.addLogin(username, password)) {
                    // Create a new account
                    editor.putBoolean("logged_in", true);
                    editor.apply();
                    Toast.makeText(getContext(), "Login created successfully!.", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(view).navigate(R.id.list_fragment);
                } else {
                    Toast.makeText(getContext(), "Username already registered.", Toast.LENGTH_SHORT).show();
                }
            } catch (NoSuchAlgorithmException e) {
                // Since the algorithm is hardcoded, we should never get this error.
            }
        });
        return rootView;
    }
}

