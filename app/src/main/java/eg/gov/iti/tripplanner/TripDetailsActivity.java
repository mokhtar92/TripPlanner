package eg.gov.iti.tripplanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import eg.gov.iti.tripplanner.model.Trip;

public class TripDetailsActivity extends AppCompatActivity {
    Intent intent;
    TextView tripName;
    TextView tripDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        intent = getIntent();
        if (intent != null) {
            Trip trip = intent.getParcelableExtra("trip");
            if (trip != null) {

            }
        }

    }
}
