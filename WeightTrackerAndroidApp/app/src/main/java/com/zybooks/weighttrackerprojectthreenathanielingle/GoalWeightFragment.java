package com.zybooks.weighttrackerprojectthreenathanielingle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;


public class GoalWeightFragment extends Fragment {

    private EditText mEnteredWeightEditText;

    private SharedPreferences sharedPreferences;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the permission launcher
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (!isGranted) { // If the user denies permission, we stop asking for it
                        sharedPreferences = requireActivity().getSharedPreferences("goal_prefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("request_sms", false);
                        editor.apply();
                        Toast.makeText(getContext(), "SMS permission denied. Will not send messages.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_goal_weight, container, false);

        sharedPreferences = requireActivity().getSharedPreferences("goal_prefs", Context.MODE_PRIVATE);
        // Load any existing goal weight
        float savedGoalWeight = sharedPreferences.getFloat("goal_weight", 0f);
        boolean requestSMS = sharedPreferences.getBoolean("request_sms", true); // whether the user accepted SMS permissions
        boolean validPhone = sharedPreferences.getBoolean("valid_phone", true); // whether the user input a valid phone number

        if (!validPhone) {
            // If the user accepted SMS permissions but didn't provide a valid phone number,
            // we ask for it again.
            showPhoneNumberDialog();
        }

        // Autofill the text box with a goal weight if the user previously entered one
        mEnteredWeightEditText = rootView.findViewById(R.id.user_weight);
        if (savedGoalWeight != 0f) {
            mEnteredWeightEditText.setText(String.valueOf(savedGoalWeight));
        }


        Button addWeightButton = rootView.findViewById(R.id.set_goal_weight);
        addWeightButton.setOnClickListener(view -> {
            // Check SMS permission
            if (ContextCompat.checkSelfPermission(
                    getContext(), android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {

                // Permission already granted

            } else if (requestSMS) {
                // Directly request the permission
                requestPermissionLauncher.launch(android.Manifest.permission.SEND_SMS);
                showPhoneNumberDialog();
            }

            try {
                float goalWeight = Float.parseFloat(mEnteredWeightEditText.getText().toString());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putFloat("goal_weight", goalWeight);
                editor.apply();
                Toast.makeText(getContext(), "Goal weight set to " + goalWeight + " lbs.", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).navigate(R.id.list_fragment);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
            }

        });
        return rootView;
    }
    private void showPhoneNumberDialog() {
        // Create an EditText to enter phone number
        EditText input = new EditText(getContext());
        input.setTextColor(Color.WHITE);
        input.setTextSize(32);
        input.setHint("Enter Phone Number.");
        input.setHintTextColor(Color.LTGRAY);

        // Build the dialog
        new AlertDialog.Builder(getContext())
                .setTitle("Receive SMS Notifications")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    sharedPreferences = requireActivity().getSharedPreferences("goal_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                        try {
                            // Parse new weight
                            String phoneNumber = input.getText().toString();
                           boolean validPhone = PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber);
                            if (!validPhone) {
                                editor.putBoolean("valid_phone", validPhone);
                                editor.apply();
                                throw new NumberFormatException();
                            }
                            editor.putString("phone_number", phoneNumber);
                            editor.putBoolean("valid_phone", true);
                            editor.apply();
                            Toast.makeText(getContext(), "SMS permission granted!", Toast.LENGTH_SHORT).show();
                        } catch (NumberFormatException e) {
                            Toast.makeText(getContext(), "Invalid phone number entered", Toast.LENGTH_SHORT).show();
                        }
                })
                .setNeutralButton("Maybe Later", null)
                .setNegativeButton("Don't Ask Again", (dialog, which) -> {
                    sharedPreferences = requireActivity().getSharedPreferences("goal_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("valid_phone", true);
                    editor.apply();
                })
                .show();
    }

}