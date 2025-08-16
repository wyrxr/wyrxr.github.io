package com.zybooks.weighttrackerprojectthreenathanielingle;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class WeighInFragment extends Fragment {
    public static final String ARG_WEIGHT_ID = "weigh_in_id";

    private EditText mEnteredWeightEditText;
    private SharedPreferences sharedPreferences;
    private final static String TAG = "WeighInFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weigh_in, container, false);

        sharedPreferences = requireActivity().getSharedPreferences("goal_prefs", Context.MODE_PRIVATE);
        // Load any existing goal weight
        float savedGoalWeight = sharedPreferences.getFloat("goal_weight", 0f);
        String savedPhone = sharedPreferences.getString("phone_number", null);

        WeighInDatabaseHelper db = new WeighInDatabaseHelper(getContext());
        mEnteredWeightEditText = rootView.findViewById(R.id.user_weight);

        Button addWeightButton = rootView.findViewById(R.id.add_new_weight);
        addWeightButton.setOnClickListener(view -> {
            try {
                // Get entered weight and current time.
                float newWeight = Float.parseFloat(mEnteredWeightEditText.getText().toString());
                String currentTime = new SimpleDateFormat("MMM dd HH:mm").format(Calendar.getInstance().getTime());
                db.addWeighIn(currentTime, newWeight);
                db.close();
                if (newWeight == savedGoalWeight) { // Check if we've reached goal weight
                    // Check if we can send an SMS message
                    if ((ContextCompat.checkSelfPermission(
                            getContext(), android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) &&
                    savedPhone != null){
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(savedPhone, null,
                                "Congrats! You've reached your goal weight! of " + savedGoalWeight + " lbs!", null, null);
                        Log.i(TAG, "Sent SMS message!");
                    }
                    Toast.makeText(getContext(),
                            "Congratulations! You've reached your goal weight of " + savedGoalWeight + " lbs!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Current weight " + newWeight + " lbs. entered.", Toast.LENGTH_SHORT).show();
                }
                Navigation.findNavController(view).navigate(R.id.list_fragment);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

}