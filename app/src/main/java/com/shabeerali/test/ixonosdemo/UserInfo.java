package com.shabeerali.test.ixonosdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/*
 *  Userinfo class. Helps to store and retrive user data
 */
public class UserInfo {

    private static UserInfo mUserInfo;

    private static final String PREFS = "UserDetails";
    private static final String EMAIL_KEY = "EMAIL";
    private static final String FIRSTNAME_KEY = "FirstName";
    private static final String LASTNAME_KEY = "LastName";
    private static final String REGISTERED_KEY = "Registered";
    private static final String LAST_LATITUDE_KEY = "LastKnownLatitude";
    private static final String LAST_LONGITUDE_KEY = "LastKnownLongitude";
    private static final String LAST_ADDRESS_KEY = "LastKnownAddress";

    private SharedPreferences userPreferences;
    private boolean isRegistered;
    private String email_id;
    private String first_name;
    private String last_name;
    private double lastKnownLatitude;
    private double lastKnownLongitude;
    private String lastKnownAddress;

    public static UserInfo getInstance(Context context) {
        if (mUserInfo == null) {
            mUserInfo = new UserInfo(context);
        }
        return mUserInfo;
    }

    private UserInfo(Context context) {
        mUserInfo = null;
        userPreferences = context.getSharedPreferences(PREFS,Context.MODE_PRIVATE);
        getUserData();
        getLocationData();
    }

    public void saveUserInformation() {
        saveUserData();
        saveLocationData();
    }

    private void saveUserData() {
        SharedPreferences.Editor prefsEditor = userPreferences.edit();
        prefsEditor.putString(EMAIL_KEY, email_id);
        prefsEditor.putString(FIRSTNAME_KEY, first_name);
        prefsEditor.putString(LASTNAME_KEY, last_name);
        prefsEditor.putBoolean(REGISTERED_KEY, isRegistered);
        prefsEditor.commit();
    }

    private void getUserData() {
        isRegistered = userPreferences.getBoolean(REGISTERED_KEY, false);
        email_id = userPreferences.getString(EMAIL_KEY, "");
        first_name = userPreferences.getString(FIRSTNAME_KEY, "");
        last_name = userPreferences.getString(LASTNAME_KEY, "");
    }


    private void clearUserData() {
        email_id = "";
        first_name = "";
        last_name = "";
        isRegistered = false;
    }

    private void saveLocationData() {
        SharedPreferences.Editor prefsEditor = userPreferences.edit();
        prefsEditor.putLong(LAST_LATITUDE_KEY, Double.doubleToLongBits(lastKnownLatitude));
        prefsEditor.putLong(LAST_LONGITUDE_KEY, Double.doubleToLongBits(lastKnownLongitude));
        prefsEditor.putString(LAST_ADDRESS_KEY, lastKnownAddress);
        prefsEditor.commit();

    }

    private void getLocationData() {
        if (userPreferences!= null) {
            lastKnownAddress = userPreferences.getString(LAST_ADDRESS_KEY, "");
            lastKnownLatitude = Double.longBitsToDouble(userPreferences.getLong(LAST_LATITUDE_KEY, 0));
            lastKnownLongitude = Double.longBitsToDouble(userPreferences.getLong(LAST_LONGITUDE_KEY, 0));
        }
    }

    private void clearLocationData() {
        lastKnownLatitude = 0;
        lastKnownLongitude = 0;
        lastKnownAddress = "";
    }

    public void logoutUser() {
        clearUserData();
        clearLocationData();
        saveUserInformation();
    }

    public boolean isRegisteredUser () {
        return isRegistered;
    }

    public void registerUser() {
        isRegistered = true;
        saveUserInformation();

    }
    public String getUserEmail() {
        return  email_id;
    }

    public void setUserEmail(String email) {
        email_id = email;
    }

    public String getUserFirstName() {
        return  first_name;
    }

    public void setUserFirstName(String fname) {
        first_name = fname;
    }

    public String getUserLastName() {
        return  last_name;
    }

    public void setUserLastName(String lname) {
        last_name = lname;
    }

    public double getLastKnownLatitude() {
        return lastKnownLatitude;
    }

    public void setLastKnownLatitude(double latitude) {
         lastKnownLatitude = latitude;
    }

    public double getLastKnownLongitude() {
        return lastKnownLongitude;
    }

    public void setLastKnownLongitude(double longitude) {
        lastKnownLongitude = longitude;
    }

    public String getLastKnownAddress() {
        return lastKnownAddress;
    }

    public void setLastKnownAddress (String address) {
        lastKnownAddress = address;
    }
}