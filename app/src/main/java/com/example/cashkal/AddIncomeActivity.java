package com.example.cashkal;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cashkal.databinding.ActivityAddIncomeBinding;
import com.example.cashkal.model.Income;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.Calendar;
import java.util.Locale;

public class AddIncomeActivity extends AppCompatActivity {

    private ActivityAddIncomeBinding binding;
    private final Calendar selectedDate = Calendar.getInstance();

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddIncomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        updateDateField();

        binding.etDate.setOnClickListener(v -> showDatePicker());
        binding.btnSaveIncome.setOnClickListener(v -> saveIncome());
        binding.btnCancelIncome.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    selectedDate.set(year, month, day);
                    updateDateField();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void updateDateField() {
        String text = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                selectedDate.get(Calendar.DAY_OF_MONTH),
                selectedDate.get(Calendar.MONTH) + 1,
                selectedDate.get(Calendar.YEAR));

        binding.etDate.setText(text);
    }


     /// Valid the form and saves the income in Firestore

    private void saveIncome() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, R.string.auth_error_not_logged_in, Toast.LENGTH_SHORT).show();
            return;
        }

        String amountText = binding.etAmount.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();
        String note = binding.etNote.getText().toString().trim();

        if (TextUtils.isEmpty(amountText)) {
            binding.etAmount.setError(getString(R.string.field_amount));
            return;
        }

        double amount;

        try {
            amount = Double.parseDouble(amountText.replace(",", "."));
        } catch (NumberFormatException e) {
            binding.etAmount.setError(getString(R.string.transaction_error_invalid_amount));
            return;
        }

        if (amount <= 0) {
            binding.etAmount.setError(getString(R.string.transaction_error_positive_amount));
            return;
        }

        String category = binding.spCategory.getSelectedItem().toString();

        setLoading(true);

        String uid = currentUser.getUid();

        DocumentReference incomeRef = firestore.collection("users")
                .document(uid)
                .collection("incomes")
                .document();

        Income income = new Income(
                incomeRef.getId(),
                uid,
                amount,
                description,
                category,
                selectedDate.getTimeInMillis(),
                note,
                System.currentTimeMillis()
        );

        DocumentReference userRef = firestore.collection("users").document(uid);

        WriteBatch batch = firestore.batch();
        batch.set(incomeRef, income);
        batch.update(userRef, "expectedMonthlyIncome", FieldValue.increment(amount));

        batch.commit()
                .addOnSuccessListener(unused -> {
                    setLoading(false);
                    Toast.makeText(this, R.string.income_save_success, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, R.string.income_save_failed, Toast.LENGTH_SHORT).show();
                });
    }

    private void setLoading(boolean isLoading) {
        binding.btnSaveIncome.setEnabled(!isLoading);
        binding.btnCancelIncome.setEnabled(!isLoading);
        binding.etAmount.setEnabled(!isLoading);
        binding.etDescription.setEnabled(!isLoading);
        binding.spCategory.setEnabled(!isLoading);
        binding.etDate.setEnabled(!isLoading);
        binding.etNote.setEnabled(!isLoading);
    }
}