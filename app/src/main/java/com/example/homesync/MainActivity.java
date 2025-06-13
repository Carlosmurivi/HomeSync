package com.example.homesync;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleCoroutineScope;
import androidx.viewpager.widget.ViewPager;

import com.example.homesync.Fragments.GroupFragment;
import com.example.homesync.Fragments.ProfileFragment;
import com.example.homesync.Fragments.SettingsFragment;
import com.example.homesync.Fragments.ShoppingListFragment;
import com.example.homesync.Fragments.TasksFragment;
import com.example.homesync.Fragments.ViewPagerAdapter;
import com.example.homesync.Model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    public static Activity activityA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityA = this;

        mAuth = FirebaseAuth.getInstance();
        FirebaseRealtimeDatabase.getUserById(mAuth.getUid(), this, new FirebaseRealtimeDatabase.UserCallback() {
            @Override
            public void onSuccess(User user) {
                if(user.getGroupCode() != null && !user.getGroupCode().equals("")){
                    FirebaseRealtimeDatabase.checkReset(user.getGroupCode(), activityA);
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });


        TabLayout tabs = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ShoppingListFragment(), R.drawable.carrito_de_la_compra1);
        adapter.addFragment(new TasksFragment(), R.drawable.list_tareas);
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