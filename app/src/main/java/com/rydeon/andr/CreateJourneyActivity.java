package com.rydeon.andr;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.rydeon.andr.app.AppConfig;
import com.rydeon.andr.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by HP on 07/04/2018.
 */

public class CreateJourneyActivity extends AppCompatActivity implements Button.OnClickListener {
    private static int REQUEST_CODE = 2;
    Spinner spinnerSource, spinnerDestination, spinnerMaxUsers;
    Button btnStartDate, btnStartTime, btnAddJourney;
    TextInputEditText edtMaxRiders;
    String sourceAddress, destinationAddress;
    SessionManager sm;
   TextView txtAmount;
   String amount="";
   ImageButton btnTrackLocation;
    Calendar myCalendar = Calendar.getInstance();
String journey_date = "";
    String  sourceCoordinates, destinationCoordinates;



    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            journey_date = Integer.toString(year) + "-" + Integer.toString(monthOfYear) + "-" + Integer.toString(dayOfMonth);

            String beautySdate = journey_date;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date newdate = sdf.parse(beautySdate);
                SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd, yyyy");
                btnStartDate.setText(sdf2.format(newdate));
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

    };
    String journey_time = "";
    TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            myCalendar.set(Calendar.MINUTE, minute);

            journey_time = Integer.toString(hourOfDay) + ":" + Integer.toString(minute);
            String beautyStime = journey_time;
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            try {
                Date newdate = sdf.parse(beautyStime);
                SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm a");
                btnStartTime.setText(sdf2.format(newdate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };
    List<String> sourceList = null;
    List<String> destinationList = null;
    List<String> maxUserList = null;
    int max_users = 1;

    Button btnPickLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_journey2);

        getSupportActionBar().setTitle("Create A Journey");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


      //  spinnerSource = findViewById(R.id.spinnerSource);
      //  spinnerDestination = findViewById(R.id.spinnerDestination);
     //   spinnerMaxUsers = findViewById(R.id.spinnerMaxRiders);
        txtAmount  = findViewById(R.id.amount);
     //   btnTrackLocation = findViewById(R.id.trackLocation);
        edtMaxRiders = findViewById(R.id.edtMaxRiders);


        sm = new SessionManager(this);

        btnStartDate = findViewById(R.id.btndatePicker);
        btnStartTime = findViewById(R.id.btntimePicker);
        btnAddJourney = findViewById(R.id.btn_add_journey);
        btnPickLocation = findViewById(R.id.btnChooseLocation);

        btnStartDate.setOnClickListener(this);
        btnStartTime.setOnClickListener(this);
        btnAddJourney.setOnClickListener(this);
        btnPickLocation.setOnClickListener(this);
//        btnTrackLocation.setOnClickListener(this);

        //this method load spinner data
      //  loadPlaces();
      //  createPlaceAutocomplete();

     //   fromPlaceAutocomplete();
        toPlaceAutocomplete();
      //  displayMaxNumberOfArrays();
    }

    @Override
    public void onClick(View v) {

        if (v == btnStartDate) {
            pickDate();
        }
        if (v == btnStartTime) {
            pickTime();
        }
        if (v == btnAddJourney) {
                postJourney();
        }
        if(v == btnTrackLocation){
            /**if the user doest grant the permission, and didnt use facebook login prompt him again **/
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //permission already granted
           //     openMaps(); /**if permission already granted, open maps **/

            } else {

                /**if not granted, ask for permission again */
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

            }

        }
        if( v == btnPickLocation){

            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(CreateJourneyActivity.this), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }

            //startActivity(new Intent(CreateJourneyActivity.this, LocationSearch.class));

        }

    }

    int PLACE_PICKER_REQUEST = 43;

    private void pickDate() {
        new DatePickerDialog(CreateJourneyActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void pickTime() {

        new TimePickerDialog(CreateJourneyActivity.this, time, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE) {
            //checking if permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission is granted
                //  preambleDialog();
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    //spinner for source address

//    private void openMaps(){
//
//        Intent intent = new Intent(this, GMaps.class);
//        startActivityForResult(intent, REQUEST_CODE);
//
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //  super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                String toastMsg = String.format("Place: %s", place.getAddress());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }

        if(requestCode == REQUEST_CODE){

            if(resultCode == Activity.RESULT_OK){
          //      plus_code = data.getStringExtra("plus_code");
            //    local_address = data.getStringExtra("local_address");
                sourceAddress = data.getStringExtra("best_address");
                sourceCoordinates = data.getStringExtra("location_coordinates");
                //autocompleteFragment.setText(sourceAddress);
                //    Toast.makeText(getActivity(), locationCoordinates, Toast.LENGTH_SHORT).show();
            //    txtLocationDetails.setText("PLUS_CODE: "+plus_code+"\nLOCAL ADDRESS: "+local_address+"\nBest Address: "+best_streetAdd);
                Toast.makeText(this, "Location Tracked", Toast.LENGTH_SHORT).show();
            }
            if(resultCode == Activity.RESULT_CANCELED){
                //  locationCoordinates = "null";
                //  btnTrackLocation.setText(getString(R.string.track_location));
                Toast.makeText(this, "canceled", Toast.LENGTH_SHORT).show();
                sourceCoordinates = null;
            }

        }



        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i("PLACE:", "Place: " + place.getLatLng());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("PLACE", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }


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

    private void loadPlaces() {

        Ion.with(CreateJourneyActivity.this)
                .load("GET", AppConfig.PLACES)
                .addHeader("x-auth-token", sm.getToken())
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
              //  Log.d("result: ", result.getResult());
                if (e == null) {


                    try {

                        JSONObject object = new JSONObject(result.getResult());

                        JSONArray array = object.getJSONArray("result");
                        sourceList = new ArrayList<>();
                        destinationList = new ArrayList<>();
                        sourceList.add("FROM");
                        destinationList.add("TO");
                        for (int i = 0; i < array.length(); i++) {

                            JSONObject obj = array.getJSONObject(i);
                            String place = obj.getString("placeName");
                            String id = obj.getString("id");

                            sourceList.add(place);
                            destinationList.add(place);

                            Log.d("array: ", place);

                        }

                        displayDataforSourceDestinationSpinner();


                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }


                }

            }
        });

    }

    private void displayDataforSourceDestinationSpinner() {

        //creating adapter for the spinner
        ArrayAdapter<String> sourceDataAdapter = new ArrayAdapter<String>(CreateJourneyActivity.this, android.R.layout.simple_spinner_item, sourceList);

        //Dropdown layout Style
        sourceDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //attaching data adapter to spinner
        spinnerSource.setAdapter(sourceDataAdapter);

        spinnerSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //sourceAddress = parent.getSelectedItemPosition();

//                Toast.makeText(CreateJourneyActivity.this, sourceAddress, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //data for destination spinner
        //creating adapter for the spinner
        ArrayAdapter<String> destinationDataAdapter = new ArrayAdapter<String>(CreateJourneyActivity.this, android.R.layout.simple_spinner_item, destinationList);

        //Dropdown layout Style
        destinationDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //attaching data adapter to spinner
        spinnerDestination.setAdapter(destinationDataAdapter);

        spinnerDestination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
             //   destinationAddress =parent.getSelectedItemPosition();


             //   Toast.makeText(CreateJourneyActivity.this, destinationAddress, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




    }//end of display status spinner

    private void displayMaxNumberOfArrays(){
        maxUserList = new ArrayList<>();
        maxUserList.add("MAXIMUM RIDERS");
        maxUserList.add("1");
        maxUserList.add("2");
        maxUserList.add("3");
        maxUserList.add("4");
        maxUserList.add("5");
        maxUserList.add("6");
        ArrayAdapter<String> usersAdapter = new ArrayAdapter<String>(CreateJourneyActivity.this, android.R.layout.simple_spinner_item, maxUserList);


        //attaching data adapter to spinner
        spinnerMaxUsers.setAdapter(usersAdapter);

        spinnerMaxUsers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                max_users = parent.getSelectedItemPosition();


//                Toast.makeText(CreateJourneyActivity.this, max_users, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //post journey to server
    private void postJourney(){

        btnAddJourney.setText(getString(R.string.please_wait));

        Ion.with(CreateJourneyActivity.this).load("POST", AppConfig.CREATE_JOURNEY)
                .addHeader("x-auth-token", sm.getToken())
                .setBodyParameter("person", sm.getUsername())
                .setBodyParameter("source", sourceAddress)
                .setBodyParameter("sourceCoord", sourceCoordinates)
                .setBodyParameter("destination", destinationAddress )
                .setBodyParameter("destCoord", destinationCoordinates)
                .setBodyParameter("status", "0")
                .setBodyParameter("maxRiders", String.valueOf(edtMaxRiders.getText().toString()))
                .setBodyParameter("journeyDate", journey_date)
                .setBodyParameter("startTime", journey_time)
                .setBodyParameter("amount", txtAmount.getText().toString())
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
//                Log.d("RESP:" , result.getResult());
                if(e == null){

                    try {
                        JSONObject object = new JSONObject(result.getResult());

                        if(object.getString("message").contentEquals("SUCCESS")){
                            Toast.makeText(CreateJourneyActivity.this, "Journey Created Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CreateJourneyActivity.this, MainActivity.class));
                            finish();
                        }
                        else{
                            Toast.makeText(CreateJourneyActivity.this, "error creating journey.. try again", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                }
                btnAddJourney.setText(getString(R.string.create_journey));

            }
        });

    }

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private void createPlaceAutocomplete(){
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }
//    PlaceAutocompleteFragment autocompleteFragment;
//    private void fromPlaceAutocomplete() {
//
//         autocompleteFragment = (PlaceAutocompleteFragment)
//                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragmentFrom);
//
//
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                // TODO: Get info about the selected place.
//                Log.i("TAG", "Place: " + place.getName());
//
//                sourceAddress = place.getName().toString();
//                sourceCoordinates = place.getLatLng().toString().replaceAll("\\(", "").replaceAll("\\)", "").replace("lat/lng:", "");
//
//            }
//
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//                Log.i("TAG", "An error occurred: " + status);
//            }
//        });
//    }

    private void toPlaceAutocomplete() {

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("TAG", "Place: " + place.getLatLng());
                destinationAddress = place.getName().toString();
                destinationCoordinates = place.getLatLng().toString().replaceAll("\\(", "").replaceAll("\\)", "").replace("lat/lng:", "");

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });
    }

}
