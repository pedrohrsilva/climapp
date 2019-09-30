package com.example.climaapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private static final int MY_PERMISSIONS_REQUEST = 144563;
    private String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET};
    private GoogleMap map;
    private Marker marker;
    private Boolean toggle = true;
    RequestQueue queue;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestMultiplePermissions();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        queue = Volley.newRequestQueue(this);

        Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                requestCitiesFromOpenWeather(marker.getPosition());
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

    private boolean isPermissionGranted(String permission){
        return  ContextCompat.checkSelfPermission(this, permission ) == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestMultiplePermissions(){
        List<String> remainingPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (!isPermissionGranted(permission)) {
                remainingPermissions.add(permission);
            }
        }
        if(remainingPermissions.size()>0) {
            requestPermissions(remainingPermissions.toArray(new String[remainingPermissions.size()]), MY_PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST:
                if (grantResults.length > 0) {
                    boolean isAllGranted = true;
                    for (int permission:grantResults) {
                        isAllGranted = isAllGranted && permission == PackageManager.PERMISSION_GRANTED;
                    }
                    if(!isAllGranted){
                        Toast toast = Toast.makeText(this, "Por favor, certifique-se de dar as permissões a este App.", Toast.LENGTH_LONG);
                        toast.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                System.exit(-1);
                            }
                        }, 5000);
                    }
                    }



        }
    }
}
