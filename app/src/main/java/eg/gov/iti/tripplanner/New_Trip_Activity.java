package eg.gov.iti.tripplanner;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import eg.gov.iti.tripplanner.adapters.PlaceAutocompleteAdapter;
import eg.gov.iti.tripplanner.model.Trip;

public class New_Trip_Activity extends AppCompatActivity {
    Button saveButton;
    AutoCompleteTextView tripFrom, tripTo;
    EditText tripName, tripNotes;
    DatePicker datePicker;
    TimePicker timePicker;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String userId;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new__trip_);
        saveButton = findViewById(R.id.saveTripButton);
        tripName = findViewById(R.id.tripName);
        tripFrom = findViewById(R.id.tripFrom);
        tripTo = findViewById(R.id.tripTo);
        tripNotes = findViewById(R.id.notes);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);

        mAuth= FirebaseAuth.getInstance();
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        myRef=mFirebaseDatabase.getReference();
        user=mAuth.getCurrentUser();
        userId=user.getUid();
        Toast.makeText(this, userId, Toast.LENGTH_LONG).show();
        // create auto complete Adapter
        LatLngBounds Lat_Lang_bounds = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
        GeoDataClient geoDataClient = Places.getGeoDataClient(this, null);
//        // check network connection
//        if (isNetworkConnected()) {
//            Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "please check your network connection", Toast.LENGTH_SHORT).show();
//        }
        PlaceAutocompleteAdapter placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this, geoDataClient, Lat_Lang_bounds, null);
        tripFrom.setAdapter(placeAutocompleteAdapter);
        tripTo.setAdapter(placeAutocompleteAdapter);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String TName = tripName.getText().toString();
                String TFrom = tripFrom.getText().toString();
                String TTo = tripTo.getText().toString();
//                String TNotes = tripNotes.getText().toString();
                Trip trip = new Trip();
                List noteList = new ArrayList();
//                noteList.add(TNotes);
                Calendar calendar = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                Long unixTime = calendar.getTimeInMillis() / 1000;
                /// get lat and long
               // Address fromAddress = getLat_Lang(TFrom);
                //trip.setStartLong(fromAddress.getLongitude());
                trip.setStartLong(30.067401);
                //trip.setStartLat(fromAddress.getLatitude());
                trip.setStartLat(30.067401);
                //Address toAddress = getLat_Lang(TTo);
                //trip.setEndLong(toAddress.getLongitude());
                //trip.setEndLat(toAddress.getLatitude());

                trip.setEndLat(30.067401);
                trip.setEndLong(31.026179);
                trip.setTripName(TName);
                trip.setStartName(TFrom);
                trip.setEndName(TTo);
                trip.setNotes(noteList);
                if (TName.isEmpty() || TFrom.isEmpty() || TTo.isEmpty()) {
                    Toast.makeText(New_Trip_Activity.this, "Some Fields are empty!", Toast.LENGTH_SHORT).show();
                    return;

                } else {
                    Toast.makeText(New_Trip_Activity.this, "Trip add successfully!", Toast.LENGTH_SHORT).show();
                }

                String tripId=myRef.push().getKey();
                trip.setFireBaseTripId(tripId);
                String userId2=new String(userId);
                myRef.child("users").child(userId2).child(tripId).setValue(trip);


            }

        });
    }

    protected Address getLat_Lang(String place) {
        Geocoder coder = new Geocoder(New_Trip_Activity.this);
        List<Address> address;
        Address location = null;

        try {
            address = coder.getFromLocationName(place, 5);

            if (address == null) {

            }
            location = address.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return location;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                //save action
                return true;

            case R.id.action_cancel:
                //cancel action
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //network connection func
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    //get only first name of location
    private String getShortAddress(String detailedAddress) {

        if (detailedAddress.contains(",")) {
            String[] splitAddress = detailedAddress.split(",");
            return splitAddress[0];

        } else {
            return detailedAddress;
        }
    }
}

