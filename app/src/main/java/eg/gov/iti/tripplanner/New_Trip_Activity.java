package eg.gov.iti.tripplanner;

import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import eg.gov.iti.tripplanner.model.Trip;

public class New_Trip_Activity extends AppCompatActivity {
    Button saveButton;
    EditText tripName,tripFrom,tripTo,tripNotes;
    DatePicker datePicker;
    TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new__trip_);
        saveButton=findViewById(R.id.saveTripButton);
        tripName=findViewById(R.id.tripName);
        tripFrom=findViewById(R.id.tripFrom);
        tripTo=findViewById(R.id.tripTo);
        tripNotes=findViewById(R.id.notes);
        datePicker=findViewById(R.id.datePicker);
        timePicker=findViewById(R.id.timePicker);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String TName = tripName.getText().toString();
                String TFrom = tripFrom.getText().toString();
                String TTo = tripTo.getText().toString();
                String TNotes= tripNotes.getText().toString();
                Trip trip=new Trip();
                List noteList=new ArrayList();
                noteList.add(TNotes);
                Calendar calendar = new GregorianCalendar(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth(),timePicker.getCurrentHour(),timePicker.getCurrentMinute());
                Long unixTime=calendar.getTimeInMillis();
                ///// get lat and long

                Geocoder coder = new Geocoder(New_Trip_Activity.this);
                List<Address> address;

                try {
                    address = coder.getFromLocationName(TFrom,5);

                if (address==null) {

                    }
                    Address location=address.get(0);
                    System.out.println("____*****************_________");
                    trip.setStartLat(location.getLatitude());
                    trip.setStartLong(location.getLongitude());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    address = coder.getFromLocationName(TTo,5);

                    if (address==null) {

                    }
                    Address location=address.get(0);
                    trip.setEndLat(location.getLatitude());
                    trip.setEndLong(location.getLongitude());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                trip.setTripName(TName);
                trip.setStartName(TFrom);
                trip.setEndName(TTo);
                trip.setNotes(noteList);

            }

        });
    }
}
