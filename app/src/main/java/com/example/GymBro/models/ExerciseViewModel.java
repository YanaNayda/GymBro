package com.example.GymBro.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.GymBro.handlers.ExerciseHandler;

import java.util.ArrayList;

public class ExerciseViewModel extends ViewModel {

    private final MutableLiveData<ExerciseHandler> handlerLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<ExerciseModel>> exercisesLiveData = new MutableLiveData<>();

    public void setHandler(ExerciseHandler handler) {
        handlerLiveData.setValue(handler);
    }

    public LiveData<ExerciseHandler> getHandler() {
        return handlerLiveData;
    }

    public void setExercises(ArrayList<ExerciseModel> exercises) {
        exercisesLiveData.setValue(exercises);
    }

    public LiveData<ArrayList<ExerciseModel>> getExercises() {
        return exercisesLiveData;

    }

}

