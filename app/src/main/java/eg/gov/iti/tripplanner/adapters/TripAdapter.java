package eg.gov.iti.tripplanner.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import eg.gov.iti.tripplanner.AddTrip;
import eg.gov.iti.tripplanner.R;
import eg.gov.iti.tripplanner.model.Trip;

/**
 * Created by IbrahimDesouky on 3/18/2018.
 */

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder>{
    private Context myContext;
    private OnItemClickListener myListener;
    public  interface  OnItemClickListener{

        void onItemClicked(int position);
    }
    public  void setOnItemClickListener(OnItemClickListener listener){


        myListener=listener;
    }

    public TripAdapter(Context myContext, ArrayList<Trip> myList) {
        this.myContext = myContext;
        this.myList = myList;
    }

    ArrayList<Trip>myList;

    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(myContext);
        View view=inflater.inflate(R.layout.card_view,null);
        TripViewHolder tripViewHolder=new TripViewHolder(view,myListener);
        return tripViewHolder;
    }

    @Override
    public void onBindViewHolder(TripViewHolder holder, final int position) {
        final Trip trip=myList.get(position);
        holder.tripName.setText(trip.getTripName());
        holder.tripTime.setText(trip.getTripTime());
        holder.tripDay.setText("friday");
        holder.tripDate.setText(trip.getTripDate());
        holder.startName.setText(trip.getStartName());
        holder.endName.setText(trip.getEndName());
        holder.imageView.setImageResource(R.drawable.trip);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(myContext, "item deleted on position "+position, Toast.LENGTH_SHORT).show();
                removeItem(trip);
            }
        });


    }

    private void removeItem(Trip t) {
        int itemPosition=myList.indexOf(t);
        myList.remove(t);
        notifyItemRemoved(itemPosition);

    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    class TripViewHolder extends RecyclerView.ViewHolder{
        View view;

        ImageView imageView;
        TextView tripName,tripTime,tripDay,tripDate,startName,endName;
        Button start,delete;
        public TripViewHolder(View itemView , final OnItemClickListener listener) {
            super(itemView);
            view=itemView;

            imageView=itemView.findViewById(R.id.imageView);
            tripName=itemView.findViewById(R.id.tripName);
            tripTime=itemView.findViewById(R.id.tripTime);
            tripDay=itemView.findViewById(R.id.tripDay);
            tripDate=itemView.findViewById(R.id.tripDate);
            startName=itemView.findViewById(R.id.startName);
            endName=itemView.findViewById(R.id.endName);
            start=itemView.findViewById(R.id.startButton);
            delete=itemView.findViewById(R.id.deleteButton);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){

                        int position =getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){

                            listener.onItemClicked(position);
                        }
                    }

                }
            });




        }
    }
}
