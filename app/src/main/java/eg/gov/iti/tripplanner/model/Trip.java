package eg.gov.iti.tripplanner.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IbrahimDesouky on 3/18/2018.
 */

public class Trip implements Parcelable {

    private String tripName;
    private int tripId;
    private int tripStatus;
    private int tripType;

    private String fireBaseTripId;

    public String getFireBaseTripId() {
        return fireBaseTripId;
    }

    public void setFireBaseTripId(String fireBaseTripId) {
        this.fireBaseTripId = fireBaseTripId;
    }

    private String startName;
    private double startLong;
    private double startLat;

    private String endName;
    private double endLong;
    private double endLat;

    private List<String> notes;

    private String tripDate;
    private String tripTime;


    public Trip() {
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public int getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(int tripStatus) {
        this.tripStatus = tripStatus;
    }

    public int getTripType() {
        return tripType;
    }

    public void setTripType(int tripType) {
        this.tripType = tripType;
    }

    public String getStartName() {
        return startName;
    }

    public void setStartName(String startName) {
        this.startName = startName;
    }

    public double getStartLong() {
        return startLong;
    }

    public void setStartLong(double startLong) {
        this.startLong = startLong;
    }

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public String getEndName() {
        return endName;
    }

    public void setEndName(String endName) {
        this.endName = endName;
    }

    public double getEndLong() {
        return endLong;
    }

    public void setEndLong(double endLong) {
        this.endLong = endLong;
    }

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public String getNotes() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < notes.size(); i++) {
            builder.append(notes.get(i).concat(","));
        }

        return builder.toString();
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }

    public String getTripDate() {
        return tripDate;
    }

    public void setTripDate(String tripDate) {
        this.tripDate = tripDate;
    }

    public String getTripTime() {
        return tripTime;
    }

    public void setTripTime(String tripTime) {
        this.tripTime = tripTime;
    }


    protected Trip(Parcel in) {
        tripName = in.readString();
        tripId = in.readInt();
        tripStatus = in.readInt();
        tripType = in.readInt();
        startName = in.readString();
        startLong = in.readDouble();
        startLat = in.readDouble();
        endName = in.readString();
        endLong = in.readDouble();
        endLat = in.readDouble();
        if (in.readByte() == 0x01) {
            notes = new ArrayList<String>();
            in.readList(notes, String.class.getClassLoader());
        } else {
            notes = null;
        }
        tripDate = in.readString();
        tripTime = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tripName);
        dest.writeInt(tripId);
        dest.writeInt(tripStatus);
        dest.writeInt(tripType);
        dest.writeString(startName);
        dest.writeDouble(startLong);
        dest.writeDouble(startLat);
        dest.writeString(endName);
        dest.writeDouble(endLong);
        dest.writeDouble(endLat);
        if (notes == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(notes);
        }
        dest.writeString(tripDate);
        dest.writeString(tripTime);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Trip> CREATOR = new Parcelable.Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };
}