package com.example.GymBro.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.GymBro.R;
import com.example.GymBro.fragments.Course;
import com.example.GymBro.fragments.Exercise;
import com.example.GymBro.fragments.Workout;
import com.example.GymBro.fragments.Motivation;
import com.example.GymBro.fragments.Settings;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GymActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Set the Toolbar as the ActionBar
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new Workout())
                    .commit();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.workout_button) {
                selectedFragment = new Workout();
            } else if (item.getItemId() == R.id.course_button) {
                selectedFragment = new Course();
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
        } else if (itemId == R.id.account_button) {
            startActivity(new Intent(this, Settings.class)); // Replace with the correct activity if needed
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}