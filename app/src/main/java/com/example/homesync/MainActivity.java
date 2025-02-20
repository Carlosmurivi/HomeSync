package com.example.homesync;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleCoroutineScope;
import androidx.viewpager.widget.ViewPager;

import com.example.homesync.Fragments.ProfileFragment;
import com.example.homesync.Fragments.SettingsFragment;
import com.example.homesync.Fragments.ViewPagerAdapter;
import com.example.homesync.Model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    public static Activity activityA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityA = this;


        TabLayout tabs = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ProfileFragment(), R.drawable.productos_de_limpieza__6_);
        adapter.addFragment(new SettingsFragment(), R.drawable.baseline_settings_24);


        /*FirebaseRealtimeDatabase.getUserById(mAuth.getCurrentUser().getUid(), MainActivity.this, new FirebaseRealtimeDatabase.UserCallback() {
            @Override
            public void onSuccess(User user) {
                if(user.getGroupCode().trim().equals("")) {
                    adapter.addFragment(new SettingsFragment(), R.drawable.baseline_settings_24);
                } else {
                    adapter.addFragment(new ProfileFragment(), R.drawable.productos_de_limpieza__6_);
                }
            }
            @Override
            public void onFailure(Exception e) {

            }
        });*/



        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);


        for(int i = 0; i < tabs.getTabCount(); i++){
            TabLayout.Tab tab = tabs.getTabAt(i);
            if(tab != null){
                tab.setIcon(adapter.getIcon(i));
            }
        }

    }
}