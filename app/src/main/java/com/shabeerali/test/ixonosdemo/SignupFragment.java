package com.shabeerali.test.ixonosdemo;


import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;


public class SignupFragment extends android.support.v4.app.Fragment {

    private EditText emailField;
    private EditText firstNameField;
    private EditText lastNameField;
    private Button goButton;
    private UserInfo userDetails;
    private NavigationView nvView;
    private Toolbar myToolBar;
    private ActionBarDrawerToggle myDrawerToggle;
    private String email_address;
    private String first_name;
    private String last_name;
    private Handler myHandler;
    private Runnable myRunnable;
    private MainActivity myActivity;


    public SignupFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myActivity = (MainActivity) getActivity();
        myToolBar = myActivity.getToolbar();
        myDrawerToggle = myActivity.getDrawerToggle();
        myToolBar.setTitle("");

        View v = inflater.inflate(R.layout.fragment_signup, container, false);
        nvView = (NavigationView) getActivity().findViewById(R.id.nvView);
        nvView.getMenu().getItem(myActivity.MENU_ITEM_LOGOUT_INDEX).setVisible(false);

        emailField = (EditText) v.findViewById(R.id.email);
        firstNameField = (EditText) v.findViewById(R.id.firstname);
        lastNameField = (EditText) v.findViewById(R.id.lastname);
        userDetails = UserInfo.getInstance(v.getContext());
        email_address = "";
        first_name = "";
        last_name = "";

        String receivedEmail = myActivity.getRecievedMailId();
        if(!receivedEmail.equals("")) {
            emailField.setText(receivedEmail);
        }

        goButton = (Button) v.findViewById(R.id.goButton);
        goButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                email_address = emailField.getText().toString();
                first_name = firstNameField.getText().toString();
                last_name = lastNameField.getText().toString();
                goButton.setEnabled(false);
                emailField.setEnabled(false);
                firstNameField.setEnabled(false);
                lastNameField.setEnabled(false);
                if (checkAllFields()) {
                    registerUser(v);
                } else {

                    return;
                }
            }
        });

        myRunnable  = new Runnable() {
            public void run() {

                myToolBar.removeAllViews();
                myDrawerToggle.setDrawerIndicatorEnabled(true);
                myDrawerToggle.syncState();
                // Enable edit fields
                enableFields();
            }
        };

        myHandler = new Handler();
        return v;
    }

    void registerUser(View v) {
        userDetails.setUserEmail(email_address);
        userDetails.setUserFirstName(first_name);
        userDetails.setUserLastName(last_name);
        userDetails.registerUser();

        Fragment fragment = null;

        Class fragmentClass;
        fragmentClass = UserLocationFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Display the user location fragment
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        nvView.getMenu().getItem(3).setVisible(true);
    }

    boolean checkAllFields() {
        if(email_address.isEmpty() | first_name.isEmpty() | last_name.isEmpty()) {
            setToolbarError("Please Enter all the details");
            return false;
        }

        boolean isemailValid = isValidEmail(email_address);
        if(!isemailValid) {
            setToolbarError("Email entered is invalid");
            return false;
        }
        return isemailValid;
    }

    // Email format verifier
    private boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            boolean valid = android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
            return valid;
        }
    }

    // Set the Error message on Toolbar
    public void setToolbarError(String error_messag) {
        myDrawerToggle.setDrawerIndicatorEnabled(false);
        myToolBar.removeAllViews();
        View logo = getActivity().getLayoutInflater().inflate(R.layout.toolbar_edit, null);
        myToolBar.addView(logo);
        TextView text = (TextView)getActivity().findViewById(R.id.errorMessage);
        text.setText(error_messag);
        ImageButton closeButton = (ImageButton) getActivity().findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myHandler.removeCallbacks(myRunnable);
                myToolBar.removeAllViews();
                myDrawerToggle.setDrawerIndicatorEnabled(true);
                myDrawerToggle.syncState();
                enableFields();
            }
        });
        myHandler.postDelayed(myRunnable, 3000);

    }

    void enableFields() {
        goButton.setEnabled(true);
        emailField.setEnabled(true);
        firstNameField.setEnabled(true);
        lastNameField.setEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        IxonosDemoApplication.getInstance().trackScreenView("Sign Up View");
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
