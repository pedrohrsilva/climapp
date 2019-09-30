package com.example.climaapp;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.climaapp.model.City;
import com.example.climaapp.model.CityJSONParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.inlocomedia.android.core.permissions.PermissionResult;
import com.inlocomedia.android.core.permissions.PermissionsListener;
import com.inlocomedia.android.engagement.InLocoEngagement;
import com.inlocomedia.android.engagement.InLocoEngagementOptions;
import com.inlocomedia.android.engagement.request.FirebasePushProvider;
import com.inlocomedia.android.engagement.request.PushProvider;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACTIVITY_RECOGNITION, Manifest.permission.RECEIVE_BOOT_COMPLETED};
    private GoogleMap map;
    private Marker marker;
    private Boolean toggle = true;
    RequestQueue queue;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPermissions();
        fireBaseConfig();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        queue = Volley.newRequestQueue(this);

        InLocoEngagementOptions options = InLocoEngagementOptions.getInstance(this);
        options.setApplicationId(getString(R.string.inloco_key));
        options.setLogEnabled(true);
        options.setDevelopmentDevices("D5EF1C61A416BEB6B15AFDF1C7FBD3F5");
        InLocoEngagement.init(this, options);
        InLocoEngagement.setUserId(this, "TEST_USER");

        Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                requestCitiesFromOpenWeather(marker.getPosition());
            }
        });
    }

    private void fireBaseConfig(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FIREBASE_TEST", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        final PushProvider pushProvider = new FirebasePushProvider.Builder()
                                .setFirebaseToken(token)
                                .build();
                        InLocoEngagement.setPushProvider(getActivity(), pushProvider);
                        Log.d("InLocoMedia", "Token: "+ token);
                    }
                });
    }

    private void openCityListActivity(){
        Intent intent = new Intent(this, CityListActivity.class);
        startActivity(intent);
    }

    public void requestCitiesFromOpenWeather(LatLng latLng) {
        String APIKey = getString(R.string.openweather_key);
        String requestURL = "http://api.openweathermap.org/data/2.5/find?lat=%.2f&lon=%.2f&cnt=15&APPID=%s&units=metric";
        requestURL = String.format(requestURL, latLng.latitude, latLng.longitude, APIKey);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            ArrayList<City> cityList = (ArrayList<City>) CityJSONParser.getCityList(response);
                            Singleton.getSingleton().setCityList(cityList);
                            openCityListActivity();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        });
        queue.add(stringRequest);
    }

    public MainActivity getActivity(){
        return this;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng BeloHorizonte = new LatLng(-19.865961, -43.971067);
        if(marker == null) {
            marker = map.addMarker( new MarkerOptions().title("Clima aqui perto").position(BeloHorizonte));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(BeloHorizonte, 10));
            map.setOnMapClickListener(this);
            marker.showInfoWindow();
        }
    }

    @Override
    public void onMapClick(final LatLng latLng) {
        toggle = !toggle;
        Log.d("DEBUG", "Latitude: "+latLng.toString()+ " toggle: "+ toggle);
        marker.setPosition(latLng);
        marker.showInfoWindow();
    }

    private void getPermissions(){
        final boolean askIfDenied = true; // Will prompt the user if he has previously denied the permission

        InLocoEngagement.requestPermissions(this, permissions, askIfDenied, new PermissionsListener() {

            @Override
            public void onPermissionRequestCompleted(final HashMap<String, PermissionResult> authorized) {
                if (authorized.get(Manifest.permission.ACCESS_FINE_LOCATION).isAuthorized()) {
                    // Permission enabled
                }
            }
        });
    }


}
