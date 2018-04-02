package eg.gov.iti.tripplanner;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import eg.gov.iti.tripplanner.model.Trip;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList points = null;
    PolylineOptions polylineOptions = null;
    ArrayList<Trip> tripsPast = null;

    DatabaseReference myRef;
    FirebaseDatabase mFirebaseDatabase;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private String userId;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);




        mapFragment.getMapAsync(this);


        tripsPast=getIntent().getParcelableArrayListExtra("pastTrips");





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


//        mMap.setMinZoomPreference(6f);
//        mMap.setMaxZoomPreference(14f);

        mMap.getUiSettings().setZoomControlsEnabled(true);


        for (Trip t : tripsPast){

            ArrayList points = new ArrayList();

            points.add(new LatLng(	t.getStartLat(), t.getStartLong()));
            points.add(new LatLng(		t.getEndLat(), t.getEndLong()));
            String url = getRequestedUrl((LatLng)points.get(0),(LatLng) points.get(1));
            TaskRequestConnection taskRequestConnection = new TaskRequestConnection();
            taskRequestConnection.execute(url);

        }






    }

    private String getRequestedUrl(LatLng origin, LatLng destination) {
        String str_Org = "origin=" + origin.latitude + "," + origin.longitude;
        String des_Org = "destination=" + destination.latitude +","+destination.longitude;
        String sensor = "sesnsor=false";
        String mode = "mode=driving";
        String param = str_Org+"&"+des_Org + "&" + sensor + "&" + mode;
        String output = "json";
        String url = "http://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        return url;

    }

    private String requestDestination(String reqUrl){
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection= null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();


            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null ){
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();



        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                    httpURLConnection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return responseString;
        }


    }


    public class TaskRequestConnection extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            responseString = requestDestination(strings[0]);

            return responseString;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String , Void,List<List<HashMap<String, String>>>>{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes= null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            ArrayList pointsRet = null;
            PolylineOptions polylineOptions = null;
            for (List<HashMap<String, String>> path :lists ){
                pointsRet = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path){
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));
                    pointsRet.add(new LatLng(lat, lon));

                }
                polylineOptions.addAll(pointsRet);
                polylineOptions.width(10);

                int [] colors = new int[5];
                colors[0] = Color.BLUE;
                colors[1] = Color.RED;
                colors[2] = Color.GREEN;
                colors[3] = Color.CYAN;
                colors[4] = Color.GRAY;





                polylineOptions.color(colors[new Random().nextInt((4 - 0) + 1) + 0]);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions != null){
                mMap.setMinZoomPreference(3.0f);
                mMap.setMaxZoomPreference(10.0f);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(	31.205753, 29.924526), 5);

                mMap.animateCamera(cameraUpdate);

                mMap.addPolyline(polylineOptions);
//                LatLngBounds loc = new LatLngBounds(new LatLng(	31.205753, 29.924526), new LatLng(	30.044281, 31.340002));
//
//
////                points.add(new LatLng(	30.044281, 31.340002));
////                points.add(new LatLng(		31.205753, 29.924526));
//                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds();
            }else {
                Toast.makeText(getApplicationContext(), "Location Not Found", Toast.LENGTH_SHORT).show();



            }

        }
    }
}
