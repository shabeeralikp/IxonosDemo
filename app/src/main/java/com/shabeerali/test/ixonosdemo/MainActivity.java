package com.shabeerali.test.ixonosdemo;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.android.gms.maps.MapFragment;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;



public class MainActivity extends AppCompatActivity {

    private DrawerLayout dlDrawer;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    NavigationView nvView;
    ActionBarDrawerToggle drawerToggle;
    UserInfo myUserInfo;
    private Tracker mTracker;
    boolean emailIntent;
    String receivedEmailId;

    public static final int MENU_ITEM_LOGOUT_INDEX = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        nvView = (NavigationView) findViewById(R.id.nvView);

        // Setup drawer view
        setupDrawerContent(nvView);

        // Find our drawer view
        dlDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        // Tie DrawerLayout events to the ActionBarToggle
        dlDrawer.setDrawerListener(drawerToggle);


        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlack));
        }

        emailIntent = false;
        myUserInfo =  UserInfo.getInstance(this);

        receivedEmailId = "";
        Intent intent = getIntent();

        if(intent.getData() != null ) {
            String data = intent.getData().toString();
            if(data.contains("mailto")) {
                emailIntent = true;
                receivedEmailId = data.substring(data.indexOf("mailto:") + 7);
            }
            IxonosDemoApplication.getInstance().trackEvent("Application Launch", "Email Link", "");
        } else {
            IxonosDemoApplication.getInstance().trackEvent("Application Launch", "Launcher", "");
        }

        if(savedInstanceState == null) {
            try {

                Fragment fragment = null;
                Class fragmentclass;

                if (emailIntent) {
                    if(receivedEmailId.equals(myUserInfo.getUserEmail())) {
                        fragmentclass = UserLocationFragment.class;
                    } else {
                        fragmentclass = SignupFragment.class;
                    }
                } else {
                    if (myUserInfo.isRegisteredUser())
                        fragmentclass = UserLocationFragment.class;
                    else
                        fragmentclass = SignupFragment.class;
                }

                fragment = (Fragment) fragmentclass.newInstance();
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.flContent, fragment);
                transaction.commit();
            } catch (Exception e){
                e.printStackTrace();
            }
        }


    }


    private ActionBarDrawerToggle setupDrawerToggle() {
        ActionBarDrawerToggle toggle;
        toggle = new ActionBarDrawerToggle(this, dlDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
        return toggle;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
        drawerToggle.setDrawerIndicatorEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }


                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment to show based on position
        Fragment fragment = null;

        Class fragmentClass = null;
        switch(menuItem.getItemId()) {
            case R.id.nav_menu:
                dlDrawer.closeDrawers();
                return;
            case R.id.nav_home:
                if(myUserInfo.isRegisteredUser())
                    fragmentClass = UserLocationFragment.class;
                else
                    fragmentClass = SignupFragment.class;
                break;
            case R.id.nav_about:
                fragmentClass = AboutFragment.class;
                break;

            case R.id.nav_logout:
                myUserInfo.logoutUser();
                receivedEmailId = "";
                fragmentClass = SignupFragment.class;
                break;
            default:
                return;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();


        Fragment currentFragment = fragmentManager.findFragmentById(R.id.flContent);
        if((currentFragment != null))
        {
            if(currentFragment instanceof UserLocationFragment) {
                MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.googleMap);
                if (mapFragment != null)
                    getFragmentManager().beginTransaction()
                            .remove(mapFragment).commit();

            }
        }


        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        dlDrawer.closeDrawers();
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public ActionBarDrawerToggle getDrawerToggle() {
        return drawerToggle;
    }

    public String getRecievedMailId() {
        return receivedEmailId;
    }


}

