package com.rydeon.andr.registration;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dx.dxloadingbutton.lib.LoadingButton;
import com.github.irvingryan.VerifyCodeView;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.rydeon.andr.MainActivity;
import com.rydeon.andr.R;
import com.rydeon.andr.app.AppConfig;
import com.rydeon.andr.helper.SessionManager;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import spencerstudios.com.bungeelib.Bungee;

/**
 * Created by HP on 06/06/2018.
 */

public class VerifyCodeActivity extends AppCompatActivity implements Button.OnClickListener {
    Button btn_change_number, btn_send_code;
    VerifyCodeView verifyCodeView;
    SessionManager sm;
    LoadingButton btn_verify;
//    CheckPermission checkPermission;
    SmsVerifyCatcher smsVerifyCatcher;
    String phoneNumber, v_code = "";
    TextView verificationMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_pin);
    //      getSupportActionBar().setTitle(R.string.verify);

    //    OtpReader.bind(this,"rydeOn GH");

        Bundle extra = getIntent().getExtras();
        if(extra != null){
            phoneNumber = extra.getString("phoneNumber");
        }

        sm = new SessionManager(this);
//        checkPermission = new CheckPermission(this);
//        checkPermission.checkMultiple(new String[]{Permission.RECEIVE_SMS, Permission.READ_SMS}, null);

        verificationMessage = findViewById(R.id.verificationMessage);
        verificationMessage.setText("A verification code has been sent to "+phoneNumber.substring(0, 8)+"*****. Enter code to confirm registration");
        verifyCodeView = findViewById(R.id.verifyCodeView);
        btn_verify = findViewById(R.id.btn_verify);
        btn_change_number = findViewById(R.id.btn_change_number);
        btn_send_code = findViewById(R.id.btn_resend_code);
        btn_verify.setOnClickListener(this);
        btn_change_number.setOnClickListener(this);
        btn_send_code.setOnClickListener(this);

        btn_send_code.setText("");

        verifyCodeView.setTextSize(6);

        verifyCodeView.setListener(new VerifyCodeView.OnTextChangListener() {
            @Override
            public void afterTextChanged(String text) {

             //   Toast.makeText(VerifyCodeActivity.this, text, Toast.LENGTH_SHORT).show();
                if (verifyCodeView.getText().length() == 6) {
                    verifyCode();
                }

            }
        });


        smsVerifyCatcher = new SmsVerifyCatcher(VerifyCodeActivity.this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                Log.d("PHONE", message);
                 v_code = parseCode(message);
                verifyCodeView.setText(v_code.trim());

                Log.d("V_CODE", v_code);

                if (verifyCodeView.getText().length() == 6) {
                    verifyCode();
                }
            }
        });
        smsVerifyCatcher.setPhoneNumberFilter("rydeOn GH");
       // smsVerifyCatcher.setFilter("code is ");

        startHandlerAndWait10Seconds(); //start timer when verification page starts
    }

    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{6}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
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

            resendCode();

        }
        if (v == btn_change_number) {
            finish();
            Bungee.slideRight(VerifyCodeActivity.this);

        }

    }


    //this method verifies the code
    private void verifyCode() {
      //  sm.setLogin(true);
      //  startActivity(new Intent(VerifyCodeActivity.this, MainActivity.class));
      //  finish();
      //  Bungee.spin(VerifyCodeActivity.this);

        btn_verify.startLoading();
        btn_verify.setEnabled(false);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("phoneNumber", phoneNumber);
        jsonObject.addProperty("code", verifyCodeView.getText());

        Ion.with(VerifyCodeActivity.this).load("POST", AppConfig.VERIFY_CODE)
                .setJsonObjectBody(jsonObject)
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {

                if(e == null){

                    try {
                        JSONObject jsonObject1 = new JSONObject(result.getResult());
                        boolean status = jsonObject1.getBoolean("status");
                        if(!status){
                            Toast.makeText(VerifyCodeActivity.this, jsonObject1.getString("message"), Toast.LENGTH_SHORT).show();
                            btn_verify.loadingFailed();
                            btn_verify.reset();
                            btn_verify.setEnabled(true);
                        }else{
                            String token = result.getHeaders().getHeaders().get("x-auth-token");
                            JSONObject successObject = jsonObject1.getJSONObject("result");
                            String phone = successObject.getString("phone");
                            String full_name = successObject.getString("firstname")+" "+successObject.getString("lastname");
                            String id = successObject.getString("id");
                            String imageurl  = successObject.getString("image");

                            sm.setLogin(true);
                            sm.setPhoneNumber(phone);
                            sm.setUsername(full_name);
                            sm.setUserID(id);
                            sm.setToken(token);
                            sm.setImageUrl(imageurl);
                        //    checkPermission.checkMultiple(new String[]{Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION}, null);
                            btn_verify.loadingSuccessful();
                            startActivity(new Intent(VerifyCodeActivity.this, MainActivity.class));
                            finish();
                            Bungee.spin(VerifyCodeActivity.this);
                        }

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        btn_verify.loadingFailed();
                        btn_verify.reset();
                        btn_verify.setEnabled(true);
                    }

                }else{
                    e.printStackTrace();
                    btn_verify.loadingFailed();
                    btn_verify.reset();
                    btn_verify.setEnabled(true);

                }

            }
        });
    }


    private void resendCode(){

        btn_send_code.setEnabled(false);
        btn_send_code.setText(getString(R.string.please_wait));
        JsonObject object = new JsonObject();
        object.addProperty("phoneNumber", phoneNumber);

        Ion.with(VerifyCodeActivity.this).load("POST", AppConfig.RESEND_VERIFICATION)
                .setJsonObjectBody(object)
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {

                if(e == null){
                    smsVerifyCatcher.onStart();
                    Toast.makeText(VerifyCodeActivity.this, "Verification code sent", Toast.LENGTH_SHORT).show();
                    btn_send_code.setText("");
                    const_sec = 60;
                    seconds = new_seconds * 2;
                    new_seconds = seconds;
                    stopTimer = false;
                    startHandlerAndWait10Seconds();


                }else{
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
    }

    private boolean stopTimer = false;
    Handler handler1;
    private void startHandlerAndWait10Seconds(){
        handler1 = new Handler();
        handler1.postDelayed(new Runnable() {

            public void run() {
                // Start Countdown timer after wait for 10 seconds
                startCountDown();

            }
        }, 10000);
    }
    int seconds = 60;
    int new_seconds = seconds;
    int const_sec = 60;
    private void startCountDown (){

        final Handler handler2 = new Handler();
        handler2.post(new Runnable() {


            public void run() {
                seconds--;
              //  mhello.setText("" + seconds);
                if(const_sec == 0) const_sec = 60;

                const_sec --;
                int mins = seconds/60;
                btn_send_code.setText(mins+":"+ const_sec);
                btn_send_code.setEnabled(false);
                if (seconds < 0) {
                    // DO SOMETHING WHEN TIMES UP
                    stopTimer = true;
                    btn_send_code.setText(getString(R.string.resend_code));
                    btn_send_code.setEnabled(true);
                }
                if(!stopTimer) {
                    handler2.postDelayed(this, 1000);
                }

            }
        });
    }


    /**
     * need for Android 6 real time permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

//    @Override
//    public void otpReceived(String messageText) {
//        v_code = parseCode(messageText);
//        Log.d("V_CODE", v_code.trim());
//
//        verifyCodeView.setText();
//
//    }
}
