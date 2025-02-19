package com.example.homesync.Fragments;

import android.annotation.SuppressLint;
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

import com.example.homesync.FirebaseRealtimeDatabase;
import com.example.homesync.Index;
import com.example.homesync.MainActivity;
import com.example.homesync.Model.User;
import com.example.homesync.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsFragment extends Fragment {

    private FirebaseAuth mAuth;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings_fragment, container, false);


        mAuth = FirebaseAuth.getInstance();
        String mail = mAuth.getCurrentUser().getEmail();
        TextView titulo = view.findViewById(R.id.textView);
        TextView aviso = view.findViewById(R.id.createGroupTextView);
        titulo.setText(mail);
        Button backButton = view.findViewById(R.id.backButton);
        Button createGroupButton = view.findViewById(R.id.createGroupButton);

        FirebaseRealtimeDatabase.getUserById(mAuth.getCurrentUser().getUid(), MainActivity.activityA, new FirebaseRealtimeDatabase.UserCallback() {
            @Override
            public void onSuccess(User user) {
                if(user.getGroupCode().trim().equals("")) {
                    createGroupButton.setVisibility(View.VISIBLE);
                    aviso.setVisibility(View.VISIBLE);
                } else {
                    backButton.setVisibility(View.VISIBLE);
                    titulo.setVisibility(View.VISIBLE);
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



        return view;
    }
}