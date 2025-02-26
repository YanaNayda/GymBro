package com.example.GymBro.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GymBro.R;
import com.example.GymBro.models.EquipmentModel;
import com.example.GymBro.models.ExerciseModel;

import java.util.ArrayList;
import java.util.List;

public class AdapterExercise  extends RecyclerView.Adapter<AdapterExercise.MyViewHolder> {

    private ArrayList<ExerciseModel> exerciseArr;

    public AdapterExercise (ArrayList<ExerciseModel> exerciseAdapter) {
        this.exerciseArr = exerciseAdapter;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameExercise;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameExercise = itemView.findViewById(R.id.text_name_exercise);
        }
    }

    public void setExercisesList(ArrayList<ExerciseModel> newExercises) {
        this.exerciseArr = newExercises;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdapterExercise.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_exercise, parent, false);
        MyViewHolder  myViewHolder = new MyViewHolder(view);
        return   myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterExercise.MyViewHolder holder, int position) {

        ExerciseModel exercise = exerciseArr.get(position);
        holder.nameExercise.setText(exercise.getName());
        if (exercise.isSelected()) {
            holder.nameExercise.setBackgroundColor(Color.LTGRAY); // Change background color
            holder.nameExercise.setTextColor(Color.WHITE);
        }
        else {
            holder.nameExercise.setBackgroundColor(Color.TRANSPARENT); // Reset background color
            holder.nameExercise.setTextColor(Color.BLACK);
        }
        holder.nameExercise.setOnClickListener(view -> {
            exercise.setSelected(!exercise.isSelected());
            notifyItemChanged(position); // Refresh the item view
        });
    }

    @Override
    public int getItemCount() {
        return exerciseArr.size();
    }
}