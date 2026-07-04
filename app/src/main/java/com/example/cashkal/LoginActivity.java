package com.example.cashkal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cashkal.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Handle user login with Firebase
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.btnLogin.setOnClickListener(view -> loginUser());

        binding.tvGoToRegister.setOnClickListener(view ->
                Toast.makeText(this, R.string.auth_register_coming_next, Toast.LENGTH_SHORT).show()
        );
    }

    /**
     * Valid input
     */
    private void loginUser() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.auth_error_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    setLoading(false);

                    if (task.isSuccessful()) {
                        Toast.makeText(this, R.string.auth_login_success, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, R.string.auth_login_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void setLoading(boolean isLoading) {
        binding.progressLogin.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!isLoading);
        binding.etEmail.setEnabled(!isLoading);
        binding.etPassword.setEnabled(!isLoading);
    }
}