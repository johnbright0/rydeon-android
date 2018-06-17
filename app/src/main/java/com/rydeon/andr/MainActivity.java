package com.rydeon.andr;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.rydeon.andr.adapters.PlaceAutoCompleteAdapter;
import com.rydeon.andr.app.Util;
import com.rydeon.andr.helper.SessionManager;
import com.rydeon.andr.registration.LoginActivity;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONException;
import org.json.JSONObject;


import spencerstudios.com.bungeelib.Bungee;

public class MainActivity extends AppCompatActivity
        implements Button.OnClickListener, LocationListener, NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static int REQUEST_CODE = 52;
    LocationRequest mLocationRequest;
    LocationManager locationManager;
    SessionManager sm;

    private GoogleMap mMap;
    private int DEFAULT_ZOOM = 16;
    private Location currentLocation;
    private boolean permission_g = false;

    SlidingUpPanelLayout slidingUpPanelLayout;

    private Button btnSearchMap;
    private ImageButton btn_dropSearch, drawerToggle;
    private TextView txtUsernameDisplay;
    private CircularImageView userImage, requestCurrentLocation;
    ImageView send;

    AutoCompleteTextView starting;
    AutoCompleteTextView destination;
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutoCompleteAdapter mAdapter;
    protected LatLng start;
    protected LatLng end;
    CardView cardView;

    EditText destination3;

    Marker marker;
     ListView location_list;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final LatLngBounds BOUNDS_GHANA_OLD = new LatLngBounds(new LatLng(-57.965341647205726, 144.9987719580531),
            new LatLng(72.77492067739843, -9.998857788741589));

    LatLngBounds BOUNDS_GHANA = new LatLngBounds( new LatLng(4.840755, 0.1608107), new LatLng(11.143390, -3.143019));

    //check if location is enabled
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //   Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //   toolbar.setTitle("");
        //   setSupportActionBar(toolbar);
//       if(Build.VERSION.SDK_INT > 20){
//           toolbar.setElevation(0);         }

        slidingUpPanelLayout = findViewById(R.id.sliding_layout);
        location_list = findViewById(R.id.location_list);
        starting = findViewById(R.id.start);
        destination = findViewById(R.id.destination);
        send = findViewById(R.id.send);
        drawerToggle = findViewById(R.id.btnDrawerToggle);
        cardView = findViewById(R.id.cardview);
        btnSearchMap = findViewById(R.id.searchMap);
        btn_dropSearch = findViewById(R.id.btn_dropSearch);
        requestCurrentLocation = findViewById(R.id.requestCurrentLocation);
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        requestCurrentLocation.setOnClickListener(this);
        btnSearchMap.setOnClickListener(this);
        btn_dropSearch.setOnClickListener(this);
        drawerToggle.setOnClickListener(this);
        sm = new SessionManager(this);

        if (!sm.isLoggedIn()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            // finish();
        } else if (checkPermissions()) {

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Places.GEO_DATA_API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            MapsInitializer.initialize(this);
            mGoogleApiClient.connect();

            SupportMapFragment mapFragment =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(MainActivity.this);


            mAdapter = new PlaceAutoCompleteAdapter(this, android.R.layout.simple_list_item_1,
                    mGoogleApiClient, BOUNDS_GHANA, null);


        /*
        * Adds auto complete adapter to both auto complete
        * text views.
        * */
           starting.setAdapter(mAdapter);
           destination.setAdapter(mAdapter);


           location_list.setAdapter(mAdapter);

            autoCompleteListeners(); /**this listens to two textviews */

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0, MainActivity.this);
            checkLocationService();


        }


        /**initialization of drawer toggle */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        userImage = headerView.findViewById(R.id.imageView1);
        txtUsernameDisplay = headerView.findViewById(R.id.txtUsernameDisplay);

        txtUsernameDisplay.setText(sm.getUsername());
        if (sm.getImageUrl() != null)
            Glide.with(this).load(sm.getImageUrl()).into(userImage);
        navigationView.setNavigationItemSelectedListener(this);

        searchLocationSlidingMenu();

    }

    /**check if location service is enabled **/
    private void checkLocationService() {
        if (isLocationEnabled(this)) {
            Toast.makeText(this, "Location service enabled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Location service not enabled", Toast.LENGTH_SHORT).show();
            dialogToTurnOnLocation();
        }
    }

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
                    start = null;
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
                    end = null;
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
                if (Util.Operations.isOnline(MainActivity.this)) {
                    /** this method searches through the db **/

                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    slidingUpPanelLayout.animate();

                    searchRide();

                } else {
                    Toast.makeText(MainActivity.this, "No internet connectivity", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainActivity.this, "Place: "+place.getAddress()+" latLong: "+place.getLatLng(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_payments) {
            // Handle the camera action
        } else if (id == R.id.nav_trips) {

            startActivity(new Intent(MainActivity.this, MyRidesActivity.class));
            Bungee.inAndOut(MainActivity.this);

        } else if (id == R.id.nav_settings) {

        }
        else if ( id == R.id.nav_create_journey){
            startActivity(new Intent(MainActivity.this, MyCarsActivity.class));
        }
        else if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_logout) {
            sm.setLogin(false);
            sm.setPhoneNumber("");
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

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
        /**   locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, new android.location.LocationListener() {
        @Override public void onLocationChanged(Location location) {
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
        }

        @Override public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override public void onProviderEnabled(String provider) {

        }

        @Override public void onProviderDisabled(String provider) {

        }
        });
         */

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

    private boolean checkPermissions() {
        //check if there is permission to write on external storage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //permission already granted
            return true;

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return permission_g;

        }
    }//end of check permissions method



    @Override
    protected void onResume() {
        super.onResume();
        // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0, (android.location.LocationListener)this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE) {
            //checking if permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission is granted
                permission_g = true;
            } else {
                //   Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                permission_g = false;
                dialogToExplain();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void dialogToTurnOnLocation() {
        // notify user
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setMessage(getString(R.string.gps_network_not_enabled));
        dialog.setPositiveButton(getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                // TODO Auto-generated method stub
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
                //get gps
            }
        });
        dialog.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                // TODO Auto-generated method stub

            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void dialogToExplain() {
        // notify user
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setMessage(getString(R.string.grant_location_access));
        dialog.setPositiveButton(getString(R.string.grant_access), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                if (checkPermissions()) ;
            }
        });
        dialog.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                // TODO Auto-generated method stub

            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }


    @Override
    public void onLocationChanged(Location location) {

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLongitude(), location.getLatitude()), DEFAULT_ZOOM));
        
    }

    @Override
    public void onClick(View v) {
     //   startActivity(new Intent(MainActivity.this, LocationSearch.class));

        if(v == btnSearchMap) {
          //  cardView.setVisibility(View.VISIBLE);
           // YoYo.with(Techniques.SlideInUp).duration(500).playOn(findViewById(R.id.cardview));
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            slidingUpPanelLayout.animate();
            Toast.makeText(this, "search", Toast.LENGTH_SHORT).show();
        }
        if(v == btn_dropSearch){

        //  YoYo.with(Techniques.SlideOutDown).duration(500).playOn(findViewById(R.id.cardview));
            //cardView.setVisibility(View.GONE);
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            slidingUpPanelLayout.animate();
        }
        if(v == drawerToggle){
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.openDrawer(Gravity.START);
        }
        if(v == requestCurrentLocation){
            if(isLocationEnabled(this)) {
                Toast.makeText(this, "requesting location...", Toast.LENGTH_SHORT).show();
                requestSingleLocation(mMap);
            }else{
                dialogToTurnOnLocation();
            }
        }
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



    /**this method requests location name using latlong parameter */
    private void requestLocationName(final double latitude, final double longitude){

        String latLong = latitude +","+longitude;

        String url = "https://plus.codes/api?address="+latLong+"&ekey="+getString(R.string.gmap_key)+"&email=brightnyamata@gmail.com";


        Ion.with(MainActivity.this).load(url)
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
                        if(marker != null){
                            marker.remove();
                        }
                      marker =  mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(place_name));

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


    /**sliding menu for searching location**/
    private void searchLocationSlidingMenu(){

        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if(slideOffset > 0)
                panel.animate();
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

            }
        });

//
//        SlidingMenu slidingMenu = new SlidingMenu(this);
//        slidingMenu.setMode(SlidingMenu.RIGHT);
//        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
//       // slidingMenu.setBehindOffsetRes(R.dimen.sl);
//        slidingMenu.setFadeDegree(0.35f);
//        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
//        slidingMenu.setMenu(R.layout.location_search_fragment);
    }//end of search location sliding menu


    /** this method searches ride **/
    private void searchRide(){

    }
}



