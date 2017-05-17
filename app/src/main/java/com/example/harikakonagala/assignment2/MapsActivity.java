package com.example.harikakonagala.assignment2;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static android.R.attr.data;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    String BASE_URL = "";
    private GoogleMap mMap;
    private String email,full_name;
    private LatLng myLocation;
    private Handler myHandler;
    private LocationManager locationManager;
    private boolean flag;

    //private double latitude = 0.0;
    //private double longitude = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        BASE_URL = getString(R.string.baseUrl);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent i = getIntent();
        email = i.getStringExtra("email");
        full_name = i.getStringExtra("full_name");
        myHandler = new Handler();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (myLocation != null)
        {
            mMap.addMarker(new MarkerOptions().position(myLocation).title("Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        intervalTask();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(this);
        endIntervalTask();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        endIntervalTask();
        email = "";
        full_name = "";
    }

    private void endIntervalTask() {myHandler.removeCallbacks(checkInterval);}
    private void intervalTask() {checkInterval.run();}
    Runnable checkInterval = new Runnable() {
        @Override
        public void run() {
            try {
                if (myLocation != null) {
                    String[] data = new String[3];
                    data[0] = email;
                    data[1] = String.valueOf(myLocation.latitude);
                    data[2] = String.valueOf(myLocation.longitude);
                    FriendFinderTask friendsTask = new FriendFinderTask();
                    friendsTask.execute(data);
                }
            } finally {
                int interval = 5000;
                myHandler.postDelayed(checkInterval, interval);
            }
        }
    };

    private class FriendFinderTask extends AsyncTask<String, Integer, List<users>> {

        @Override
        protected List<users> doInBackground(String... params) {
            URL url;
            String response = "";
            String REQUEST_URL = BASE_URL + "findFriends.php";
            List<users> friends_nearby = new ArrayList<>();
            try
            {

                url = new URL(REQUEST_URL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                String requestJsonString = new JSONObject()
                        .put("email", params[0])
                        .put("latitude", params[1])
                        .put("longitude", params[2])
                        .toString();
                Log.d("REQUEST BODY : ", requestJsonString);
                writer.write(requestJsonString);

                writer.flush();
                writer.close();
                os.close();
                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    line = br.readLine();
                    while(line != null){
                        response += line;
                        line = br.readLine();
                    }

                    br.close();
                }
                Log.d("RESPONSE BODY: ", "Response is : " + response);
                JSONArray array = new JSONArray(response);
                if(array.length() > 0){
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject child = array.getJSONObject(i);
                        users user = new users(child.getString("email"),
                                child.getString("full_name"),
                                child.getDouble("latitude"),
                                child.getDouble("longitude"),
                                child.getString("last_active_time"));
                        friends_nearby.add(user);
                    }
                }
                conn.disconnect();

            }catch (IOException | JSONException e)
            {
                e.printStackTrace();
            }
            Log.d("RESPONSE :", response);
            return friends_nearby;
        }

        @Override
        protected void onPostExecute(List<users> friends_nearby) {
            super.onPostExecute(friends_nearby);

            mMap.clear();
            LatLng my_Loc;

            for (users friend : friends_nearby){
                Log.d("Locating: ", friend.getFull_name());
                my_Loc = new LatLng(friend.getLatitude(), friend.getLongitude());
                if (mMap != null) {
                    mMap.addMarker(new MarkerOptions().position(my_Loc).title(friend.getFull_name()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))).showInfoWindow();
                }
            }
            my_Loc = myLocation;
            if (myLocation!= null && myLocation.latitude!= 0) {
                mMap.addMarker(new MarkerOptions().position(my_Loc).title("my location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))).showInfoWindow();
                if(!flag) {
                    CameraPosition cp = new CameraPosition.Builder().target(my_Loc).zoom(15).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
                    flag = true;
                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        String latitude = String.valueOf(location.getLatitude());
        String longitude = String.valueOf(location.getLongitude());
        myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        String[] data = new String[3];
        data[0] = email;
        data[1] = latitude;
        data[2] = longitude;

        LocationUpdateTask updateUserLocation = new LocationUpdateTask();
        updateUserLocation.execute(data);

    }

    private class LocationUpdateTask extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... params) {
            URL url;
            String response = "";
            String REQUEST_URL = BASE_URL + "updateUsers.php";
            try
            {

                url = new URL(REQUEST_URL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                String requestJsonString = new JSONObject()
                        .put("email", params[0])
                        .put("latitude", params[1])
                        .put("longitude", params[2])
                        .toString();
                Log.d("REQUEST  : ", requestJsonString);
                writer.write(requestJsonString);

                writer.flush();
                writer.close();
                os.close();
                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    line = br.readLine();
                    while(line != null){
                        response += line;
                        line = br.readLine();
                    }

                    br.close();
                }
                else {
                    response="Error Registering";
                }
                conn.disconnect();

            }catch (IOException | JSONException e)
            {
                e.printStackTrace();
            }
            Log.d("RESPONSE :", response);
            return response;
        }
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


}

