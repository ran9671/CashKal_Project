package com.example.cashkal.utils;

import android.app.Activity;
import android.content.Intent;

import com.example.cashkal.HomeActivity;
import com.example.cashkal.WelcomeActivity;
import com.google.firebase.auth.FirebaseAuth;

public class AuthNavigator {

    public static void openHomeAndClearTask(Activity activity) {
        Intent intent = new Intent(activity, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    public static void openWelcomeAndClearTask(Activity activity) {
        Intent intent = new Intent(activity, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    public static void logout(Activity activity) {
        FirebaseAuth.getInstance().signOut();
        openWelcomeAndClearTask(activity);
    }
}