package com.example.cashkal;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cashkal.databinding.ActivityAddIncomeBinding;

import java.util.Calendar;
import java.util.Locale;

// screen for adding a new income (UI only for now, no database)
public class AddIncomeActivity extends AppCompatActivity {

    private ActivityAddIncomeBinding binding;
    private final Calendar selectedDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddIncomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // show today's date to start
        updateDateField();

        // open a date picker when the date field is tapped
        binding.etDate.setOnClickListener(v -> showDatePicker());

        // handle the save button
        binding.btnSaveIncome.setOnClickListener(v -> saveIncome());

        // cancel and return without saving
        binding.btnCancelIncome.setOnClickListener(v -> finish());
    }

    // opens the calendar dialog and stores the chosen date
    private void showDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    selectedDate.set(year, month, day);
                    updateDateField();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    // writes the selected date into the date field as dd/MM/yyyy
    private void updateDateField() {
        String text = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                selectedDate.get(Calendar.DAY_OF_MONTH),
                selectedDate.get(Calendar.MONTH) + 1,
                selectedDate.get(Calendar.YEAR));
        binding.etDate.setText(text);
    }

    // validates the form and (for now) shows a confirmation toast
    private void saveIncome() {
        String amount = binding.etAmount.getText().toString().trim();

        if (TextUtils.isEmpty(amount)) {
            binding.etAmount.setError(getString(R.string.field_amount));
            return;
        }

        // TODO: Ran will save this income to Firestore
        Toast.makeText(this, R.string.save_income, Toast.LENGTH_SHORT).show();
        finish();
    }
}