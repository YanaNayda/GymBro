package com.example.gymbro.activities;

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

import com.example.gymbro.R;
import com.example.gymbro.handlers.ExerciseHandler;
import com.example.gymbro.models.Exercise;
import com.example.gymbro.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

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
        ExerciseHandler s = new ExerciseHandler();
        
        s.fetchAllExercises(new ExerciseHandler.ExerciseDataCallback() {
            @Override
            public void onExercisesLoaded(ArrayList<Exercise> exercises) {
                Log.d("MainActivity", "Loaded " + exercises.size() + " exercises successfully!");
                Snackbar.make(findViewById(android.R.id.content), 
                            "Loaded " + exercises.size() + " exercises successfully!", 
                            Snackbar.LENGTH_LONG).show();
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

    public void registerUser (View view,String email, String password ){
        mAuth.createUserWithEmailAndPassword(email,password )
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            addData();
                            //Navigation.findNavController(view).navigate(R.id.action_registration_to_start2);

                        } else {
                            Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_SHORT).show();

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

    public void addData(){

        EditText email = findViewById(R.id.edit_txt_registration_email);
        EditText phone = findViewById(R.id.edit_txt_registration_phone);
        EditText password = findViewById(R.id.edit_txt_registration_password);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String UserCurrentId = currentUser.getUid();

        DatabaseReference myRef = database.getReference("Users").child(UserCurrentId);

        User s = new User(email.getText().toString(), password.getText().toString(),phone.getText().toString());
        myRef.setValue(s);

    }


}