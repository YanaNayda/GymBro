package com.example.GymBro.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.activity.OnBackPressedCallback;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.GymBro.R;
import com.example.GymBro.classes.ExerciseHandlerSingleton;
import com.example.GymBro.handlers.ExerciseHandler;
import com.example.GymBro.models.ExerciseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExerciseItem#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExerciseItem extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageView img_exercise;
    private TextView txt_name_exercise;
    private TextView txt_equipment_exercise;
    private TextView txt_level_exercise;
    private TextView txt_category_exercise;
    private TextView txt_primary_muscles;
    private TextView txt_secondary_muscles;
    private TextView txt_instructions;
    
    private ExerciseModel exercise;
    private Handler imageHandler = new Handler();
    private boolean isImage0 = true;
    private Bitmap image0, image1;

    public ExerciseItem() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExerciseItem.
     */
    // TODO: Rename and change types and number of parameters
    public static ExerciseItem newInstance(String param1, String param2) {
        ExerciseItem fragment = new ExerciseItem();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Enable handling of back press
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Get the NavController
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                
                // Get the previous destination ID
                int previousDestId = navController.getPreviousBackStackEntry().getDestination().getId();
                
                // Navigate back and reload the previous fragment
                if (previousDestId == R.id.workout) {
                    navController.navigate(R.id.workout);
                } else if (previousDestId == R.id.exercise2) {
                    navController.navigate(R.id.exercise2);
                } else {
                    // If we can't determine the previous destination, just pop back
                    navController.popBackStack();
                }
            }
        });

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            exercise = (ExerciseModel) getArguments().getSerializable("exercise");
            
            // Log the received exercise data
            if (exercise != null) {
                Log.d("ExerciseItem", "Received exercise: " + exercise.getName() + 
                    ", Level: " + exercise.getLevel() + 
                    ", Equipment: " + exercise.getEquipment());
            } else {
                Log.e("ExerciseItem", "Received null exercise");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_item, container, false);
        
        // Initialize views
        initializeViews(view);
        
        // Set exercise data
        if (exercise != null) {
            displayExerciseData();
            setupImageAnimation();
        }

        return view;
    }

    private void initializeViews(View view) {
        txt_name_exercise = view.findViewById(R.id.txt_name_exercise);
        txt_equipment_exercise = view.findViewById(R.id.txt_equipment_exercise);
        txt_level_exercise = view.findViewById(R.id.txt_level_exercise);
        txt_category_exercise = view.findViewById(R.id.txt_category_exercise);
        txt_primary_muscles = view.findViewById(R.id.txt_primary_muscles);
        txt_secondary_muscles = view.findViewById(R.id.txt_secondary_muscles);
        txt_instructions = view.findViewById(R.id.txt_instructions);
        img_exercise = view.findViewById(R.id.img_exercise);
    }

    private void displayExerciseData() {
        txt_name_exercise.setText(exercise.getName());
        txt_equipment_exercise.setText(exercise.getEquipment());
        txt_level_exercise.setText(exercise.getLevel());
        txt_category_exercise.setText(exercise.getCategory());
        
        // Handle primary muscles with null check
        List<String> primaryMuscles = exercise.getPrimaryMuscles();
        String primaryMusclesText = primaryMuscles != null ? String.join(", ", primaryMuscles) : "None";
        txt_primary_muscles.setText(primaryMusclesText);
        
        // Handle secondary muscles with null check
        List<String> secondaryMuscles = exercise.getSecondaryMuscles();
        String secondaryMusclesText = secondaryMuscles != null ? String.join(", ", secondaryMuscles) : "None";
        txt_secondary_muscles.setText(secondaryMusclesText);
        
        txt_instructions.setText(exercise.getInstructions());

        // Get the handler from singleton to convert Base64 to Bitmap
        ExerciseHandlerSingleton handlerSingleton = ExerciseHandlerSingleton.getInstance(requireContext());
        ExerciseHandler handler = handlerSingleton.getHandler();
        
        // Convert Base64 strings to Bitmaps with null check
        ArrayList<Bitmap> images = handler.getBitmapsFromExercise(exercise);
        if (images != null && images.size() >= 2) {
            image0 = images.get(0);
            image1 = images.get(1);
            
            // Set initial image
            if (image0 != null) {
                img_exercise.setImageBitmap(image0);
            }
        } else {
            Log.e("ExerciseItem", "Failed to load exercise images");
            // Optionally set a placeholder image
            img_exercise.setImageResource(R.drawable.img_lock_close); // Use an appropriate placeholder
        }
    }

    private void setupImageAnimation() {
        // Only start animation if we have both images
        if (image0 == null || image1 == null) {
            return;
        }

        // Create a runnable for image switching
        Runnable imageSwitch = new Runnable() {
            @Override
            public void run() {
                if (isAdded() && image0 != null && image1 != null) {
                    if (isImage0) {
                        img_exercise.setImageBitmap(image1);
                    } else {
                        img_exercise.setImageBitmap(image0);
                    }
                    isImage0 = !isImage0;
                    imageHandler.postDelayed(this, 1000); // Switch every second
                }
            }
        };

        // Start the animation
        imageHandler.post(imageSwitch);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove callbacks when fragment is destroyed
        imageHandler.removeCallbacksAndMessages(null);
    }
}