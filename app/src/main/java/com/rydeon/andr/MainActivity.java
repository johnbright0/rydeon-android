package com.rydeon.andr;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.rydeon.andr.helper.SessionManager;
import com.rydeon.andr.registration.LoginActivity;

import java.security.Permission;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, OnMapReadyCallback, LocationListener {

    LocationRequest mLocationRequest;
    Location mCurrentLocation;
    LocationManager locationManager;

    private PlaceDetectionClient mPlaceDetectionClient;
    private GeoDataClient mGeoDataClient;
    private GoogleMap mMap;
    private boolean mLocationPermissionGranted;
    private int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private int DEFAULT_ZOOM = 45;
    private Location mLastKnownLocation, currentLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    String location_coordinates= "";
    SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
//       if(Build.VERSION.SDK_INT > 20){
//           toolbar.setElevation(0);         }

        sm = new SessionManager(this);
        sm.setLogin(true);
        if(!sm.isLoggedIn()){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }else if(checkPermissions()){
          //  sm.setLogin(false);

            // Construct a GeoDataClient.
            mGeoDataClient = Places.getGeoDataClient(this, null);

            // Construct a PlaceDetectionClient.
            mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

            // Construct a FusedLocationProviderClient.
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

            SupportMapFragment mapFragment =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(MainActivity.this);

            locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
           // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0, MainActivity.this);
            checkLocationService();
        }




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void checkLocationService(){
        if(isLocationEnabled(this)){
            Toast.makeText(this, "Location service enabled", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Location service not enabled", Toast.LENGTH_SHORT).show();
            dialogToTurnOnLocation();
        }
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        }
        else if (id == R.id.logout){
            sm.setLogin(false);
            sm.setPhoneNumber("");
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
        if(checkPermissions()) {
            mMap.setMyLocationEnabled(true);
        }
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        getLocationCordinates();
    }

    private String myCordinates = "6.666600400000001,-1.6162709";
    private double latitude = 5.666667;
    private double longitude = 0.000000;
    private void getLocationCordinates(){

         locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        if(checkPermissions()) {
            currentLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
        }

      //  if(currentLocation != null){
            latitude = currentLocation.getLatitude();
            longitude = currentLocation.getLongitude();

            myCordinates = Double.toString(latitude) + "," + Double.toString(longitude);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, latitude), DEFAULT_ZOOM));

         //   getPlusCodeData(myCordinates);



    //    }

    }

    private static int REQUEST_CODE = 52;
    private boolean permission_g = false;
    private boolean checkPermissions(){
        //check if there is permission to write on external storage
        if(ContextCompat.checkSelfPermission(this,  Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //permission already granted
            return true;

        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return permission_g;

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0, (android.location.LocationListener)this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE){
            //checking if permission is granted
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //permission is granted
                permission_g = true;
            }
            else{
                //   Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                permission_g = false;
                dialogToExplain();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //check if location is enabled
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }

    }

    private void dialogToTurnOnLocation(){
        // notify user
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setMessage(getString(R.string.gps_network_not_enabled));
        dialog.setPositiveButton(getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                // TODO Auto-generated method stub
                Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
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

    private void dialogToExplain(){
        // notify user
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setMessage(getString(R.string.grant_location_access));
        dialog.setPositiveButton(getString(R.string.grant_access), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                if(checkPermissions());
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

    private boolean stopTimer = false;
    private void startHandlerAndWait10Seconds(){
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {

            public void run() {

                // Start Countdown timer after wait for 10 seconds
                startCountDown();

            }
        }, 10000);
    }

    private void startCountDown (){
        final Handler handler2 = new Handler();
        handler2.post(new Runnable() {
            int seconds = 30;

            public void run() {
                seconds--;
                //  mhello.setText("" + seconds);
                if (seconds < 0) {
                    // DO SOMETHING WHEN TIMES UP
                    stopTimer = true;

                    getLocationCordinates();
                }
                if(!stopTimer) {
                    handler2.postDelayed(this, 1000);
                }

            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLongitude(), location.getLatitude()), DEFAULT_ZOOM));

    }
}



