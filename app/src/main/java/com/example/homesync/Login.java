package com.example.homesync;

import static com.example.homesync.R.id.loginButton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private TextInputLayout mailLayout;
    private TextInputEditText mailEditText;
    private TextInputLayout passwordLayout;
    private TextInputEditText passwordEditText;
    private TextView error;
    private Button login;
    private Button back;

    private FirebaseAuth mAuth;

    private String errorMessage = "Correo o contraseña incorrectos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mailLayout = findViewById(R.id.mailLayout);
        mailEditText = findViewById(R.id.mailEditText);
        passwordLayout = findViewById(R.id.passwordLayout);
        passwordEditText = findViewById(R.id.passwordEditText);
        error = findViewById(R.id.error);
        login = findViewById(loginButton);
        back = findViewById(R.id.backButton);



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = mailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if(mail.equals("")){
                    mailLayout.setError("Este campo es obligatorio");
                } else {
                    mailLayout.setError(null);   // Quita el mensaje de error
                }

                if(password.equals("")){
                    passwordLayout.setError("Este campo es obligatorio");
                } else {
                    passwordLayout.setError(null);   // Quita el mensaje de error
                }

                if(!mail.equals("") && !password.equals("")){
                    loginUser(mail, password);
                }
            }
        });

        back.setOnClickListener(v -> {
            finish();
        });
    }

    private void loginUser(String mail, String password) {
        mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(Login.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                    Index.activityA.finish();
                    finish();
                    startActivity(new Intent(Login.this, MainActivity.class));
                } else {
                    error.setText(errorMessage);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}