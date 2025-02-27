package com.example.GymBro.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import com.example.GymBro.R;
import com.example.GymBro.handlers.ExerciseHandler;
import com.example.GymBro.models.ExerciseModel;
import java.util.ArrayList;
import android.graphics.Bitmap;

public class DayExercisesAdapter extends RecyclerView.Adapter<DayExercisesAdapter.ExerciseViewHolder> {
    private ArrayList<ExerciseModel> exercises;
    private NavController navController;
    private ExerciseHandler handler;

    public DayExercisesAdapter(ArrayList<ExerciseModel> exercises, NavController navController, ExerciseHandler handler) {
        this.exercises = exercises;
        this.navController = navController;
        this.handler = handler;
    }

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName;
        ImageView exerciseIcon;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.text_exercise_name);
            exerciseIcon = itemView.findViewById(R.id.img_exercise_icon);
        }
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        ExerciseModel exercise = exercises.get(position);
        holder.exerciseName.setText(exercise.getName());

        // Get and set the exercise image
        ArrayList<Bitmap> images = handler.getBitmapsFromExercise(exercise);
        if (images != null && !images.isEmpty() && images.get(0) != null) {
            holder.exerciseIcon.setImageBitmap(images.get(0));
        } else {
            holder.exerciseIcon.setImageResource(R.drawable.img_lock_close);
        }

        holder.itemView.setOnClickListener(v -> {
            if (navController != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("exercise", exercise);
                navController.navigate(R.id.action_workout_to_exerciseItem, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exercises != null ? exercises.size() : 0;
    }
} 