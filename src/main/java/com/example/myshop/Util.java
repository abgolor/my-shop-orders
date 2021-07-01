package com.example.myshop;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

public class Util {
    public static void changeStatusBarAndNavigationBarColor(Activity activity, int statusBarColor, int navigationColor){
        Window window  = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, statusBarColor));
        window.setNavigationBarColor(ContextCompat.getColor(activity, navigationColor));
    }
}
