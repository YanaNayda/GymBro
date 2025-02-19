package com.example.gymbro.fragments;

import static com.example.gymbro.R.drawable.edit_txt_invalid;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gymbro.R;
import com.example.gymbro.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;


public class Registration extends Fragment {

    private EditText edit_txt_registration_password;
    private EditText edit_txt_registration_passwordConfirm;
    private EditText edit_txt_registration_phone;
    private EditText edit_txt_registration_email;
    private Drawable iconVisible, iconHidden;

    private TextView txt_error_email;
    private TextView txt_error_phone;
    private TextView txt_error_password;
    private TextView txt_error_password_confirm;

    private String emailAddress;
    private String password ;
    private String passwordConfirm ;
    private String phoneNumber;


    private boolean isPhoneValid = false; // Track name validity
    private boolean isEmailValid = false;
    private boolean isPassValid= false;
    private boolean isPassConfValid =false;

    private FirebaseAuth mAuth;

    public Registration() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


            View view = inflater.inflate(R.layout.fragment_registration, container, false);

            mAuth = FirebaseAuth.getInstance();

            // Initialize views
            Button btn_register = view.findViewById(R.id.btn_registration);
            Button btn_haveAccount = view.findViewById(R.id.btn_have_account);

            edit_txt_registration_email = view.findViewById(R.id.edit_txt_registration_email);
            edit_txt_registration_phone = view.findViewById(R.id.edit_txt_registration_phone);
            edit_txt_registration_password = view.findViewById(R.id.edit_txt_registration_password);
            edit_txt_registration_passwordConfirm = view.findViewById(R.id.edit_txt_registration_password_confirm);

            txt_error_email = view.findViewById(R.id.txt_error_email);
            txt_error_phone = view.findViewById(R.id.txt_error_phone);
            txt_error_password = view.findViewById(R.id.txt_error_password);
            txt_error_password_confirm = view.findViewById(R.id.txt_error_password_confirm);


            iconVisible = ContextCompat.getDrawable(requireContext(), R.drawable.img_lock_open);
            iconHidden = ContextCompat.getDrawable(requireContext(), R.drawable.img_lock_close);

            // Set up password toggle for both fields
            setupPasswordToggle(edit_txt_registration_password);
            setupPasswordToggle(edit_txt_registration_passwordConfirm);


        btn_haveAccount.setPaintFlags(btn_haveAccount.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        // Set up TextWatcher for email field
        edit_txt_registration_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailAddress = s.toString().trim();
                if (emailAddress.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
                    isEmailValid = true;
                    edit_txt_registration_email.setBackgroundResource(R.drawable.edit_txt_valid);
                    txt_error_email.setText("");
                }
                else {
                    edit_txt_registration_email.setBackgroundResource(R.drawable.edit_txt_invalid);
                    txt_error_email.setText(R.string.invalid_email);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        // Set up TextWatcher for phone field
        edit_txt_registration_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phoneNumber = s.toString();
                if (phoneNumber.matches("[0-9]+") && phoneNumber.length()>=10) {
                    isPhoneValid = true;
                    edit_txt_registration_phone.setBackgroundResource(R.drawable.edit_txt_valid);
                    txt_error_phone.setText("");
                } else {
                    if(!phoneNumber.matches("[0-9]+")){
                        txt_error_phone.setText(R.string.phone_number_must_contain_only_numbers);
                    }
                    else{
                        txt_error_phone.setText("Phone number must contain at least 10 numbers");
                    }
                    isPhoneValid = false;
                    edit_txt_registration_phone.setBackgroundResource(edit_txt_invalid);

                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        // Set up TextWatcher for password field
        edit_txt_registration_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password = edit_txt_registration_password.getText().toString();
                if (password.length() >= 6) {
                    isPassValid = true;
                    edit_txt_registration_password.setBackgroundResource(R.drawable.edit_txt_valid);
                    txt_error_password.setText("");
                }
                else{
                    edit_txt_registration_password.setBackgroundResource(edit_txt_invalid);
                    txt_error_password.setText(R.string.password_must_contain_at_least_6_characters);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                phoneNumber = edit_txt_registration_phone.getText().toString();
                password = edit_txt_registration_password.getText().toString();
                passwordConfirm = edit_txt_registration_passwordConfirm.getText().toString();
                emailAddress = edit_txt_registration_email.getText().toString();

                if (!Objects.equals(password, passwordConfirm)){
                    isPassConfValid=false;
                    txt_error_password_confirm.setText(R.string.there_aren_t_equal);
                }
                else{
                    isPassConfValid=true;
                    txt_error_password_confirm.setText("");
                }

                if(Objects.equals(password, "") || Objects.equals(passwordConfirm, "") || Objects.equals(phoneNumber, "") || Objects.equals(emailAddress, "")){
                    Toast.makeText(getActivity(), "Fill all fields", Toast.LENGTH_SHORT).show();
                }
                else{
                    if (isPassConfValid && isPassValid && isPhoneValid && isEmailValid) {

                        MainActivity activity = (MainActivity) getActivity();
                        assert activity != null;

                        activity.registerUser(view, emailAddress, password );

                    }
                }
            }

        });

        btn_haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_registration_to_logIn);
            }
        });





        return view;
    }

    // Set up password toggle for both fields
    @SuppressLint("ClickableViewAccessibility")
    private void setupPasswordToggle (EditText passwordField){
        passwordField.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (passwordField.getRight() - passwordField.getCompoundDrawables()[2].getBounds().width())) {
                    togglePasswordVisibility(passwordField);
                    return true;
                }
            }
            return false;
        });
    }


    // Toggle password visibility for both fields
    private void togglePasswordVisibility (EditText passwordField){
        if (passwordField.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordField.setCompoundDrawablesWithIntrinsicBounds(null, null, iconHidden, null);
        } else {
            passwordField.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordField.setCompoundDrawablesWithIntrinsicBounds(null, null, iconVisible, null);
        }
        passwordField.setSelection(passwordField.getText().length());
    }
}