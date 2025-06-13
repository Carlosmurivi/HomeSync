package com.example.homesync.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.homesync.FirebaseRealtimeDatabase;
import com.example.homesync.MainActivity;
import com.example.homesync.Model.Group;
import com.example.homesync.Model.Task;
import com.example.homesync.Model.User;
import com.example.homesync.R;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private View view;
    private FirebaseAuth mAuth;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_profile_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();

        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        View separatingLine = view.findViewById(R.id.separatingLine);
        ImageView image = view.findViewById(R.id.imageView);
        TextView nickName = view.findViewById(R.id.textViewNicknameUser);
        TextView points = view.findViewById(R.id.textViewPoints);
        ImageView imageGoldMedal = view.findViewById(R.id.imageGoldMedal);
        TextView goldMedal = view.findViewById(R.id.goldMedal);
        ImageView imageSilverMedal = view.findViewById(R.id.imageSilverMedal);
        TextView silverMedal = view.findViewById(R.id.silverMedal);
        ImageView imageBronzeMedal = view.findViewById(R.id.imageBronzeMedal);
        TextView bronzeMedal = view.findViewById(R.id.bronzeMedal);
        TextView textViewCompletedTasksUserTable = view.findViewById(R.id.textViewCompletedTasksUserTable);
        TableLayout tableLayout = view.findViewById(R.id.tableLayoutCompletedTaskUser);
        TextView aviso = view.findViewById(R.id.createGroupTextView);
        Button createGroupButton = view.findViewById(R.id.createGroupButton);
        Button joinGroupButton = view.findViewById(R.id.joinGroupButton);


        FirebaseRealtimeDatabase.getUserById(mAuth.getUid(), MainActivity.activityA, new FirebaseRealtimeDatabase.UserCallback() {
            @Override
            public void onSuccess(User user) {
                if (user.getGroupCode().trim().equals("")) {
                    createGroupButton.setVisibility(View.VISIBLE);
                    joinGroupButton.setVisibility(View.VISIBLE);
                    aviso.setVisibility(View.VISIBLE);

                    createGroupButton.setOnClickListener(v -> Group.createGroup(user.getId(), MainActivity.activityA));
                    joinGroupButton.setOnClickListener(v -> Group.joinGroup(MainActivity.activityA));
                } else {
                    textViewTitle.setVisibility(View.VISIBLE);
                    separatingLine.setVisibility(View.VISIBLE);
                    image.setVisibility(View.VISIBLE);
                    nickName.setVisibility(View.VISIBLE);
                    points.setVisibility(View.VISIBLE);
                    imageGoldMedal.setVisibility(View.VISIBLE);
                    goldMedal.setVisibility(View.VISIBLE);
                    imageSilverMedal.setVisibility(View.VISIBLE);
                    silverMedal.setVisibility(View.VISIBLE);
                    imageBronzeMedal.setVisibility(View.VISIBLE);
                    bronzeMedal.setVisibility(View.VISIBLE);
                    textViewCompletedTasksUserTable.setVisibility(View.VISIBLE);
                    tableLayout.setVisibility(View.VISIBLE);

                    if (isAdded() && getActivity() != null) {
                        Glide.with(requireContext()).load(user.getImage()).into(image);
                    }

                    nickName.setText(user.getNickname());
                    points.setText(Integer.toString(user.getPoints()));
                    goldMedal.setText(Integer.toString(user.getGoldMedals()));
                    silverMedal.setText(Integer.toString(user.getSilverMedals()));
                    bronzeMedal.setText(Integer.toString(user.getBronzeMedals()));

                    Task.generateCompletedTasksUserTable(tableLayout, user.getId(), MainActivity.activityA);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Error", "Error al generar los elementos");
                Toast.makeText(getContext(), "Error al generar los elementos", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }






    // Que un adminsitrador pueda eliminar las tareas de la tabla en el apartado grupo

    // Listar el registro de medallas en el grupo

    // Obligar a sacar una foto al completar la tarea

    // Modificar el select de cuando se completa una tarea para que salgan en azul las predeterminadas y se muestren los puntos




    // Crear la lista de la compra

    // Incorporar unas tareas predeterminadas al grupo (Pasar aspiradora, Limpiar los baños, Recoger la habitación, Tender la ropa, Recoger lavavajillas, Hacer la compra)
}