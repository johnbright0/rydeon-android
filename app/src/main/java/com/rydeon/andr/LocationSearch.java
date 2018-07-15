package com.rydeon.andr;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.rydeon.andr.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import spencerstudios.com.bungeelib.Bungee;

/**
 * Created by HP on 08/06/2018.
 */

public class LocationSearch extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnMarkerDragListener {

    LocationManager locationManager;
    SessionManager sm;

    private GoogleMap mMap;
    private int DEFAULT_ZOOM = 16;
    private Location currentLocation;

    Marker mMarker;

    Button btnCancel, btnConfirmLocation;
    TextView txtLocationName;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_search_fragment);

        btnCancel = findViewById(R.id.btnCancel);
        btnConfirmLocation = findViewById(R.id.btnConfirm);
        txtLocationName = findViewById(R.id.txtPickedLocation);

        btnConfirmLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double latit = mMarker.getPosition().latitude;
                double longi = mMarker.getPosition().longitude;

                Toast.makeText(LocationSearch.this, "lat: "+latit+" long: "+longi, Toast.LENGTH_SHORT).show();
            }
        });

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);


        mapFragment.getMapAsync(this);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(5.600259, -0.191749));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);

        if (mMarker != null) {
            mMarker.remove();
        }

         mMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(5.600259, -0.191749)));

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

                if (mMarker != null) {
                    mMarker.remove();
                }

               mMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
                mMarker.setDraggable(false);
                mMarker.setFlat(true);

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


    /**
     * this method requests location name using latlong parameter
     */
    private void requestLocationName(final double latitude, final double longitude) {

        String latLong = latitude + "," + longitude;

        String url = "https://plus.codes/api?address=" + latLong + "&ekey=" + getString(R.string.gmap_key) + "&email=brightnyamata@gmail.com";


        Ion.with(LocationSearch.this).load(url)
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {

                if (e == null) {

                    try {
                        JSONObject object = new JSONObject(result.getResult());

                        //getting the location name
                        JSONObject plus_code = object.getJSONObject("plus_code");
                        JSONObject local_address = plus_code.getJSONObject("locality");
                        String place_name = local_address.getString("local_address");

                        txtLocationName.setText(place_name);


//                        if(marker != null){
//                            marker.remove();
//                        }
//                        marker =  mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(place_name));

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                } else {
                    Log.d("ERROR", "not found");
                    e.printStackTrace();
                }

            }
        });

    }//END OF request location name

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {
        mMarker = marker;

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        mMarker = marker;

        LatLng latLng = mMarker.getPosition();
        requestLocationName(latLng.latitude, latLng.longitude);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
