package eg.gov.iti.tripplanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import eg.gov.iti.tripplanner.model.Trip;

public class AddTrip extends AppCompatActivity {
    Intent intent;
    TextView tripName;
    TextView tripDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        tripDate=findViewById(R.id.add_trip_time);
        tripName=findViewById(R.id.add_trip_name);
        intent=getIntent();
        if (intent!=null){

            Trip trip=intent.getParcelableExtra("trip");
            tripName.setText(trip.getTripName());
            tripDate.setText(trip.getTripDate());

        }

    }
}
