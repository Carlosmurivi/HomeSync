package com.example.homesync.Model;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.homesync.FirebaseRealtimeDatabase;
import com.example.homesync.R;
import com.example.homesync.UserDetails;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Task {

    // ATRIBUTOS
    private int id;
    private String description;
    private int points;
    private String userId;
    private String imageUrl;
    private boolean predetermined;
    private String dateTime;



    // CONSTRUCTORES
    public Task() {
    }

    public Task(int id, String description, int points, String userId, boolean predetermined) {
        this.id = id;
        this.description = description;
        this.points = points;
        this.userId = userId;
        this.predetermined = predetermined;
    }

    public Task(int id, String description, int points, String userId, String imageUrl, String dateTime, boolean predetermined) {
        this.id = id;
        this.description = description;
        this.points = points;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.dateTime = dateTime;
        this.predetermined = predetermined;
    }

    public Task(int id, String description, int points, boolean predetermined) {
        this.id = id;
        this.description = description;
        this.points = points;
        this.predetermined = predetermined;
    }

    public Task(String description, int points, String userId, boolean predetermined) {
        this.description = description;
        this.points = points;
        this.userId = userId;
        this.predetermined = predetermined;
    }



    // GETTERS AND SETTERS
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isPredetermined() {
        return predetermined;
    }

    public void setPredetermined(boolean predetermined) {
        this.predetermined = predetermined;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }





    // METODOS

    /**
     * Genera una tabla con el ranking de los usuarios.
     * @param tableLayout
     * @param user
     * @param context
     */
    public static void generateRankingTable(TableLayout tableLayout, User user, Context context) {
        ArrayList<String[]> datos = new ArrayList<>();

        completeRankingTable(datos, user.getId(), context);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Limpia la tabla antes de añadir filas nuevas
            //tableLayout.removeAllViews();

            tableLayout.setPadding(8, 8, 8, 8);

            for (String[] row : datos) {
                TableRow fila = new TableRow(context);

                for (int j = 0; j < (row.length) - 1; j++) {
                    TextView celda = new TextView(context);
                    celda.setText(row[j]);
                    celda.setPadding(16, 16, 16, 16);
                    celda.setTextSize(16);
                    celda.setTextColor(ContextCompat.getColor(context, R.color.darkBlue));
                    celda.setBackgroundColor(ContextCompat.getColor(context, R.color.background));

                    // Establecer márgenes entre celdas
                    TableRow.LayoutParams params = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(4, 4, 4, 4); // izquierda, arriba, derecha, abajo
                    celda.setLayoutParams(params);

                    // Listener solo para las celdas que no son de encabezado
                    celda.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, UserDetails.class);
                            intent.putExtra("idUser", row[(row.length)-1]);
                            intent.putExtra("administrator", user.isAdministrator());
                            context.startActivity(intent);
                        }
                    });

                    fila.addView(celda);
                }

                tableLayout.addView(fila);
            }
        }, 1500); // 1000 milisegundos = 1 segundo
    }

    /**
     * Devuelve los registros de una tabla que muestra un ranking con el puesto, nombre y puntos totales.
     * @param datos
     * @param idUser
     * @return Lista de registros para imprimir en la tabla.
     */
    private static ArrayList<String[]> completeRankingTable(ArrayList<String[]> datos, String idUser, Context context) {
        FirebaseRealtimeDatabase.getUserById(idUser, context, new FirebaseRealtimeDatabase.UserCallback() {
            @Override
            public void onSuccess(User user) {
                FirebaseRealtimeDatabase.getUsersByGroupId(user.getGroupCode(), new FirebaseRealtimeDatabase.UsersCallback() {
                    @Override
                    public ArrayList<String[]> onTasksLoaded(List<User> users) {
                        users.sort((u1, u2) -> Integer.compare(u2.getPoints(), u1.getPoints()));
                        int puesto = 1;

                        for (User u : users) {
                            String[] user = {Integer.toString(puesto), u.getNickname(), Integer.toString(u.getPoints()), u.getId()};
                            datos.add(user);
                            puesto++;
                        }

                        return datos;
                    }

                    @Override
                    public void onError(DatabaseError error) {
                        Toast.makeText(context, "Error al obtener las tareas", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(context, "Error al obtener el Usuario", Toast.LENGTH_SHORT).show();
            }
        });

        return datos;
    }





    /**
     * Genera una tabla con todas las tareas disponibles para completar.
     * @param tableLayout
     * @param user
     * @param context
     */
    public static void generateAllTaskTable(TableLayout tableLayout, User user, Context context) {
        ArrayList<String[]> datos = new ArrayList<>();

        completeAllTaskTable(datos, user.getId(), context);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            tableLayout.setPadding(8, 8, 8, 8);

            for (String[] row : datos) {
                TableRow fila = new TableRow(context);

                for (int j = 0; j < (row.length) - 2; j++) {
                    TextView celda = new TextView(context);
                    celda.setText(row[j]);
                    celda.setPadding(16, 16, 16, 16);
                    celda.setTextSize(16);

                    // Comprobamos si es una tarea predeterminada para cambiarle el color del texto
                    if (row[(row.length) - 2].equals("true")) {
                        celda.setTextColor(ContextCompat.getColor(context, R.color.darkBlue));
                    } else {
                        celda.setTextColor(ContextCompat.getColor(context, R.color.texts));
                    }

                    celda.setBackgroundColor(ContextCompat.getColor(context, R.color.background));

                    // Establecer márgenes entre celdas
                    TableRow.LayoutParams params = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(4, 4, 4, 4); // izquierda, arriba, derecha, abajo
                    celda.setLayoutParams(params);

                    fila.addView(celda);
                }

                tableLayout.addView(fila);
            }
        }, 1500); // 1000 milisegundos = 1 segundo
    }


    /**
     * Devuelve los registros de una tabla que muestra todas las tareas disponibles para realizar con los campos Descripción y Puntos.
     * @param datos
     * @param idUser
     * @param context
     * @return Lista de registros para imprimir en la tabla.
     */
    private static ArrayList<String[]> completeAllTaskTable(ArrayList<String[]> datos, String idUser, Context context) {
        FirebaseRealtimeDatabase.getUserById(idUser, context, new FirebaseRealtimeDatabase.UserCallback() {
            @Override
            public void onSuccess(User user) {
                FirebaseRealtimeDatabase.getTasksByGroupId(user.getGroupCode(), new FirebaseRealtimeDatabase.TasksCallback() {
                    @Override
                    public ArrayList<String[]> onTasksLoaded(List<Task> tasks) {
                        tasks = sortTasksByValue(tasks);
                        for (Task t : tasks) {
                            String[] task = {t.getDescription(), Integer.toString(t.getPoints()), Boolean.toString(t.isPredetermined()), Integer.toString(t.getId())};
                            datos.add(task);
                        }

                        return datos;
                    }

                    @Override
                    public void onError(DatabaseError error) {

                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(context, "Error al obtener el Usuario", Toast.LENGTH_SHORT).show();
            }
        });

        return datos;
    }





    /**
     * Genera una tabla con las últimas 20 tareas completadas de un usuario.
     * @param tableLayout
     * @param idUser
     * @param context
     */
    public static void generateCompletedTasksUserTable(TableLayout tableLayout, String idUser, Context context) {
        ArrayList<String[]> datos = new ArrayList<>();

        completeCompletedTasksUserTable(datos, idUser, context);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            boolean firstRow = true;

            int length = datos.size();

            // Solo se muestran hasta los 20 últimos registros
            if (length > 20) {
                length = 20;
            }

            for (int i = 0; i < length; i++) {
                String[] row = datos.get(i);
                TableRow fila = new TableRow(context);

                for (int j = 0; j < (row.length) - 2; j++) {
                    TextView celda = new TextView(context);
                    celda.setText(row[j]);
                    celda.setPadding(16, 16, 16, 16);
                    celda.setTextSize(16);

                    // Comprobamos si es una tarea predeterminada para cambiarle el color del texto
                    if (row[(row.length) - 2].equals("true")) {
                        celda.setTextColor(ContextCompat.getColor(context, R.color.darkBlue));
                    } else {
                        celda.setTextColor(ContextCompat.getColor(context, R.color.texts));
                    }

                    celda.setBackgroundColor(ContextCompat.getColor(context, R.color.background));


                    // Establecer márgenes entre celdas
                    TableRow.LayoutParams params = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(4, 4, 4, 4); // izquierda, arriba, derecha, abajo
                    celda.setLayoutParams(params);


                    // Listener solo para las celdas que no son de encabezado
                    celda.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            displayImageDialog(row[(row.length)-1], context);
                        }
                    });

                    fila.addView(celda);
                }

                firstRow = false;
                tableLayout.addView(fila);
            }
        }, 1500); // 1000 milisegundos = 1 segundo
    }

    /**
     * Devuelve los registros de una tabla que muestra las últimas 20 tareas completadas de un usuario.
     * @param datos
     * @param idUser
     * @return Lista de registros para imprimir en la tabla.
     */
    private static ArrayList<String[]> completeCompletedTasksUserTable(ArrayList<String[]> datos, String idUser, Context context) {
        FirebaseRealtimeDatabase.getUserById(idUser, context, new FirebaseRealtimeDatabase.UserCallback() {
            @Override
            public void onSuccess(User user) {
                FirebaseRealtimeDatabase.getCompletedTasksByGroupId(user.getGroupCode(), new FirebaseRealtimeDatabase.TasksCallback() {
                    @Override
                    public ArrayList<String[]> onTasksLoaded(List<Task> tasks) {
                        for (Task t : tasks) {
                            if (t.getUserId().equals(idUser)){
                                String[] task = {t.getDescription(), Integer.toString(t.getPoints()), extractDate(t.getDateTime()), Boolean.toString(t.isPredetermined()), t.getImageUrl()};
                                datos.add(task);
                            }
                        }
                        investArrayList(datos);
                        return datos;
                    }

                    @Override
                    public void onError(DatabaseError error) {
                        Toast.makeText(context, "Error al obtener las tareas", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(context, "Error al obtener el Usuario", Toast.LENGTH_SHORT).show();
            }
        });

        return datos;
    }





    /**
     * Obtiene únicamente el valor fecha de un DataTime
     * @param fechaCompleta
     * @return
     */
    private static String extractDate(String fechaCompleta) {
        if (fechaCompleta == null || !fechaCompleta.contains(" ")) {
            return fechaCompleta; // o puedes retornar "" si prefieres
        }
        return fechaCompleta.split(" ")[0];
    }

    /**
     * Invierte los valores de un ArrayList
     * @param lista
     * @param <T>
     */
    private static <T> void investArrayList(ArrayList<T> lista) {
        int n = lista.size();
        for (int i = 0; i < n / 2; i++) {
            T temp = lista.get(i);
            lista.set(i, lista.get(n - 1 - i));
            lista.set(n - 1 - i, temp);
        }
    }

    /**
     * Genera un Dialog mostrando la imagen que se le pase como url por parámetros.
     * @param imageUrl
     */
    private static void displayImageDialog(String imageUrl, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_dialog_imagen, null);

        ImageView imageView = view.findViewById(R.id.imageView);

        // Cargar imagen desde la URL usando Glide
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.fondo_x) // Opcional: imagen mientras carga
                //.error(R.drawable.error_image)       // Opcional: imagen si falla
                .into(imageView);

        builder.setView(view);
        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Ordena las tareas poniendo al principio las tareas con valor "predeterminado" = true.
     * @param tasks
     * @return Devuelve un ArrayList con las tareas ordenadas.
     */
    private static ArrayList<Task> sortTasksByValue(List<Task> tasks){
        ArrayList<Task> tasksReturn = new ArrayList<>();
        ArrayList<Task> nonDefaultTasks = new ArrayList<>();

        for (Task t: tasks){
            if(t.isPredetermined()) {
                tasksReturn.add(t);
            } else {
                nonDefaultTasks.add(t);
            }
        }

        for (Task t: nonDefaultTasks){
            tasksReturn.add(t);
        }

        return tasksReturn;
    }





    /**
     * Crea una tarea.
     */
    public static void createTask(String idUser, Context context) {
        // Crear el diálogo
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_dialog_create_task);
        dialog.setCancelable(true);

        // Referencias a los botones
        TextInputEditText nameTaskEditText = dialog.findViewById(R.id.nameTaskEditText);
        TextInputEditText pointsEditText = dialog.findViewById(R.id.pointsEditText);
        Switch predeterminedSwitch = dialog.findViewById(R.id.predeterminedSwitch);
        Button btnCreateTask = dialog.findViewById(R.id.createButton);
        Button btnCancel = dialog.findViewById(R.id.backButton);

        // Acción para crear la tarea
        btnCreateTask.setOnClickListener(v1 -> {
            // Se recogen todos los datos, se comprueba que todo este completo, y se crea una nueva tarea
            if (nameTaskEditText.getText().toString().trim() != null && !nameTaskEditText.getText().toString().trim().equals("") && pointsEditText.getText().toString() != null && !pointsEditText.getText().toString().equals("")) {
                generateUniqueIdAndCreateTask(nameTaskEditText.getText().toString(), Integer.parseInt(pointsEditText.getText().toString()), predeterminedSwitch.isChecked(), idUser, context);
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Se deben completar todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        // Acción para cancelar
        btnCancel.setOnClickListener(v1 -> dialog.dismiss());

        // Mostrar el diálogo
        dialog.show();
    }

    /**
     * Genera un id para la tarea y la crea.
     *
     * @param nameTask
     * @param points
     * @param predetermined
     */
    private static void generateUniqueIdAndCreateTask(String nameTask, int points, boolean predetermined, String idUser, Context context) {
        Random random = new Random();
        int id = random.nextInt(1000000); // Genera un número entre 0 y 999999

        FirebaseRealtimeDatabase.getUserById(idUser, context, new FirebaseRealtimeDatabase.UserCallback() {
            @Override
            public void onSuccess(User user) {
                FirebaseRealtimeDatabase.checkTaskExists(user.getGroupCode(), Integer.toString(id), context, new FirebaseRealtimeDatabase.OnTaskCheckListener() {
                    @Override
                    public void onTaskExists(boolean exists) {
                        if (exists) {
                            generateUniqueIdAndCreateTask(nameTask, points, predetermined, idUser, context);
                        } else {
                            Task task = new Task(id, nameTask, points, predetermined);
                            FirebaseRealtimeDatabase.getUserById(idUser, context, new FirebaseRealtimeDatabase.UserCallback() {
                                @Override
                                public void onSuccess(User user) {
                                    FirebaseRealtimeDatabase.addTaskToGroup(task, user.getGroupCode(), context);
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(context, "Error durante el proceso", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(context, "Error durante el proceso", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
