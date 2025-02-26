package com.example.GymBro.classes;

import android.content.Context;

import com.example.GymBro.handlers.ExerciseHandler;

public class ExerciseHandlerSingleton {
    private static ExerciseHandlerSingleton instance;
    private ExerciseHandler handler;

    private ExerciseHandlerSingleton(Context context) {
        // Initialize the ExerciseHandler
        handler = new ExerciseHandler(context);
    }

    public static ExerciseHandlerSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new ExerciseHandlerSingleton(context);
        }
        return instance;
    }

    public ExerciseHandler getHandler() {
        return handler;
    }
}
