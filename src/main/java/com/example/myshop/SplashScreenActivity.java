package com.example.myshop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity{

    private static int SPLASH_SCREEN_LENGTH = 5000; //Time to load splash screen is 5s

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //Handler - It is a java method used to load to a particular method after a period of time
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), StartUpActivity.class)); //start activity
                finish(); //Finish removes the Splash Screen Activity from the stack of activities
            }
        }, SPLASH_SCREEN_LENGTH); //After splash screen seconds then start activity method is called
    }
}
