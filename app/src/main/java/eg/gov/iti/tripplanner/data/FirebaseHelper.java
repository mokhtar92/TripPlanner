package eg.gov.iti.tripplanner.data;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import eg.gov.iti.tripplanner.model.Trip;

/**
 * Created by IbrahimDesouky on 4/2/2018.
 */

public class FirebaseHelper {

    private static FirebaseHelper firebaseHelper;

    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;

    //private ArrayList<TripModel> tripsList;

    private FirebaseHelper(){

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabase.setPersistenceEnabled(true);
        myRef = mFirebaseDatabase.getReference();

    }

    public static FirebaseHelper getInstance(){
        if(firebaseHelper==null){
            firebaseHelper=new FirebaseHelper();
        }
        return firebaseHelper;
    }

    public DatabaseReference getFirebaseRefrence(){
        return myRef;
    }



    public void addTrip(Trip trip, String userId){
        String tripId = myRef.push().getKey();
        trip.setFireBaseTripId(tripId);

        myRef.child("users").child(userId).child(tripId).setValue(trip);
    }



    public void updateTrip(Trip trip, String userId){
        myRef.child("users").child(userId).child(trip.getFireBaseTripId()).setValue(trip);
    }

    public void deleteTrip(Trip trip, String userId){
        myRef.child("users").child(userId).child(trip.getFireBaseTripId()).removeValue();
    }
//
}
