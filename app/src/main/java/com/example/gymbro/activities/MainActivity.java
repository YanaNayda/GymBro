package com.example.gymbro.activities;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.Navigation;

import com.example.gymbro.R;
import com.example.gymbro.handlers.ExerciseHandler;
import com.example.gymbro.models.Exercise;
import com.example.gymbro.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    FirebaseDatabase database ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ExerciseHandler s = new ExerciseHandler();
        
        s.fetchAllExercises(new ExerciseHandler.ExerciseDataCallback() {
            @Override
            public void onExercisesLoaded(ArrayList<Exercise> exercises) {
                Log.d("MainActivity", "Loaded " + exercises.size() + " exercises successfully!");
                Snackbar.make(findViewById(android.R.id.content), 
                            "Loaded " + exercises.size() + " exercises successfully!", 
                            Snackbar.LENGTH_LONG).show();
                
                ArrayList<String> equipment = new ArrayList<>(Arrays.asList(
                        "e-z curl bar", "foam roll", "kettlebells", "machine",
                        "dumbbell", "exercise ball", "medicine ball", "barbell",
                        "bands", "cable", "body only"
                ));
                
                ArrayList<String> levels = new ArrayList<>(Arrays.asList(
                        "beginner", "intermediate", "expert"  // You can include any combination of levels
                ));
                
                s.generateWeeklyWorkout(exercises, 7, equipment, levels);
            }

            @Override
            public void onError(Exception e) {
                Log.e("MainActivity", "Error loading exercises", e);
                Snackbar.make(findViewById(android.R.id.content),
                            "Error loading exercises: " + e.getMessage(),
                            Snackbar.LENGTH_LONG)
                        .setBackgroundTint(getResources().getColor(android.R.color.holo_red_light, getTheme()))
                        .show();
            }
        });
    }

    public void logInUser(View view, String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                           // Navigation.findNavController(view).navigate(R.id.action_logIn_to_start2);

                        } else {
                            Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    public void registerUser(View view, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser newUser = task.getResult().getUser();
                            if (newUser != null) {
                                String userId = newUser.getUid();
                                addData(userId); // Передаём userId в addData()
                            }

                            Toast.makeText(MainActivity.this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(view).navigate(R.id.action_registration_to_settings);
                        } else {
                            Toast.makeText(MainActivity.this, "Ошибка регистрации: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("FirebaseAuth", "Ошибка регистрации", task.getException());
                        }
                    }
                });
    }

    public void sendPasswordResetEmail(View view, String email){
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "Password reset email not sent", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    public void addData(String userId) {
        EditText emailField = findViewById(R.id.edit_txt_registration_email);
        EditText phoneField = findViewById(R.id.edit_txt_registration_phone);
        EditText passwordField = findViewById(R.id.edit_txt_registration_password);

        if (database == null) {
            Log.e("FirebaseError", "Ошибка: database == null");
            return;
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        myRef = database.getReference("Users").child("" + userId);

        myRef.setValue("Hello, World!");

        User newUser = new User(
                emailField.getText().toString(),
                passwordField.getText().toString(),
                phoneField.getText().toString()
        );

        myRef.setValue(newUser).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FirebaseDatabase", "Данные успешно записаны!");
            } else {
                Log.e("FirebaseDatabase", "Ошибка записи данных", task.getException());
            }
        });
    }

    public void getData(String UserCurrentId , View view){
        DatabaseReference myRef = database.getReference("Users").child(UserCurrentId);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User value = snapshot.getValue(User.class);


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("effireg", "Failed to read value.", error.toException());
            }
        });

    }


}