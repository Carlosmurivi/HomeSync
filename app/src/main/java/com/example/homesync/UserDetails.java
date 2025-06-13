package com.example.homesync;

import android.graphics.Typeface;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.homesync.Fragments.SettingsFragment;
import com.example.homesync.Model.Task;
import com.example.homesync.Model.User;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.homesync.databinding.ActivityUserDetailsBinding;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class UserDetails extends AppCompatActivity {

    private String idUser;
    private Boolean administrator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        idUser = getIntent().getStringExtra("idUser");
        administrator = getIntent().getBooleanExtra("administrator", false);


        ImageView imageViewImageProfile = findViewById(R.id.imageViewImageProfile);
        TextView textViewNicknameUser = findViewById(R.id.textViewNicknameUser);
        TextView textViewPoints = findViewById(R.id.textViewPoints);
        ImageView imageGoldMedal = findViewById(R.id.imageGoldMedal);
        TextView goldMedal = findViewById(R.id.goldMedal);
        ImageView imageSilverMedal = findViewById(R.id.imageSilverMedal);
        TextView silverMedal = findViewById(R.id.silverMedal);
        ImageView imageBronzeMedal = findViewById(R.id.imageBronzeMedal);
        TextView bronzeMedal = findViewById(R.id.bronzeMedal);
        TextView textViewCompletedTasksUserTable = findViewById(R.id.textViewCompletedTasksUserTable);
        TableLayout tableLayoutCompletedTaskUser = findViewById(R.id.tableLayoutCompletedTaskUser);
        Button buttonMakeUserAdministrator = findViewById(R.id.buttonMakeUserAdministrator);
        Button buttonRemoveUserFromGroup = findViewById(R.id.buttonRemoveUserFromGroup);


        FirebaseRealtimeDatabase.getUserById(idUser, MainActivity.activityA, new FirebaseRealtimeDatabase.UserCallback() {
            @Override
            public void onSuccess(User user) {
                // Se visualizan los elementos
                imageViewImageProfile.setVisibility(View.VISIBLE);
                textViewNicknameUser.setVisibility(View.VISIBLE);
                textViewPoints.setVisibility(View.VISIBLE);
                imageGoldMedal.setVisibility(View.VISIBLE);
                goldMedal.setVisibility(View.VISIBLE);
                imageSilverMedal.setVisibility(View.VISIBLE);
                silverMedal.setVisibility(View.VISIBLE);
                imageBronzeMedal.setVisibility(View.VISIBLE);
                bronzeMedal.setVisibility(View.VISIBLE);
                textViewCompletedTasksUserTable.setVisibility(View.VISIBLE);
                tableLayoutCompletedTaskUser.setVisibility(View.VISIBLE);

                if(administrator) {
                    buttonMakeUserAdministrator.setVisibility(View.VISIBLE);
                    if(user.isAdministrator()){
                        buttonMakeUserAdministrator.setText("Quitar de administrador");
                        buttonMakeUserAdministrator.setOnClickListener(v -> User.makeUserNotAdministrator(idUser, UserDetails.this));
                    } else {
                        buttonMakeUserAdministrator.setOnClickListener(v -> User.makeUserAdministrator(idUser, UserDetails.this));
                    }
                    buttonRemoveUserFromGroup.setVisibility(View.VISIBLE);

                    buttonRemoveUserFromGroup.setOnClickListener(v -> User.removeUserFromGroup(idUser, user.getGroupCode(), UserDetails.this));
                }

                Glide.with(UserDetails.this)
                        .load(user.getImage())
                        .skipMemoryCache(true) // Evita la caché en RAM
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // Evita la caché en disco
                        .into(imageViewImageProfile);
                Glide.with(UserDetails.this).load(user.getImage()).into(imageViewImageProfile);

                textViewNicknameUser.setText(user.getNickname());
                textViewPoints.setText(Integer.toString(user.getPoints()));
                goldMedal.setText(Integer.toString(user.getGoldMedals()));
                silverMedal.setText(Integer.toString(user.getSilverMedals()));
                bronzeMedal.setText(Integer.toString(user.getBronzeMedals()));

                Task.generateCompletedTasksUserTable(tableLayoutCompletedTaskUser, idUser, UserDetails.this);
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MainActivity.activityA, "Error al obtener el Usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }


}