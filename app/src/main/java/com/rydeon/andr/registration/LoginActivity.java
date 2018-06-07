package com.rydeon.andr.registration;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
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
                checkPermission.checkMultiple(new String[]{Permission.ACCESS_COARSE_LOCATION, Permission.ACCESS_FINE_LOCATION}, null);

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
                    loginUser(phone, password);
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
                        String token = result.getHeaders().getHeaders().get("x-auth-token");
                        JWT jwt = new JWT(token);
                        String subject = jwt.getSubject();
                        Log.d("SUBJ: ", subject);
                        try {
                            JSONObject jsonObject = new JSONObject(subject);
                            JSONObject person = jsonObject.getJSONObject("person");
                            String email = person.getString("email");
                            String phonenumber = person.getString("phone");

                            sm.setUsername(email);
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
                        Toast.makeText(LoginActivity.this, "Error signing in...", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    e.printStackTrace();
                }

            }
        });

    }

}
