package com.rydeon.andr.registration;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.dx.dxloadingbutton.lib.LoadingButton;
import com.rydeon.andr.R;

import spencerstudios.com.bungeelib.Bungee;

/**
 * Created by HP on 05/06/2018.
 */

public class LoginActivity extends AppCompatActivity {

    TextView txtRegister;
    LoadingButton btnLogin;
    Button btn_forgetPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);


        txtRegister = findViewById(R.id.txtRegisterUser);
        btnLogin  = findViewById(R.id.btn_login);
        btn_forgetPassword = findViewById(R.id.btn_forg_password);

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
                btnLogin.startLoading();
            }
        });

        btn_forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogin.loadingSuccessful();
            }
        });
    }
}
