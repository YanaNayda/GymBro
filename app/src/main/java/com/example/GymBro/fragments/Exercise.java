package com.example.GymBro.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.GymBro.R;
import com.example.GymBro.activities.GymActivity;
import com.example.GymBro.activities.MainActivity;
import com.example.GymBro.adapters.AdapterEquipmentSettings;
import com.example.GymBro.adapters.AdapterExercise;
import com.example.GymBro.classes.DataEquipment;
import com.example.GymBro.handlers.ExerciseHandler;
import com.example.GymBro.models.EquipmentModel;
import com.example.GymBro.models.ExerciseModel;
import com.example.GymBro.models.SettingsModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Exercise#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Exercise extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recycleExercise;
    private LinearLayoutManager layoutManager;
    private AdapterExercise adapter;
    private ArrayList<ExerciseModel> ExerciseSetExercises;

    public static ExerciseHandler handler;



    public Exercise() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Exercise.
     */
    // TODO: Rename and change types and number of parameters
    public static Exercise newInstance(String param1, String param2) {
        Exercise fragment = new Exercise();
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
        View view = inflater.inflate(R.layout.fragment_exercise, container, false);


        recycleExercise = view.findViewById(R.id.recycle_view_exercise);

        recycleExercise.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recycleExercise.setLayoutManager(layoutManager);
        recycleExercise.setItemAnimator(new DefaultItemAnimator());


        //handler = new ExerciseHandler();


        //GymActivity activity = (GymActivity) getActivity();

        //if (activity != null) {
        //    handler = activity.getHendler();
        //    ArrayList<ExerciseModel> exercises = handler.getExercisesList();
//
//
        //    if (exercises != null && !exercises.isEmpty()) {
        //        adapter = new AdapterExercise(exercises);
        //        recycleExercise.setAdapter(adapter);
        //    } else {
        //        Log.e("FragmentExercise", "Exercises list is empty!");
        //    }
        //}
        return view;
    }

    public RecyclerView getRecyclerView() {
        return recycleExercise;
    }




}