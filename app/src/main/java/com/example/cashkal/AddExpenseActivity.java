package com.example.cashkal;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cashkal.databinding.ActivityAddExpenseBinding;
import com.example.cashkal.model.Expense;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.Calendar;
import java.util.Locale;


/// Screen for adding a new expense and saving it to Firestore.

public class AddExpenseActivity extends AppCompatActivity {

    private ActivityAddExpenseBinding binding;
    private final Calendar selectedDate = Calendar.getInstance();

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        updateDateField();

        binding.etDate.setOnClickListener(v -> showDatePicker());
        binding.btnSaveExpense.setOnClickListener(v -> saveExpense());
        binding.btnCancelExpense.setOnClickListener(v -> finish());
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

    ///Writes the selected date into the date field as dd/MM/yyyy.

    private void updateDateField() {
        String text = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                selectedDate.get(Calendar.DAY_OF_MONTH),
                selectedDate.get(Calendar.MONTH) + 1,
                selectedDate.get(Calendar.YEAR));

        binding.etDate.setText(text);
    }


    /// Valid the form and saves the expense in Firestore.

    private void saveExpense() {
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
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            binding.etAmount.setError(getString(R.string.transaction_error_invalid_amount));
            return;
        }

        if (amount <= 0) {
            binding.etAmount.setError(getString(R.string.transaction_error_positive_amount));
            return;
        }

        String category = binding.spCategory.getSelectedItem().toString();
        String expenseType = getSelectedExpenseType();

        if (expenseType == null) {
            Toast.makeText(this, R.string.expense_error_choose_type, Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        String uid = currentUser.getUid();

        DocumentReference expenseRef = firestore.collection("users")
                .document(uid)
                .collection("expenses")
                .document();

        Expense expense = new Expense(
                expenseRef.getId(),
                uid,
                amount,
                description,
                category,
                expenseType,
                selectedDate.getTimeInMillis(),
                note,
                System.currentTimeMillis()
        );

        DocumentReference userRef = firestore.collection("users").document(uid);

        WriteBatch batch = firestore.batch();
        batch.set(expenseRef, expense);
        batch.update(userRef, "monthlyBudget", FieldValue.increment(amount));

        batch.commit()
                .addOnSuccessListener(unused -> {
                    setLoading(false);
                    Toast.makeText(this, R.string.expense_save_success, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, R.string.expense_save_failed, Toast.LENGTH_SHORT).show();
                });
    }

    private String getSelectedExpenseType() {
        int checkedId = binding.rgExpenseType.getCheckedRadioButtonId();

        if (checkedId == View.NO_ID) {
            return null;
        }

        RadioButton selectedButton = findViewById(checkedId);
        return selectedButton.getText().toString();
    }

    private void setLoading(boolean isLoading) {
        binding.btnSaveExpense.setEnabled(!isLoading);
        binding.btnCancelExpense.setEnabled(!isLoading);
        binding.etAmount.setEnabled(!isLoading);
        binding.etDescription.setEnabled(!isLoading);
        binding.spCategory.setEnabled(!isLoading);
        binding.rgExpenseType.setEnabled(!isLoading);
        binding.etDate.setEnabled(!isLoading);
        binding.etNote.setEnabled(!isLoading);
    }
}