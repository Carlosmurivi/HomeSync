package com.example.homesync.Fragments;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.homesync.FirebaseRealtimeDatabase;
import com.example.homesync.Index;
import com.example.homesync.MainActivity;
import com.example.homesync.Model.User;
import com.example.homesync.Prueba;
import com.example.homesync.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser user;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();
        String id = mAuth.getCurrentUser().getUid();

        ImageView image = view.findViewById(R.id.imageView);
        TextView titulo = view.findViewById(R.id.textView);
        TextView aviso = view.findViewById(R.id.createGroupTextView);
        Button backButton = view.findViewById(R.id.backButton);
        Button createGroupButton = view.findViewById(R.id.createGroupButton);
        FloatingActionButton fab = view.findViewById(R.id.fab);


        FirebaseRealtimeDatabase.getUserById(mAuth.getCurrentUser().getUid(), MainActivity.activityA, new FirebaseRealtimeDatabase.UserCallback() {
            @Override
            public void onSuccess(User user) {
                if(!user.getGroupCode().trim().equals("")) {
                    createGroupButton.setVisibility(View.VISIBLE);
                    aviso.setVisibility(View.VISIBLE);
                } else {
                    backButton.setVisibility(View.VISIBLE);
                    titulo.setVisibility(View.VISIBLE);
                    image.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);

                    Glide.with(ProfileFragment.this).load(user.getImage()).into(image);
                    titulo.setText("@" + user.getNickname());
                }
            }
            @Override
            public void onFailure(Exception e) {

            }
        });



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                MainActivity.activityA.finish();
                startActivity(new Intent(MainActivity.activityA, Index.class));
            }
        });






        // Configuración del click listener del FAB
        fab.setOnClickListener(v -> {

        });


        return view;
    }
}