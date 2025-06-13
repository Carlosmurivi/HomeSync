package com.example.homesync;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.homesync.Model.PurchaseProduct;
import com.example.homesync.Model.User;
import com.example.homesync.Model.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FirebaseRealtimeDatabase {


    // USERS
    public static void saveUser(User user, String id, Context context){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
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
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Imagen de usuario actualizada", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error al actualizar la imagen", Toast.LENGTH_SHORT).show();
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
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Apodo actualizado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error al actualizar el apodo", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void updateUserAdministrator(String userId, Boolean administrator, Context context) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        // Referencia al campo específico del nickname del usuario
        mDatabase.child("users").child(userId).child("administrator").setValue(administrator)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Administrador actualizado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error al actualizar el estado", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void addUserPoints(String userId, int points, Context context) {
        DatabaseReference pointsRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("points");

        pointsRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Integer currentValue = currentData.getValue(Integer.class);
                if (currentValue == null) {
                    currentData.setValue(points); // si no había valor, lo inicializa
                } else {
                    currentData.setValue(currentValue + points); // suma
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (committed) {
                    Toast.makeText(context, "Puntos incrementados correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error al incrementar puntos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void restUserPoints(String userId, int points, Context context) {
        DatabaseReference pointsRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("points");

        pointsRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Integer currentValue = currentData.getValue(Integer.class);
                Log.d("restUserPoints", "Valor actual puntos: " + currentValue);

                if (currentValue == null) {
                    currentValue = 0;
                }

                int nuevoValor = currentValue - points;
                if (nuevoValor < 0) {
                    nuevoValor = 0;
                }

                currentData.setValue(nuevoValor);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
            }
        });
    }

    public static void getUsersByGroupId(String groupId, UsersCallback callback) {
        DatabaseReference tasksRef = FirebaseDatabase.getInstance()
                .getReference("groups")
                .child(groupId)
                .child("userIdList");

        tasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<User> users = new ArrayList<>();
                List<String> userIds = new ArrayList<>();

                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    String userId = taskSnapshot.getValue(String.class);
                    if (userId != null) {
                        userIds.add(userId);
                    }
                }

                if (userIds.isEmpty()) {
                    callback.onTasksLoaded(users); // Devuelve vacío si no hay usuarios
                    return;
                }

                // Contador para saber cuándo hemos terminado
                AtomicInteger counter = new AtomicInteger(0);

                for (String userId : userIds) {
                    getUserById(userId, MainActivity.activityA, new UserCallback() {
                        @Override
                        public void onSuccess(User user) {
                            users.add(user);
                            if (counter.incrementAndGet() == userIds.size()) {
                                callback.onTasksLoaded(users);
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            if (counter.incrementAndGet() == userIds.size()) {
                                callback.onTasksLoaded(users); // Devuelve los que se hayan podido cargar
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onError(error);
            }
        });
    }





    // GROUPS
    public static void removeUserFromGroup(String userId, String groupCode, Context context) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("groups").child(groupCode).child("userIdList").child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Usuario eliminado del grupo", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error al eliminar usuario del grupo", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Referencia al campo específico del codigo del grupo
        mDatabase.child("users").child(userId).child("groupCode").setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                        if (task.isSuccessful()) {
                            mDatabase.child("users").child(userId).child("administrator").setValue(false);
                            mDatabase.child("users").child(userId).child("points").setValue(0);
                            mDatabase.child("users").child(userId).child("goldMedals").setValue(0);
                            mDatabase.child("users").child(userId).child("silverMedals").setValue(0);
                            mDatabase.child("users").child(userId).child("bronzeMedals").setValue(0);
                        } else {
                            Toast.makeText(context, "Error al abandonar el grupo", Toast.LENGTH_SHORT).show();
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

    public static void addUserToGroup(String userId, String groupCode, boolean createGroup, String lastReset, Context context) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        // Referencia al campo específico del nickname del usuario
        mDatabase.child("users").child(userId).child("groupCode").setValue(groupCode);
        mDatabase.child("groups").child(groupCode).child("userIdList").child(userId).setValue(userId)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                        if (task.isSuccessful()) {
                            mDatabase.child("users").child(userId).child("administrator").setValue(createGroup);

                            if (createGroup) {
                                Toast.makeText(context, "Se creó el grupo", Toast.LENGTH_SHORT).show();
                                mDatabase.child("groups").child(groupCode).child("options").child("lastReset").setValue(lastReset);
                                if (lastReset.equals("weekly")) {
                                    mDatabase.child("groups").child(groupCode).child("lastReset").setValue(new SimpleDateFormat("yyyy-'W'ww", Locale.getDefault()).format(new Date()));
                                } else {
                                    mDatabase.child("groups").child(groupCode).child("lastReset").setValue(new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date()));
                                }
                            } else {
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

    public static void checkReset(String groupCode, Context context){
        FirebaseDatabase.getInstance().getReference()
                .child("groups")
                .child(groupCode)
                .child("options")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String lastReset = snapshot.child("lastReset").getValue(String.class);

                        if (lastReset.equals("monthly")) {
                            checkMonth(groupCode, context);
                        } else if (lastReset.equals("weekly")){
                            checkWeek(groupCode, context);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Error al comprobar el tipo de reseteo", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void checkMonth(String groupCode, Context context){
        String currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());

        FirebaseDatabase.getInstance().getReference()
                .child("groups")
                .child(groupCode)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String lastReset = snapshot.child("lastReset").getValue(String.class);

                        if (lastReset == null || !lastReset.equals(currentMonth)) {
                            getUsersByGroupId(groupCode, new UsersCallback() {
                                @Override
                                public ArrayList<String[]> onTasksLoaded(List<User> users) {
                                    resetMonthlyPointsAndAssignMedal(snapshot.getRef(), users, lastReset);
                                    return null;
                                }

                                @Override
                                public void onError(DatabaseError error) {
                                    Toast.makeText(context, "Error al obtener los usuarios", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Error al comprobar el mes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static void resetMonthlyPointsAndAssignMedal(DatabaseReference groupRef, List<User> users, String lastReset) {
        // Se ordenan los usuarios en orden por la cantidad de puntos que poseen
        users.sort((u1, u2) -> Integer.compare(u2.getPoints(), u1.getPoints()));

        String goldMedalUserId = null;
        String silverMedalUserId = null;
        String bronzeMedalUserId = null;
        int medal = 1;

        // Se resetean los puntos a los usuarios y se entregan las medallas
        for (User user : users) {
            String userId = user.getId();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            Map<String, Object> updates = new HashMap<>();
            updates.put("points", 0); // reset points

            if (medal == 1) {
                goldMedalUserId = userId;
                updates.put("goldMedals", user.getGoldMedals() + 1); // add medal
                medal++;
            } else if (medal == 2) {
                silverMedalUserId = userId;
                updates.put("silverMedals", user.getSilverMedals() + 1); // add medal
                medal++;
            } else if (medal == 3) {
                bronzeMedalUserId = userId;
                updates.put("bronzeMedals", user.getBronzeMedals() + 1); // add medal
                medal++;
            }

            userRef.updateChildren(updates);
        }



        // Se registran los ganadores
        Map<String, Object> map = new HashMap<>();
        if (goldMedalUserId != null){
            map.put("goldMedal", goldMedalUserId); // reset points
        }
        if (silverMedalUserId != null){
            map.put("silverMedal", silverMedalUserId); // reset points
        }
        if (bronzeMedalUserId != null){
            map.put("bronzeMedal", bronzeMedalUserId); // reset points
        }
        groupRef.child("winnersRegister").child(lastReset).setValue(map);



        // Actualiza el mes del último reset
        String currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
        groupRef.child("lastReset").setValue(currentMonth);
    }

    public static void checkWeek(String groupCode, Context context){
        String currentWeek = new SimpleDateFormat("yyyy-'W'ww", Locale.getDefault()).format(new Date());

        FirebaseDatabase.getInstance().getReference()
                .child("groups")
                .child(groupCode)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String lastReset = snapshot.child("lastReset").getValue(String.class);

                        if (lastReset == null || !lastReset.equals(currentWeek)) {
                            getUsersByGroupId(groupCode, new UsersCallback() {
                                @Override
                                public ArrayList<String[]> onTasksLoaded(List<User> users) {
                                    resetWeeklyPointsAndAssignMedal(snapshot.getRef(), users, lastReset);
                                    return null;
                                }

                                @Override
                                public void onError(DatabaseError error) {
                                    Toast.makeText(context, "Error al obtener los usuarios", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Error al comprobar el mes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static void resetWeeklyPointsAndAssignMedal(DatabaseReference groupRef, List<User> users, String lastReset) {
        // Se ordenan los usuarios en orden por la cantidad de puntos que poseen
        users.sort((u1, u2) -> Integer.compare(u2.getPoints(), u1.getPoints()));

        String goldMedalUserId = null;
        String silverMedalUserId = null;
        String bronzeMedalUserId = null;
        int medal = 1;

        // Se resetean los puntos a los usuarios y se entregan las medallas
        for (User user : users) {
            String userId = user.getId();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            Map<String, Object> updates = new HashMap<>();
            updates.put("points", 0); // reset points

            if (medal == 1) {
                goldMedalUserId = userId;
                updates.put("goldMedals", user.getGoldMedals() + 1); // add medal
                medal++;
            } else if (medal == 2) {
                silverMedalUserId = userId;
                updates.put("silverMedals", user.getSilverMedals() + 1); // add medal
                medal++;
            } else if (medal == 3) {
                bronzeMedalUserId = userId;
                updates.put("bronzeMedals", user.getBronzeMedals() + 1); // add medal
                medal++;
            }

            userRef.updateChildren(updates);
        }



        // Se registran los ganadores
        Map<String, Object> map = new HashMap<>();
        if (goldMedalUserId != null){
            map.put("goldMedal", goldMedalUserId); // reset points
        }
        if (silverMedalUserId != null){
            map.put("silverMedal", silverMedalUserId); // reset points
        }
        if (bronzeMedalUserId != null){
            map.put("bronzeMedal", bronzeMedalUserId); // reset points
        }
        groupRef.child("winnersRegister").child(lastReset).setValue(map);



        // Guarda la semana actual como la última en la que se reseteó
        String currentWeek = new SimpleDateFormat("yyyy-'W'ww", Locale.getDefault()).format(new Date());
        groupRef.child("lastReset").setValue(currentWeek);
    }

    public static void getPurchaseProductToGroup(String groupId, PurchaseProductCallback callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("groups")
                .child(groupId)
                .child("shoppingList");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<PurchaseProduct> favoriteList = new ArrayList<>();
                List<PurchaseProduct> nonFavoriteList = new ArrayList<>();

                for (DataSnapshot Snapshot : snapshot.getChildren()) {
                    PurchaseProduct purchaseProduct = Snapshot.getValue(PurchaseProduct.class);
                    if (purchaseProduct != null) {
                        if (purchaseProduct.isFavorite()) { // O getFavorite() si usas boolean getFavorite()
                            favoriteList.add(purchaseProduct);
                        } else {
                            nonFavoriteList.add(purchaseProduct);
                        }
                    }
                }

                // Unimos ambos: favoritos primero
                List<PurchaseProduct> finalList = new ArrayList<>();
                finalList.addAll(favoriteList);
                finalList.addAll(nonFavoriteList);

                callback.onPurchaseProductLoaded(finalList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onError(error);
            }
        });
    }

    public static void addPurchaseProductToGroup(String groupCode, PurchaseProduct purchaseProduct, Context context) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("groups").child(groupCode).child("shoppingList");

        DatabaseReference newRef = mDatabase.push();
        String generatedId = newRef.getKey();

        purchaseProduct.setId(generatedId);

        newRef.setValue(purchaseProduct)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Producto añadido", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al añadir el producto", Toast.LENGTH_SHORT).show();
                });
    }

    public static void removePurchaseProduct(String userId, String purchaseProductId, Context context) {
        getUserById(userId, context, new UserCallback() {
            @Override
            public void onSuccess(User user) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("groups").child(user.getGroupCode()).child("shoppingList").child(purchaseProductId).removeValue();
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }

    public static void addPurchaseProductToFavorites(String userId, String purchaseProductId, Context context) {
        getUserById(userId, context, new UserCallback() {
            @Override
            public void onSuccess(User user) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("groups").child(user.getGroupCode()).child("shoppingList").child(purchaseProductId).child("favorite");
                mDatabase.setValue(true).addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Añadido a favoritos", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al añadir a favoritos", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }

    public static void addPurchaseProductToNotFavorites(String userId, String purchaseProductId, Context context) {
        getUserById(userId, context, new UserCallback() {
            @Override
            public void onSuccess(User user) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("groups").child(user.getGroupCode()).child("shoppingList").child(purchaseProductId).child("favorite");
                mDatabase.setValue(false).addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Quitado de favoritos", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al quitar de favoritos", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }

    public static void addPurchaseProductToBought(String userId, String purchaseProductId, Context context) {
        getUserById(userId, context, new UserCallback() {
            @Override
            public void onSuccess(User user) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("groups").child(user.getGroupCode()).child("shoppingList").child(purchaseProductId).child("bought");
                mDatabase.setValue(true);
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }

    public static void addPurchaseProductToNotBought(String userId, String purchaseProductId, Context context) {
        getUserById(userId, context, new UserCallback() {
            @Override
            public void onSuccess(User user) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("groups").child(user.getGroupCode()).child("shoppingList").child(purchaseProductId).child("bought");
                mDatabase.setValue(false);
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }





    // TASK
    public static void getTasksByGroupId(String groupId, TasksCallback callback) {
        DatabaseReference tasksRef = FirebaseDatabase.getInstance()
                .getReference("groups")
                .child(groupId)
                .child("taskList");

        tasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Task> taskList = new ArrayList<>();
                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {

                    Task task = taskSnapshot.getValue(Task.class);
                    if (task != null) {
                        taskList.add(task);
                    }
                }
                callback.onTasksLoaded(taskList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onError(error);
            }
        });
    }

    public static void getCompletedTasksByGroupId(String groupId, TasksCallback callback) {
        DatabaseReference tasksRef = FirebaseDatabase.getInstance()
                .getReference("groups")
                .child(groupId)
                .child("taskCompleteList");

        tasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Task> taskList = new ArrayList<>();
                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {

                    Task task = taskSnapshot.getValue(Task.class);
                    if (task != null) {
                        taskList.add(task);
                    }
                }
                callback.onTasksLoaded(taskList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onError(error);
            }
        });
    }

    public static void checkTaskExists(String groupCode, String taskId, Context context, OnTaskCheckListener listener) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("groups").child(groupCode).child("taskList").child(taskId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listener.onTaskExists(true); // La tarea existe
                } else {
                    listener.onTaskExists(false); // La tarea no existe
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error al verificar la tarea", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void getTaskById(String groupId, String taskId, TaskCallback callback, Context context) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("groups").child(groupId).child("taskList").child(taskId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Task task = dataSnapshot.getValue(Task.class);
                    callback.onSuccess(task);
                } else {
                    Toast.makeText(context, "Tarea no encontrada", Toast.LENGTH_SHORT).show();
                    callback.onFailure(new Exception("Tarea no encontrada"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Error al leer los datos", Toast.LENGTH_SHORT).show();
                callback.onFailure(databaseError.toException());
            }
        });
    }

    public static void addTaskToGroup(Task task, String groupCode, Context context) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        // Referencia al campo específico del nickname del usuario
        mDatabase.child("groups").child(groupCode).child("taskList").child("" + task.getId()).setValue(task)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                        Toast.makeText(context, "Se creó la tarea", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void completeTask(String userId, Task task, String groupCode, Context context) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("groups").child(groupCode).child("taskCompleteList").push().setValue(task)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> taskk) {
                        if (!task.isPredetermined()) {
                            removeTask(groupCode, Integer.toString(task.getId()));
                        }
                        Toast.makeText(context, "Se completó la tarea", Toast.LENGTH_SHORT).show();
                    }
                });

        addUserPoints(userId, task.getPoints(), context);
    }

    public static void removeTask(String groupCode, String taskId) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("groups").child(groupCode).child("taskList").child(taskId).removeValue();
    }

    public static void removeCompleteTask(String groupCode, String date, String userId, String points, Context context) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance()
                .getReference()
                .child("groups")
                .child(groupCode)
                .child("taskCompleteList");

        int point = Integer.parseInt(points);

        // Leer las tareas completas para buscar y borrar las que coincidan en fecha
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    String taskDate = taskSnapshot.child("dateTime").getValue(String.class);
                    if (taskDate != null && taskDate.equals(date)) {
                        taskSnapshot.getRef().removeValue();
                        restUserPoints(userId, point, context);
                        Toast.makeText(context, "Tarea eliminada y puntos restados", Toast.LENGTH_SHORT).show();
                    }
                }
                Toast.makeText(context, "No se encontró la tarea", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error al acceder a tareas", Toast.LENGTH_SHORT).show();
            }
        });
    }





    // CALLBACK
    public interface UserCallback {
        void onSuccess(User user);
        void onFailure(Exception e);
    }

    public interface UsersCallback {
        ArrayList<String[]> onTasksLoaded(List<User> users);
        void onError(DatabaseError error);
    }

    public interface OnGroupCheckListener {
        void onGroupExists(boolean exists);
    }

    public interface OnTaskCheckListener {
        void onTaskExists(boolean exists);
    }

    public interface TasksCallback {
        ArrayList<String[]> onTasksLoaded(List<Task> tasks);
        void onError(DatabaseError error);
    }

    public interface PurchaseProductCallback {
        ArrayList<String[]> onPurchaseProductLoaded(List<PurchaseProduct> purchaseProducts);
        void onError(DatabaseError error);
    }

    public interface TaskCallback {
        void onSuccess(Task task);
        void onFailure(Exception e);
    }
}
