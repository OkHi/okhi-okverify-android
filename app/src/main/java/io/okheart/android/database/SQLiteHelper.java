package io.okheart.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ramogiochola on 6/18/16.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "io.okheart.android.sdk.database.db";
    private static final int DATABASE_VERSION = 11;

    private static final String DATABASE_CREATE_RUNLIST =
            "create table " + io.okheart.android.utilities.Constants.TABLE_NAME_RUNLIST + " (" +

                    io.okheart.android.utilities.Constants.COLUMN_ID + " integer primary key autoincrement, " +
                    io.okheart.android.utilities.Constants.COLUMN_CUSTOMERNAME + " VARCHAR, " +
                    io.okheart.android.utilities.Constants.COLUMN_AFFILIATION + " VARCHAR, " +
                    io.okheart.android.utilities.Constants.COLUMN_PHONECUSTOMER + " VARCHAR, " +


                    io.okheart.android.utilities.Constants.COLUMN_CLAIMAFLID + " VARCHAR, " +
                    io.okheart.android.utilities.Constants.COLUMN_CLAIMUALID + " VARCHAR, " +
                    io.okheart.android.utilities.Constants.COLUMN_UNIT + " VARCHAR, " +
                    io.okheart.android.utilities.Constants.COLUMN_DIRECTION + " VARCHAR, " +
                    io.okheart.android.utilities.Constants.COLUMN_ROUTE + " VARCHAR, " +

                    io.okheart.android.utilities.Constants.COLUMN_PROPERTYNAME + " VARCHAR, " +
                    io.okheart.android.utilities.Constants.COLUMN_PROPERTYNUMBER + " VARCHAR, " +
                    io.okheart.android.utilities.Constants.COLUMN_FLOOR + " VARCHAR, " +
                    io.okheart.android.utilities.Constants.COLUMN_ISODDRESS + " INTEGER, " +
                    io.okheart.android.utilities.Constants.COLUMN_LAT + " REAL, " +

                    io.okheart.android.utilities.Constants.COLUMN_LNG + " REAL, " +
                    io.okheart.android.utilities.Constants.COLUMN_BRANCH + " VARCHAR, " +
                    io.okheart.android.utilities.Constants.COLUMN_IMAGEURL + " VARCHAR, " +
                    io.okheart.android.utilities.Constants.COLUMN_DELIVERY_NOTES + " VARCHAR, " +

                    io.okheart.android.utilities.Constants.COLUMN_LOCATIONNICKNAME + " VARCHAR, " +
                    io.okheart.android.utilities.Constants.COLUMN_TRADITIONALBUILDINGNAME + " VARCHAR, " +
                    io.okheart.android.utilities.Constants.COLUMN_BUSINESSNAME + " VARCHAR, " +
                    io.okheart.android.utilities.Constants.COLUMN_TRADITIONALSTREETNUMBER + " VARCHAR, " +
                    io.okheart.android.utilities.Constants.COLUMN_TRADITIONALSTREETNAME + " VARCHAR, " +

                    io.okheart.android.utilities.Constants.COLUMN_TOTHEDOOR + " VARCHAR, " +
                    io.okheart.android.utilities.Constants.COLUMN_TRADITIONALBUILDINGNUMBER + " VARCHAR, " +
                    io.okheart.android.utilities.Constants.COLUMN_STREETNAME + " VARCHAR, " +
                    io.okheart.android.utilities.Constants.COLUMN_STREETNUMBER + " VARCHAR, " +

                    io.okheart.android.utilities.Constants.COLUMN_CUSTOMERUSERID + " VARCHAR, " +
                    io.okheart.android.utilities.Constants.COLUMN_ADDRESSTYPE + " VARCHAR, " +
                    io.okheart.android.utilities.Constants.COLUMN_INTERNAL_ADDRESSTYPE + " VARCHAR, " +
                    io.okheart.android.utilities.Constants.COLUMN_ISNEWUSER + " VARCHAR, " +
                    io.okheart.android.utilities.Constants.COLUMN_ISEMPTYUAL + " VARCHAR, " +
                    io.okheart.android.utilities.Constants.COLUMN_ACCURACY + " VARCHAR, " +
                    io.okheart.android.utilities.Constants.COLUMN_ADDRESSFREQUENCY + " INTEGER, " +
                    io.okheart.android.utilities.Constants.COLUMN_CREATEDON + " VARCHAR, " +
                    io.okheart.android.utilities.Constants.COLUMN_LASTUSED + " VARCHAR, " +
                    io.okheart.android.utilities.Constants.COLUMN_LOCATIONNAME + " VARCHAR, " +
                    io.okheart.android.utilities.Constants.COLUMN_UNIQUEID + " VARCHAR, " +
                    " UNIQUE(" + io.okheart.android.utilities.Constants.COLUMN_CLAIMUALID + ") ON CONFLICT REPLACE);";

    private static final String DATABASE_CREATE_STUFF =
            "create table " + io.okheart.android.utilities.Constants.TABLE_NAME_STUFF + " (" +
                    io.okheart.android.utilities.Constants.COLUMN_ID + " integer primary key autoincrement, " +
                    io.okheart.android.utilities.Constants.COLUMN_PROPERTY + " VARCHAR NOT NULL UNIQUE, " +
                    io.okheart.android.utilities.Constants.COLUMN_VALUE + " VARCHAR , " +
                    " UNIQUE(" + io.okheart.android.utilities.Constants.COLUMN_PROPERTY + ") ON CONFLICT REPLACE);";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_RUNLIST);
        database.execSQL(DATABASE_CREATE_STUFF);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + io.okheart.android.utilities.Constants.TABLE_NAME_RUNLIST);
        db.execSQL("DROP TABLE IF EXISTS " + io.okheart.android.utilities.Constants.TABLE_NAME_STUFF);
        onCreate(db);
    }
}
