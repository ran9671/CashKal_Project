package com.example.cashkal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cashkal.databinding.FragmentHomeBinding;
import com.example.cashkal.model.User;
import com.example.cashkal.ui.home.HomeViewModel;

public class HomeFragment extends Fragment {

    private HomeViewModel mViewModel;
    private FragmentHomeBinding binding;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // inflate the home layout using view binding
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init the view model
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // observe the user and update the screen when it changes
        mViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) return;

            // greeting with the user name, or default greeting when empty
            if (user.getFullName() != null && !user.getFullName().isEmpty()) {
                binding.tvGreeting.setText(getString(R.string.home_greeting, user.getFullName()));
            } else {
                binding.tvGreeting.setText(getString(R.string.home_greeting_default));
            }

            // show the three money values
            binding.tvBudgetAmount.setText(getString(R.string.currency_format, user.getMonthlyBudget()));
            binding.tvIncomeAmount.setText(getString(R.string.currency_format, user.getExpectedMonthlyIncome()));

            double net = user.getExpectedMonthlyIncome() - user.getMonthlyBudget();
            binding.tvBalanceAmount.setText(getString(R.string.currency_format, net));
        });
        mViewModel.getErrorMessage().observe(getViewLifecycleOwner(), messageResId -> {
            if (messageResId == null) return;
            Toast.makeText(getContext(), messageResId, Toast.LENGTH_SHORT).show();
        });
        // load the user data
        mViewModel.loadUser();

        // buttons are placeholders until the add transaction screen exists
        binding.btnAddExpense.setOnClickListener(v -> {
            Toast.makeText(getContext(), R.string.coming_soon, Toast.LENGTH_SHORT).show();
        });
        binding.btnAddIncome.setOnClickListener(v -> {
            Toast.makeText(getContext(), R.string.coming_soon, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // release the binding to avoid memory leaks
        binding = null;
    }
}