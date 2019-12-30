package com.example.footballlive;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.footballlive.fragment.MatchBoardFragment;
import com.example.footballlive.fragment.RecruitingFragment;
import com.example.footballlive.fragment.SettingFragment;
import com.example.footballlive.fragment.TeamFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends BaseActivity {
    Fragment matchFragment, teamFragment, settingFragment, recruitingFragment;
    FragmentManager fragmentManager = getSupportFragmentManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        matchFragment = new MatchBoardFragment();
        teamFragment = new TeamFragment();
        settingFragment = new SettingFragment();
        recruitingFragment = new RecruitingFragment();

        fragmentManager.beginTransaction().replace(R.id.main_container, matchFragment).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.main_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.tab1:
                        fragmentManager.beginTransaction().replace(R.id.main_container, matchFragment).commit();
                        return true;
                    case R.id.tab2:
                        fragmentManager.beginTransaction().replace(R.id.main_container, recruitingFragment).commit();
                        return true;
                    case R.id.tab3:
                        fragmentManager.beginTransaction().replace(R.id.main_container, teamFragment).commit();
                        return true;
                    case R.id.tab4:
                        fragmentManager.beginTransaction().replace(R.id.main_container, settingFragment).commit();
                        return true;

                }
                return false;
            }
        });

    }
}
