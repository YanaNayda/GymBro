package com.example.GymBro.handlers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.GymBro.models.ExerciseModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Arrays;

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

    private void fetchNextBatch(ArrayList<ExerciseModel> exerciseList, ExerciseDataCallback callback) {
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

                    ExerciseModel exercise = new ExerciseModel();
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

    public ArrayList<Bitmap> getBitmapsFromExercise(ExerciseModel exercise) {
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

    public ArrayList<ArrayList<ExerciseModel>> generateWeeklyWorkout(ArrayList<ExerciseModel> allExercises,
                                                                     int daysPerWeek,
                                                                     ArrayList<String> availableEquipment,
                                                                     ArrayList<String> levels) {
        // Filter exercises by levels and equipment
        ArrayList<ExerciseModel> validExercises = filterExercises(allExercises, availableEquipment, levels);
        
        // Separate exercises by type
        ArrayList<ExerciseModel> strengthExercises = new ArrayList<>();
        ArrayList<ExerciseModel> stretchingExercises = new ArrayList<>();
        ArrayList<ExerciseModel> cardioExercises = new ArrayList<>();
        
        for (ExerciseModel exercise : validExercises) {
            switch (exercise.getCategory()) {
                case "strength":
                case "plyometrics":
                    strengthExercises.add(exercise);
                    break;
                case "stretching":
                    stretchingExercises.add(exercise);
                    break;
                case "cardio":
                    cardioExercises.add(exercise);
                    break;
            }
        }

        // Create muscle coverage tracking
        HashSet<String> musclesCovered = new HashSet<>();
        ArrayList<ArrayList<ExerciseModel>> weeklyWorkout = new ArrayList<>();

        // Determine exercises per workout based on days per week
        int exercisesPerWorkout = (daysPerWeek == 1) ? 8 : // Full body
                                 (daysPerWeek == 2) ? 6 :   // Upper/Lower split
                                 (daysPerWeek == 3) ? 5 :   // Push/Pull/Legs
                                 4;                         // Body part split

        // Generate workouts for each day
        for (int day = 0; day < daysPerWeek; day++) {
            ArrayList<ExerciseModel> workout = new ArrayList<>();
            
            // Add a stretching exercise at the start
            if (!stretchingExercises.isEmpty()) {
                workout.add(stretchingExercises.get(new Random().nextInt(stretchingExercises.size())));
            }

            // Add strength/plyometric exercises
            ArrayList<ExerciseModel> dayExercises = selectExercisesForDay(
                strengthExercises, 
                musclesCovered, 
                exercisesPerWorkout, 
                daysPerWeek, 
                day
            );
            workout.addAll(dayExercises);

            // Add a cardio exercise at the end
            if (!cardioExercises.isEmpty()) {
                workout.add(cardioExercises.get(new Random().nextInt(cardioExercises.size())));
            }

            weeklyWorkout.add(workout);
        }

        return weeklyWorkout;
    }

    private ArrayList<ExerciseModel> filterExercises(ArrayList<ExerciseModel> exercises,
                                                     ArrayList<String> availableEquipment,
                                                     ArrayList<String> levels) {
        ArrayList<ExerciseModel> filtered = new ArrayList<>();
        for (ExerciseModel exercise : exercises) {
            if (levels.contains(exercise.getLevel()) && 
                (availableEquipment.contains(exercise.getEquipment()) || 
                 exercise.getEquipment().equals("body only"))) {
                filtered.add(exercise);
            }
        }
        return filtered;
    }

    private ArrayList<ExerciseModel> selectExercisesForDay(ArrayList<ExerciseModel> exercises,
                                                           HashSet<String> musclesCovered,
                                                           int exercisesNeeded,
                                                           int totalDays,
                                                           int currentDay) {
        ArrayList<ExerciseModel> selected = new ArrayList<>();
        ArrayList<ExerciseModel> availableExercises = new ArrayList<>(exercises);
        Collections.shuffle(availableExercises);

        if (totalDays == 1) {
            // Full body workout - select exercises that target different muscles
            while (selected.size() < exercisesNeeded && !availableExercises.isEmpty()) {
                ExerciseModel bestExercise = findBestExercise(availableExercises, musclesCovered);
                if (bestExercise != null) {
                    selected.add(bestExercise);
                    availableExercises.remove(bestExercise);
                    musclesCovered.addAll(bestExercise.getPrimaryMuscles());
                    musclesCovered.addAll(bestExercise.getSecondaryMuscles());
                }
            }
        } else if (totalDays == 2) {
            // Upper/Lower split
            boolean isUpperDay = currentDay == 0;
            for (ExerciseModel exercise : availableExercises) {
                boolean isUpperExercise = isUpperBodyExercise(exercise);
                if ((isUpperDay && isUpperExercise) || (!isUpperDay && !isUpperExercise)) {
                    if (selected.size() < exercisesNeeded) {
                        selected.add(exercise);
                        musclesCovered.addAll(exercise.getPrimaryMuscles());
                        musclesCovered.addAll(exercise.getSecondaryMuscles());
                    }
                }
            }
        } else {
            // For 3+ days, focus on complementary muscle groups each day
            while (selected.size() < exercisesNeeded && !availableExercises.isEmpty()) {
                ExerciseModel exercise = findBestExercise(availableExercises, musclesCovered);
                if (exercise != null) {
                    selected.add(exercise);
                    availableExercises.remove(exercise);
                    musclesCovered.addAll(exercise.getPrimaryMuscles());
                    musclesCovered.addAll(exercise.getSecondaryMuscles());
                }
            }
        }

        return selected;
    }

    private ExerciseModel findBestExercise(ArrayList<ExerciseModel> exercises, HashSet<String> musclesCovered) {
        ExerciseModel bestExercise = null;
        int maxNewMuscles = -1;

        for (ExerciseModel exercise : exercises) {
            HashSet<String> newMuscles = new HashSet<>();
            newMuscles.addAll(exercise.getPrimaryMuscles());
            newMuscles.addAll(exercise.getSecondaryMuscles());
            newMuscles.removeAll(musclesCovered);

            if (newMuscles.size() > maxNewMuscles) {
                maxNewMuscles = newMuscles.size();
                bestExercise = exercise;
            }
        }

        return bestExercise;
    }

    private boolean isUpperBodyExercise(ExerciseModel exercise) {
        HashSet<String> upperBodyMuscles = new HashSet<>(Arrays.asList(
            "shoulders", "triceps", "chest", "forearms", "lats",
            "middle back", "neck", "traps", "biceps"
        ));

        for (String muscle : exercise.getPrimaryMuscles()) {
            if (upperBodyMuscles.contains(muscle)) {
                return true;
            }
        }
        return false;
    }

    public interface ExerciseDataCallback {
        void onExercisesLoaded(ArrayList<ExerciseModel> exercises);
        void onError(Exception e);
    }
}
