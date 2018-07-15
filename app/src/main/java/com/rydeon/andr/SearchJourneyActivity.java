package com.rydeon.andr;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hzn.lib.EasyTransition;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.rydeon.andr.adapters.PlaceAutoCompleteAdapter;
import com.rydeon.andr.app.Util;
import com.rydeon.andr.helper.SessionManager;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by HP on 15/07/2018.
 */

public class SearchJourneyActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    View lineView;
    AutoCompleteTextView destination, starting;
    ImageView send;

    private PlaceAutoCompleteAdapter mAdapter;
    protected LatLng start;
    protected LatLng end;
    protected GoogleApiClient mGoogleApiClient;
    ListView location_list;
    private CardView cardView;
    private CardView listviewLayout;

    String sourceName, destinationName;
    String sourceCordinate, destinationCordinate;
    LocationManager locationManager;
    SessionManager sm;

    private GoogleMap mMap;
    private int DEFAULT_ZOOM = 16;

    private static final String LOG_TAG = SearchJourneyActivity.class.getSimpleName();

    LatLngBounds BOUNDS_GHANA = new LatLngBounds( new LatLng(4.840755, 0.1608107), new LatLng(11.143390, -3.143019));


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_journey);


        initViews();



        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        MapsInitializer.initialize(this);
        mGoogleApiClient.connect();


        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(SearchJourneyActivity.this);




        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mAdapter = new PlaceAutoCompleteAdapter(this, android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS_GHANA, null);
         /*
        * Adds auto complete adapter to both auto complete
        * text views.
        * */
        starting.setAdapter(mAdapter);
        destination.setAdapter(mAdapter);


        location_list.setAdapter(mAdapter);



        EasyTransition.enter(SearchJourneyActivity.this, 400,  new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                initOtherViews();

            }
        });

    }

    private void initViews(){

        listviewLayout = findViewById(R.id.listViewLayout);
        listviewLayout.setVisibility(View.GONE);

        cardView = findViewById(R.id.cardview);
      //  cardView.setVisibility(View.GONE);

        starting  = findViewById(R.id.start);

        location_list = findViewById(R.id.location_list);

        destination = findViewById(R.id.destination);
        destination.setVisibility(View.GONE);

        send = findViewById(R.id.send);
        send.setVisibility(View.GONE);

        lineView = findViewById(R.id.lineView);
        lineView.setVisibility(View.GONE);




    }

    private void initOtherViews(){

        //cardView layout animation
//        cardView.setAlpha(0);
//        cardView.setVisibility(View.VISIBLE);
//        cardView.setTranslationY(30);
//        cardView.animate()
//                .setDuration(400)
//                .alpha(1)
//                .translationY(0);



        //destinationAutocomplete
        destination.setAlpha(0);
        destination.setVisibility(View.VISIBLE);
        destination.setTranslationY(-30);
        destination.animate()
                .setDuration(400)
                .alpha(1)
                .translationY(0);

        //send ImageView
        send.setVisibility(View.VISIBLE);
        send.setTranslationX(20);
        send.animate()
                .setDuration(400)
                .alpha(1)
                .translationX(0);

        //ViewLine
        lineView.setAlpha(0);
        lineView.setVisibility(View.VISIBLE);
        lineView.setTranslationX(-40);
        lineView.animate()
                .setDuration(400)
                .alpha(1)
                .translationX(0);

        //listview  layout animation
        listviewLayout.setAlpha(0);
        listviewLayout.setVisibility(View.VISIBLE);
        listviewLayout.setTranslationY(-70);
        listviewLayout.animate()
                .setDuration(400)
                .alpha(1)
                .translationY(0);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        autoCompleteListeners(); /**this listens to two textviews */


    }//initOtherViews


    boolean start_click = false;
    boolean end_click = false;
    private void autoCompleteListeners() {
         /*
        * Sets the start and destination points based on the values selected
        * from the autocomplete text views.
        * */

//         starting.setOnClickListener(new View.OnClickListener() {
//             @Override
//             public void onClick(View v) {
//                 start_click = true;
//                 end_click = false;
//                 Toast.makeText(MainActivity.this, "start", Toast.LENGTH_SHORT).show();
//             }
//         });
//
//         destination.setOnClickListener(new View.OnClickListener() {
//             @Override
//             public void onClick(View v) {
//                 end_click = true;
//                 start_click = false;
//                 Toast.makeText(MainActivity.this, "endig", Toast.LENGTH_SHORT).show();
//             }
//         });

        starting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final PlaceAutoCompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
                final String placeId = String.valueOf(item.placeId);
                Log.i(LOG_TAG, "Autocomplete item selected: " + item.description);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            Log.e(LOG_TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                            places.release();
                            return;
                        }
                        // Get the Place object from the buffer.
                        final Place place = places.get(0);

                        start = place.getLatLng();
                    }
                });

            }
        });
        destination.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final PlaceAutoCompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
                final String placeId = String.valueOf(item.placeId);
                Log.i(LOG_TAG, "Autocomplete item selected: " + item.description);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            Log.e(LOG_TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                            places.release();
                            return;
                        }
                        // Get the Place object from the buffer.
                        final Place place = places.get(0);

                        end = place.getLatLng();
                    }
                });

            }
        });

        /*
        These text watchers set the start and end points to null because once there's
        * a change after a value has been selected from the dropdown
        * then the value has to reselected from dropdown to get
        * the correct location.
        * */
        starting.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int startNum, int before, int count) {
                if (start != null) {
                 //   start = null;
                }
                start_click = true;
                end_click = false;
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

        destination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (end != null) {
                 //   end = null;
                }
                start_click = false;
                end_click = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.Operations.isOnline(SearchJourneyActivity.this)) {
                    /** this method searches through the db **/

                    sourceCordinate = String.valueOf(start.latitude+","+start.longitude);
                    destinationCordinate = String.valueOf(end.latitude+","+end.longitude);

                    sourceName = starting.getText().toString();
                    destinationName = destination.getText().toString();

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("sourceName", sourceName);
                    returnIntent.putExtra("destinationName", destinationName);
                    returnIntent.putExtra("sourceCord", sourceCordinate);
                    returnIntent.putExtra("destinationCord", destinationCordinate);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();

                } else {
                    Toast.makeText(SearchJourneyActivity.this, "No internet connectivity", Toast.LENGTH_SHORT).show();
                }
            }
        });


        location_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final PlaceAutoCompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
                final String placeId = String.valueOf(item.placeId);
                Log.i(LOG_TAG, "Autocomplete item selected: " + item.description);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            Log.e(LOG_TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                            places.release();
                            return;
                        }
                        // Get the Place object from the buffer.
                        final Place place = places.get(0);

                        if(start_click) {
                            start = place.getLatLng();
                            starting.setText(place.getAddress());
                            destination.requestFocus();
                        }
                        if(end_click){
                            end = place.getLatLng();
                            destination.setText(place.getAddress());
                        }
                        mAdapter.clear();
                        Toast.makeText(SearchJourneyActivity.this, "Place: "+place.getAddress()+" latLong: "+place.getLatLng(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == android.R.id.home){

            exitAnimation();
           // finish();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
     //   super.onBackPressed();
        exitAnimation();
    }

    private void exitAnimation(){

        listviewLayout.animate()
                .setDuration(200)
                .alpha(0)
                .translationY(-50)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // transition exit after our anim
                        EasyTransition.exit(SearchJourneyActivity.this, 300, new DecelerateInterpolator());
                    }
                });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(5.600259, -0.191749));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e("STYLE_RES", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("STYLE_RES", "Can't find style. Error: ", e);
        }

        requestSingleLocation(mMap);
    }

    private void requestSingleLocation(final GoogleMap map) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(DEFAULT_ZOOM);

                map.moveCamera(center);
                map.animateCamera(zoom);

                start = new LatLng(location.getLatitude(), location.getLongitude());

                requestLocationName(latitude, longitude);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        }, null);

    }

    /**this method requests location name using latlong parameter */
    private void requestLocationName(final double latitude, final double longitude){

        String latLong = latitude +","+longitude;

        String url = "https://plus.codes/api?address="+latLong+"&ekey="+getString(R.string.gmap_key)+"&email=brightnyamata@gmail.com";


        Ion.with(SearchJourneyActivity.this).load(url)
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {

                if(e == null){

                    try {
                        JSONObject object = new JSONObject(result.getResult());

                        //getting the location name
                        JSONObject plus_code = object.getJSONObject("plus_code");
                        JSONObject local_address = plus_code.getJSONObject("locality");
                        String place_name = local_address.getString("local_address");

                        starting.setText(place_name);

                    //    if(marker != null){
                    //        marker.remove();
                    //    }
                    //    marker =  mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(place_name));

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                }else{
                    Log.d("ERROR", "not found");
                    e.printStackTrace();
                }

            }
        });

    }//END OF request location name

}

