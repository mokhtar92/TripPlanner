package eg.gov.iti.tripplanner.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import eg.gov.iti.tripplanner.model.Trip;

/**
 * Created by Ahmed_Mokhtar on 3/18/2018.
 */

public class TripDbAdapter {

    private TripDbHelper dbHelper;

    public TripDbAdapter(Context context) {
        dbHelper = TripDbHelper.getsInstance(context);
    }

    public long insertTrip(Trip trip) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TripDbHelper.COLUMN_TRIP_NAME, trip.getTripName());
        values.put(TripDbHelper.COLUMN_TRIP_STATUS, trip.getTripStatus());

        long id = db.insert(TripDbHelper.TABLE_NAME, null, values);
        db.close();

        return id;
    }

    public int updateTrip(Trip trip) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TripDbHelper.COLUMN_TRIP_NAME, trip.getTripName());
        values.put(TripDbHelper.COLUMN_TRIP_STATUS, trip.getTripStatus());

        int rowsUpdated = db.update(TripDbHelper.TABLE_NAME, values, TripDbHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(trip.getTripId())});
        db.close();

        return rowsUpdated;
    }

    public int deleteTrip(Trip trip) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rowsDeleted = db.delete(TripDbHelper.TABLE_NAME, TripDbHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(trip.getTripId())});
        db.close();

        return rowsDeleted;
    }

    public Trip getSingleTrip(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {TripDbHelper.COLUMN_TRIP_NAME, TripDbHelper.COLUMN_TRIP_STATUS};

        Cursor cursor = db.query(TripDbHelper.TABLE_NAME,
                columns,
                TripDbHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Trip trip = new Trip();
        trip.setTripName(cursor.getString(cursor.getColumnIndex(TripDbHelper.COLUMN_TRIP_NAME)));
        trip.setTripStatus(cursor.getInt(cursor.getColumnIndex(TripDbHelper.COLUMN_TRIP_STATUS)));
        cursor.close();

        return trip;
    }

    public List<Trip> getAllTrips() {
        List<Trip> allTrips = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TripDbHelper.TABLE_NAME + " ORDER BY " + TripDbHelper.COLUMN_TRIP_DATE + ", " + TripDbHelper.COLUMN_TRIP_TIME;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        while (cursor.moveToNext()) {
            Trip trip = new Trip();
            trip.setTripName(cursor.getString(cursor.getColumnIndex(TripDbHelper.COLUMN_TRIP_NAME)));
            trip.setTripStatus(cursor.getInt(cursor.getColumnIndex(TripDbHelper.COLUMN_TRIP_STATUS)));

            allTrips.add(trip);
        }
        cursor.close();

        return allTrips;
    }
}
