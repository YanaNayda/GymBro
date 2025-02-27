package com.example.GymBro.activities;

import static android.app.PendingIntent.getActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.GymBro.R;
import com.example.GymBro.classes.ExerciseHandlerSingleton;
import com.example.GymBro.fragments.Exercise;
import com.example.GymBro.fragments.Workout;
import com.example.GymBro.fragments.Motivation;
import com.example.GymBro.fragments.Settings;
import com.example.GymBro.handlers.ExerciseHandler;
import com.example.GymBro.models.ExerciseModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class GymActivity extends AppCompatActivity {

    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;

    public ExerciseHandler getHandler() {
        return handler;
    }

    public void setHandler(ExerciseHandler handler) {
        this.handler = handler;
    }

    ExerciseHandler handler;
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym);

        // Initialize toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Get NavController from NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        // Setup bottom navigation with NavController
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.workout_button) {
                navController.navigate(R.id.workout);
                return true;
            } else if (item.getItemId() == R.id.exercise_button) {
                navController.navigate(R.id.exercise2);
                return true;
            } else if (item.getItemId() == R.id.motivation_button) {
                navController.navigate(R.id.motivation2);
                return true;
            }
            return false;
        });

        // Retrieve the ExerciseHandler from the Singleton
        ExerciseHandlerSingleton handlerSingleton = ExerciseHandlerSingleton.getInstance(this);
        handler = handlerSingleton.getHandler();

        // Load the weekly workout from Firebase
        if (handler != null) {
            handler.getWeeklyWorkout(new ExerciseHandler.WorkoutCallback() {
                @Override
                public void onWorkoutLoaded(ArrayList<ArrayList<ExerciseModel>> workout) {
                    Log.d("GymActivity", "Workout loaded successfully");
                }

                @Override
                public void onError(Exception e) {
                    Log.e("GymActivity", "Error loading workout: " + e.getMessage());
                }
            }, navController);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu resource
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.action_settings) {
            // Navigate to settings
            navController.navigate(R.id.action_to_settings);
            return true;
        } 
        else if (itemId == R.id.action_logout) {
            // Clear cached data
            if (handler != null) {
                handler.clearCache();
            }
            
            // Handle logout
            FirebaseAuth.getInstance().signOut();
            
            // Navigate to login activity
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}