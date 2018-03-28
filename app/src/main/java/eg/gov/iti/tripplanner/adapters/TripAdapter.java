package eg.gov.iti.tripplanner.adapters;

import android.content.Context;
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

/**
 * Created by IbrahimDesouky on 3/18/2018.
 */

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private Context myContext;
    private ArrayList<Trip> myList;
    private OnItemClickListener myListener;

    public interface OnItemClickListener {
        void onItemClicked(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        myListener = listener;
    }

    public TripAdapter(Context myContext, ArrayList<Trip> myList) {
        this.myContext = myContext;
        this.myList = myList;
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
        holder.tripName.setText(trip.getTripName());
        holder.tripTime.setText(trip.getTripTime());
        holder.tripDate.setText(trip.getTripDate());
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
                removeItem(trip);
            }
        });
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
