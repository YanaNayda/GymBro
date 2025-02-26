package com.example.GymBro.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.GymBro.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Motivation extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private TextView animalLevelTextView;
    private TextView levelsLeftTextView;
    private ImageView animalImageView;

    public Motivation() {
        // Required empty public constructor
    }

    public static Motivation newInstance(String param1, String param2) {
        Motivation fragment = new Motivation();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_motivation, container, false);
        animalLevelTextView = view.findViewById(R.id.animalLevel);
        levelsLeftTextView = view.findViewById(R.id.levelsLeft);
        animalImageView = view.findViewById(R.id.animalImageView);

        // Fetch and display the animal level, image, and levels left
        fetchAnimalLevel();

        return view;
    }

    private void fetchAnimalLevel() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // User is not logged in, handle accordingly
            return;
        }

        String userId = user.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int animalLevel = 0;

                // Iterate through all child nodes under the user
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    // Check if the child node is a week (e.g., "week1_Jan_2024", "week2_Feb_2024", etc.)
                    String key = childSnapshot.getKey();
                    if (key != null && key.startsWith("week")) {
                        animalLevel++;
                    }
                }

                // Update the TextView with the number of weeks saved
                animalLevelTextView.setText("Animal Level: " + animalLevel);

                // Set the appropriate image and levels left text based on the animal level
                setAnimalImageAndLevelsLeft(animalLevel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
                animalLevelTextView.setText("Failed to load animal level.");
                levelsLeftTextView.setText("");
            }
        });
    }

    private void setAnimalImageAndLevelsLeft(int animalLevel) {
        int imageResource;
        String levelsLeftText;

        if (animalLevel < 5) {
            // Charmander image for levels under 5
            imageResource = R.drawable.charmander;
            levelsLeftText = "Levels left until Charmeleon: " + (5 - animalLevel);
        } else if (animalLevel >= 5 && animalLevel <= 10) {
            // Charmeleon image for levels between 5 and 10
            imageResource = R.drawable.charmeleon;
            levelsLeftText = "Levels left until Charizard: " + (11 - animalLevel);
        } else {
            // Charizard image for levels above 10
            imageResource = R.drawable.charizard;
            levelsLeftText = "You've reached the highest level!";
        }

        // Set the image in the ImageView
        animalImageView.setImageResource(imageResource);

        // Set the levels left text in the TextView
        levelsLeftTextView.setText(levelsLeftText);
    }
}