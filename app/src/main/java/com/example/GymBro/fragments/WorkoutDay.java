package com.example.GymBro.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.GymBro.R;
import com.example.GymBro.adapters.AdapterWorkout;
import com.example.GymBro.models.ExerciseModel;
import com.example.GymBro.models.WorkoutModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkoutDay#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkoutDay extends Fragment {


    private RecyclerView recycleWorkout;
    private LinearLayoutManager layoutManager;
    private AdapterWorkout adapter;


    private ArrayList<ExerciseModel> workout = new ArrayList<>();



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WorkoutDay() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Workout.
     */
    // TODO: Rename and change types and number of parameters
    public static WorkoutDay newInstance(String param1, String param2) {
        WorkoutDay fragment = new WorkoutDay();
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

        View view = inflater.inflate(R.layout.fragment_workout_day, container, false);

        recycleWorkout = view.findViewById(R.id.recycle_view_workout);
        recycleWorkout.setHasFixedSize(true);
        recycleWorkout.setLayoutManager(new LinearLayoutManager(getContext()));


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            loadWorkoutDays(userId);
        }

        return view;
    }

    private void loadWorkoutDays(String userId) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child(getCurrentWeekKey());

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<WorkoutModel> daysList = new ArrayList<>();

                for (DataSnapshot daySnapshot : snapshot.getChildren()) {
                    Long dayNumberLong = daySnapshot.getValue(Long.class); // Используем Long, чтобы избежать ошибок
                    if (dayNumberLong != null) {
                        daysList.add(new WorkoutModel(dayNumberLong.intValue()));
                    } else {
                        Log.e("Firebase", "Null value in snapshot: " + daySnapshot.getKey());
                    }
                }


                if (adapter == null) {
                    adapter = new AdapterWorkout(daysList);
                    recycleWorkout.setAdapter(adapter);
                } else {
                    adapter.updateData(daysList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error: " + error.getMessage());
            }
        });
    }
    public String getCurrentWeekKey() {
        // Get the current date using Calendar
        Calendar calendar = Calendar.getInstance();
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int weekOfMonth = (dayOfMonth - 1) / 7 + 1;

        // Format the month name (e.g., "Jan", "Feb")
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.US); // Explicit locale
        String monthName = monthFormat.format(calendar.getTime());

        // Get the year
        int year = calendar.get(Calendar.YEAR);

        // Return the formatted string with an explicit locale
        return String.format(Locale.US, "week%d_%s_%d", weekOfMonth, monthName, year);
    }

}
