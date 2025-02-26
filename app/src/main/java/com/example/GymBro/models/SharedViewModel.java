package com.example.GymBro.models;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class SharedViewModel extends ViewModel {
    private ArrayList<ArrayList<ExerciseModel>> workout;

    public ArrayList<ArrayList<ExerciseModel>> getWorkout() {
        return workout;
    }

    public void setWorkout(ArrayList<ArrayList<ExerciseModel>> workout) {
        this.workout = workout;
    }
}