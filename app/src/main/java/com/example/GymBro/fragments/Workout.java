package com.example.GymBro.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.app.AlertDialog;

import com.example.GymBro.R;
import com.example.GymBro.adapters.WorkoutAdapter;
import com.example.GymBro.classes.ExerciseHandlerSingleton;
import com.example.GymBro.handlers.ExerciseHandler;
import com.example.GymBro.models.ExerciseModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Workout#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Workout extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private WorkoutAdapter adapter;
    private ArrayList<ArrayList<ExerciseModel>> weeklyWorkout;
    private TextView weekNumberText;
    private Button btnPreviousWorkouts;

    public Workout() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Workout.
     */
    // TODO: Rename and change types and number of parameters
    public static Workout newInstance(String param1, String param2) {
        Workout fragment = new Workout();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.recycler_view_workout);
        weekNumberText = view.findViewById(R.id.text_week_number);
        btnPreviousWorkouts = view.findViewById(R.id.btn_previous_workouts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get the handler from singleton
        ExerciseHandlerSingleton handlerSingleton = ExerciseHandlerSingleton.getInstance(requireContext());
        ExerciseHandler handler = handlerSingleton.getHandler();

        // Setup button click listener
        btnPreviousWorkouts.setOnClickListener(v -> {
            if (handler != null) {
                handler.getPreviousWorkouts(new ExerciseHandler.PreviousWorkoutsCallback() {
                    @Override
                    public void onWorkoutsLoaded(Map<String, ArrayList<ArrayList<ExerciseModel>>> workouts) {
                        showPreviousWorkoutsDialog(workouts);
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getContext(), 
                            "Error loading previous workouts: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Get weekly workout
        weeklyWorkout = handler.getWeeklyWorkout();
        
        // Get animal level with callback
        handler.getAnimalLevel(level -> {
            if (isAdded()) { // Check if fragment is still attached
                weekNumberText.setText("Week " + level);
            }
        });
        
        if (weeklyWorkout != null && !weeklyWorkout.isEmpty()) {
            if (adapter == null) {
                adapter = new WorkoutAdapter(weeklyWorkout, handler);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.updateWorkout(weeklyWorkout);
            }
        } else {
            Log.e("Workout", "Weekly workout is null or empty");
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (adapter != null) {
            NavController navController = Navigation.findNavController(view);
            adapter.setNavController(navController);
        }
    }

    private void showPreviousWorkoutsDialog(Map<String, ArrayList<ArrayList<ExerciseModel>>> workouts) {
        // Remove current week's workout from the list
        String currentWeekKey = ExerciseHandlerSingleton.getInstance(requireContext())
                .getHandler()
                .getCurrentWeekKey();
        workouts.remove(currentWeekKey);

        if (workouts.isEmpty()) {
            // Show message if there are no previous workouts
            new AlertDialog.Builder(requireContext())
                .setTitle("Previous Workouts")
                .setMessage("No previous workouts found")
                .setPositiveButton("OK", null)
                .show();
            return;
        }

        // Continue with showing previous workouts
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Previous Workouts");

        // Create the list of workout weeks
        ArrayList<String> weekKeys = new ArrayList<>(workouts.keySet());
        Collections.sort(weekKeys, Collections.reverseOrder()); // Show newest first

        builder.setItems(weekKeys.toArray(new String[0]), (dialog, which) -> {
            String selectedWeek = weekKeys.get(which);
            ArrayList<ArrayList<ExerciseModel>> selectedWorkout = workouts.get(selectedWeek);
            if (selectedWorkout != null) {
                // Update the current view with the selected workout
                weekNumberText.setText(selectedWeek);
                adapter.updateWorkout(selectedWorkout);
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}