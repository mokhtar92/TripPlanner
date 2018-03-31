package eg.gov.iti.tripplanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

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
                tripDate.setText(trip.getTripDate());
                tripTime.setText(trip.getTripTime());
                tripName.setText(trip.getTripName());
                tripFrom.setText(trip.getStartName());
                tripTo.setText(trip.getEndName());

                List<String> notes = trip.getNotes();
                for (String note : notes) {
                    tripNotes.append("-" + note.concat("\n"));
                }
            }
        }

    }
}
