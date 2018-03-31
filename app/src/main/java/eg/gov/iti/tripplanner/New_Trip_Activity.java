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
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
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

import eg.gov.iti.tripplanner.adapters.AddNoteAdapter;
import eg.gov.iti.tripplanner.adapters.PlaceAutocompleteAdapter;
import eg.gov.iti.tripplanner.model.Trip;

public class New_Trip_Activity extends AppCompatActivity {
    private Button addNote;
    private AutoCompleteTextView tripFrom, tripTo;
    private EditText tripName, tripNotes;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private ListView notesListView;

    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String userId;
    private FirebaseUser user;
    private List<String> notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new__trip_);

        addNote = findViewById(R.id.add_note_button);
        tripName = findViewById(R.id.tripName);
        tripFrom = findViewById(R.id.tripFrom);
        tripTo = findViewById(R.id.tripTo);
        tripNotes = findViewById(R.id.notes);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        notesListView = findViewById(R.id.add_note_list_view);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        user = mAuth.getCurrentUser();
        userId = user.getUid();
        Toast.makeText(this, userId, Toast.LENGTH_LONG).show();
        // create auto complete Adapter
        LatLngBounds Lat_Lang_bounds = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
        GeoDataClient geoDataClient = Places.getGeoDataClient(this, null);

        PlaceAutocompleteAdapter placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this, geoDataClient, Lat_Lang_bounds, null);
        tripFrom.setAdapter(placeAutocompleteAdapter);
        tripTo.setAdapter(placeAutocompleteAdapter);

        notes = new ArrayList<>();

        final AddNoteAdapter noteAdapter = new AddNoteAdapter(getApplicationContext(), notes);
        notesListView.setAdapter(noteAdapter);

        notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                notes.remove(position);
                noteAdapter.notifyDataSetChanged();
            }
        });

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editTextNote = tripNotes.getText().toString().trim();
                if (!editTextNote.isEmpty()) {
                    notes.add(editTextNote);
                    tripNotes.setText("");
                    noteAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(New_Trip_Activity.this, "Can't add empty note", Toast.LENGTH_SHORT).show();
                }
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

    private void saveNewTrip() {
        String TName = tripName.getText().toString();
        String TFrom = tripFrom.getText().toString();
        String TTo = tripTo.getText().toString();

        Calendar calendar = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());
        Long unixTime = calendar.getTimeInMillis() / 1000;

        /// get lat and long
        Trip trip = new Trip();
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
        trip.setNotes(notes);
        if (TName.isEmpty() || TFrom.isEmpty() || TTo.isEmpty()) {
            Toast.makeText(New_Trip_Activity.this, "Some Fields are empty!", Toast.LENGTH_SHORT).show();
            return;

        } else {
            Toast.makeText(New_Trip_Activity.this, "Trip add successfully!", Toast.LENGTH_SHORT).show();
        }

        String tripId = myRef.push().getKey();
        trip.setFireBaseTripId(tripId);
        String userId2 = new String(userId);
        myRef.child("users").child(userId2).child(tripId).setValue(trip);
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
                saveNewTrip();
                return true;

            case R.id.action_cancel:
                //cancel action
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

