package eg.gov.iti.tripplanner;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;


import eg.gov.iti.tripplanner.adapters.AddNoteAdapter;
import eg.gov.iti.tripplanner.adapters.PlaceAutocompleteAdapter;
import eg.gov.iti.tripplanner.model.Trip;
public class editTrip extends AppCompatActivity {
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
    Trip trip;
    private ListView notesListView;
    private List<String> notes;
    private Button addNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);
        mAuth= FirebaseAuth.getInstance();
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        myRef=mFirebaseDatabase.getReference();
        user=mAuth.getCurrentUser();
        userId=user.getUid();
        Toast.makeText(this, userId, Toast.LENGTH_LONG).show();

        addNote = findViewById(R.id.edit_note_button);
        notesListView = findViewById(R.id.edit_note_list_view);
        tripName = findViewById(R.id.edit_tripName);
        tripFrom = findViewById(R.id.edit_tripFrom);
        tripTo = findViewById(R.id.edit_tripTo);
        tripNotes = findViewById(R.id.edit_notes);
        datePicker = findViewById(R.id.edit_datePicker);
        timePicker = findViewById(R.id.edit_timePicker);
        Intent editIntent=getIntent();
        trip=new Trip();
        if (editIntent!=null){
            trip=(Trip) editIntent.getParcelableExtra("trip");
            tripName.setText(trip.getTripName());
            tripFrom.setText(trip.getStartName());
            tripTo.setText(trip.getEndName());


//            Calendar calendar=Calendar.getInstance();
//            calendar.setTimeZone(TimeZone.getTimeZone("GMT+2"));
//            calendar.setTimeInMillis(Long.parseLong(trip.getTripTime())*1000);

            Date date = new Date(Long.parseLong(trip.getTripTime())*1000);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
//            DateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
//            Toast.makeText(this,  formatter.format(calendar.getTime()), Toast.LENGTH_LONG).show();
            datePicker.updateDate(calendar.getTime().getYear(),calendar.getTime().getMonth(),calendar.getTime().getDay());
            timePicker.setCurrentHour(calendar.getTime().getHours());
            timePicker.setCurrentMinute(calendar.getTime().getMinutes());



            notes = trip.getNotes();

            if (notes == null){
                notes=new ArrayList<>();
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



        // create auto complete Adapter
        LatLngBounds Lat_Lang_bounds = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
        GeoDataClient geoDataClient = Places.getGeoDataClient(this, null);

        PlaceAutocompleteAdapter placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this, geoDataClient, Lat_Lang_bounds, null);
        tripFrom.setAdapter(placeAutocompleteAdapter);
        tripTo.setAdapter(placeAutocompleteAdapter);

     //   saveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String TName = tripName.getText().toString();
//                String TFrom = tripFrom.getText().toString();
//                String TTo = tripTo.getText().toString();
////                String TNotes = tripNotes.getText().toString();
//                Trip trip = new Trip();
//                List noteList = new ArrayList();
////                noteList.add(TNotes);
//                Calendar calendar = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());
//
//
//                trip.setStartLong(30.067401);
//                trip.setStartLat(30.067401);
//                trip.setEndLat(30.067401);
//                trip.setEndLong(31.026179);
//                trip.setTripName(TName);
//                trip.setStartName(TFrom);
//                trip.setEndName(TTo);
//                trip.setNotes(noteList);
//                if (TName.isEmpty() || TFrom.isEmpty() || TTo.isEmpty()) {
//                    Toast.makeText(editTrip.this, "Some Fields are empty!", Toast.LENGTH_SHORT).show();
//                    return;
//
//                } else {
//                    Toast.makeText(editTrip.this, "Trip add successfully!", Toast.LENGTH_SHORT).show();
//                }
//
//                String tripId=myRef.push().getKey();
//                trip.setFireBaseTripId(tripId);
//                String userId2=new String(userId);
//                myRef.child("users").child(userId2).child(tripId).setValue(trip);
//
//
//            }
//
//        });
    }

    protected Address getLat_Lang(String place) {
        Geocoder coder = new Geocoder(editTrip.this);
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


