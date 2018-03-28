package eg.gov.iti.tripplanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import eg.gov.iti.tripplanner.adapters.TripAdapter;
import eg.gov.iti.tripplanner.model.Trip;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TripAdapter adapter;
    ArrayList<Trip> myList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNewTripActivity = new Intent(MainActivity.this, New_Trip_Activity.class);
                startActivity(addNewTripActivity);
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        myList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Trip trip = new Trip();
        trip.setEndName("smartVillage");
        trip.setTripName("Gahim fel ITI");
        trip.setStartName("Giza");
        trip.setTripDate("21/3/2018");
        trip.setTripTime("10:45 am");
        myList.add(trip);

        trip = new Trip();
        trip.setEndName("smartVillage");
        trip.setTripName("Gahim fel ITI");
        trip.setStartName("Giza");
        trip.setTripDate("21/3/2018");
        trip.setTripTime("10:45 am");
        myList.add(trip);

        trip = new Trip();
        trip.setEndName("smartVillage");
        trip.setTripName("Gahim fel ITI");
        trip.setStartName("Giza");
        trip.setTripDate("21/3/2018");
        trip.setTripTime("10:45 am");
        myList.add(trip);


        adapter = new TripAdapter(this, myList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new TripAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                Intent intent = new Intent(MainActivity.this, TripDetailsActivity.class);
                intent.putExtra("trip", myList.get(position));
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sync) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
