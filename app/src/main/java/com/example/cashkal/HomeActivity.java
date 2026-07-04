package com.example.cashkal;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.cashkal.databinding.ActivityHomeBinding;

// hosts the bottom navigation and the fragment screens
public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // find the nav host and get its controller
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        // link the bottom navigation bar to the nav graph
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
    }
}