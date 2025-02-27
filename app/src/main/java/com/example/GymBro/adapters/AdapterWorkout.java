package com.example.GymBro.adapters;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GymBro.R;
import com.example.GymBro.models.ExerciseModel;
import com.example.GymBro.models.WorkoutModel;

import java.util.ArrayList;

public class AdapterWorkout extends RecyclerView.Adapter<AdapterWorkout.MyViewHolder> {

    private ArrayList<WorkoutModel> workoutDays;

    public AdapterWorkout(ArrayList<WorkoutModel> workoutDays) {
        this.workoutDays = workoutDays;
    }

    public void updateData(ArrayList<WorkoutModel> daysList) {
        this.workoutDays.clear();
        this.workoutDays.addAll(daysList);
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dayNameText;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            dayNameText = itemView.findViewById(R.id.text_day_workout);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_workout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        WorkoutModel currentItem = workoutDays.get(position);
        holder.dayNameText.setText(currentItem.getDayName());
    }

    @Override
    public int getItemCount() {
        return workoutDays.size();
    }
}
