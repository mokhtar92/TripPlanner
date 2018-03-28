package eg.gov.iti.tripplanner.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eg.gov.iti.tripplanner.model.Trip;
import eg.gov.iti.tripplanner.utils.Definitions;

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
        values.put(TripDbHelper.COLUMN_TRIP_STATUS, Definitions.STATUS_UPCOMING);
        values.put(TripDbHelper.COLUMN_TRIP_TYPE, trip.getTripType());

        values.put(TripDbHelper.COLUMN_START_NAME, trip.getStartName());
        values.put(TripDbHelper.COLUMN_START_LONG, trip.getStartLong());
        values.put(TripDbHelper.COLUMN_START_LAT, trip.getStartLat());

        values.put(TripDbHelper.COLUMN_END_NAME, trip.getEndName());
        values.put(TripDbHelper.COLUMN_END_LONG, trip.getEndLong());
        values.put(TripDbHelper.COLUMN_END_LAT, trip.getEndLat());

        values.put(TripDbHelper.COLUMN_TRIP_DATE, trip.getTripDate());
        values.put(TripDbHelper.COLUMN_TRIP_TIME, trip.getTripTime());
        values.put(TripDbHelper.COLUMN_TRIP_NOTES, trip.getNotes());

        long id = db.insert(TripDbHelper.TABLE_NAME, null, values);
        db.close();

        return id;
    }

    public int updateTrip(Trip trip) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TripDbHelper.COLUMN_TRIP_NAME, trip.getTripName());
        values.put(TripDbHelper.COLUMN_TRIP_STATUS, trip.getTripStatus());
        values.put(TripDbHelper.COLUMN_TRIP_TYPE, trip.getTripType());

        values.put(TripDbHelper.COLUMN_START_NAME, trip.getStartName());
        values.put(TripDbHelper.COLUMN_START_LONG, trip.getStartLong());
        values.put(TripDbHelper.COLUMN_START_LAT, trip.getStartLat());

        values.put(TripDbHelper.COLUMN_END_NAME, trip.getEndName());
        values.put(TripDbHelper.COLUMN_END_LONG, trip.getEndLong());
        values.put(TripDbHelper.COLUMN_END_LAT, trip.getEndLat());

        values.put(TripDbHelper.COLUMN_TRIP_DATE, trip.getTripDate());
        values.put(TripDbHelper.COLUMN_TRIP_TIME, trip.getTripTime());
        values.put(TripDbHelper.COLUMN_TRIP_NOTES, trip.getNotes());

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

    public Trip getSingleTrip(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {TripDbHelper.COLUMN_TRIP_NAME, TripDbHelper.COLUMN_TRIP_STATUS};

        Cursor cursor = db.query(TripDbHelper.TABLE_NAME,
                columns,
                TripDbHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        Trip trip = null;
        if (cursor != null) {
            cursor.moveToFirst();
            trip = constructTripFromCursor(cursor);

            cursor.close();
        }

        return trip;
    }

    public List<Trip> getAllTrips() {
        List<Trip> allTrips = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TripDbHelper.TABLE_NAME + " ORDER BY " + TripDbHelper.COLUMN_TRIP_DATE + ", " + TripDbHelper.COLUMN_TRIP_TIME;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        while (cursor.moveToNext()) {
            allTrips.add(constructTripFromCursor(cursor));
        }
        cursor.close();

        return allTrips;
    }


    private Trip constructTripFromCursor(Cursor cursor) {
        Trip trip = new Trip();
        trip.setTripId(cursor.getInt(cursor.getColumnIndex(TripDbHelper.COLUMN_ID)));
        trip.setTripName(cursor.getString(cursor.getColumnIndex(TripDbHelper.COLUMN_TRIP_NAME)));
        trip.setTripStatus(cursor.getInt(cursor.getColumnIndex(TripDbHelper.COLUMN_TRIP_STATUS)));
        trip.setTripType(cursor.getInt(cursor.getColumnIndex(TripDbHelper.COLUMN_TRIP_TYPE)));

        trip.setStartName(cursor.getString(cursor.getColumnIndex(TripDbHelper.COLUMN_START_NAME)));
        trip.setStartLong(Double.parseDouble(cursor.getString(cursor.getColumnIndex(TripDbHelper.COLUMN_START_LONG))));
        trip.setStartLat(Double.parseDouble(cursor.getString(cursor.getColumnIndex(TripDbHelper.COLUMN_START_LAT))));

        trip.setEndName(cursor.getString(cursor.getColumnIndex(TripDbHelper.COLUMN_END_NAME)));
        trip.setEndLong(Double.parseDouble(cursor.getString(cursor.getColumnIndex(TripDbHelper.COLUMN_END_LONG))));
        trip.setEndLat(Double.parseDouble(cursor.getString(cursor.getColumnIndex(TripDbHelper.COLUMN_END_LAT))));

        trip.setTripDate(cursor.getColumnName(cursor.getColumnIndex(TripDbHelper.COLUMN_TRIP_DATE)));
        trip.setTripTime(cursor.getColumnName(cursor.getColumnIndex(TripDbHelper.COLUMN_TRIP_TIME)));

        //Convert string of notes to list
        String combinedNotes = cursor.getColumnName(cursor.getColumnIndex(TripDbHelper.COLUMN_TRIP_NOTES));
        String[] notesArray = combinedNotes.split(",");
        List<String> notes = new ArrayList<>();
        notes.addAll(Arrays.asList(notesArray));
        trip.setNotes(notes);

        return trip;
    }
}
