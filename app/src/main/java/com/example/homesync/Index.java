package com.example.homesync;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Index extends AppCompatActivity {

    public static Activity activityA;
    private Button loginButton;
    private Button createAccountButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        mAuth = FirebaseAuth.getInstance();
        activityA = this;
        loginButton = findViewById(R.id.loginButton);
        createAccountButton = findViewById(R.id.createAccountButton);


        loginButton.setOnClickListener(v -> {
            startActivity(new Intent(Index.this, Login.class));
        });

        createAccountButton.setOnClickListener(v -> {
            startActivity(new Intent(Index.this, CreateAccount.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            finish();
            startActivity(new Intent(Index.this, MainActivity.class));
        }
    }
}