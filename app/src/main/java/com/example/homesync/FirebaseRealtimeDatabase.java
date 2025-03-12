package com.example.homesync;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.homesync.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseRealtimeDatabase {

    public static void saveUser(User user, String id, Context context){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Usuario Creado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error al registrar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static List<User> readUsers(Context context, final UsersCallback callback) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<User> usersList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    usersList.add(user);
                }
                callback.onSuccess(usersList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Error al leer los datos", Toast.LENGTH_SHORT).show();
                callback.onFailure(databaseError.toException());
            }
        });
        return null;
    }

    public static void getUserById(String id, Context context, final UserCallback callback) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    callback.onSuccess(user);
                } else {
                    Toast.makeText(context, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                    callback.onFailure(new Exception("Usuario no encontrado"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Error al leer los datos", Toast.LENGTH_SHORT).show();
                callback.onFailure(databaseError.toException());
            }
        });
    }

    public static void updateUserImage(String userId, String newImageUrl, Context context) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        // Referencia al campo específico de la URL de la imagen
        mDatabase.child("users").child(userId).child("image").setValue(newImageUrl)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Imagen de usuario actualizada", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error al actualizar la imagen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public interface UsersCallback {
        void onSuccess(List<User> users);
        void onFailure(Exception e);
    }

    public interface UserCallback {
        void onSuccess(User user);
        void onFailure(Exception e);
    }
}
