package com.example.cashkal;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cashkal.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_fragment_container, new HomeFragment())
                    .commit();
        }
    }
}