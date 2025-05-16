package com.example.homesync.Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.homesync.CloudinaryDataBase;
import com.example.homesync.Dialogs.DialogJoinGroup;
import com.example.homesync.FirebaseRealtimeDatabase;
import com.example.homesync.Index;
import com.example.homesync.MainActivity;
import com.example.homesync.Model.Group;
import com.example.homesync.Model.Task;
import com.example.homesync.Model.User;
import com.example.homesync.Prueba;
import com.example.homesync.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ProfileFragment extends Fragment {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();


    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ImageView btnInsertImage;
    private Bitmap imageBitmap;
    private ActivityResultLauncher<Intent> cameraLauncher;

    private List<Task> allTask = new ArrayList<>();
    private List<String> tasksList = new ArrayList<>();
    private Task task = new Task();
    private String urlFoto;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getExtras() != null) {
                            imageBitmap = (Bitmap) data.getExtras().get("data");

                            // Buscar la imagen del diálogo y mostrar la foto
                            if (btnInsertImage != null && imageBitmap != null) {
                                btnInsertImage.setImageBitmap(imageBitmap);
                            }
                        }
                    }
                });
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();
        String id = mAuth.getCurrentUser().getUid();

        ImageView image = view.findViewById(R.id.imageView);
        TextView titulo = view.findViewById(R.id.textView);
        TextView aviso = view.findViewById(R.id.createGroupTextView);
        Button createGroupButton = view.findViewById(R.id.createGroupButton);
        Button joinGroupButton = view.findViewById(R.id.joinGroupButton);
        FloatingActionButton fab = view.findViewById(R.id.fab);


        FirebaseRealtimeDatabase.getUserById(mAuth.getCurrentUser().getUid(), MainActivity.activityA, new FirebaseRealtimeDatabase.UserCallback() {
            @Override
            public void onSuccess(User user) {
                if(user.getGroupCode().trim().equals("")) {
                    createGroupButton.setVisibility(View.VISIBLE);
                    joinGroupButton.setVisibility(View.VISIBLE);
                    aviso.setVisibility(View.VISIBLE);
                } else {
                    titulo.setVisibility(View.VISIBLE);
                    image.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);

                    if (isAdded() && getActivity() != null) {
                        Glide.with(requireContext()).load(user.getImage()).into(image);
                    }
                    titulo.setText(user.getNickname());
                }
            }
            @Override
            public void onFailure(Exception e) {
                Log.e("Error", "Error al generar los elementos");
                Toast.makeText(getContext(), "Error al generar los elementos", Toast.LENGTH_SHORT).show();
            }
        });


        createGroupButton.setOnClickListener(v -> createGroup());

        joinGroupButton.setOnClickListener(v -> joinGroup());






        // Configuración del click listener del FAB
        fab.setOnClickListener(v -> completeTask());


        return view;
    }

    /*
     * Método para completar una tarea.
     * */
    private void completeTask(){
        // Crear el diálogo
        Dialog dialog = new Dialog(MainActivity.activityA);
        dialog.setContentView(R.layout.activity_dialog_complete_task);
        dialog.setCancelable(true);

        // Referencias a los botones
        Spinner spinnerTask = dialog.findViewById(R.id.spinnerTask);
        btnInsertImage = dialog.findViewById(R.id.imageTask);
        Button btnCompleteTask = dialog.findViewById(R.id.completeButton);
        Button btnCancel = dialog.findViewById(R.id.backButton);


        // Cargamos las tareas al spinner
        getTasks(dialog);


        // Acción para añadir la fotografia
        btnInsertImage.setOnClickListener(v1 -> {
            // Se abre la camara y se hace una foto.
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                cameraLauncher.launch(intent);
            }
            Toast.makeText(getContext(), "Abriendo cámara...", Toast.LENGTH_SHORT).show();
        });

        // Acción para crear la tarea
        btnCompleteTask.setOnClickListener(v1 -> {
            // Se recogen todos los datos, se comprueba que todo este completo, y se crea una nueva tarea
            if(spinnerTask.getSelectedItem().toString() != null && !spinnerTask.getSelectedItem().toString().equals("Selecciona una tarea")){

                for (Task t : allTask) {
                    if(t.getDescription().equals(spinnerTask.getSelectedItem().toString())) {
                        task = t;
                        task.setUserId(mAuth.getUid());
                        break;
                    }
                }

                Bitmap croppedBitmap = CloudinaryDataBase.cropToSquare(imageBitmap);
                new ProfileFragment.uploadCompleteTask().execute(CloudinaryDataBase.getImageUri(MainActivity.activityA, croppedBitmap));

                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Se deben completar todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        // Acción para cancelar
        btnCancel.setOnClickListener(v1 -> dialog.dismiss());

        // Mostrar el diálogo
        dialog.show();
    }

    /*
    * Método para crear una tarea.
    * */
    private void createTask(){
        // Crear el diálogo
        Dialog dialog = new Dialog(MainActivity.activityA);
        dialog.setContentView(R.layout.activity_dialog_create_task);
        dialog.setCancelable(true);

        // Referencias a los botones
        TextInputLayout nameTaskLayout = dialog.findViewById(R.id.nameTaskLayout);
        TextInputEditText nameTaskEditText = dialog.findViewById(R.id.nameTaskEditText);
        TextInputLayout pointsLayout = dialog.findViewById(R.id.pointsLayout);
        TextInputEditText pointsEditText = dialog.findViewById(R.id.pointsEditText);
        Switch predeterminedSwitch = dialog.findViewById(R.id.predeterminedSwitch);
        //btnInsertImage = dialog.findViewById(R.id.imageTask);
        Button btnCreateTask = dialog.findViewById(R.id.createButton);
        Button btnCancel = dialog.findViewById(R.id.backButton);

        // Acción para añadir la fotografia
        /*btnInsertImage.setOnClickListener(v1 -> {
            // Se abre la camara y se hace una foto.
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                cameraLauncher.launch(intent);
            }
            Toast.makeText(getContext(), "Abriendo cámara...", Toast.LENGTH_SHORT).show();
        });*/

        // Acción para crear la tarea
        btnCreateTask.setOnClickListener(v1 -> {
            // Se recogen todos los datos, se comprueba que todo este completo, y se crea una nueva tarea
            if(nameTaskEditText.getText().toString().trim() != null && !nameTaskEditText.getText().toString().trim().equals("") && pointsEditText.getText().toString() != null && !pointsEditText.getText().toString().equals("")){
                generateUniqueIdAndCreateTask(nameTaskEditText.getText().toString(), Integer.parseInt(pointsEditText.getText().toString()), predeterminedSwitch.isChecked());
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Se deben completar todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        // Acción para cancelar
        btnCancel.setOnClickListener(v1 -> dialog.dismiss());

        // Mostrar el diálogo
        dialog.show();
    }

    /*
    * Esta función genera un id para la tarea y la crea.
    * */
    private void generateUniqueIdAndCreateTask(String nameTask, int points, boolean predetermined){
        Random random = new Random();
        int id = random.nextInt(1000000); // Genera un número entre 0 y 999999

        FirebaseRealtimeDatabase.getUserById(mAuth.getUid(), MainActivity.activityA, new FirebaseRealtimeDatabase.UserCallback() {
            @Override
            public void onSuccess(User user) {
                FirebaseRealtimeDatabase.checkTaskExists(user.getGroupCode(), Integer.toString(id), MainActivity.activityA, new FirebaseRealtimeDatabase.OnTaskCheckListener() {
                    @Override
                    public void onTaskExists(boolean exists) {
                        if(exists) {
                            generateUniqueIdAndCreateTask(nameTask, points, predetermined);
                        } else {
                            Task task = new Task(id, nameTask, points, predetermined);
                            FirebaseRealtimeDatabase.getUserById(mAuth.getUid(), MainActivity.activityA, new FirebaseRealtimeDatabase.UserCallback(){
                                @Override
                                public void onSuccess(User user) {
                                    FirebaseRealtimeDatabase.addTaskToGroup(task, user.getGroupCode(), MainActivity.activityA);
                                }
                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(getContext(), "Error durante el proceso", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Error durante el proceso", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
    * Esta función muestra el dialog para crear un grupo.
    * */
    private void createGroup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.activityA, R.style.CustomAlertDialog);
        builder.setTitle("Confirmación");
        builder.setMessage("¿Quieres crear un grupo?");

        // Botón Confirmar
        builder.setPositiveButton("Crear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                generateUniqueCodeAndCreateGroup();
            }
        });

        // Botón Cancelar
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.activityA, "Acción cancelada", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        // Mostrar el diálogo
        builder.create().show();
    }

    /*
    * Esta función genera un id para el grupo y lo crea.
    * */
    private void generateUniqueCodeAndCreateGroup(){
        StringBuilder code = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }

        String finalCode = code.toString();

        FirebaseRealtimeDatabase.checkGroupExists(finalCode, MainActivity.activityA, new FirebaseRealtimeDatabase.OnGroupCheckListener() {
            @Override
            public void onGroupExists(boolean exists) {
                if (!exists) {
                    // El grupo no existe: se puede crear
                    FirebaseRealtimeDatabase.addUserToGroup(mAuth.getUid(), finalCode, true, MainActivity.activityA);
                    Toast.makeText(MainActivity.activityA, "Grupo creado", Toast.LENGTH_SHORT).show();
                    MainActivity.activityA.recreate();
                } else {
                    // El grupo ya existe: volver a intentar con un nuevo código
                    generateUniqueCodeAndCreateGroup(); // Llamada recursiva
                }
            }
        });
    }

    /*
     * Esta funcion se encarga de cargar todas las tareas en el spinner para poder seleccionarla al completar una de ellas.
     * */
    private void getTasks(Dialog dialog){
        Spinner spinnerTask = dialog.findViewById(R.id.spinnerTask);
        FirebaseRealtimeDatabase.getUserById(mAuth.getUid(), MainActivity.activityA, new FirebaseRealtimeDatabase.UserCallback() {
            @Override
            public void onSuccess(User user) {
                FirebaseRealtimeDatabase.getTasksByGroupId(user.getGroupCode(), new FirebaseRealtimeDatabase.TasksCallback() {
                    @Override
                    public void onTasksLoaded(List<Task> tasks) {
                        allTask = tasks;
                        // Se limpia el array y se le añade el valor predeterminado.
                        tasksList = new ArrayList<>();
                        tasksList.add("Selecciona una tarea");

                        for (Task task : tasks) {

                            tasksList.add(task.getDescription());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.activityA, R.layout.spinner_item, tasksList);
                        adapter.setDropDownViewResource(R.layout.spinner_item);
                        spinnerTask.setAdapter(adapter);
                    }
                    @Override
                    public void onError(DatabaseError error) {
                        Toast.makeText(MainActivity.activityA, "Error al obtener las tareas", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MainActivity.activityA, "Error al obtener el Usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
    * Esta función guarda la imagen y la añade su url a la tarea.
    * También almacena la tarea en la base de datos.
    * */
    private class uploadCompleteTask extends AsyncTask<Uri, Void, String> {
        @Override
        protected String doInBackground(Uri... uris) {
            return CloudinaryDataBase.SaveImage(uris[0], MainActivity.activityA);
        }
        @Override
        protected void onPostExecute(String url) {
            // Aquí puedes manejar la URL de la imagen subida
            urlFoto =  url;

            if (urlFoto.startsWith("http://")) {
                urlFoto = urlFoto.replace("http://", "https://");
            }

            task.setImageUrl(urlFoto);


            FirebaseRealtimeDatabase.getUserById(mAuth.getUid(), MainActivity.activityA, new FirebaseRealtimeDatabase.UserCallback() {
                @Override
                public void onSuccess(User user) {
                    FirebaseRealtimeDatabase.completeTask(task, user.getGroupCode(), MainActivity.activityA);
                }
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(MainActivity.activityA, "Error al obtener el Usuario", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /*
    * Esta función muestra el dialog para acceder a un grupo
    * */
    private void joinGroup(){
        DialogJoinGroup dialog = DialogJoinGroup.newInstance("Introduce el código", "Código del grupo", "Código incorrecto");
        dialog.show(getActivity().getSupportFragmentManager(), "DialogJoinGroup");
    }
}