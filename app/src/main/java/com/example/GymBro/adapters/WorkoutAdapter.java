package com.example.GymBro.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.GymBro.R;
import com.example.GymBro.handlers.ExerciseHandler;
import com.example.GymBro.models.ExerciseModel;
import java.util.ArrayList;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {
    private ArrayList<ArrayList<ExerciseModel>> weeklyWorkout;
    private ArrayList<Integer> nonEmptyDays;
    private NavController navController;
    private ExerciseHandler handler;
    private String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    public WorkoutAdapter(ArrayList<ArrayList<ExerciseModel>> weeklyWorkout, ExerciseHandler handler) {
        this.weeklyWorkout = weeklyWorkout;
        this.handler = handler;
        updateNonEmptyDays();
    }

    private void updateNonEmptyDays() {
        this.nonEmptyDays = new ArrayList<>();
        if (weeklyWorkout != null) {
            for (int i = 0; i < weeklyWorkout.size(); i++) {
                ArrayList<ExerciseModel> dayWorkout = weeklyWorkout.get(i);
                if (dayWorkout != null && !dayWorkout.isEmpty()) {
                    nonEmptyDays.add(i);
                    Log.d("WorkoutAdapter", "Day " + i + " (" + daysOfWeek[i] + ") has " + 
                        dayWorkout.size() + " exercises");
                }
            }
        }
    }

    public void updateWorkout(ArrayList<ArrayList<ExerciseModel>> newWorkout) {
        this.weeklyWorkout = newWorkout;
        updateNonEmptyDays();
        notifyDataSetChanged();
    }

    public void setNavController(NavController navController) {
        this.navController = navController;
    }

    public static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView dayTitle;
        RecyclerView exercisesRecyclerView;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTitle = itemView.findViewById(R.id.text_day_title);
            exercisesRecyclerView = itemView.findViewById(R.id.recycler_view_day_exercises);
        }
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_workout_day, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        if (position < 0 || position >= nonEmptyDays.size()) {
            Log.e("WorkoutAdapter", "Invalid position: " + position);
            return;
        }

        int dayIndex = nonEmptyDays.get(position);
        if (dayIndex < 0 || dayIndex >= daysOfWeek.length) {
            Log.e("WorkoutAdapter", "Invalid day index: " + dayIndex);
            return;
        }

        ArrayList<ExerciseModel> dayWorkout = weeklyWorkout.get(dayIndex);
        
        // Set the day title
        holder.dayTitle.setText(daysOfWeek[dayIndex]);
        Log.d("WorkoutAdapter", "Setting day " + position + " to " + daysOfWeek[dayIndex]);

        // Setup nested RecyclerView
        holder.exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        DayExercisesAdapter exercisesAdapter = new DayExercisesAdapter(dayWorkout, navController, handler);
        holder.exercisesRecyclerView.setAdapter(exercisesAdapter);
    }

    @Override
    public int getItemCount() {
        return nonEmptyDays != null ? nonEmptyDays.size() : 0;
    }
}
