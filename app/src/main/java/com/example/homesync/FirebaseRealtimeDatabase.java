package com.example.homesync;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.homesync.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseRealtimeDatabase {

    public static void saveUser(User user){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");

        List<User> users = new ArrayList<>();
        users.add(user);
        // Utiliza push() para agregar un nuevo usuario sin sobrescribir los existentes
        myRef.push().setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.e("111", "Usuario guardado correctamente.");
            } else {
                Log.e("111", "Error al guardar el usuario: " + task.getException().getMessage());
            }
        });
    }

    public static void readUser(final FirebaseCallbackUser callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<HashMap<String, Object>> users = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HashMap<String, Object> user = (HashMap<String, Object>) snapshot.getValue();
                    users.add(user);
                }
                callback.onCallback(users);  // Devuelve el valor usando el callback
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to read value.", error.toException());
                callback.onCallback(null);  // En caso de error, devuelve null
            }
        });
    }

    public interface FirebaseCallbackUser {
        void onCallback(List<HashMap<String, Object>> value);
    }
}
