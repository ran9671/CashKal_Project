package com.example.cashkal.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cashkal.model.User;

// holds the home screen data and survives screen rotation
public class HomeViewModel extends ViewModel {

    // internal, editable data holder
    private final MutableLiveData<User> user = new MutableLiveData<>();

    // read-only view of the user that the fragment observes
    public LiveData<User> getUser() {
        return user;
    }

    // loads the current user's profile
    // TODO: read from Firestore users/{uid} after feat/auth is merged
    public void loadUser() {
        // temporary placeholder so the screen shows something before Firebase is wired
        User placeholder = new User();
        placeholder.setFullName("");
        placeholder.setMonthlyBudget(0);
        placeholder.setExpectedMonthlyIncome(0);
        user.setValue(placeholder);
    }
}