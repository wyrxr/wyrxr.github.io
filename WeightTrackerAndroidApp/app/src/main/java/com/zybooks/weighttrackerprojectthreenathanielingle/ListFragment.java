package com.zybooks.weighttrackerprojectthreenathanielingle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ListFragment extends Fragment {

    private View rootView;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_list, container, false);
        sharedPreferences = requireActivity().getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        boolean logged_in = sharedPreferences.getBoolean("logged_in", false);

        // Check if the user is logged in. If not, show the login screen.
        if (!logged_in) {
            rootView.post(() -> {
                Navigation.findNavController(requireView()).navigate(R.id.login_fragment);
            });
        }
        // Click listener for the RecyclerView
        View.OnClickListener onClickListener = itemView -> {

            // Create fragment arguments containing the selected band ID
            int selectedBandId = (int) itemView.getTag();
            Bundle args = new Bundle();
            args.putInt(WeighInFragment.ARG_WEIGHT_ID, selectedBandId);

            // Replace list with details
            Navigation.findNavController(itemView).navigate(R.id.add_weight, args);
        };

        Button addWeightButton = rootView.findViewById(R.id.add_weight);
        addWeightButton.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.weigh_in_fragment);
        });

        Button setGoalWeightButton = rootView.findViewById(R.id.set_goal_weight);
        setGoalWeightButton.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.goal_weight_fragment);
        });

        Button logoutButton = rootView.findViewById(R.id.logout);
        logoutButton.setOnClickListener(view -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("logged_in", false);
            editor.apply();
            Navigation.findNavController(view).navigate(R.id.login_fragment);
        });

        // Send weigh ins to RecyclerView
        RecyclerView recyclerView = rootView.findViewById(R.id.weight_list);
        WeighInDatabaseHelper helper = new WeighInDatabaseHelper(getContext());
        List<WeighIn> weighIns = helper.getWeights();
        recyclerView.setAdapter(new WeightAdapter(weighIns, onClickListener));

        return rootView;
    }

    private class WeightAdapter extends RecyclerView.Adapter<WeightHolder> {

        private final List<WeighIn> mWeights;

        public WeightAdapter(List<WeighIn> weights, View.OnClickListener onClickListener) {
            mWeights = weights;
        }

        @NonNull
        @Override
        public WeightHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new WeightHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(WeightHolder holder, int position) {
            WeighIn weighIn = mWeights.get(position);
            holder.bind(weighIn);

            // Set item click listener
            holder.itemView.setOnClickListener(view -> {
                // Show dialog to edit the weight
                showEditDialog(weighIn.getId(), weighIn.getWeight());
            });
        }

        @Override
        public int getItemCount() {
            return mWeights.size();
        }

        public void updateWeights(List<WeighIn> newWeights) {
            mWeights.clear();               // Clear the existing data
            mWeights.addAll(newWeights);    // Add the new data
            notifyDataSetChanged();         // Notify RecyclerView to refresh
        }
    }

    private static class WeightHolder extends RecyclerView.ViewHolder {

        private final TextView mNameTextView;

        public WeightHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_weigh_in, parent, false));
            mNameTextView = itemView.findViewById(R.id.weight_in);
        }

        public void bind(WeighIn weight) {
            mNameTextView.setText(weight.getDate() + "\n " + weight.getWeight() + " lbs.");
        }
    }

    private void showEditDialog(long weighInId, float currentWeight) {
        // Create an EditText to enter the new weight
        EditText input = new EditText(getContext());
        input.setTextColor(Color.WHITE);
        input.setTextSize(32);
        input.setHint("Enter new weight");
        input.setHintTextColor(Color.LTGRAY);

        // Build the dialog
        new AlertDialog.Builder(getContext())
                .setTitle("Edit Weight")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    try {
                        // Parse new weight
                        float newWeight = Float.parseFloat(input.getText().toString());

                        // Update the database
                        WeighInDatabaseHelper db = new WeighInDatabaseHelper(getContext());
                        db.editWeighIn(weighInId, newWeight);
                        db.close();

                        // Update RecyclerView
                        refreshRecyclerView();
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Invalid weight entered", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton("Delete", (dialog, which) -> {
                    WeighInDatabaseHelper db = new WeighInDatabaseHelper(getContext());
                    db.deleteWeighIn(weighInId);
                    db.close();
                    refreshRecyclerView();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void refreshRecyclerView() {
        // Re-query the database for the updated list
        WeighInDatabaseHelper helper = new WeighInDatabaseHelper(getContext());
        List<WeighIn> updatedWeighIns = helper.getWeights();
        helper.close();

        RecyclerView recyclerView = rootView.findViewById(R.id.weight_list);
        WeightAdapter adapter = (WeightAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.updateWeights(updatedWeighIns);
        }
    }

}