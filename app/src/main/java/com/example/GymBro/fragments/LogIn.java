package com.example.GymBro.fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.graphics.Paint;
import com.example.GymBro.R;
import com.example.GymBro.activities.MainActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LogIn#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogIn extends Fragment {

    // Arguments for the fragment initialization (not currently used)
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Parameters for the fragment
    private String mParam1;
    private String mParam2;

    // Variables for email and password
    private String emailAddress;
    private String password;

    // Drawables for password visibility icons
    private Drawable iconVisible, iconHidden;

    // Flags to track whether the email and password are valid
    private boolean isEmailValid = false;
    private boolean isPassValid = false;

    private FirebaseAuth mAuth;
    FirebaseDatabase database ;

    public LogIn() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LogIn.
     */
    public static LogIn newInstance(String param1, String param2) {
        LogIn fragment = new LogIn();
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
            // Retrieve arguments if provided
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log_in, container, false);

        // Initialize buttons and EditTexts
        Button btn_forgot_password = view.findViewById(R.id.btn_forgot_password);
        Button btn_signIn = view.findViewById(R.id.btn_google_sign_in);
        Button btn_log_in = view.findViewById(R.id.btn_sign_in);
        Button btn_register = view.findViewById(R.id.btn_to_registration);


        EditText edit_email = view.findViewById(R.id.edit_tx_email);
        EditText edit_password = view.findViewById(R.id.edit_tx_password);

        TextView error_email = view.findViewById(R.id.txt_error_email);
        TextView error_password = view.findViewById(R.id.txt_error_password);

        // Set up the icons for password visibility
        iconVisible = ContextCompat.getDrawable(requireContext(), R.drawable.img_lock_open);
        iconHidden = ContextCompat.getDrawable(requireContext(), R.drawable.img_lock_close);

        FirebaseAuth mAuth;
        FirebaseDatabase database ;

        // Set the underline style for the forgot password text
        btn_forgot_password.setPaintFlags(btn_forgot_password.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Set up password visibility toggle
        setupPasswordToggle(edit_password);

        // Set up the TextWatcher for the email field
        edit_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailAddress = edit_email.getText().toString();
                // Check if the email is valid using a simple regex pattern
                if (emailAddress.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
                    isEmailValid = true;
                    edit_email.setBackgroundResource(R.drawable.edit_txt_valid);
                    error_email.setText("");
                } else {
                    isEmailValid = false;
                    edit_email.setBackgroundResource(R.drawable.edit_txt_invalid);
                    error_email.setText(R.string.invalid_email);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Set up the TextWatcher for the password field
        edit_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password = edit_password.getText().toString();
                // Check if the password length is greater than 6 characters
                if (password.length() >= 6) {
                    isPassValid = true;
                    edit_password.setBackgroundResource(R.drawable.edit_txt_valid);
                    error_password.setText("");
                } else {
                    isPassValid = false;
                    edit_password.setBackgroundResource(R.drawable.edit_txt_invalid);
                    error_password.setText(R.string.min_6_characters);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Set up the click listener for the sign-in button
        btn_log_in.setOnClickListener(v -> {
            MainActivity newActivity = (MainActivity) getActivity();
            // Get email and password from the EditText fields
            String string_email = edit_email.getText().toString();
            String string_password = edit_password.getText().toString();

            // Check if the email and password are valid
            if (!isEmailValid) {
                error_email.setText(R.string.invalid_email);
                edit_email.setBackgroundResource(R.drawable.edit_txt_invalid);
            }
            if (!isPassValid) {
                error_password.setText(R.string.invalid_password);
                edit_password.setBackgroundResource(R.drawable.edit_txt_invalid);
            }
            assert newActivity != null;
            if (newActivity.getHandler().getExercisesList() == null)
                Snackbar.make(newActivity.findViewById(android.R.id.content),
                                "Did not fetch exercises yet, wait a couple of seconds",
                                Snackbar.LENGTH_LONG)
                        .setBackgroundTint(getResources().getColor(android.R.color.holo_red_light, newActivity.getTheme()))
                        .show();
            else if (isEmailValid && isPassValid) {
                // If valid, call the logInUser method in MainActivity
                newActivity.logInUser(view, string_email, string_password);
            }
        });

        // Set up the click listener for the register button to navigate to the registration screen
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity newActivity = (MainActivity) getActivity();
                assert newActivity != null;
                if (newActivity.getHandler().getExercisesList() == null)
                    Snackbar.make(newActivity.findViewById(android.R.id.content),
                                    "Did not fetch exercises yet, wait a couple of seconds",
                                    Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(android.R.color.holo_red_light, newActivity.getTheme()))
                            .show();
                else
                    Navigation.findNavController(view).navigate(R.id.action_logIn_to_registration);
            }
        });

        btn_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_email.getText().toString().isEmpty()){
                    error_email.setText(R.string.please_enter_your_email);
                }else {
                    MainActivity newActivity = (MainActivity) getActivity();
                    newActivity.sendPasswordResetEmail(v, edit_email.getText().toString());
                }
            }
        });


        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupPasswordToggle(EditText passwordField) {
        // Set up touch listener to toggle password visibility
        passwordField.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Check if the user clicked on the password visibility icon
                if (event.getRawX() >= (passwordField.getRight() - passwordField.getCompoundDrawables()[2].getBounds().width())) {
                    togglePasswordVisibility(passwordField);
                    return true;
                }
            }
            return false;
        });
    }

    // Toggle password visibility between visible and hidden
    private void togglePasswordVisibility(EditText passwordField) {
        if (passwordField.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordField.setCompoundDrawablesWithIntrinsicBounds(null, null, iconHidden, null);
        } else {
            passwordField.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordField.setCompoundDrawablesWithIntrinsicBounds(null, null, iconVisible, null);
        }
        // Set the cursor to the end of the password text
        passwordField.setSelection(passwordField.getText().length());
    }
}