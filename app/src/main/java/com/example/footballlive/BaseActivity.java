package com.example.footballlive;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.footballlive.data.Team;
import com.example.footballlive.data.User;

public class BaseActivity extends AppCompatActivity {

    public static User user;
    public static Team team;
    public static String fcmSeverKey;
    public static String myToken;
    public static String naverApiSecretKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

    }
}
