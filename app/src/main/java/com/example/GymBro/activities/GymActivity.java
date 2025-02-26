package com.example.GymBro.activities;

import static android.app.PendingIntent.getActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.GymBro.R;
import com.example.GymBro.classes.ExerciseHandlerSingleton;
import com.example.GymBro.fragments.Exercise;
import com.example.GymBro.fragments.Workout;
import com.example.GymBro.fragments.Motivation;
import com.example.GymBro.fragments.Settings;
import com.example.GymBro.handlers.ExerciseHandler;
import com.example.GymBro.models.ExerciseModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class GymActivity extends AppCompatActivity {


    ExerciseHandler handler;
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Set the Toolbar as the ActionBar
        setSupportActionBar(toolbar);

        // Retrieve the ExerciseHandler from the Singleton
        ExerciseHandlerSingleton handlerSingleton = ExerciseHandlerSingleton.getInstance(this);
        ExerciseHandler handler = handlerSingleton.getHandler();


        // Now you can use the handler
        if (handler != null) {
            Log.d("GymActivity", "ExerciseHandler retrieved successfully");
            ArrayList <ExerciseModel> exercises = handler.getExercisesList();

            if (exercises != null) {
                Log.d("GymActivity", "Exercises loaded: " + exercises.size());

            } else {
                Log.e("GymActivity", "Exercises list is null");
            }
        } else {
            Log.e("GymActivity", "ExerciseHandler is null");
        }



        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;



            if (item.getItemId() == R.id.workout_button) {
                selectedFragment = new Workout();
            } else if (item.getItemId() == R.id.exercise_button) {
                selectedFragment = new Exercise();
            } else if (item.getItemId() == R.id.motivation_button) {
                selectedFragment = new Motivation();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, selectedFragment)
                        .commit();
            }

            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.settings_button) {
            startActivity(new Intent(this, Settings.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}