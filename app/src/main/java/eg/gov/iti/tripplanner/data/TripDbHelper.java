package eg.gov.iti.tripplanner.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ahmed_Mokhtar on 3/18/2018.
 */

public class TripDbHelper extends SQLiteOpenHelper {

    private static TripDbHelper sInstance;

    private static final String DATABASE_NAME = "trips.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "TRIP_REC";

    public static final String COLUMN_ID = "_ID";
    public static final String COLUMN_FIREBASE_ID = "FIREBASE_ID";
    public static final String COLUMN_TRIP_NAME = "TRIP_NAME";
    public static final String COLUMN_TRIP_STATUS = "TRIP_STATUS";
    public static final String COLUMN_TRIP_TYPE = "TRIP_TYPE";

    public static final String COLUMN_START_NAME = "START_NAME";
    public static final String COLUMN_START_LONG = "START_LONG";
    public static final String COLUMN_START_LAT = "START_LAT";

    public static final String COLUMN_END_NAME = "END_NAME";
    public static final String COLUMN_END_LONG = "END_LONG";
    public static final String COLUMN_END_LAT = "END_LAT";

    public static final String COLUMN_TRIP_NOTES = "TRIP_NOTES";
    public static final String COLUMN_TRIP_DATE = "TRIP_DATE";
    public static final String COLUMN_TRIP_TIME = "TRIP_TIME";


    public static synchronized TripDbHelper getsInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TripDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private TripDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME +
                "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FIREBASE_ID + " TEXT, " +
                COLUMN_TRIP_NAME + " TEXT, " +
                COLUMN_TRIP_STATUS + " INTEGER, " +
                COLUMN_TRIP_TYPE + " INTEGER, " +
                COLUMN_START_NAME + " TEXT, " +
                COLUMN_START_LONG + " TEXT, " +
                COLUMN_START_LAT + " TEXT, " +
                COLUMN_END_NAME + " TEXT, " +
                COLUMN_END_LONG + " TEXT, " +
                COLUMN_END_LAT + " TEXT, " +
                COLUMN_TRIP_NOTES + " TEXT, " +
                COLUMN_TRIP_DATE + " TEXT, " +
                COLUMN_TRIP_TIME + " TEXT" +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
