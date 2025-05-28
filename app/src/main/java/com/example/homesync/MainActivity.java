package com.example.homesync;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleCoroutineScope;
import androidx.viewpager.widget.ViewPager;

import com.example.homesync.Fragments.GroupFragment;
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
        adapter.addFragment(new ProfileFragment(), R.drawable.user_picture);
        adapter.addFragment(new GroupFragment(), R.drawable.productos_de_limpieza__6_);
        adapter.addFragment(new SettingsFragment(), R.drawable.baseline_settings_24);



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