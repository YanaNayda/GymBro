package com.example.GymBro.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.GymBro.R;
import com.example.GymBro.activities.MainActivity;
import com.example.GymBro.adapters.AdapterEquipmentSettings;
import com.example.GymBro.classes.DataEquipment;

import com.example.GymBro.handlers.ExerciseHandler;
import com.example.GymBro.models.EquipmentModel;
import com.example.GymBro.models.ExerciseModel;
import com.example.GymBro.models.SettingsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Settings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Settings extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;

    private RecyclerView recyclerSettings;
    private LinearLayoutManager layoutManager;
    private AdapterEquipmentSettings adapter;
    private ArrayList<EquipmentModel> equipmentSetEquipments;
    private ArrayList<SettingsModel> settingsModels;

    Button btn_beginner, btn_intermediate, btn_expert;
    Button btn_sunday, btn_monday, btn_tuesday, btn_wednesday, btn_thursday, btn_friday, btn_saturday;
    Button btn_ready;

    public Settings() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Settings.
     */
    // TODO: Rename and change types and number of parameters
    public static Settings newInstance(String param1, String param2) {
        Settings fragment = new Settings();
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Initialize settingsModels
        settingsModels = new ArrayList<>();

        // Initialize views
        btn_beginner = view.findViewById(R.id.btn_beginner);
        btn_intermediate = view.findViewById(R.id.btn_intermediate);
        btn_expert = view.findViewById(R.id.btn_expert);

        btn_sunday = view.findViewById(R.id.btn_sunday);
        btn_monday = view.findViewById(R.id.btn_monday);
        btn_tuesday = view.findViewById(R.id.btn_tuesday);
        btn_wednesday = view.findViewById(R.id.btn_wednesday);
        btn_thursday = view.findViewById(R.id.btn_thursday);
        btn_friday = view.findViewById(R.id.btn_friday);
        btn_saturday = view.findViewById(R.id.btn_saturday);
        btn_ready = view.findViewById(R.id.btn_ready);

        // Initialize RecyclerView and adapter
        recyclerSettings = view.findViewById(R.id.recycler_equipment_settings);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerSettings.setLayoutManager(layoutManager);
        recyclerSettings.setItemAnimator(new DefaultItemAnimator());

        equipmentSetEquipments = new ArrayList<>();
        for (int i = 0; i < DataEquipment.nameEquipment.length; i++) {
            equipmentSetEquipments.add(new EquipmentModel(
                    DataEquipment.nameEquipment[i],
                    DataEquipment.id_[i]
            ));
        }
        adapter = new AdapterEquipmentSettings(equipmentSetEquipments);
        recyclerSettings.setAdapter(adapter);

        // Set up button click listeners
        setupButtonListeners(view);

        return view;
    }

    private void setupButtonListeners(View view) {
        // "Select All" button
        Button btnSelectAll = view.findViewById(R.id.btn_select_all);
        btnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAllSelected = isAllEquipmentSelected();

                // Toggle selection state of all items
                for (EquipmentModel equipment : equipmentSetEquipments) {
                    equipment.setSelected(!isAllSelected);
                }

                // Notify adapter that data has changed
                adapter.notifyDataSetChanged();

                // Update "Select All" button text
                btnSelectAll.setText(isAllSelected ? "Select All" : "Deselect All");
            }
        });

        // Other button click listeners
        btn_beginner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
            }
        });

        btn_intermediate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
            }
        });

        btn_expert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
            }
        });

        btn_sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
            }
        });

        btn_monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
            }
        });

        btn_tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
            }
        });

        btn_wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
            }
        });

        btn_thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
            }
        });

        btn_friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
            }
        });

        btn_saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
            }
        });

        btn_ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> selectedLevels = new ArrayList<>();
                ArrayList<String> selectedDays = new ArrayList<>();
                ArrayList<String> selectedEquipment = new ArrayList<>();

                // Add selected levels, days, and equipment to their respective lists
                if (btn_beginner.isSelected()) {
                    selectedLevels.add("beginner");
                }
                if (btn_intermediate.isSelected()) {
                    selectedLevels.add("intermediate");
                }
                if (btn_expert.isSelected()) {
                    selectedLevels.add("expert");
                }

                if (btn_sunday.isSelected()) {
                    selectedDays.add("Sunday");
                }
                if (btn_monday.isSelected()) {
                    selectedDays.add("Monday");
                }
                if (btn_tuesday.isSelected()) {
                    selectedDays.add("Tuesday");
                }
                if (btn_wednesday.isSelected()) {
                    selectedDays.add("Wednesday");
                }
                if (btn_thursday.isSelected()) {
                    selectedDays.add("Thursday");
                }
                if (btn_friday.isSelected()) {
                    selectedDays.add("Friday");
                }
                if (btn_saturday.isSelected()) {
                    selectedDays.add("Saturday");
                }

                for (EquipmentModel equipment : equipmentSetEquipments) {
                    if (equipment.isSelected()) {
                        selectedEquipment.add(equipment.getName());
                    }
                }
                if (selectedEquipment.isEmpty()) {
                    selectedEquipment.add("full body");
                }

                // Check if mAuth is initialized
                if (mAuth == null) {
                    Toast.makeText(getActivity(), "FirebaseAuth not initialized", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get the current user
                FirebaseUser user = mAuth.getCurrentUser();
                if (user == null) {
                    Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
                    return;
                }

                String uid = user.getUid();

                if (!selectedLevels.isEmpty() || !selectedDays.isEmpty() || !selectedEquipment.isEmpty()) {
                    SettingsModel model = new SettingsModel(
                            selectedLevels,
                            selectedEquipment,
                            selectedDays
                    );

                    MainActivity newActivity = (MainActivity) getActivity();
                    ExerciseHandler handler = newActivity.getHandler();
                    ArrayList<ExerciseModel> exercises = handler.getExercisesList();
                    ArrayList<ArrayList<ExerciseModel>> generatedList = handler.generateWeeklyWorkout(exercises, selectedDays, selectedEquipment, selectedLevels);


                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("settings");
                    databaseReference.setValue(model)
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(getActivity(), "Settings saved", Toast.LENGTH_SHORT).show()
                            )
                            .addOnFailureListener(e ->
                                    Toast.makeText(getActivity(), "Failed to save settings", Toast.LENGTH_SHORT).show()
                            );

                    databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("weeklyExercise");
                    databaseReference.setValue(generatedList)
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(getActivity(), "Generated exercises saved", Toast.LENGTH_SHORT).show()
                            )
                            .addOnFailureListener(e ->
                                    Toast.makeText(getActivity(), "Failed to save generated exercises", Toast.LENGTH_SHORT).show()
                            );
                } else {
                    if (selectedLevels.isEmpty()) {
                        Toast.makeText(getActivity(), "You must choose a level", Toast.LENGTH_SHORT).show();
                    }
                    if (selectedDays.isEmpty()) {
                        Toast.makeText(getActivity(), "You must choose days", Toast.LENGTH_SHORT).show();
                    }
                }

                Navigation.findNavController(view).navigate(R.id.action_settings_to_gymActivity2);
            }
        });
    }

    // Helper method to check if all equipment items are selected
    private boolean isAllEquipmentSelected() {
        for (EquipmentModel equipment : equipmentSetEquipments) {
            if (!equipment.isSelected()) {
                return false;
            }
        }
        return true;
    }
}