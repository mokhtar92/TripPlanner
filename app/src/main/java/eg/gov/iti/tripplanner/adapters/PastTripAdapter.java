package eg.gov.iti.tripplanner.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import eg.gov.iti.tripplanner.R;
import eg.gov.iti.tripplanner.model.Trip;

public class PastTripAdapter extends RecyclerView.Adapter<PastTripAdapter.TripViewHolder> {

private Context myContext;
private ArrayList<Trip> myList;
private OnItemClickListener myListener;

public interface OnItemClickListener {
    void onItemClicked(int position);
}

    public void setOnItemClickListener(PastTripAdapter.OnItemClickListener listener) {
        myListener = listener;
    }

    public PastTripAdapter(Context myContext, ArrayList<Trip> myList) {
        this.myContext = myContext;
        this.myList = myList;
    }

    @Override
    public PastTripAdapter.TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(myContext);
        View view = inflater.inflate(R.layout.card_view_for_past_trips, null, false);

        return new PastTripAdapter.TripViewHolder(view, myListener);
    }

    @Override
    public void onBindViewHolder(PastTripAdapter.TripViewHolder holder, final int position) {
        final Trip trip = myList.get(position);
        holder.tripName.setText(trip.getTripName());
        holder.tripTime.setText(trip.getTripTime());
        holder.tripDate.setText(trip.getTripDate());
        holder.startName.setText(trip.getStartName());
        holder.endName.setText(trip.getEndName());
        holder.tripImageView.setImageResource(R.drawable.trip_upcoming);
    }

    private void removeItem(Trip t) {
        int itemPosition = myList.indexOf(t);
        myList.remove(t);
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

    public TripViewHolder(View itemView, final PastTripAdapter.OnItemClickListener listener) {
        super(itemView);
        view = itemView;

        tripImageView = itemView.findViewById(R.id.trip_image_view_past);
        tripName = itemView.findViewById(R.id.trip_name_text_view_past);
        tripTime = itemView.findViewById(R.id.trip_time_text_view_past);
        tripDate = itemView.findViewById(R.id.trip_date_text_view_past);
        startName = itemView.findViewById(R.id.trip_from_text_view_past);
        endName = itemView.findViewById(R.id.trip_to_text_view_past);
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
