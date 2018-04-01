package eg.gov.iti.tripplanner;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
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

public class editTrip extends AppCompatActivity {
    Button saveButton;
    EditText tripName, tripNotes;
    DatePicker datePicker;
    TimePicker timePicker;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String userId;
    private FirebaseUser user;
    Trip trip;
    private ListView notesListView;
    private List<String> notes;
    private Button addNote;
    String TFrom = "";
    String TTo = "";
    LatLng fromLatLng;
    LatLng toLatLng;
    CheckBox doneCheckbox;
    int tripStatus= Definitions.STATUS_UPCOMING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        user = mAuth.getCurrentUser();
        userId = user.getUid();
        Toast.makeText(this, userId, Toast.LENGTH_LONG).show();
        doneCheckbox= findViewById(R.id.doneCheckbox);
        doneCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                   tripStatus=Definitions.STATUS_DONE;
                    Log.i("TAG1", "status : " + tripStatus);

                }
                else {
                   tripStatus=Definitions.STATUS_UPCOMING;
                }
            }
        });
        addNote = findViewById(R.id.edit_note_button);
        notesListView = findViewById(R.id.edit_note_list_view);
        tripName = findViewById(R.id.edit_tripName);
        final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("TAG1", "Place: " + place.getName());
                TFrom = place.getName().toString();
                fromLatLng = place.getLatLng();

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

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG1", "An error occurred: " + status);
            }
        });
        tripNotes = findViewById(R.id.edit_notes);
        datePicker = findViewById(R.id.edit_datePicker);
        timePicker = findViewById(R.id.edit_timePicker);
        Intent editIntent = getIntent();
        trip = new Trip();
        if (editIntent != null) {
            trip = (Trip) editIntent.getParcelableExtra("trip");
            tripName.setText(trip.getTripName());
            TFrom = trip.getStartName();
            autocompleteFragment.setText(TFrom);
            TTo = trip.getEndName();
            autocompleteFragment2.setText(TTo);
            fromLatLng = new LatLng(trip.getStartLat(), trip.getStartLong());
            toLatLng = new LatLng(trip.getEndLat(), trip.getEndLong());

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(Long.parseLong(trip.getTripTime()) * 1000);
            System.out.println("********************************************");
            System.out.println(c.getTime());
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            datePicker.updateDate(year, month, day);
            timePicker.setCurrentHour(c.getTime().getHours());
            timePicker.setCurrentMinute(c.getTime().getMinutes());


            notes = trip.getNotes();

            if (notes == null) {
                notes = new ArrayList<>();
                notes.add("No notes to display");
            }

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
                        Toast.makeText(editTrip.this, "Can't add empty note", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }


    private void saveEditingTrip() {


        String TName = tripName.getText().toString();
        trip.setNotes(notes);
        Calendar calendar = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
        Long unixTime = calendar.getTimeInMillis() / 1000;
        trip.setTripName(TName);
        trip.setStartName(TFrom);
        trip.setEndName(TTo);
        trip.setTripTime(unixTime.toString());
        /// get lat and long from google places autocomplete Api
        trip.setStartLong(fromLatLng.longitude);
        trip.setStartLat(fromLatLng.latitude);
        trip.setEndLong(toLatLng.longitude);
        trip.setEndLat(toLatLng.latitude);
        trip.setTripStatus(tripStatus);

        if (TName.isEmpty() || TFrom.isEmpty() || TTo.isEmpty()) {
            Toast.makeText(editTrip.this, "Some fields are empty!", Toast.LENGTH_SHORT).show();
            return;

        } else {
            myRef.child("users").child(userId).child(trip.getFireBaseTripId()).setValue(trip);
            //Set alarm here
            Intent intent = new Intent(getApplicationContext(), TripReminderActivity.class);
            intent.putExtra("reminderTrip", trip);
            int currentTime = (int) (System.currentTimeMillis() / 1000);
            int timeOfTrip = Integer.parseInt(trip.getTripTime());

            TripManager.scheduleNewTrip(getApplicationContext(), timeOfTrip, intent, timeOfTrip - currentTime);
            Toast.makeText(editTrip.this, "Trip updated successfully!", Toast.LENGTH_SHORT).show();
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
                //save action
                saveEditingTrip();
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


