package com.example.homesync;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.homesync.Model.Group;
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

    public static void updateUserGroupCode(String userId, String groupCode, boolean creator, Context context) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        // Referencia al campo específico del codigo del grupo
        mDatabase.child("users").child(userId).child("groupCode").setValue(groupCode)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if(creator){
                                mDatabase.child("users").child(userId).child("administrator").setValue(creator);
                                Toast.makeText(MainActivity.activityA, "Grupo creado", Toast.LENGTH_SHORT).show();
                            } else {
                                mDatabase.child("users").child(userId).child("administrator").setValue(false);
                                Toast.makeText(MainActivity.activityA, "Unido a un grupo", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if(creator){
                                Toast.makeText(MainActivity.activityA, "Error al crear el grupo", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.activityA, "Error al unirse al grupo", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    public static void removeUserFromGroup(String userId, String groupCode, Context context) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("groups").child(groupCode).child("userIdList").child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.e("Entra", "Entra 3");
                    Toast.makeText(context, "Usuario eliminado del grupo", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("Entra", "Entra 4");
                    Toast.makeText(context, "Error al eliminar usuario del grupo", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Referencia al campo específico del codigo del grupo
        mDatabase.child("users").child(userId).child("groupCode").setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.e("Entra", "Entra 1");
                            mDatabase.child("users").child(userId).child("administrator").setValue(false);
                        } else {
                            Log.e("Entra", "Entra 2");
                            Toast.makeText(MainActivity.activityA, "Error al abandonar el grupo", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public static void checkGroupExists(String groupCode, Context context, OnGroupCheckListener listener) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("groups").child(groupCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listener.onGroupExists(true); // El grupo existe
                } else {
                    listener.onGroupExists(false); // El grupo no existe
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error al verificar el grupo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void addUserToGroup(String userId, String groupCode, boolean createGroup, Context context) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        // Referencia al campo específico del nickname del usuario
        mDatabase.child("users").child(userId).child("groupCode").setValue(groupCode);
        mDatabase.child("groups").child(groupCode).child("userIdList").child(userId).setValue(userId)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (createGroup) {
                                mDatabase.child("users").child(userId).child("administrator").setValue(createGroup);
                                Toast.makeText(context, "Se creó el grupo", Toast.LENGTH_SHORT).show();
                            } else {
                                mDatabase.child("users").child(userId).child("administrator").setValue(createGroup);
                                Toast.makeText(context, "Se accedió al grupo", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (createGroup) {
                                Toast.makeText(context, "Error al crear el grupo", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Error al acceder al grupo", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    public static void updateUserNickname(String userId, String newNickname, Context context) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        // Referencia al campo específico del nickname del usuario
        mDatabase.child("users").child(userId).child("nickname").setValue(newNickname)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Apodo actualizado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error al actualizar el apodo", Toast.LENGTH_SHORT).show();
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

    public interface OnGroupCheckListener {
        void onGroupExists(boolean exists);
    }
}
