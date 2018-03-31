package eg.gov.iti.tripplanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import eg.gov.iti.tripplanner.adapters.TripAdapter;
import eg.gov.iti.tripplanner.model.Trip;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TripAdapter adapter;
    ArrayList<Trip> myList;

    DatabaseReference myRef;
    FirebaseDatabase mFirebaseDatabase;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private String userId;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mFirebaseDatabase=FirebaseDatabase.getInstance();

        //mFirebaseDatabase.getReference("users").keepSynced(true);
        mAuth=FirebaseAuth.getInstance();

        myRef=mFirebaseDatabase.getReference();


        user=mAuth.getCurrentUser();
        if (user==null){

            Intent intent=new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
        userId=user.getUid();


        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override

            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {



                    toastMessage("Successfully signed in with: " + user.getEmail());

                } else {


                    toastMessage("Successfully signed out.");

                }

                // ...

            }

        };


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

        myRef.child("users").child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Trip trip=dataSnapshot.getValue(Trip.class);
                myList.add(trip);
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
            Intent startDetailsActivity = new Intent(MainActivity.this, TripDetailsActivity.class);
            startActivity(startDetailsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toastMessage(String message){

        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();

    }

    @Override

    public void onStart() {

        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override

    public void onStop() {

        super.onStop();

        if (mAuthListener != null) {

            mAuth.removeAuthStateListener(mAuthListener);

        }

    }
}
