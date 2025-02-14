package com.example.homesync;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.example.homesync.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateAccount extends AppCompatActivity {

    private TextInputLayout nameLayout;
    private TextInputEditText nameEditText;
    private TextInputLayout nicknameLayout;
    private TextInputEditText nicknameEditText;
    private TextInputLayout mailLayout;
    private TextInputEditText mailEditText;
    private TextInputLayout passwordLayout;
    private TextInputEditText passwordEditText;
    private Button create;
    private Button back;


    //FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        //mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        nameLayout = findViewById(R.id.nameLayout);
        nameEditText = findViewById(R.id.nameEditText);
        nicknameLayout = findViewById(R.id.nicknameLayout);
        nicknameEditText = findViewById(R.id.nicknameEditText);
        mailLayout = findViewById(R.id.mailLayout);
        mailEditText = findViewById(R.id.mailEditText);
        passwordLayout = findViewById(R.id.passwordLayout);
        passwordEditText = findViewById(R.id.passwordEditText);
        create = findViewById(R.id.createButton);
        back = findViewById(R.id.backButton);


        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String nickname = nicknameEditText.getText().toString().trim();
                String mail = mailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();


                if (name.equals("")){
                    nameLayout.setError("Este campo es obligatorio");
                } else {
                    nameLayout.setError(null);   // Quita el mensaje de error
                }

                if (nickname.equals("")){
                    nicknameLayout.setError("Este campo es obligatorio");
                } else {
                    nicknameLayout.setError(null);   // Quita el mensaje de error
                }

                if (mail.equals("")){
                    mailLayout.setError("Este campo es obligatorio");
                } else if (!isValidEmail(mail)) {
                    mailLayout.setError("Formato de correo inválido");
                } else {
                    mailLayout.setError(null);   // Quita el mensaje de error
                }

                if (password.equals("")){
                    passwordLayout.setError("Este campo es obligatorio");
                } else if (password.length() < 6) {
                    passwordLayout.setError("La contraseña debe tener al menos 6 caracteres");
                } else {
                    passwordLayout.setError(null);   // Quita el mensaje de error
                }

                if (!password.equals("") && password.length() >= 6 && isValidEmail(mail) && !mail.equals("") && !nickname.equals("") && !name.equals("")){
                    registerUser(name, nickname, mail, password);
                }
            }
        });
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    private void registerUser(String name, String nickname, String mail, String password) {
        mAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //String id = mAuth.getCurrentUser().getUid();
                String id = "2";
                Map<String, Object> map = new HashMap<>();
                map.put("id", id);
                map.put("name", name);
                map.put("nickname", nickname);
                map.put("mail", mail);
                map.put("password", password);

                User user = new User(id, name, nickname, mail, password);

                Log.e("user", user.getMail());

                FirebaseRealtimeDatabase.saveUser(user);

                /*mFirestore.collection("user").document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        finish();
                        startActivity(new Intent(CreateAccount.this, MainActivity.class));
                        Toast.makeText(CreateAccount.this, "Usuario Creado", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateAccount.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                    }
                });*/
                Toast.makeText(CreateAccount.this, "Usuario Creado", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateAccount.this, "Error al registrar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}