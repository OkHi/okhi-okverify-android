package io.okheart.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static io.okheart.android.utilities.Constants.COLUMN_ACCURACY;
import static io.okheart.android.utilities.Constants.COLUMN_ADDRESSFREQUENCY;
import static io.okheart.android.utilities.Constants.COLUMN_ADDRESSTYPE;
import static io.okheart.android.utilities.Constants.COLUMN_AFFILIATION;
import static io.okheart.android.utilities.Constants.COLUMN_BRANCH;
import static io.okheart.android.utilities.Constants.COLUMN_BUSINESSNAME;
import static io.okheart.android.utilities.Constants.COLUMN_CLAIMAFLID;
import static io.okheart.android.utilities.Constants.COLUMN_CLAIMUALID;
import static io.okheart.android.utilities.Constants.COLUMN_CREATEDON;
import static io.okheart.android.utilities.Constants.COLUMN_CUSTOMERNAME;
import static io.okheart.android.utilities.Constants.COLUMN_CUSTOMERUSERID;
import static io.okheart.android.utilities.Constants.COLUMN_DELIVERY_NOTES;
import static io.okheart.android.utilities.Constants.COLUMN_DIRECTION;
import static io.okheart.android.utilities.Constants.COLUMN_FLOOR;
import static io.okheart.android.utilities.Constants.COLUMN_ID;
import static io.okheart.android.utilities.Constants.COLUMN_IMAGEURL;
import static io.okheart.android.utilities.Constants.COLUMN_INTERNAL_ADDRESSTYPE;
import static io.okheart.android.utilities.Constants.COLUMN_ISEMPTYUAL;
import static io.okheart.android.utilities.Constants.COLUMN_ISNEWUSER;
import static io.okheart.android.utilities.Constants.COLUMN_ISODDRESS;
import static io.okheart.android.utilities.Constants.COLUMN_LASTUSED;
import static io.okheart.android.utilities.Constants.COLUMN_LAT;
import static io.okheart.android.utilities.Constants.COLUMN_LNG;
import static io.okheart.android.utilities.Constants.COLUMN_LOCATIONNAME;
import static io.okheart.android.utilities.Constants.COLUMN_LOCATIONNICKNAME;
import static io.okheart.android.utilities.Constants.COLUMN_PHONECUSTOMER;
import static io.okheart.android.utilities.Constants.COLUMN_PROPERTY;
import static io.okheart.android.utilities.Constants.COLUMN_PROPERTYNAME;
import static io.okheart.android.utilities.Constants.COLUMN_PROPERTYNUMBER;
import static io.okheart.android.utilities.Constants.COLUMN_ROUTE;
import static io.okheart.android.utilities.Constants.COLUMN_STREETNAME;
import static io.okheart.android.utilities.Constants.COLUMN_STREETNUMBER;
import static io.okheart.android.utilities.Constants.COLUMN_TOTHEDOOR;
import static io.okheart.android.utilities.Constants.COLUMN_TRADITIONALBUILDINGNAME;
import static io.okheart.android.utilities.Constants.COLUMN_TRADITIONALBUILDINGNUMBER;
import static io.okheart.android.utilities.Constants.COLUMN_TRADITIONALSTREETNAME;
import static io.okheart.android.utilities.Constants.COLUMN_TRADITIONALSTREETNUMBER;
import static io.okheart.android.utilities.Constants.COLUMN_UNIT;
import static io.okheart.android.utilities.Constants.COLUMN_VALUE;
import static io.okheart.android.utilities.Constants.TABLE_NAME_RUNLIST;
import static io.okheart.android.utilities.Constants.TABLE_NAME_STUFF;

/**
 * Created by ramogiochola on 6/18/16.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "okverify.db";
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE_RUNLIST =
            "create table " + TABLE_NAME_RUNLIST + " (" +

                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_CUSTOMERNAME + " VARCHAR, " +
                    COLUMN_AFFILIATION + " VARCHAR, " +
                    COLUMN_PHONECUSTOMER + " VARCHAR, " +

                    COLUMN_CLAIMAFLID + " VARCHAR, " +
                    COLUMN_CLAIMUALID + " VARCHAR, " +
                    COLUMN_UNIT + " VARCHAR, " +
                    COLUMN_DIRECTION + " VARCHAR, " +
                    COLUMN_ROUTE + " VARCHAR, " +

                    COLUMN_PROPERTYNAME + " VARCHAR, " +
                    COLUMN_PROPERTYNUMBER + " VARCHAR, " +
                    COLUMN_FLOOR + " VARCHAR, " +
                    COLUMN_ISODDRESS + " INTEGER, " +
                    COLUMN_LAT + " REAL, " +

                    COLUMN_LNG + " REAL, " +
                    COLUMN_BRANCH + " VARCHAR, " +
                    COLUMN_IMAGEURL + " VARCHAR, " +
                    COLUMN_DELIVERY_NOTES + " VARCHAR, " +

                    COLUMN_LOCATIONNICKNAME + " VARCHAR, " +
                    COLUMN_TRADITIONALBUILDINGNAME + " VARCHAR, " +
                    COLUMN_BUSINESSNAME + " VARCHAR, " +
                    COLUMN_TRADITIONALSTREETNUMBER + " VARCHAR, " +
                    COLUMN_TRADITIONALSTREETNAME + " VARCHAR, " +

                    COLUMN_TOTHEDOOR + " VARCHAR, " +
                    COLUMN_TRADITIONALBUILDINGNUMBER + " VARCHAR, " +
                    COLUMN_STREETNAME + " VARCHAR, " +
                    COLUMN_STREETNUMBER + " VARCHAR, " +

                    COLUMN_CUSTOMERUSERID + " VARCHAR, " +
                    COLUMN_ADDRESSTYPE + " VARCHAR, " +
                    COLUMN_INTERNAL_ADDRESSTYPE + " VARCHAR, " +
                    COLUMN_ISNEWUSER + " VARCHAR, " +
                    COLUMN_ISEMPTYUAL + " VARCHAR, " +
                    COLUMN_ACCURACY + " VARCHAR, " +
                    COLUMN_ADDRESSFREQUENCY + " INTEGER, " +
                    COLUMN_CREATEDON + " VARCHAR, " +
                    COLUMN_LASTUSED + " VARCHAR, " +
                    COLUMN_LOCATIONNAME + " VARCHAR, " +
                    " UNIQUE(" + COLUMN_CLAIMUALID + ") ON CONFLICT REPLACE);";

    private static final String DATABASE_CREATE_STUFF =
            "create table " + TABLE_NAME_STUFF + " (" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_PROPERTY + " VARCHAR NOT NULL UNIQUE, " +
                    COLUMN_VALUE + " VARCHAR , " +
                    " UNIQUE(" + COLUMN_PROPERTY + ") ON CONFLICT REPLACE);";

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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_RUNLIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_STUFF);
        onCreate(db);
    }
}
