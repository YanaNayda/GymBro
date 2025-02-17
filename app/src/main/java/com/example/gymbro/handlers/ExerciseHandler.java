package com.example.gymbro.handlers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.gymbro.models.Exercise;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ExerciseHandler {
    private final DatabaseReference dbRef;
    private static final int LIMIT_PER_QUERY = 100;  // Load just 3 exercises at a time
    private String lastKey = null;

    public ExerciseHandler() {
        this.dbRef = FirebaseDatabase.getInstance("https://gymbro-c8ca6-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("exercises");
    }

    public void fetchAllExercises(ExerciseDataCallback callback) {
        fetchNextBatch(new ArrayList<>(), callback);
    }

    private void fetchNextBatch(ArrayList<Exercise> exerciseList, ExerciseDataCallback callback) {
        Query query;
        if (lastKey == null) {
            query = dbRef.orderByKey().limitToFirst(LIMIT_PER_QUERY);
        } else {
            query = dbRef.orderByKey().startAfter(lastKey).limitToFirst(LIMIT_PER_QUERY);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    count++;
                    lastKey = snapshot.getKey();

                    Exercise exercise = new Exercise();
                    exercise.setName(snapshot.child("name").getValue(String.class));
                    exercise.setLevel(snapshot.child("level").getValue(String.class));
                    exercise.setEquipment(snapshot.child("equipment").getValue(String.class));
                    exercise.setCategory(snapshot.child("category").getValue(String.class));
                    exercise.setInstructions(snapshot.child("instructions").getValue(String.class));
                    exercise.setImg0(snapshot.child("img0").getValue(String.class));
                    exercise.setImg1(snapshot.child("img1").getValue(String.class));

                    ArrayList<String> primaryMuscles = new ArrayList<>();
                    for (DataSnapshot muscleSnapshot : snapshot.child("primaryMuscles").getChildren()) {
                        primaryMuscles.add(muscleSnapshot.getValue(String.class));
                    }
                    exercise.setPrimaryMuscles(primaryMuscles);

                    ArrayList<String> secondaryMuscles = new ArrayList<>();
                    for (DataSnapshot muscleSnapshot : snapshot.child("secondaryMuscles").getChildren()) {
                        secondaryMuscles.add(muscleSnapshot.getValue(String.class));
                    }
                    exercise.setSecondaryMuscles(secondaryMuscles);
                    
                    if (exercise.getName() != null) {
                        exerciseList.add(exercise);
                    }
                }

                if (count == LIMIT_PER_QUERY) {
                    // There might be more data, fetch next batch
                    Log.d("ExerciseHandler", "Fetched " + exerciseList.size() + " exercises so far, getting next batch...");
                    fetchNextBatch(exerciseList, callback);
                } else {
                    // No more data, return the complete list
                    Log.d("ExerciseHandler", "Finished fetching all " + exerciseList.size() + " exercises");
                    callback.onExercisesLoaded(exerciseList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching exercises", databaseError.toException());
                callback.onError(databaseError.toException());
            }
        });
    }

    public ArrayList<Bitmap> getBitmapsFromExercise(Exercise exercise) {
        ArrayList<Bitmap> lst = new ArrayList<>();
        if (exercise.getImg0() != null && !exercise.getImg0().isEmpty()) {
            byte[] decodedBytes = Base64.decode(exercise.getImg0(), Base64.DEFAULT);
            lst.add(BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length));
        }
        if (exercise.getImg1() != null && !exercise.getImg1().isEmpty()) {
            byte[] decodedBytes = Base64.decode(exercise.getImg1(), Base64.DEFAULT);
            lst.add(BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length));
        }
        return lst;
    }

    public interface ExerciseDataCallback {
        void onExercisesLoaded(ArrayList<Exercise> exercises);
        void onError(Exception e);
    }
}
