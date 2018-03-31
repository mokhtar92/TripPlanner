package eg.gov.iti.tripplanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import eg.gov.iti.tripplanner.model.Trip;

public class TripDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        TextView tripDate, tripTime, tripName, tripFrom, tripTo, tripNotes;

        tripDate = findViewById(R.id.details_trip_date);
        tripTime = findViewById(R.id.details_trip_time);
        tripName = findViewById(R.id.details_trip_name);
        tripFrom = findViewById(R.id.details_trip_from);
        tripTo = findViewById(R.id.details_trip_to);
        tripNotes = findViewById(R.id.details_trip_notes);

        Intent intent = getIntent();
        if (intent != null) {
            Trip trip = intent.getParcelableExtra("tripDetails");
            if (trip != null) {

                long unixTime = Long.parseLong(trip.getTripTime());
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(unixTime * 1000);

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                String test = sdf.format(unixTime).toString();

                tripDate.setText(day + "/" + month + "/" + year);
                tripTime.setText(test);
                tripName.setText(trip.getTripName());
                tripFrom.setText(trip.getStartName());
                tripTo.setText(trip.getEndName());

                List<String> notes = trip.getNotes();
                if (notes != null) {
                    for (String note : notes) {
                        tripNotes.append("-" + note.concat("\n"));
                    }
                }

            }
        }

    }
}
