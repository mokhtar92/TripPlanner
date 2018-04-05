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
    private Context context;

    public TripDbAdapter(Context context) {
        dbHelper = TripDbHelper.getsInstance(context);
        this.context = context;
    }

    public long insertTrip(Trip trip) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TripDbHelper.COLUMN_TRIP_NAME, trip.getTripName());
        values.put(TripDbHelper.COLUMN_FIREBASE_ID, trip.getFireBaseTripId());
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

        //convert ArrayList of notes to string
        if (trip.getNotes() != null) {
            String notes = getNotesAsOneString(trip.getNotes());
            values.put(TripDbHelper.COLUMN_TRIP_NOTES, notes);
        }

        long id = db.insert(TripDbHelper.TABLE_NAME, null, values);
        db.close();

        return id;
    }

    public int updateTrip(Trip trip) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TripDbHelper.COLUMN_TRIP_NAME, trip.getTripName());
        values.put(TripDbHelper.COLUMN_FIREBASE_ID, trip.getFireBaseTripId());
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

        //convert ArrayList of notes to string
        if (trip.getNotes() != null) {
            String notes = getNotesAsOneString(trip.getNotes());
            values.put(TripDbHelper.COLUMN_TRIP_NOTES, notes);
        }

        int rowsUpdated = db.update(TripDbHelper.TABLE_NAME, values, TripDbHelper.COLUMN_FIREBASE_ID + "=?",
                new String[]{trip.getFireBaseTripId()});
        db.close();

        return rowsUpdated;
    }

    public int deleteTrip(Trip trip) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rowsDeleted = db.delete(TripDbHelper.TABLE_NAME, TripDbHelper.COLUMN_FIREBASE_ID + "=?",
                new String[]{trip.getFireBaseTripId()});
        db.close();

        return rowsDeleted;
    }

    public void insertOrUpdate(Trip trip) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TripDbHelper.COLUMN_TRIP_NAME, trip.getTripName());
        values.put(TripDbHelper.COLUMN_FIREBASE_ID, trip.getFireBaseTripId());
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

        //convert ArrayList of notes to string
        if (trip.getNotes() != null) {
            String notes = getNotesAsOneString(trip.getNotes());
            values.put(TripDbHelper.COLUMN_TRIP_NOTES, notes);
        }

        int id = (int) db.insertWithOnConflict(TripDbHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (id == -1) {
            db.update(TripDbHelper.TABLE_NAME, values, TripDbHelper.COLUMN_FIREBASE_ID + "=?", new String[]{trip.getFireBaseTripId()});
        }
    }


    public List<Trip> getUpcomingTrips() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<Trip> allTrips = new ArrayList<>();

        String selectQuery =
                "SELECT * FROM " + TripDbHelper.TABLE_NAME + " WHERE " + TripDbHelper.COLUMN_TRIP_STATUS + "=" + Definitions.STATUS_UPCOMING;

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
        trip.setFireBaseTripId(cursor.getString(cursor.getColumnIndex(TripDbHelper.COLUMN_FIREBASE_ID)));
        trip.setTripName(cursor.getString(cursor.getColumnIndex(TripDbHelper.COLUMN_TRIP_NAME)));
        trip.setTripStatus(cursor.getInt(cursor.getColumnIndex(TripDbHelper.COLUMN_TRIP_STATUS)));
        trip.setTripType(cursor.getInt(cursor.getColumnIndex(TripDbHelper.COLUMN_TRIP_TYPE)));

        trip.setStartName(cursor.getString(cursor.getColumnIndex(TripDbHelper.COLUMN_START_NAME)));
        trip.setStartLong(Double.parseDouble(cursor.getString(cursor.getColumnIndex(TripDbHelper.COLUMN_START_LONG))));
        trip.setStartLat(Double.parseDouble(cursor.getString(cursor.getColumnIndex(TripDbHelper.COLUMN_START_LAT))));

        trip.setEndName(cursor.getString(cursor.getColumnIndex(TripDbHelper.COLUMN_END_NAME)));
        trip.setEndLong(Double.parseDouble(cursor.getString(cursor.getColumnIndex(TripDbHelper.COLUMN_END_LONG))));
        trip.setEndLat(Double.parseDouble(cursor.getString(cursor.getColumnIndex(TripDbHelper.COLUMN_END_LAT))));

        trip.setTripDate(cursor.getString(cursor.getColumnIndex(TripDbHelper.COLUMN_TRIP_DATE)));
        trip.setTripTime(cursor.getString(cursor.getColumnIndex(TripDbHelper.COLUMN_TRIP_TIME)));

        //Convert string of notes to list
        String combinedNotes = cursor.getString(cursor.getColumnIndex(TripDbHelper.COLUMN_TRIP_NOTES));
        if (combinedNotes != null) {
            String[] notesArray = combinedNotes.split(",");
            List<String> notes = new ArrayList<>();
            notes.addAll(Arrays.asList(notesArray));
            trip.setNotes(notes);
        }

        return trip;
    }


    private String getNotesAsOneString(List<String> notes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < notes.size(); i++) {
            builder.append(notes.get(i).concat(","));
        }

        return builder.toString();
    }
}
