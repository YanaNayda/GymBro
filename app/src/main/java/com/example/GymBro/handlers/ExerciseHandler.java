package com.example.GymBro.handlers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.util.JsonReader;
import android.util.JsonToken;

import com.example.GymBro.R;
import com.example.GymBro.models.ExerciseModel;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ExerciseHandler {
    private Context context;
    private final DatabaseReference dbRef;
    private final ExecutorService executor;
    private final Handler mainHandler;
    private boolean isLoaded;
    private ArrayList<ExerciseModel> exercisesList;

    public interface ExerciseLoadCallback {
        void onExercisesLoaded(ArrayList<ExerciseModel> exercises);
        void onError(Exception e);
    }

    public interface WorkoutCallback {
        void onWorkoutLoaded(ArrayList<ArrayList<ExerciseModel>> workout);
        void onError(Exception e);
    }

    public ExerciseHandler(Context context) {
        this.context = context;
        this.isLoaded = false;
        this.dbRef = FirebaseDatabase.getInstance("https://gymbro-4fb99-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("Users");
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public ArrayList<ExerciseModel> getExercisesList() {
        return exercisesList;
    }

    public void setExercisesList(ArrayList<ExerciseModel> exercisesList) {
        this.exercisesList = exercisesList;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    public void fetchAllExercises(ExerciseLoadCallback callback) {
        executor.execute(() -> {
            try {
                ArrayList<ExerciseModel> exerciseList = new ArrayList<>();
                InputStream is = context.getResources().openRawResource(R.raw.exercises);
                JsonReader reader = new JsonReader(new InputStreamReader(is, "UTF-8"));

                reader.beginObject(); // Start of JSON
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equals("exercises")) {
                        reader.beginObject(); // Start of exercises object

                        while (reader.hasNext()) {
                            reader.nextName(); // Exercise name/key
                            ExerciseModel exercise = readExercise(reader);
                            if (exercise != null) {
                                exerciseList.add(exercise);
                            }
                        }

                        reader.endObject(); // End of exercises object
                    } else {
                        reader.skipValue();
                    }
                }
                this.isLoaded = true;
                reader.endObject(); // End of JSON
                reader.close();

                // Return result on main thread
                mainHandler.post(() -> callback.onExercisesLoaded(exerciseList));

            } catch (IOException e) {
                Log.e("ExerciseHandler", "Error reading JSON file", e);
                mainHandler.post(() -> callback.onError(e));
            }
        });
    }

    private ExerciseModel readExercise(JsonReader reader) throws IOException {
        ExerciseModel exercise = new ExerciseModel();
        ArrayList<String> primaryMuscles = new ArrayList<>();
        ArrayList<String> secondaryMuscles = new ArrayList<>();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (reader.peek() == JsonToken.NULL) {
                reader.skipValue();
                continue;
            }

            switch (name) {
                case "name":
                    exercise.setName(reader.nextString());
                    break;
                case "level":
                    exercise.setLevel(reader.nextString());
                    break;
                case "equipment":
                    exercise.setEquipment(reader.nextString());
                    break;
                case "category":
                    exercise.setCategory(reader.nextString());
                    break;
                case "instructions":
                    exercise.setInstructions(reader.nextString());
                    break;
                case "primaryMuscles":
                    reader.beginArray();
                    while (reader.hasNext()) {
                        primaryMuscles.add(reader.nextString());
                    }
                    reader.endArray();
                    exercise.setPrimaryMuscles(primaryMuscles);
                    break;
                case "secondaryMuscles":
                    reader.beginArray();
                    while (reader.hasNext()) {
                        secondaryMuscles.add(reader.nextString());
                    }
                    reader.endArray();
                    exercise.setSecondaryMuscles(secondaryMuscles);
                    break;
                case "img0":
                    exercise.setImg0(reader.nextString());
                    break;
                case "img1":
                    exercise.setImg1(reader.nextString());
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();

        // Ensure primaryMuscles is never null
        if (exercise.getPrimaryMuscles() == null) {
            exercise.setPrimaryMuscles(new ArrayList<>());
        }

        // Ensure secondaryMuscles is never null
        if (exercise.getSecondaryMuscles() == null) {
            exercise.setSecondaryMuscles(new ArrayList<>());
        }

        return exercise;
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
                                                                     ArrayList<String> workoutDays,
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

        // Initialize the weekly workout with null (resting days)
        ArrayList<ArrayList<ExerciseModel>> weeklyWorkout = new ArrayList<>(Collections.nCopies(7, null));

        // Map days to their respective 0-based indices (Monday = 0, Sunday = 6)
        Map<String, Integer> dayToIndex = new HashMap<>();
        dayToIndex.put("Monday", 1);
        dayToIndex.put("Tuesday", 2);
        dayToIndex.put("Wednesday", 3);
        dayToIndex.put("Thursday", 4);
        dayToIndex.put("Friday", 5);
        dayToIndex.put("Saturday", 6);
        dayToIndex.put("Sunday", 0);

        // Determine exercises per workout based on the number of workout days
        int exercisesPerWorkout = (workoutDays.size() == 1) ? 8 : // Full body
                (workoutDays.size() == 2) ? 6 :   // Upper/Lower split
                        (workoutDays.size() == 3) ? 5 :   // Push/Pull/Legs
                                4;                         // Body part split

        // Generate workouts for each specified day
        for (String day : workoutDays) {
            int dayIndex = dayToIndex.get(day); // Get the 0-based index for the day

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
                    workoutDays.size(),
                    dayIndex
            );
            workout.addAll(dayExercises);

            // Add a cardio exercise at the end
            if (!cardioExercises.isEmpty()) {
                workout.add(cardioExercises.get(new Random().nextInt(cardioExercises.size())));
            }

            // Add the workout to the weekly plan
            weeklyWorkout.set(dayIndex, workout);
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

    /*public void getOrCreateWeeklyWorkout(ArrayList<ExerciseModel> allExercises,
                                         int daysPerWeek,
                                         ArrayList<String> availableEquipment,
                                         ArrayList<String> levels,
                                         WorkoutCallback callback) {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String weekKey = getCurrentWeekKey();

        DatabaseReference workoutRef = dbRef.child(userId).child(weekKey);

        workoutRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Workout exists, load it
                    ArrayList<ArrayList<ExerciseModel>> weeklyWorkout = new ArrayList<>();

                    for (int i = 0; i < daysPerWeek; i++) {
                        ArrayList<ExerciseModel> dayWorkout = new ArrayList<>();
                        DataSnapshot daySnapshot = snapshot.child("day" + i);

                        for (DataSnapshot exerciseSnapshot : daySnapshot.getChildren()) {
                            ExerciseModel exercise = exerciseSnapshot.getValue(ExerciseModel.class);
                            dayWorkout.add(exercise);
                        }
                        weeklyWorkout.add(dayWorkout);
                    }

                    mainHandler.post(() -> callback.onWorkoutLoaded(weeklyWorkout));

                } else {
                    // Create new workout
                    ArrayList<ArrayList<ExerciseModel>> newWorkout = generateWeeklyWorkout(
                            allExercises, daysPerWeek, availableEquipment, levels);

                    // Save to Firebase
                    Map<String, Object> updates = new HashMap<>();
                    for (int i = 0; i < newWorkout.size(); i++) {
                        updates.put("day" + i, newWorkout.get(i));
                    }

                    workoutRef.updateChildren(updates)
                            .addOnSuccessListener(aVoid -> {
                                mainHandler.post(() -> callback.onWorkoutLoaded(newWorkout));
                            })
                            .addOnFailureListener(e -> {
                                mainHandler.post(() -> callback.onError(e));
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mainHandler.post(() -> callback.onError(error.toException()));
            }
        });
    }*/

    @SuppressLint("NewApi")
    private String getCurrentWeekKey() {
        LocalDate date = LocalDate.now();
        int weekOfMonth = (date.getDayOfMonth() - 1) / 7 + 1;
        return String.format("week%d_%s_%d",
                weekOfMonth,
                date.format(DateTimeFormatter.ofPattern("MMM")),
                date.getYear());
    }
}
