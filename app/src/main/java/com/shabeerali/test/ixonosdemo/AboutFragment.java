package com.shabeerali.test.ixonosdemo;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class AboutFragment extends android.support.v4.app.Fragment {


    public AboutFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the toolbar_normal for this fragment
        View v = inflater.inflate(R.layout.fragment_about, container, false);

        TextView txtVersion = (TextView) v.findViewById(R.id.txtVersion);
        TextView txtAuthor = (TextView) v.findViewById(R.id.txtAuthor);

        txtVersion.setText("Application version " + BuildConfig.VERSION_CODE);
        txtAuthor.setText("Author " + getString(R.string.app_developer));

        Toolbar toolbar = ((MainActivity)getActivity()).getToolbar();
        toolbar.setTitle("");
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Report to Google Analytics
        IxonosDemoApplication.getInstance().trackScreenView("About View");
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
