package com.rydeon.andr.fblogin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.rydeon.andr.MainActivity;
import com.rydeon.andr.R;
import com.rydeon.andr.app.AppConfig;
import com.rydeon.andr.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import spencerstudios.com.bungeelib.Bungee;


/**
 * Created by HP on 17/06/2018.
 */

public class FBLoginActivity extends Activity {

    SessionManager sm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb_login);

        sm = new SessionManager(this);

        AccessToken accessToken = AccountKit.getCurrentAccessToken();

        if (accessToken != null) {
            //Handle Returning User
        } else {
            //Handle new or logged out user

        }

        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(final Account account) {
                // Get Account Kit ID
                String accountKitId = account.getId();


                // Get phone number
                PhoneNumber phoneNumber = account.getPhoneNumber();
                if (phoneNumber != null) {
                    String phoneNumberString = phoneNumber.toString();
                }

            }

            @Override
            public void onError(final AccountKitError error) {
                // Handle Error
            }
        });


        phoneLogin();
    }

    public static int APP_REQUEST_CODE = 99;

    public void phoneLogin() {
        final Intent intent = new Intent(FBLoginActivity.this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
        // ... perform additional configuration ... //
        configurationBuilder.setReceiveSMS(true);
        configurationBuilder.setReadPhoneStateEnabled(true);
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage;
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
                //  showErrorActivity(loginResult.getError());
                System.out.println(loginResult.getError().getErrorType().getMessage());
                Log.d("ERRORS", loginResult.getError().toString());
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";
            } else {
                if (loginResult.getAccessToken() != null) {
                 //   toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();
                 //   Log.d("TOKEN_ONE", loginResult.getAccessToken().getAccountId());
                //    Log.d("TOKEN_TWO", loginResult.getAccessToken().getToken());
                } else {
                    toastMessage = String.format(
                            "Success:%s...",
                            loginResult.getAuthorizationCode().substring(0,10));
                }

                // If you have an authorization code, retrieve it from
                // loginResult.getAuthorizationCode()
                // and pass it to your server and exchange it for an access token.

                // Success! Start your next activity...
                verifyAuthorizationCode(loginResult.getAccessToken().getToken());
            }


        }
    }

    ProgressBar progressBar;
    private void verifyAuthorizationCode(String token) {

         progressBar = findViewById(R.id.progressBar);

        JsonObject object = new JsonObject();
        object.addProperty("token", token);

        Ion.with(FBLoginActivity.this).load(AppConfig.LOGIN)
                .setJsonObjectBody(object)
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
           progressBar.setVisibility(View.GONE);

                if(e == null){
                    Log.d("RESPONSE", result.getResult());

                    try {

                        JSONObject response_obj = new JSONObject(result.getResult());

                        String token_new = result.getHeaders().getHeaders().get("x-auth-token");
                        sm.setToken(token_new);

                        boolean status = response_obj.getBoolean("status");
                        String message = response_obj.getString("message");
                        JSONObject result2 = response_obj.getJSONObject("result");
                        boolean registered = result2.getBoolean("registered");

                        JWT jwt = new JWT(token_new);
                        String subject = jwt.getSubject();

                        if(message.equalsIgnoreCase("success")){

                            sm.setLogin(true);

                            startActivity(new Intent(FBLoginActivity.this, MainActivity.class));
                            finish();
                            Bungee.spin(FBLoginActivity.this);
                        }


//                        if(message.equalsIgnoreCase("SUCCESS") && !registered){
//
//                            startActivity(new Intent(LoginActivity.this, UpdateProfile.class));
//                            finish();
//                        }else{
                        //   startActivity(new Intent(LoginActivity.this, FoodsActivity.class));

                        //  }


                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }


                }else{
                    e.printStackTrace();
                }


            }
        });

    }

}
