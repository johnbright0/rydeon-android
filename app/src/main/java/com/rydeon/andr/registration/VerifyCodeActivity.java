package com.rydeon.andr.registration;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dx.dxloadingbutton.lib.LoadingButton;
import com.github.irvingryan.VerifyCodeView;
import com.rydeon.andr.MainActivity;
import com.rydeon.andr.R;
import com.rydeon.andr.helper.SessionManager;

import spencerstudios.com.bungeelib.Bungee;

/**
 * Created by HP on 06/06/2018.
 */

public class VerifyCodeActivity extends AppCompatActivity implements Button.OnClickListener {
    Button btn_change_number, btn_send_code;
    VerifyCodeView verifyCodeView;
    SessionManager sm;
    LoadingButton btn_verify;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_pin);
        getSupportActionBar().setTitle(R.string.verify);

        sm = new SessionManager(this);

        verifyCodeView = findViewById(R.id.verifyCodeView);
        btn_verify = findViewById(R.id.btn_verify);
        btn_change_number = findViewById(R.id.btn_change_number);
        btn_send_code = findViewById(R.id.btn_resend_code);
        btn_verify.setOnClickListener(this);
        btn_change_number.setOnClickListener(this);
        btn_send_code.setOnClickListener(this);

        verifyCodeView.setListener(new VerifyCodeView.OnTextChangListener() {
            @Override
            public void afterTextChanged(String text) {
                Toast.makeText(VerifyCodeActivity.this, text, Toast.LENGTH_SHORT).show();
                if (verifyCodeView.getText().length() == 6) {
                    verifyCode();
                }

            }
        });

    }

    @Override
    public void onClick(View v) {

        if (v == btn_verify) {
            if (verifyCodeView.getText().length() == 6) {
                verifyCode();
            } else {

                Toast.makeText(this, "Enter the 6 digit code sent to your phone", Toast.LENGTH_SHORT).show();

            }
        }
        if (v == btn_send_code) {

        }
        if (v == btn_change_number) {
            finish();
            Bungee.slideRight(VerifyCodeActivity.this);

        }

    }


    //this method verifies the code
    private void verifyCode() {
        sm.setLogin(true);
        startActivity(new Intent(VerifyCodeActivity.this, MainActivity.class));
        finish();
        Bungee.spin(VerifyCodeActivity.this);

    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
