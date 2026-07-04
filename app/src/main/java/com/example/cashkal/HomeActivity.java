package com.example.cashkal;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cashkal.databinding.ActivityHomeBinding;
import com.example.cashkal.utils.AuthNavigator;

public class HomeActivity extends AppCompatActivity {

    private static final int MENU_LOGOUT_ID = 1001;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_LOGOUT_ID, Menu.NONE, "התנתקות");
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_LOGOUT_ID) {
            AuthNavigator.logout(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}