package com.rydeon.andr.registration;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.dx.dxloadingbutton.lib.LoadingButton;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.reqica.drilon.androidpermissionchecklibrary.CheckPermission;
import com.reqica.drilon.androidpermissionchecklibrary.Permission;
import com.rydeon.andr.MainActivity;
import com.rydeon.andr.R;
import com.rydeon.andr.app.AppConfig;
import com.rydeon.andr.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import spencerstudios.com.bungeelib.Bungee;

/**
 * Created by HP on 05/06/2018.
 */

public class LoginActivity extends AppCompatActivity {

    TextView txtRegister;
    LoadingButton btnLogin;
    Button btn_forgetPassword;
    SessionManager sm;
    TextInputLayout layout_phone, layout_password;
    TextInputEditText edtPhone, edtPassword;

    CheckPermission checkPermission;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);


        txtRegister = findViewById(R.id.txtRegisterUser);
        btnLogin  = findViewById(R.id.btn_login);
        btn_forgetPassword = findViewById(R.id.btn_forg_password);

        edtPhone = findViewById(R.id.edt_login_phone);
        edtPassword = findViewById(R.id.edt_login_password);
        layout_phone = findViewById(R.id.layout_edt_login_phone);
        layout_password = findViewById(R.id.layout_edt_login_password);

        checkPermission = new CheckPermission(this);
        sm = new SessionManager(this);

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                Bungee.slideUp(LoginActivity.this);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone = edtPhone.getText().toString();
                String password = edtPassword.getText().toString();

                layout_phone.setErrorEnabled(false);
                layout_password.setErrorEnabled(false);
                if(phone.isEmpty()){
                    layout_phone.setErrorEnabled(true);
                    layout_phone.setError(getString(R.string.field_required));
                }
                if(password.isEmpty()){
                    layout_password.setErrorEnabled(true);
                    layout_password.setError(getString(R.string.field_required));
                }else{
                    if(phone.startsWith("0")){
                        phone = phone.replaceFirst("0", "233");
                    }
                    else{
                        layout_phone.setErrorEnabled(true);
                        layout_phone.setError(getString(R.string.check_phone_number));
                    }
                    if(checkPermissions()) {
                        loginUser(phone, password);
                    }else{
                        Toast.makeText(LoginActivity.this, "Grant permission to access your location", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        btn_forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogin.loadingSuccessful();
            }
        });
    }


    //method to login user
    private void loginUser(String phoneNumber, String password){

        btnLogin.startLoading();
        Ion.with(LoginActivity.this).load("POST", AppConfig.LOGIN)
                .setBodyParameter("username", phoneNumber)
                .setBodyParameter("password", password)
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                Log.d("LOGIN_RESP", result.getResult());


                if(e == null){
                    if(result.getHeaders().code() == 200){
                        btnLogin.loadingSuccessful();
                        String token = result.getHeaders().getHeaders().get("x-auth-token");
                        JWT jwt = new JWT(token);
                        String subject = jwt.getSubject();
                        Log.d("SUBJ: ", subject);
                        try {
                            JSONObject jsonObject = new JSONObject(subject);
                            JSONObject person = jsonObject.getJSONObject("person");
                            String email = person.getString("email");
                            String phonenumber = person.getString("phone");
                            String full_name = person.getString("firstname")+" "+person.getString("lastname");


                            sm.setUsername(full_name);
                            sm.setPhoneNumber(phonenumber);

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        sm.setToken(token);
                        sm.setLogin(true);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                        Bungee.card(LoginActivity.this);

                    }
                    else{
                        btnLogin.loadingFailed();
                        Toast.makeText(LoginActivity.this, "Error signing in...", Toast.LENGTH_SHORT).show();
                        btnLogin.setResetAfterFailed(true);
                    }
                }else{
                    e.printStackTrace();
                    btnLogin.loadingFailed();
                    btnLogin.setResetAfterFailed(true);
                }

            }
        });

    }

    private static int REQUEST_CODE = 52;
    private boolean permission_g = false;
    private boolean checkPermissions(){
        //check if there is permission to write on external storage
        if(ContextCompat.checkSelfPermission(this,  Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //permission already granted
            return true;

        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
            return permission_g;

        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE){
            //checking if permission is granted
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //permission is granted
                permission_g = true;
            }
            else{
                //   Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                permission_g = false;
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
