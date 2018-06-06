package com.rydeon.andr.registration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dx.dxloadingbutton.lib.LoadingButton;
import com.github.irvingryan.VerifyCodeView;
import com.rydeon.andr.R;
import com.rydeon.andr.helper.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import spencerstudios.com.bungeelib.Bungee;

/**
 * Created by HP on 05/06/2018.
 */

public class RegisterActivity extends AppCompatActivity {

    private LinearLayout dotsLayout;
    private int[] layouts;

    SessionManager sm;

    //login page setup
    TextInputEditText edtPassword;
    Button btn_login;
    TextView txtSignUp;
    TextView maintext;
    ScrollView layout_register;

    //registration page
    TextInputLayout layout_firstname, layout_lastname, layout_email, layout_password, layout_retypepassword, layout_phone;
    EditText edtFirstname, edtLastname, edtEmail, edt_Password, edtConfirmPassword, edtPhone;
    LoadingButton btn_register;
    Spinner spinnerGender;

    String first_name, last_name, email, password, confirm_password, phone, gender;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        getSupportActionBar().setTitle(R.string.register);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        sm = new SessionManager(this);

        spinnerGender = findViewById(R.id.spinnerGender);
        btn_register = findViewById(R.id.btn_register);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_register.startLoading();
                startActivity(new Intent(RegisterActivity.this, VerifyCodeActivity.class));
                Bungee.slideLeft(RegisterActivity.this);
            }
        });


        displayStatusSpinner();
    }



    //handle registration page
    private void registrationLogic() {

        layout_firstname = findViewById(R.id.layout_firstname);
        layout_lastname = findViewById(R.id.layout_lastname);
    //    layout_email = findViewById(R.id.layout_email);
        layout_password = findViewById(R.id.layout_password);
        layout_retypepassword = findViewById(R.id.layout_edtRetypePassword);
        layout_phone = findViewById(R.id.layout_phone);

        edtFirstname = findViewById(R.id.edtFirstName);
        edtLastname = findViewById(R.id.edtLastName);
      //  edtEmail = findViewById(R.id.edtEmail);
        edt_Password = findViewById(R.id.edt_Password);
        edtConfirmPassword = findViewById(R.id.edtRetypePassword);
        edtPhone = findViewById(R.id.edtPhone);
        spinnerGender = findViewById(R.id.spinnerGender);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!verifyInputs()) {
                    //   Toast.makeText(LoginRegisterActivity.this, "POST DATA", Toast.LENGTH_SHORT).show();
                  //  registerUser(first_name, last_name, phone, gender, password);

                }

            }
        });

    }//end of registration logic

    //verify inputs
    private boolean verifyInputs() {

        first_name = edtFirstname.getText().toString().trim().replaceAll("'", "\'");
        last_name = edtLastname.getText().toString().trim().replaceAll("'", "\'");
        email = edtEmail.getText().toString().trim().replaceAll("'", "\'");
        password = edt_Password.getText().toString().trim().replaceAll("'", "\'");
        confirm_password = edtConfirmPassword.getText().toString().trim().replaceAll("'", "\'");
        phone = edtPhone.getText().toString().trim().replaceAll("'", "\'");

        layout_firstname.setErrorEnabled(false);
        layout_lastname.setErrorEnabled(false);
        layout_email.setErrorEnabled(false);
        layout_password.setErrorEnabled(false);
        layout_retypepassword.setErrorEnabled(false);
        layout_phone.setErrorEnabled(false);

        if (first_name.isEmpty()) {
            layout_firstname.setErrorEnabled(true);
            layout_firstname.setError(getString(R.string.field_required));
            return true;
        }
        if (last_name.isEmpty()) {
            layout_lastname.setErrorEnabled(true);
            layout_lastname.setError(getString(R.string.field_required));
            return true;
        }
        if (email.isEmpty() || !validateEmailAddress(email)) {
            layout_email.setErrorEnabled(true);
            layout_email.setError(getString(R.string.format_invalid));
            return true;
        }

        if (password.length() < 5) {
            layout_password.setErrorEnabled(true);
            layout_password.setError(getString(R.string.too_short_password));
            return true;
        }
        if (!password.contentEquals(confirm_password)) {
            layout_retypepassword.setErrorEnabled(true);
            layout_retypepassword.setError(getString(R.string.unmatch_password));
            return true;
        }

        return false;
    }


    List<String> statusList = null;
    private void displayStatusSpinner() {

        //setting spinner elements
        statusList = new ArrayList<>();
        statusList.add("Gender");
        statusList.add("Male");
        statusList.add("Female");


        //creating adapter for the spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_spinner_item, statusList);

        //Dropdown layout Style
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //attaching data adapter to spinner
        spinnerGender.setAdapter(dataAdapter);

        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = parent.getItemAtPosition(position).toString();
                if(gender.contentEquals("Male")){
                    gender = "M";
                }
                if(gender.contentEquals("Female")){
                    gender = "F";
                }

                Toast.makeText(RegisterActivity.this, gender, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }//end of display status spinner



    //this method validates email pattern
    private boolean validateEmailAddress(String emailAddress) {
        String expression = "^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = emailAddress;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        return matcher.matches();
    }

}
