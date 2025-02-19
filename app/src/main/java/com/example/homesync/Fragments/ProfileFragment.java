package com.example.homesync.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.homesync.Index;
import com.example.homesync.MainActivity;
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
        String mail = mAuth.getCurrentUser().getEmail();
        TextView titulo = view.findViewById(R.id.textView);
        titulo.setText(mail);
        Button backButton = view.findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                MainActivity.activityA.finish();
                startActivity(new Intent(MainActivity.activityA, Index.class));
            }
        });





        FloatingActionButton fab = view.findViewById(R.id.fab);
        // Configuración del click listener del FAB
        fab.setOnClickListener(v -> {

        });


        return view;
    }
}