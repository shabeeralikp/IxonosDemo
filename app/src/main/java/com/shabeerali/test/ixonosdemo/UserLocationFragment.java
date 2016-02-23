package com.shabeerali.test.ixonosdemo;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import  android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import android.location.Address;


public class UserLocationFragment extends Fragment implements OnMapReadyCallback,
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener
        {
            GoogleMap mMap;
            GoogleApiClient mGoogleApiClient;
            LocationRequest mLocationRequest;

            Double lastKnownLatitude;
            Double lastKnownLongitude;
            String lastKnownLocationAddress;

            UserInfo myUserInfo;
            //private static View view;
            NavigationView nvView;
            Runnable myRunnable;
            Handler myHandler;
            Toolbar myToolBar;

            MainActivity myActivity;
            ActionBarDrawerToggle myDrawerToggle;

            String firstName;
            String lastName;
            BroadcastReceiver connectReceiver;
            boolean googleApiConnected;
            boolean internetAvailable;
            boolean mlocationUpdateRequested;
            boolean mLocationPermission;

            TextView latlong;


            public static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

            public UserLocationFragment() {
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

                myHandler = new Handler();
                myActivity = (MainActivity) getActivity();
                myToolBar = myActivity.getToolbar();
                myDrawerToggle = myActivity.getDrawerToggle();
                googleApiConnected = false;
                View view = inflater.inflate(R.layout.fragment_user_location, container, false);

                mLocationRequest = mLocationRequest.create();
                mLocationRequest.setPriority(mLocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setInterval(3000);
                mLocationRequest.setFastestInterval(2000);
                nvView = (NavigationView) getActivity().findViewById(R.id.nvView);
                myUserInfo = UserInfo.getInstance(getActivity());
                if(myUserInfo.isRegisteredUser())
                    nvView.getMenu().getItem(myActivity.MENU_ITEM_LOGOUT_INDEX).setVisible(true);

                firstName = myUserInfo.getUserFirstName();
                lastName = myUserInfo.getUserLastName();
                lastKnownLatitude = myUserInfo.getLastKnownLatitude();
                lastKnownLongitude = myUserInfo.getLastKnownLongitude();
                lastKnownLocationAddress = myUserInfo.getLastKnownAddress();
                mLocationPermission = false;

                myRunnable  = new Runnable() {
                    public void run() {
                        myToolBar.removeAllViews();
                        myDrawerToggle.setDrawerIndicatorEnabled(true);
                        myDrawerToggle.syncState();
                        myToolBar.setTitle(firstName + " " + lastName);
                    }
                };

                internetAvailable = false;
                mlocationUpdateRequested = false;
                myToolBar.setTitle(firstName + " " + lastName);

                checkforPermission();

                return view;
            }

            @Override
            public void onViewCreated(View view, Bundle savedInstanceState) {
                super.onViewCreated(view, savedInstanceState);

                latlong = (TextView) view.findViewById(R.id.latlongLocation);
                MapFragment mapFragment = (MapFragment) this.getActivity().getFragmentManager()
                        .findFragmentById(R.id.googleMap);
                mapFragment.getMapAsync(this);

                if (mGoogleApiClient == null) {
                    mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .addApi(LocationServices.API)
                            .build();
                }


                connectReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        checkInternetConnectivity() ;
                        if(internetAvailable) {
                                requestLocationUpdates();

                        } else {
                            stopLocationUpdates();
                            setToolbarError("No internet connection");

                        }
                    }
                };


            }

            void checkforPermission() {
                int permissionCheck = ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                } else {
                    mLocationPermission = true;
                }
            }

            @Override
            public void onRequestPermissionsResult(int requestCode,
                                                   String permissions[], int[] grantResults) {
                switch (requestCode) {
                    case MY_PERMISSIONS_REQUEST_LOCATION: {
                        // If request is cancelled, the result arrays are empty.
                        if (grantResults.length > 0
                                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            mLocationPermission = true;

                        } else {
                            mLocationPermission = false;
                            getActivity().finish();
                        }
                        return;
                    }

                }
            }

            public void onStart() {
                super.onStart();
                myActivity.registerReceiver(connectReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

                if(mGoogleApiClient != null)
                    mGoogleApiClient.connect();

            }


            @Override
            public void onResume() {
                super.onResume();
                if (!checkInternetConnectivity()) {
                    setToolbarError("No internet connection");
                }
                requestLocationUpdates();
                IxonosDemoApplication.getInstance().trackScreenView("User Location View");
            }

            @Override
            public void onPause() {
                super.onPause();
                stopLocationUpdates();
            }

            public void onStop() {
                super.onStop();
                stopLocationUpdates();
                myActivity.unregisterReceiver(connectReceiver);
                if(mGoogleApiClient != null) {
                    mGoogleApiClient.disconnect();
                    googleApiConnected = false;
                }
            }


            @Override
            public void onMapReady(GoogleMap map) {
                mMap = map;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }



            @Override
            public void onConnected(Bundle connectionHint) {
                googleApiConnected = true;

                if(mLocationPermission) {
                    try {
                        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                                mGoogleApiClient);

                        if (mLastLocation != null) {

                            lastKnownLongitude = mLastLocation.getLongitude();
                            lastKnownLatitude = mLastLocation.getLongitude();
                            displayLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                        } else {
                            latlong.setText("Unknown Address");
                        }

                        requestLocationUpdates();
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                //
            }

            @Override
            public void onConnectionSuspended(int i) {
                googleApiConnected = false;

            }


            @Override
            public void onLocationChanged(Location location) {
                double currentLatitude = location.getLatitude();
                double currentLongitude = location.getLongitude();

                displayLocation(currentLatitude, currentLongitude);
            }

            void displayLocation(double latitude, double longitude) {

                LatLng latLng = new LatLng(latitude, longitude);
                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title("")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_pin));
                mMap.clear();
                mMap.addMarker(options);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                displayLocationAddress(latitude, longitude);

            }

            void displayLocationAddress(double currentLatitude, double currentLongitude) {
                Geocoder geocoder= new Geocoder(this.getActivity(), Locale.ENGLISH);
                String address = "Unknown Address";
                if(internetAvailable) {
                    try {

                        //Place your latitude and longitude
                        List<Address> addresses = geocoder.getFromLocation(currentLatitude, currentLongitude, 1);

                        if (addresses != null) {

                            Address fetchedAddress = addresses.get(0);
                            StringBuilder strAddress = new StringBuilder();

                            for (int i = 0; i < fetchedAddress.getMaxAddressLineIndex(); i++) {
                                strAddress.append(fetchedAddress.getAddressLine(i)).append(" ");
                            }

                            int commaIndex =  strAddress.toString().indexOf(',');
                            if(commaIndex != -1) {
                                address = strAddress.toString();
                            } else {
                                address = strAddress.toString();
                            }
                            lastKnownLocationAddress = address;
                            Log.e("SHABEER ", "I am at: " + lastKnownLocationAddress);

                        }

                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                }
                latlong.setText(address);

            }

            public void onDestroyView()
            {
                MapFragment mapFragment = (MapFragment) getActivity()
                        .getFragmentManager().findFragmentById(R.id.googleMap);
                if (mapFragment != null)
                    getActivity().getFragmentManager().beginTransaction()
                            .remove(mapFragment).commit();
                super.onDestroyView();
            }



            private boolean checkInternetConnectivity() {

                Log.e("SHABEER " ,  "checkInternetConnectivity");
                ConnectivityManager connectivityManager
                        = (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                internetAvailable =  (activeNetworkInfo != null && activeNetworkInfo.isConnected());
                return internetAvailable;
            }

            void requestLocationUpdates() {
                if( !mlocationUpdateRequested & googleApiConnected==true) {
                    try {
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                        mlocationUpdateRequested = true;
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
            }

            void stopLocationUpdates () {
                if(mlocationUpdateRequested & googleApiConnected == true) {
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                    mlocationUpdateRequested = false;
                }
            }

       public void setToolbarError(String error_messag) {
                myDrawerToggle.setDrawerIndicatorEnabled(false);
                myDrawerToggle.syncState();
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
                        myToolBar.setTitle(firstName + " " + lastName);
                    }
                });
                myHandler.postDelayed(myRunnable, 3000);
            }
        }
