package com.example.GymBro.adapters;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GymBro.R;
import com.example.GymBro.models.EquipmentModel;
import com.example.GymBro.models.ExerciseModel;

import java.util.ArrayList;
import java.util.List;

public class AdapterExercise extends RecyclerView.Adapter<AdapterExercise.MyViewHolder> {

    private ArrayList<ExerciseModel> exerciseArr;
    private ArrayList<ExerciseModel> filterexerciseArr;
    private NavController navController;

    public AdapterExercise(ArrayList<ExerciseModel> exerciseAdapter) {
        this.exerciseArr = exerciseAdapter;
        this.filterexerciseArr = new ArrayList<>(exerciseAdapter);
    }

    public void setNavController(NavController navController) {
        this.navController = navController;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameExercise;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameExercise = itemView.findViewById(R.id.text_name_exercise);
        }
    }

    public void getFilter(String charText) {
        charText = charText.toLowerCase();
        filterexerciseArr.clear();
        if (exerciseArr != null && !exerciseArr.isEmpty()) {
            if (charText.isEmpty()) {
                filterexerciseArr.addAll(exerciseArr);
            } else {
                for (ExerciseModel dataExercise : exerciseArr) {
                    String name = dataExercise.getName().toLowerCase();
                    if (name.contains(charText)) {
                        filterexerciseArr.add(dataExercise);
                    }
                }
            }
        } else {
            Log.e("AdapterExercise", "exerciseArr is empty or null");
        }

        if (filterexerciseArr.isEmpty()) {
            Log.e("AdapterExercise", "No matching exercises found!");
        }

        notifyDataSetChanged();
    }



    public void setExercisesList(ArrayList<ExerciseModel> newExercises) {
        if (newExercises != null && !newExercises.isEmpty()) {
            this.exerciseArr = newExercises;
            filterexerciseArr.clear();
            filterexerciseArr.addAll(newExercises);
            notifyDataSetChanged();
        } else {
            Log.e("AdapterExercise", "New exercises list is empty or null");
        }
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
        if (filterexerciseArr == null || filterexerciseArr.isEmpty()) {
            Log.e("AdapterExercise", "filterexerciseArr is empty");
            return;
        }

        ExerciseModel currentItem = filterexerciseArr.get(position);
        holder.nameExercise.setText(currentItem.getName());
        holder.itemView.setOnClickListener(v -> {
            if (navController != null) {
                Bundle bundle = new Bundle();
                Log.d("AdapterExercise", "Sending exercise: " + currentItem.getName() + 
                    ", Level: " + currentItem.getLevel() + 
                    ", Equipment: " + currentItem.getEquipment());
                bundle.putSerializable("exercise", currentItem);
                navController.navigate(R.id.action_exercise2_to_exerciseItem, bundle);
            } else {
                Log.e("AdapterExercise", "NavController is null");
            }
        });
    }

    @Override
    public int getItemCount() {
       return filterexerciseArr.size();
    }
}