package com.example.cashkal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cashkal.databinding.ActivityRegisterBinding;
import com.example.cashkal.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Handle user signup with Firebase
 */
public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.btnRegister.setOnClickListener(view -> registerUser());

        binding.tvGoToLogin.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    /**
     * Valid inputs. creates a Firebase Auth user.
     * send to Firestore.
     */
    private void registerUser() {
        String fullName = binding.etFullName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();
        String budgetText = binding.etMonthlyBudget.getText().toString().trim();
        String incomeText = binding.etExpectedIncome.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()
                || confirmPassword.isEmpty() || budgetText.isEmpty() || incomeText.isEmpty()) {
            Toast.makeText(this, R.string.auth_error_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, R.string.auth_error_password_short, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, R.string.auth_error_password_mismatch, Toast.LENGTH_SHORT).show();
            return;
        }

        double monthlyBudget;
        double expectedIncome;

        try {
            monthlyBudget = Double.parseDouble(budgetText);
            expectedIncome = Double.parseDouble(incomeText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.auth_error_numbers, Toast.LENGTH_SHORT).show();
            return;
        }

        if (monthlyBudget <= 0 || expectedIncome <= 0) {
            Toast.makeText(this, R.string.auth_error_positive_numbers, Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && firebaseAuth.getCurrentUser() != null) {
                        String uid = firebaseAuth.getCurrentUser().getUid();

                        User user = new User(
                                uid,
                                fullName,
                                email,
                                monthlyBudget,
                                expectedIncome
                        );

                        saveUserToFirestore(uid, user);
                    } else {
                        setLoading(false);
                        Toast.makeText(this, R.string.auth_register_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Saves the created user profile in Firestore - users/{uid}.
     */
    private void saveUserToFirestore(String uid, User user) {
        firestore.collection("users")
                .document(uid)
                .set(user)
                .addOnSuccessListener(unused -> {
                    setLoading(false);
                    Toast.makeText(this, R.string.auth_register_success, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, R.string.auth_firestore_save_failed, Toast.LENGTH_SHORT).show();
                });
    }

    private void setLoading(boolean isLoading) {
        binding.progressRegister.setVisibility(isLoading ? View.VISIBLE : View.GONE);

        binding.btnRegister.setEnabled(!isLoading);
        binding.etFullName.setEnabled(!isLoading);
        binding.etEmail.setEnabled(!isLoading);
        binding.etPassword.setEnabled(!isLoading);
        binding.etConfirmPassword.setEnabled(!isLoading);
        binding.etMonthlyBudget.setEnabled(!isLoading);
        binding.etExpectedIncome.setEnabled(!isLoading);
    }
}