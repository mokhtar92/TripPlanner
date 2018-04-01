package eg.gov.iti.tripplanner;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import eg.gov.iti.tripplanner.adapters.AddNoteAdapter;
import eg.gov.iti.tripplanner.model.Trip;
import eg.gov.iti.tripplanner.utils.Definitions;
import eg.gov.iti.tripplanner.utils.TripManager;

public class New_Trip_Activity extends AppCompatActivity {
    private Button addNote;
    // private AutoCompleteTextView tripFrom, tripTo;
    private EditText tripName, tripNotes;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private ListView notesListView;
    private RadioGroup radioGroup;

    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String userId;
    private FirebaseUser user;
    private List<String> notes;
    String TFrom = "";
    String TTo = "";
    LatLng fromLatLng;
    LatLng toLatLng;
    Place placeCheckFrom;
    Place placeCheckTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new__trip_);

        addNote = findViewById(R.id.add_note_button);
        tripName = findViewById(R.id.tripName);
        radioGroup = findViewById(R.id.trip_type_radio_group);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("TAG1", "Place: " + place.getName());
                TFrom = place.getName().toString();
                fromLatLng = place.getLatLng();
                placeCheckFrom = place;
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG1", "An error occurred: " + status);
            }
        });
        PlaceAutocompleteFragment autocompleteFragment2 = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment2);

        autocompleteFragment2.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("TAG1", "Place: " + place.getName());

                TTo = place.getName().toString();
                toLatLng = place.getLatLng();
                placeCheckTo = place;
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG1", "An error occurred: " + status);
            }
        });
        tripNotes = findViewById(R.id.notes);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        notesListView = findViewById(R.id.add_note_list_view);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        user = mAuth.getCurrentUser();
        userId = user.getUid();

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

    ///////////////////////***********saving trip object***********************////////////////////
    private void saveNewTrip() {

        Trip trip = new Trip();
        String TName = tripName.getText().toString();
        trip.setTripStatus(Definitions.STATUS_UPCOMING);
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.single_trip_radio_btn:
                trip.setTripType(Definitions.ONE_WAY_TRIP);
                break;

            case R.id.round_trip_radio_btn:
                trip.setTripType(Definitions.ROUND_TRIP);
                break;
        }

        Calendar calendar = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
        Long unixTime = calendar.getTimeInMillis() / 1000;
        /// get lat and long from google places autocomplete Api
        trip.setStartLong(fromLatLng.longitude);
        trip.setStartLat(fromLatLng.latitude);
        trip.setEndLong(toLatLng.longitude);
        trip.setEndLat(toLatLng.latitude);

        trip.setTripName(TName);
        trip.setStartName(TFrom);
        trip.setEndName(TTo);
        trip.setTripTime(unixTime.toString());

        trip.setNotes(notes);
        if (TName.isEmpty() || TFrom.isEmpty() || TTo.isEmpty()) {
            Toast.makeText(New_Trip_Activity.this, "Some fields are empty!", Toast.LENGTH_SHORT).show();

        } else {
            String tripId = myRef.push().getKey();
            trip.setFireBaseTripId(tripId);
            String userId2 = new String(userId);
            myRef.child("users").child(userId2).child(tripId).setValue(trip);

            //Set alarm here
            Intent intent = new Intent(getApplicationContext(), TripReminderActivity.class);
            intent.putExtra("reminderTrip", trip);
            int currentTime = (int) (System.currentTimeMillis() / 1000);
            int timeOfTrip = Integer.parseInt(trip.getTripTime());
            TripManager.scheduleNewTrip(getApplicationContext(), timeOfTrip, intent, timeOfTrip - currentTime);

            Toast.makeText(New_Trip_Activity.this, "Trip added successfully!", Toast.LENGTH_SHORT).show();
            finish();
        }
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
                if (!isNetworkConnected()) {
                    Toast.makeText(this, "Internet connection is needed!", Toast.LENGTH_SHORT).show();

                } else if (placeCheckFrom == null || placeCheckTo == null) {
                    Toast.makeText(this, "failed to locate place, try again!", Toast.LENGTH_SHORT).show();
                    return false;

                } else {
                    saveNewTrip();
                    return true;
                }

            case R.id.action_cancel:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

