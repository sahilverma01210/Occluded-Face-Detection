package com.example.occludedfacedetection;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/*
    Create Splash Screen
 */
public class Splash extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Intent i = new Intent(Splash.this, Main.class);
        startActivity(i);
        finish();
    }
}
