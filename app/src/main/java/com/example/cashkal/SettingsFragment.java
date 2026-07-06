package com.example.cashkal;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cashkal.databinding.FragmentSettingsBinding;
import com.example.cashkal.utils.AuthNavigator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

// settings screen: shows profile values and opens a popup to edit each one
public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    // current values shown on screen
    private String currentName = "";
    private String currentEmail = "";
    private String currentBudget = "0";
    private String currentIncome = "0";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadUser();

        // each row opens a popup to edit that value
        binding.rowName.setOnClickListener(v ->
                showEditDialog(getString(R.string.settings_name_label), currentName,
                        InputType.TYPE_CLASS_TEXT, value -> {
                            currentName = value;
                            binding.tvNameValue.setText(value);
                            // TODO: Ran saves fullName to Firestore users/{uid}
                        }));

        binding.rowEmail.setOnClickListener(v ->
                showEditDialog(getString(R.string.settings_profile_email), currentEmail,
                        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, value -> {
                            currentEmail = value;
                            binding.tvEmailValue.setText(value);
                            // TODO: Ran updates email via FirebaseAuth (needs re-authentication)
                        }));

        binding.rowBudget.setOnClickListener(v ->
                showEditDialog(getString(R.string.settings_budget_label), currentBudget,
                        InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL, value -> {
                            currentBudget = value;
                            binding.tvBudgetValue.setText(value);
                            // TODO: Ran saves monthlyBudget to Firestore users/{uid}
                        }));

        binding.rowIncome.setOnClickListener(v ->
                showEditDialog(getString(R.string.settings_income_label), currentIncome,
                        InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL, value -> {
                            currentIncome = value;
                            binding.tvIncomeValue.setText(value);
                            // TODO: Ran saves expectedMonthlyIncome to Firestore users/{uid}
                        }));

        binding.rowPassword.setOnClickListener(v -> showPasswordDialog());

        // logout using the shared helper Ran built
        binding.btnLogout.setOnClickListener(v -> AuthNavigator.logout(requireActivity()));
    }

    // reads the current user's profile from Firestore and fills the rows
    private void loadUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        currentEmail = user.getEmail() != null ? user.getEmail() : "";
        binding.tvEmailValue.setText(currentEmail);

        FirebaseFirestore.getInstance().collection("users").document(user.getUid()).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        currentName = doc.getString("fullName") != null ? doc.getString("fullName") : "";
                        Double budget = doc.getDouble("monthlyBudget");
                        Double income = doc.getDouble("expectedMonthlyIncome");
                        currentBudget = budget != null ? String.valueOf(budget) : "0";
                        currentIncome = income != null ? String.valueOf(income) : "0";

                        binding.tvNameValue.setText(currentName);
                        binding.tvBudgetValue.setText(currentBudget);
                        binding.tvIncomeValue.setText(currentIncome);
                    }
                });
    }

    // small callback so each row can react to the saved value
    private interface OnValueSaved {
        void onSaved(String value);
    }

    // shows the styled single-field popup
    private void showEditDialog(String title, String currentValue, int inputType, OnValueSaved callback) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_field, null);
        TextView tvTitle = dialogView.findViewById(R.id.tv_dialog_title);
        EditText etValue = dialogView.findViewById(R.id.et_dialog_value);
        Button btnSave = dialogView.findViewById(R.id.btn_dialog_save);
        Button btnCancel = dialogView.findViewById(R.id.btn_dialog_cancel);

        tvTitle.setText(title);
        etValue.setInputType(inputType);
        etValue.setText(currentValue);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        btnSave.setOnClickListener(v -> {
            String value = etValue.getText().toString().trim();
            if (!value.isEmpty()) {
                callback.onSaved(value);
                Toast.makeText(getContext(), R.string.settings_saved_toast, Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // shows the styled change-password popup
    private void showPasswordDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_password, null);
        Button btnSave = dialogView.findViewById(R.id.btn_pw_save);
        Button btnCancel = dialogView.findViewById(R.id.btn_pw_cancel);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        btnSave.setOnClickListener(v -> {
            // TODO: Ran changes the password via FirebaseAuth (needs re-authentication with current password)
            Toast.makeText(getContext(), R.string.settings_saved_toast, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}