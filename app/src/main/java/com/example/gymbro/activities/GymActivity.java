package com.example.gymbro.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.gymbro.R;
import com.example.gymbro.fragments.Home;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GymActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gym);

        //ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
        //    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        //    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        //    return insets;
//
      //  });
        //findViewById(R.id.bottom_navigation).setOnItemSelectedListener(navListener);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);



    }
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        // By using switch we can easily get
        // the selected fragment
        // by using there id.
        Fragment selectedFragment = null;
        int itemId = item.getItemId();
        if (itemId == R.id.motivation) {
            selectedFragment = new Home();
        } else if (itemId == R.id.course) {
            selectedFragment = new Home();
        } else if (itemId == R.id.home) {
            selectedFragment = new Home();
        } else if (itemId == R.id.exercise) {
            selectedFragment = new Home();
        }

        if (selectedFragment != null) {
            //getSupportFragmentManager().beginTransaction().replace(R.id.ho, selectedFragment).commit();
        }
        return true;
    };


}