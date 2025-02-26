package com.example.GymBro.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.GymBro.R;
import com.example.GymBro.classes.ExerciseHandlerSingleton;
import com.example.GymBro.handlers.ExerciseHandler;
import com.example.GymBro.models.ExerciseModel;
import com.example.GymBro.models.UserModel;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    FirebaseDatabase database ;
    ExerciseHandler handler;

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

        // Initialize the Singleton and get the ExerciseHandler
        ExerciseHandlerSingleton handlerSingleton = ExerciseHandlerSingleton.getInstance(this);
        handler = handlerSingleton.getHandler();

        handler.fetchAllExercises(new ExerciseHandler.ExerciseLoadCallback() {
            @Override
            public void onExercisesLoaded(ArrayList<ExerciseModel> exercises) {
                Log.d("MainActivity", "Loaded " + exercises.size() + " exercises successfully!");
                Snackbar.make(findViewById(android.R.id.content),
                        "Loaded " + exercises.size() + " exercises successfully!",
                        Snackbar.LENGTH_LONG).show();
                handler.setExercisesList(exercises);
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

    public ExerciseHandler getHandler() {
        return handler;
    }

    public void logInUser(View view, String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();

                            // Get the NavController from the view
                            NavController navController = Navigation.findNavController(view);

                            // Call getWeeklyWorkout with both callback and NavController
                            handler.getWeeklyWorkout(new ExerciseHandler.WorkoutCallback() {
                                @Override
                                public void onWorkoutLoaded(ArrayList<ArrayList<ExerciseModel>> weeklyWorkout) {
                                    // Handle the loaded workout
                                    if (weeklyWorkout == null || weeklyWorkout.isEmpty()) {
                                        Log.d("Workout", "No workout found or generated");
                                    } else {
                                        Log.d("Workout", "Workout loaded successfully");
                                        for (int i = 0; i < weeklyWorkout.size(); i++) {
                                            Log.d("Workout", "Day " + (i + 1) + ": " + weeklyWorkout.get(i));
                                        }
                                    }
                                    handler.setWeeklyWorkout(weeklyWorkout);
                                    // Set the workout in the Singleton (if needed)
                                    //WorkoutDataHolder.getInstance().setWorkout(weeklyWorkout);

                                    // Navigate to GymActivity
                                    Intent intent = new Intent(MainActivity.this, GymActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void onError(Exception e) {
                                    // Handle errors
                                    Log.e("Workout", "Error loading workout: " + e.getMessage());

                                    // If there's an error (e.g., settings not found), do not navigate to GymActivity
                                    // The getWeeklyWorkout method will handle navigating to SettingsFragment
                                }
                            }, navController); // Pass the NavController here
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
                            Toast.makeText(MainActivity.this, "Registered Successfuly", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(view).navigate(R.id.action_registration_to_settings);
                        } else {
                            Toast.makeText(MainActivity.this, "Error Registering: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("FirebaseAuth", "Error Registering!", task.getException());
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
        DatabaseReference myRef = database.getReference("Users").child("" + userId);

        UserModel newUser = new UserModel(
                emailField.getText().toString(),
                passwordField.getText().toString(),
                phoneField.getText().toString()
        );

        myRef.setValue(newUser).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FirebaseDatabase", "Data successfully written!");
            } else {
                Log.e("FirebaseDatabase", "Data writing error", task.getException());
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
                UserModel value = snapshot.getValue(UserModel.class);


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("effireg", "Failed to read value.", error.toException());
            }
        });

    }
    public String getCurrentWeekKey() {
        // Get the current date using Calendar
        Calendar calendar = Calendar.getInstance();
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int weekOfMonth = (dayOfMonth - 1) / 7 + 1;

        // Format the month name (e.g., "Jan", "Feb")
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.US); // Explicit locale
        String monthName = monthFormat.format(calendar.getTime());

        // Get the year
        int year = calendar.get(Calendar.YEAR);

        // Return the formatted string with an explicit locale
        return String.format(Locale.US, "week%d_%s_%d", weekOfMonth, monthName, year);
    }
}