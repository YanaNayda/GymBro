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

import com.example.GymBro.models.SettingsModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ExerciseHandler {
    private Context context;
    private final DatabaseReference dbRef;
    private final ExecutorService executor;
    private final Handler mainHandler;
    private ArrayList<ExerciseModel> exercisesList;
    private ArrayList<ArrayList<ExerciseModel>> weeklyWorkout;

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
        this.weeklyWorkout = null;
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

    public void getWeeklyWorkout(WorkoutCallback callback, NavController navController) {
        // Get the current user ID and week key
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String weekKey = getCurrentWeekKey();

        // Reference to the user's node in Firebase
        DatabaseReference userRef = dbRef.child(userId);

        // Fetch both the workout and settings in one go
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Check if settings exist
                DataSnapshot settingsSnapshot = snapshot.child("settings");
                if (!settingsSnapshot.exists()) {
                    // Settings do not exist, navigate to Settings Fragment
                    mainHandler.post(() -> {
                        if (navController != null) {
                            navController.navigate(R.id.action_logIn_to_settings);
                        } else {
                            callback.onError(new Exception("Settings not found and NavController is null"));
                        }
                    });
                    return; // Exit the method early
                }

                // Settings exist, load them
                SettingsModel settings = settingsSnapshot.getValue(SettingsModel.class);
                if (settings == null) {
                    // Settings are null, navigate to Settings Fragment
                    mainHandler.post(() -> {
                        if (navController != null) {
                            navController.navigate(R.id.action_logIn_to_settings);
                        } else {
                            callback.onError(new Exception("Settings are null and NavController is null"));
                        }
                    });
                    return; // Exit the method early
                }

                // Check if the weekly workout exists
                DataSnapshot workoutSnapshot = snapshot.child(weekKey);
                if (workoutSnapshot.exists()) {
                    // Workout exists, load it
                    ArrayList<ArrayList<ExerciseModel>> weeklyWorkout = new ArrayList<>();

                    // Iterate through each day in the snapshot
                    for (DataSnapshot daySnapshot : workoutSnapshot.getChildren()) {
                        ArrayList<ExerciseModel> dayWorkout = new ArrayList<>();

                        // Iterate through each exercise in the day
                        for (DataSnapshot exerciseSnapshot : daySnapshot.getChildren()) {
                            ExerciseModel exercise = exerciseSnapshot.getValue(ExerciseModel.class);
                            if (exercise != null) {
                                dayWorkout.add(exercise);
                            }
                        }

                        // Add the day's workout to the weekly workout
                        weeklyWorkout.add(dayWorkout);
                    }

                    // Return the loaded workout via callback
                    mainHandler.post(() -> callback.onWorkoutLoaded(weeklyWorkout));
                } else {
                    // Workout does not exist, generate a new workout based on the settings
                    ArrayList<ArrayList<ExerciseModel>> generatedWorkout = generateWeeklyWorkout(
                            getExercisesList(),
                            settings.getDays(),
                            settings.getEquipment(),
                            settings.getLevel()
                    );

                    // Write the generated workout to Firebase
                    writeWeeklyWorkoutToFirebase(userId, weekKey, generatedWorkout);

                    // Return the generated workout via callback
                    mainHandler.post(() -> callback.onWorkoutLoaded(generatedWorkout));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle Firebase errors
                mainHandler.post(() -> callback.onError(error.toException()));
            }
        });
    }

    // Helper method to write the weekly workout to Firebase
    private void writeWeeklyWorkoutToFirebase(String userId, String weekKey, ArrayList<ArrayList<ExerciseModel>> weeklyWorkout) {
        DatabaseReference workoutRef = dbRef.child(userId).child(weekKey);

        // Convert the weekly workout to a format suitable for Firebase
        Map<String, Object> workoutMap = new HashMap<>();
        for (int i = 0; i < weeklyWorkout.size(); i++) {
            ArrayList<ExerciseModel> dayWorkout = weeklyWorkout.get(i);
            if (dayWorkout != null) {
                // Use the day index as the key (e.g., "0", "1", "2", etc.)
                workoutMap.put(String.valueOf(i), dayWorkout);
            }
        }

        // Write the workout to Firebase
        workoutRef.setValue(workoutMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "Weekly workout written to Firebase successfully");
                    } else {
                        Log.e("Firebase", "Failed to write weekly workout to Firebase", task.getException());
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
