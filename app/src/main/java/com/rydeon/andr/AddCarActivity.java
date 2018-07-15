package com.rydeon.andr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dx.dxloadingbutton.lib.LoadingButton;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.mindorks.paracamera.Camera;
import com.rydeon.andr.app.AppConfig;
import com.rydeon.andr.dataloaders.CarMakeModelLoader;
import com.rydeon.andr.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 07/04/2018.
 */

public class AddCarActivity extends AppCompatActivity implements Button.OnClickListener {

    TextInputLayout layout_carRegNo, layout_carYear;
    TextInputEditText edt_carRegNo, edt_carYear;
    Button btn_add_car;
    ImageButton btn_take_pic;
    ImageView picFrame;
    private LoadingButton btnAddCar;
    Button btnSelectake, btnSelectModel;
    ProgressBar progressBarMake, progressBarModel;
    CarMakeModelLoader makeModelLoader;
    SessionManager sm;

    String make_name, make_id, model_id, model_name, reg_number, year;
    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);
        getSupportActionBar().setTitle("Register Car");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sm = new SessionManager(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        picFrame = findViewById(R.id.pic_frame);

        layout_carRegNo = findViewById(R.id.layout_car_regNo);
        layout_carYear = findViewById(R.id.layout_car_year);

        edt_carRegNo = findViewById(R.id.edt_car_regNo);
        edt_carYear = findViewById(R.id.edt_car_year);

        progressBarMake = findViewById(R.id.progressBarMake);
        progressBarModel = findViewById(R.id.progressBarModel);

        btnSelectake = findViewById(R.id.btnSelectMake);
        btnSelectModel = findViewById(R.id.btnSelectModel);
        btn_add_car = findViewById(R.id.btn_add_car);
        btn_take_pic = findViewById(R.id.btnTakePic);
        btnAddCar = findViewById(R.id.btnAddCar);


        btn_take_pic.setOnClickListener(this);
        btn_add_car.setOnClickListener(this);
        btnSelectake.setOnClickListener(this);
        btnSelectModel.setOnClickListener(this);
        btnAddCar.setOnClickListener(this);


        //this method laods the car makes to listView
        loadMakeList();

    }

    @Override
    public void onClick(View v) {

        if (v == btnAddCar) {

            reg_number = edt_carRegNo.getText().toString().trim();
            year = edt_carYear.getText().toString().trim();

            layout_carRegNo.setErrorEnabled(false);
            layout_carYear.setErrorEnabled(false);

            if (model_id == null || model_id.isEmpty()) {
                Toast.makeText(this, "Select car model", Toast.LENGTH_SHORT).show();
            }
            if (make_id == null || make_id.isEmpty()) {
                Toast.makeText(this, "Select car make", Toast.LENGTH_SHORT).show();
            }
            if (reg_number.isEmpty()) {
                layout_carRegNo.setErrorEnabled(true);
                layout_carRegNo.setError(getString(R.string.field_required));
            }
            if (year.isEmpty()) {
                layout_carYear.setErrorEnabled(true);
                layout_carYear.setError(getString(R.string.field_required));
            } else {
                postCar();
            }
        }
        if (v == btn_take_pic) {
            /**if the user doest grant the permission, and didnt use facebook login prompt him again **/
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //permission already granted
                startImagePicker();
            } else {

                /**if not granted, ask for permission again */
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }

        if (v == btnSelectake) {
            displayMakeList();
        }
        if (v == btnSelectModel) {

            displayModelList();

        }
    }

    ListView makeListView;
    View makeView;

    ListView modelListView;
    View modelView;

    private void loadMakeList() {
        makeView = getLayoutInflater().inflate(R.layout.layout_listview, null);
        makeListView = makeView.findViewById(R.id.customListView);

        modelView = getLayoutInflater().inflate(R.layout.layout_listview, null);
        modelListView = modelView.findViewById(R.id.customListView);

        makeModelLoader = new CarMakeModelLoader(AddCarActivity.this, progressBarMake, makeListView);
        makeModelLoader.loadCarMake();
    }


    /**
     * this method displays the make list
     **/
    AlertDialog makeCustomDialog;

    private void displayMakeList() {

        AlertDialog.Builder dialogBulider = new AlertDialog.Builder(AddCarActivity.this);


        makeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView txtMakeId = view.findViewById(R.id.txtCarMakeId);
                TextView txtMake = view.findViewById(R.id.txtCarMake);
                Toast.makeText(AddCarActivity.this, "id: " + txtMakeId.getText().toString() + " model: " + txtMake.getText().toString(), Toast.LENGTH_SHORT).show();

                btnSelectake.setText(txtMake.getText().toString());
                make_id = txtMakeId.getText().toString();
                make_name = txtMake.getText().toString();

                model_id = "";
                model_name = "";
                btnSelectModel.setText("select car model");

                makeModelLoader = new CarMakeModelLoader(AddCarActivity.this, progressBarModel, modelListView);
                makeModelLoader.loadCarModel(txtMakeId.getText().toString());
                makeCustomDialog.dismiss();
            }
        });

        if (makeView.getParent() != null)
            ((ViewGroup) makeView.getParent()).removeView(makeView);

        dialogBulider.setView(makeView);

        makeCustomDialog = dialogBulider.create();
        makeCustomDialog.show();
        //  makeListView.removeView(makeView);

    }

    /**this method displays model list based on the make item clicked */
    /**
     * this method displays the make list
     **/
    AlertDialog modelCustomDialog;

    private void displayModelList() {

        AlertDialog.Builder dialogBulider = new AlertDialog.Builder(AddCarActivity.this);


        modelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView txtMakeId = view.findViewById(R.id.txtCarMakeId);
                TextView txtMake = view.findViewById(R.id.txtCarMake);
                Toast.makeText(AddCarActivity.this, "id: " + txtMakeId.getText().toString() + " model: " + txtMake.getText().toString(), Toast.LENGTH_SHORT).show();

                model_name = txtMake.getText().toString();
                model_id = txtMakeId.getText().toString();

                btnSelectModel.setText(txtMake.getText().toString());
                modelCustomDialog.dismiss();
            }
        });

        if (modelView.getParent() != null)
            ((ViewGroup) modelView.getParent()).removeView(modelView);
        dialogBulider.setView(modelView);

        modelCustomDialog = dialogBulider.create();
        modelCustomDialog.show();

    }


    private void postCar() {

        btnAddCar.startLoading();
        btnAddCar.setEnabled(false);

        Ion.with(this).load("POST", AppConfig.CAR)
                .setHeader("x-auth-token", sm.getToken())
                .setMultipartFile("image", "image/*", file)
                .setMultipartParameter("make", make_id)
                .setMultipartParameter("model", model_id)
                .setMultipartParameter("year", year)
                .setMultipartParameter("registrationNumber", reg_number)
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {

                if (e == null) {
                    btnAddCar.loadingSuccessful();
                    btnAddCar.reset();
                    btnAddCar.setEnabled(true);

                    Log.d("RESPONSE", result.getResult());

                    try {
                        JSONObject jsonObject = new JSONObject(result.getResult());

                        boolean status = jsonObject.getBoolean("status");
                        if (status) {
                            Toast.makeText(AddCarActivity.this, "Added", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddCarActivity.this, "Error occurred!", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e1) {
                        e1.printStackTrace();

                        btnAddCar.loadingFailed();
                        btnAddCar.reset();
                        btnAddCar.setEnabled(true);
                    }

                } else {
                    btnAddCar.loadingFailed();
                    btnAddCar.reset();
                    btnAddCar.setEnabled(true);

                    e.printStackTrace();
                }
            }
        });


    }


    //load gallery request
    private void startImagePicker() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(Intent.createChooser(i, getString(R.string.choose_image)), RESULT_LOAD_IMAGE);
    }

    String encodedString;
    String fileName;
    String filePath;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};


            Cursor cursor = null;
            if (selectedImage != null) {
                cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            }
            if (cursor != null) {
                cursor.moveToFirst();
            }

            int columnIndex = 0;
            if (cursor != null) {
                columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            }
            String picturePath = null;
            if (cursor != null) {
                picturePath = cursor.getString(columnIndex);
            }
            if (cursor != null) {
                cursor.close();
            }

            String fileNameSegments[] = new String[0];
            if (picturePath != null) {
                fileNameSegments = picturePath.split("/");
            }
            fileName = fileNameSegments[fileNameSegments.length - 1];


            //    file  = new File(Uri.parse(picturePath).toString());
            //    file = new File(selectedImage.getPath()+fileName);

            file = new File(Uri.parse(picturePath).toString());


            Bitmap myImg = BitmapFactory.decodeFile(picturePath);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            //compress  image to make easy/quick uploading
            myImg.compress(Bitmap.CompressFormat.JPEG, 30, stream);
            byte[] byte_arr = stream.toByteArray();
            //encode Image to string for upload
            encodedString = Base64.encodeToString(byte_arr, Base64.DEFAULT);

            //display image bitmap
            picFrame.setImageBitmap(myImg);
        } else {
            picFrame.setImageBitmap(BitmapFactory.decodeFile(""));
        }


        /**TO HANDLE REQUEST FROM CAMERA */
        if (requestCode == Camera.REQUEST_TAKE_PHOTO) {
            Bitmap bitmap = camera.getCameraBitmap();
            if (bitmap != null) {


                picFrame.setImageBitmap(bitmap);
            } else {
                Toast.makeText(this.getApplicationContext(), "Picture not taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private int REQUEST_CODE = 2;


    /**
     * THIS METHOD HELPS  TAKE PICTURE FROM CAMERA
     */
    Camera camera;

    private void takePic() {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    File file;
    // Get the bitmap and image path onActivityResult of an activity or fragment

    // The bitmap is saved in the app's folder
//  If the saved bitmap is not required use following code
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //camera.deleteImage();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (make_name != null)
            btnSelectake.setText(make_name);
        if (model_name != null)
            btnSelectModel.setText(model_name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /*
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
    */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE) {
            //checking if permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission is granted

            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
