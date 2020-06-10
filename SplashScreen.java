package com.example.contactmanager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;


public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                //start the next activity once the timer ends
                Intent intent = new Intent(getApplicationContext(),
                        Login.class);
                startActivity(intent);

                finish();

            }
        }, SPLASH_TIME_OUT);
    }
}
