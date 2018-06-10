package com.rydeon.andr.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by HP on 07/04/2018.
 */

public class SessionManager {

    private static String TAG = SessionManager.class.getSimpleName();

    //Shared Preferences
    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context _context;

    //Shared pref mode
    int PRIVATE_MODE = 0;

    //share pref file name
    private static final String PREF_NAME = "UserLogin";

    private static final String KEY_IS_USER_LOGGED_IN = "isUserLoggedIn";


    private static final String KEY_USER_ID = "username";
    private static final String KEY_USER_IDD = "userid";

    private static final String KEY_TOKEN = "token";
    private static final String KEY_PHONE = "phone";

    private static final String KEY_IMAGE_URL = "image_url";


    public SessionManager(Context context){
        this._context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn){
        editor.putBoolean(KEY_IS_USER_LOGGED_IN, isLoggedIn);
        editor.commit();

        //   Log.d(TAG, "login session modified!");
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_USER_LOGGED_IN, false);
    }

    public void setToken(String token){
        editor.putString(KEY_TOKEN, token);
        editor.commit();
            Log.d(TAG, "token added: "+token);
    }
    public String getToken(){
        return pref.getString(KEY_TOKEN, "def_token");
    }

    public void setUsername(String username){
        editor.putString(KEY_USER_ID, username);
        editor.commit();

        //      Log.d(TAG, "username added: "+ username);
    }
    public String getUsername(){
        return pref.getString(KEY_USER_ID, "DEF_USER_ID");
    }


    public void setUserID(String userID){
        editor.putString(KEY_USER_IDD, userID);
        editor.commit();

        //      Log.d(TAG, "username added: "+ username);
    }
    public String getUserID(){
        return pref.getString(KEY_USER_IDD, "DEF_USER_ID");
    }


    public void setPhoneNumber(String phoneNumber){
        editor.putString(KEY_PHONE, phoneNumber);
        editor.commit();

        //      Log.d(TAG, "username added: "+ username);
    }
    public String getPhoneNumber(){
        return pref.getString(KEY_PHONE, "DEF_USER_ID");
    }

    public void setImageUrl(String imageUrl){
        editor.putString(KEY_IMAGE_URL, imageUrl);
        editor.commit();
    }
    public String getImageUrl(){
        return pref.getString(KEY_IMAGE_URL, null);
    }


}
