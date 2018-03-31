package eg.gov.iti.tripplanner.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import eg.gov.iti.tripplanner.MainActivity;
import eg.gov.iti.tripplanner.New_Trip_Activity;
import eg.gov.iti.tripplanner.R;
import eg.gov.iti.tripplanner.TripReminderActivity;
import eg.gov.iti.tripplanner.editTrip;
import eg.gov.iti.tripplanner.model.Trip;
import eg.gov.iti.tripplanner.utils.TripManager;

/**
 * Created by IbrahimDesouky on 3/18/2018.
 */

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private Context myContext;
    private ArrayList<Trip> myList;
    private OnItemClickListener myListener;

    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String userId;
    private FirebaseUser user;


    public interface OnItemClickListener {
        void onItemClicked(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        myListener = listener;
    }

    public TripAdapter(Context myContext, ArrayList<Trip> myList) {
        this.myContext = myContext;
        this.myList = myList;
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        user = mAuth.getCurrentUser();
        userId = user.getUid();
    }

    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(myContext);
        View view = inflater.inflate(R.layout.card_view, null, false);

        return new TripViewHolder(view, myListener);
    }

    @Override
    public void onBindViewHolder(TripViewHolder holder, final int position) {
        final Trip trip = myList.get(position);

        Calendar c = Calendar.getInstance();
        Long unixTime = Long.parseLong(trip.getTripTime()) * 1000;
        c.setTimeInMillis(unixTime);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String test = sdf.format(unixTime).toString();

        holder.tripName.setText(trip.getTripName());
        holder.tripTime.setText(test);
        holder.tripDate.setText(day + "/" + month + "/" + year);
        holder.startName.setText(trip.getStartName());
        holder.endName.setText(trip.getEndName());
        holder.tripImageView.setImageResource(R.drawable.trip);
        holder.editTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Goto AddTripActivity in Edit Mode
            }
        });
        holder.deleteTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(myContext, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(myContext);
                }
                builder.setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this Trip?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                removeItem(trip);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
               // removeItem(trip);


                myRef.child("users").child(userId).child(trip.getFireBaseTripId()).removeValue();
            }
        });
        holder.startTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + myList.get(position).getEndLat() + "," + myList.get(position).getEndLong());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                myContext.startActivity(mapIntent);

            }
        });

        holder.editTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(myContext, editTrip.class);
                editIntent.putExtra("trip", myList.get(position));

                myContext.startActivity(editIntent);

            }
        });
    }

    private void removeItem(Trip t) {
        int itemPosition = myList.indexOf(t);
        myList.remove(t);

        //delete Scheduled trip
        Intent intent = new Intent(myContext, TripReminderActivity.class);
        int timeOfTrip = Integer.parseInt(t.getTripTime());
        TripManager.cancelScheduledTrip(myContext, timeOfTrip, intent);
        notifyItemRemoved(itemPosition);
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    class TripViewHolder extends RecyclerView.ViewHolder {

        View view;
        RelativeLayout startTrip;
        ImageView tripImageView, editTrip, deleteTrip;
        TextView tripName, tripTime, tripDate, startName, endName;


        public TripViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            view = itemView;

            tripImageView = itemView.findViewById(R.id.trip_image_view);
            tripName = itemView.findViewById(R.id.trip_name_text_view);
            tripTime = itemView.findViewById(R.id.trip_time_text_view);
            tripDate = itemView.findViewById(R.id.trip_date_text_view);
            startName = itemView.findViewById(R.id.trip_from_text_view);
            endName = itemView.findViewById(R.id.trip_to_text_view);
            startTrip = itemView.findViewById(R.id.start_trip_image_view);
            editTrip = itemView.findViewById(R.id.edit_trip_image_view);
            deleteTrip = itemView.findViewById(R.id.delete_trip_image_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClicked(position);
                        }
                    }
                }
            });
        }
    }
}
