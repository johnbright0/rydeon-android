package com.rydeon.andr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.mindorks.paracamera.Camera;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 07/04/2018.
 */

public class AddCarActivity extends AppCompatActivity implements Button.OnClickListener{

    TextInputLayout layout_carRegNo, layout_carModel, layout_carYear;
    TextInputEditText edt_carRegNo, edt_carModel, edt_carYear;
    Button btn_add_car;
    ImageButton btn_take_pic;
    ImageView picFrame;
    Spinner spinnerMaker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_journey);
        getSupportActionBar().setTitle("Register Car");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        picFrame = findViewById(R.id.pic_frame);

        spinnerMaker = findViewById(R.id.spinnerMaker);
        layout_carRegNo = findViewById(R.id.layout_car_regNo);
        layout_carModel = findViewById(R.id.layout_car_Model);
        layout_carYear = findViewById(R.id.layout_car_year);

        edt_carRegNo = findViewById(R.id.edt_car_regNo);
        edt_carModel = findViewById(R.id.edt_car_Model);
        edt_carYear = findViewById(R.id.edt_car_year);

        btn_add_car = findViewById(R.id.btn_add_car);
        btn_take_pic = findViewById(R.id.btnTakePic);


        btn_take_pic.setOnClickListener(this);
        btn_add_car.setOnClickListener(this);


        chooseMaker();
    }

    @Override
    public void onClick(View v) {

        if(v == btn_add_car){
            postCar();
        }
        if(v == btn_take_pic){
            takePic();
        }

    }


    private void postCar(){




    }


    Camera camera;

    private void takePic(){
// Build the camera
        camera = new Camera.Builder()
                .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                .setTakePhotoRequestCode(1)
                .setDirectory("pics")
                .setName("ali_" + System.currentTimeMillis())
                .setImageFormat(Camera.IMAGE_JPEG)
                .setCompression(75)
                .setImageHeight(1000)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                .build(this);


        // Call the camera takePicture method to open the existing camera
        try {
            camera.takePicture();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // Get the bitmap and image path onActivityResult of an activity or fragment
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Camera.REQUEST_TAKE_PHOTO){
            Bitmap bitmap = camera.getCameraBitmap();
            if(bitmap != null) {
                picFrame.setImageBitmap(bitmap);
            }else{
                Toast.makeText(this.getApplicationContext(),"Picture not taken!",Toast.LENGTH_SHORT).show();
            }
        }
    }
    // The bitmap is saved in the app's folder
//  If the saved bitmap is not required use following code
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //camera.deleteImage();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void chooseMaker() {

        //setting the spinner elements
        final List<String> list_extra_name = new ArrayList<String>();
        list_extra_name.add("Nissan");
        list_extra_name.add("Hundai");
        list_extra_name.add("Honda");
        list_extra_name.add("Benz");
        list_extra_name.add("Toyota");
        list_extra_name.add("Suzuki");


        //creating adapter for the spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AddCarActivity.this, android.R.layout.simple_spinner_item, list_extra_name);

        //Dropdown layout Style
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //attaching data adapter to spinner
        spinnerMaker.setAdapter(dataAdapter);

        spinnerMaker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               // extra_name = parent.getSelectedItem().toString();

              //  Toast.makeText(AddCarActivity.this, , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }//end of choose Expense Type method
}
