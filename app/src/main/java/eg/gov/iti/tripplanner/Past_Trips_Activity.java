package eg.gov.iti.tripplanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import eg.gov.iti.tripplanner.adapters.PastTripAdapter;
import eg.gov.iti.tripplanner.adapters.TripAdapter;
import eg.gov.iti.tripplanner.model.Trip;

public class Past_Trips_Activity extends Activity {

    RecyclerView recyclerView;
    PastTripAdapter adapter;
    ArrayList<Trip> myList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = findViewById(R.id.recyclerView);
        myList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        adapter = new PastTripAdapter(this, myList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new PastTripAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
//                Intent intent = new Intent(MainActivity.this, TripDetailsActivity.class);
//                intent.putExtra("trip", myList.get(position));
//                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


}
