package com.example.cashkal.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cashkal.R;
import com.example.cashkal.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<Integer> errorMessage = new MutableLiveData<>();

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

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

        firestore.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User loadedUser = documentSnapshot.toObject(User.class);

                        if (loadedUser != null) {
                            user.setValue(loadedUser);
                        } else {
                            errorMessage.setValue(R.string.home_error_load_user);
                        }
                    } else {
                        errorMessage.setValue(R.string.home_error_missing_profile);
                    }
                })
                .addOnFailureListener(e ->
                        errorMessage.setValue(R.string.home_error_load_user)
                );
    }
}