package com.example.cashkal;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cashkal.databinding.ActivityWelcomeBinding;
import android.content.Intent;

///open screen

public class WelcomeActivity extends AppCompatActivity {

    private ActivityWelcomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.btnLogin.setOnClickListener(view ->
                startActivity(new Intent(this, LoginActivity.class))
        );

        binding.btnRegister.setOnClickListener(view ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }
}