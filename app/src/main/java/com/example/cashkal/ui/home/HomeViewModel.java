package com.example.cashkal.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cashkal.R;
import com.example.cashkal.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<Integer> errorMessage = new MutableLiveData<>();

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private ListenerRegistration userListener;

    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<Integer> getErrorMessage() {
        return errorMessage;
    }

    public void loadUser() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            errorMessage.setValue(R.string.auth_error_not_logged_in);
            return;
        }

        String uid = currentUser.getUid();

        if (userListener != null) {
            userListener.remove();
        }

        userListener = firestore.collection("users")
                .document(uid)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) {
                        errorMessage.setValue(R.string.home_error_load_user);
                        return;
                    }

                    if (documentSnapshot == null || !documentSnapshot.exists()) {
                        errorMessage.setValue(R.string.home_error_missing_profile);
                        return;
                    }

                    User loadedUser = documentSnapshot.toObject(User.class);

                    if (loadedUser != null) {
                        user.setValue(loadedUser);
                    } else {
                        errorMessage.setValue(R.string.home_error_load_user);
                    }
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        if (userListener != null) {
            userListener.remove();
            userListener = null;
        }
    }
}