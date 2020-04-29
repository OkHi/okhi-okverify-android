package io.okverify.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import io.okverify.android.utilities.Constants;


/**
 * Created by ramogiochola on 6/18/16.
 */

public class DataProvider {
    private static final String TAG = "DataProvider";

    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;

    public DataProvider(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void openRead() throws SQLException {
        database = dbHelper.getReadableDatabase();
    }

    public void openWrite() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }


    public long insertAddressList(ContentValues values) {
        displayLog(" insertAddressList  with values method called");

        long insertId = 0;

        try {
            try {
                openWrite();
            } catch (Exception e) {
                displayLog("insertAddressList openWrite error " + e.toString());
            }
            displayLog("after write before insert");
            insertId = database.insert(io.okverify.android.utilities.Constants.TABLE_NAME_RUNLIST, null, values);
            displayLog("insertAddressList method executed " + insertId);


        } catch (SQLException sqle) {
            displayLog(" insertAddressList error " + sqle.toString());
        } finally {
            close();
        }
        return insertId;
    }


    public long insertOrderList(ContentValues values) {
        displayLog(" insertOrderList  with values method called");

        long insertId = 0;

        try {
            try {
                openWrite();
            } catch (Exception e) {
                displayLog("insertOrderList openWrite error " + e.toString());
            }
            displayLog("after write before insert");
            insertId = database.insert(Constants.TABLE_NAME_TRANSITS, null, values);
            displayLog("insertOrderList method executed " + insertId);


        } catch (SQLException sqle) {
            displayLog(" insertOrderList error " + sqle.toString());
        } finally {
            close();
        }
        return insertId;
    }


    private io.okverify.android.datamodel.OrderItem cursorToOrderListItem(Cursor cursor) {

        /*
             io.okverify.android.utilities.Constants.COLUMN_CLAIMUALID + " VARCHAR, " +
                    io.okverify.android.utilities.Constants.COLUMN_LAT + " REAL, " +
                    io.okverify.android.utilities.Constants.COLUMN_LNG + " REAL, " +
                    io.okverify.android.utilities.Constants.COLUMN_PHONECUSTOMER + " VARCHAR, " +
                    io.okverify.android.utilities.Constants.COLUMN_TRANSIT + " VARCHAR, " +
                    io.okverify.android.utilities.Constants.COLUMN_EVENTTIME + " REAL, " +
         */

        io.okverify.android.datamodel.OrderItem addressItem = new io.okverify.android.datamodel.OrderItem();
        addressItem.setClaimualid(cursor.getString(1));
        addressItem.setState(cursor.getString(5));
        addressItem.setCreatedat(cursor.getLong(6));
        return addressItem;
    }

    private io.okverify.android.datamodel.AddressItem cursorToAddressListItem(Cursor cursor) {

        io.okverify.android.datamodel.AddressItem addressItem = new io.okverify.android.datamodel.AddressItem();
        addressItem.setUalid(cursor.getString(1));
        addressItem.setLat(cursor.getDouble(2));

        addressItem.setLng(cursor.getDouble(3));
        /*
        addressItem.setCustomername(cursor.getString(1));
        addressItem.setAffiliation(cursor.getString(2));
        addressItem.setPhonecustomer(cursor.getString(3));

        addressItem.setAflid(cursor.getString(4));
        addressItem.setUalid(cursor.getString(5));
        addressItem.setUnit(cursor.getString(6));
        addressItem.setDirection(cursor.getString(7));
        addressItem.setRoute(cursor.getString(8));

        addressItem.setPropname(cursor.getString(9));
        addressItem.setPropnumber(cursor.getString(10));
        addressItem.setFloor(cursor.getString(11));
        addressItem.setIsOddress(cursor.getInt(12));
        addressItem.setLat(cursor.getDouble(13));

        addressItem.setLng(cursor.getDouble(14));
        addressItem.setBranch(cursor.getString(15));
        addressItem.setImageurl(cursor.getString(16));
        addressItem.setDeliverynotes(cursor.getString(17));


        addressItem.setLocationNickname(cursor.getString(18));
        addressItem.setTraditionalBuildingName(cursor.getString(19));
        addressItem.setBusinessName(cursor.getString(20));
        addressItem.setTraditionalStreetNumber(cursor.getString(21));
        addressItem.setTraditionalStreetName(cursor.getString(22));

        addressItem.setToTheDoor(cursor.getString(23));
        addressItem.setTraditionalBuildingNumber(cursor.getString(24));
        addressItem.setStreetName(cursor.getString(25));
        addressItem.setStreetNumber(cursor.getString(26));

        addressItem.setCustomeruserid(cursor.getString(27));
        addressItem.setAddressType(cursor.getString(28));
        addressItem.setInternalAddressType(cursor.getString(29));
        addressItem.setIsNewUser(cursor.getString(30));
        addressItem.setIsEmptyUal(cursor.getString(31));
        addressItem.setAcc(cursor.getDouble(32));
        addressItem.setAddressFrequency(cursor.getInt(33));
        addressItem.setCreatedon(cursor.getString(34));
        addressItem.setLastused(cursor.getString(35));
        addressItem.setLocationName(cursor.getString(35));
        addressItem.setUniqueId(cursor.getString(36));
        */


        //displayLog("floor "+addressItem.getFloor());
        //displayLog("unit "+addressItem.getUnit());
        /*
        try {
            String floor = addressItem.getFloor();
            if(floor != null) {
                if (floor.length() > 0) {

                } else {
                    addressItem.setFloor("");
                }
            }
            else{
                addressItem.setFloor("");
            }
        } catch (Exception e) {
            addressItem.setFloor("");
            displayLog("error floor " + e.toString());
        }
        try {
            String unit = addressItem.getUnit();
            if (unit != null) {
                if (unit.length() > 0) {

                } else {
                    addressItem.setUnit("");
                }
            }
            else{
                addressItem.setUnit("");
            }

        } catch (Exception e) {
            addressItem.setUnit("");
            displayLog("error unit " + e.toString());
        }
        try {
            String notes = addressItem.getDeliverynotes();
            if(notes != null) {
                if (notes.length() > 0) {

                } else {
                    addressItem.setDeliverynotes("");
                }
            }
            else{
                addressItem.setDeliverynotes("");
            }
        } catch (Exception e) {
            addressItem.setDeliverynotes("");
            displayLog("error notes " + e.toString());
        }
        */
        //displayLog(" otherId " + addressItem.getChecknumber());
        //displayLog("**************");
/*
        displayLog("**************");

        displayLog(COLUMN_IMAGEURL+" "+addressItem.getImageurl());
        displayLog(COLUMN_DRIVERNAME+" "+addressItem.getDrivername());
        displayLog(COLUMN_ISODDRESS+ " "+addressItem.getIsOddress());
        displayLog(COLUMN_PHONEDRIVER+" "+addressItem.getPhonedriver());
        displayLog(COLUMN_QUEUESTATE+" "+addressItem.getState());
        displayLog(COLUMN_QUEUESTATETIME+" "+addressItem.getStatetime());
        displayLog(COLUMN_CASHIER+" "+addressItem.getCashier());
        displayLog(COLUMN_PAYMENTTYPE+" "+addressItem.getPaymenttype());
        displayLog(COLUMN_PHONECUSTOMER+" "+addressItem.getPhonecustomer());
        displayLog(COLUMN_CARUPGRADEDELIVERED+" "+addressItem.getCarupgradedelivered());
        displayLog(COLUMN_BACKGROUNDSTATE+" "+addressItem.getBackground());
        displayLog(COLUMN_CHECKED+" "+addressItem.getChecked());
        displayLog(COLUMN_DELIVERY_NOTES+" "+addressItem.getDeliverynotes());
        displayLog(COLUMN_DELIVERYID+" "+addressItem.getDeliveryid());
        displayLog(COLUMN_UNIQUEIDENTIFIER+" "+addressItem.getUnique());
        displayLog(COLUMN_RUNLIST_DATABASE_ID+" "+addressItem.getDatabaseid());
        displayLog(COLUMN_BACKGROUNDSTATE+" "+addressItem.getBackground());
        displayLog("ticks "+addressItem.getTicks());
        displayLog("**************");
        */

        return addressItem;

    }

    /*
    public void deleteAllAddressList() {
        displayLog("deleteAllAddressList() method called");
        try {
            try {
                openWrite();
            } catch (Exception e) {
                displayLog("deleteAllAddressList openWrite error " + e.toString());
            }
            database.delete(TABLE_NAME_RUNLIST, null, null);
            displayLog("deleteAllAddressList() method executed");

        } catch (SQLException sqle) {
            displayLog(" deleteAllAddressList error " + sqle.toString());
        } finally {
            close();
        }
    }
*/

    public void deleteAddressListItemUAL(String ualid) {
        displayLog("deleteAddressListItemUAL() method called");
        String selection = io.okverify.android.utilities.Constants.COLUMN_CLAIMUALID + " = ? ";
        String[] selectionArgs = {ualid};
        try {
            try {
                openWrite();
            } catch (Exception e) {
                displayLog("deleteAddressListItemUAL openWrite error " + e.toString());
            }
            database.delete(io.okverify.android.utilities.Constants.TABLE_NAME_RUNLIST, selection, selectionArgs);
            displayLog("deleteAddressListItemUAL() method executed");

        } catch (SQLException sqle) {
            displayLog(" deleteAddressListItemUAL error " + sqle.toString());
        } finally {
            close();
        }
    }


    public void deleteAllAddresseses() {
        displayLog("deleteAllAddresseses() method called");

        try {
            try {
                openWrite();
            } catch (Exception e) {
                displayLog("deleteAllAddresseses openWrite error " + e.toString());
            }
            database.delete(io.okverify.android.utilities.Constants.TABLE_NAME_RUNLIST, null, null);
            displayLog("deleteAllAddresseses() method executed");

        } catch (SQLException sqle) {
            displayLog(" deleteAllAddresseses error " + sqle.toString());
        } finally {
            close();
        }
    }

    public List<io.okverify.android.datamodel.OrderItem> getOrderListItem(String deliveryId) {
        displayLog("List<OrderItem> getOrderListItem(" + deliveryId + ") method called");

        List<io.okverify.android.datamodel.OrderItem> ArtcaffeRunList = new ArrayList<io.okverify.android.datamodel.OrderItem>();

        String selection = io.okverify.android.utilities.Constants.COLUMN_CLAIMUALID + " = ? ";
        String[] selectionArgs = {"" + deliveryId};

        try {

            try {
                openRead();
            } catch (Exception e) {
                displayLog("getOrderListItem openRead error " + e.toString());
            }

            Cursor cursor = database.query(Constants.TABLE_NAME_TRANSITS, null, selection, selectionArgs, null, null, null);
            displayLog("getOrderListItem method executed");
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                io.okverify.android.datamodel.OrderItem AddressItem = cursorToOrderListItem(cursor);
                ArtcaffeRunList.add(AddressItem);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (SQLException sqle) {
            displayLog("getOrderListItem error " + sqle.toString());

        } finally {
            close();
        }
        return ArtcaffeRunList;

    }


    public List<io.okverify.android.datamodel.AddressItem> getAddressListItem(String deliveryId) {
        displayLog("List<AddressItem> getAddressListItem(" + deliveryId + ") method called");

        List<io.okverify.android.datamodel.AddressItem> ArtcaffeRunList = new ArrayList<io.okverify.android.datamodel.AddressItem>();

        String selection = io.okverify.android.utilities.Constants.COLUMN_CLAIMUALID + " = ? ";
        String[] selectionArgs = {"" + deliveryId};

        try {

            try {
                openRead();
            } catch (Exception e) {
                displayLog("getAllArtcaffeAddressesBackup openRead error " + e.toString());
            }

            Cursor cursor = database.query(io.okverify.android.utilities.Constants.TABLE_NAME_RUNLIST, null, selection, selectionArgs, null, null, null);
            displayLog("List<AddressItem> getAddressListItem method executed");
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                io.okverify.android.datamodel.AddressItem AddressItem = cursorToAddressListItem(cursor);
                ArtcaffeRunList.add(AddressItem);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (SQLException sqle) {
            displayLog("getAddressListItem error " + sqle.toString());

        } finally {
            close();
        }
        return ArtcaffeRunList;

    }

    public List<io.okverify.android.datamodel.AddressItem> getAllAddressList() {

        displayLog("getAllAddressList method called ");

        List<io.okverify.android.datamodel.AddressItem> artcaffeRunList = new ArrayList<>();
        Cursor cursor;
        try {
            try {
                openRead();
            } catch (Exception e) {
                displayLog("getAllAddressList openRead error " + e.toString());
            }
            cursor = database.query(io.okverify.android.utilities.Constants.TABLE_NAME_RUNLIST, null, null, null, null, null, null);
            displayLog(" List<AddressItem> getAllAddressList() method executed");
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                io.okverify.android.datamodel.AddressItem addressItem = cursorToAddressListItem(cursor);
                artcaffeRunList.add(addressItem);
                cursor.moveToNext();
            }
            cursor.close();

        } catch (Exception sqle) {
            displayLog(" getAllAddressList error " + sqle.toString());
        } finally {
            close();
        }

        return artcaffeRunList;
    }

    public void deleteAllStuff() {
        displayLog("deleteAllStuff() method called");
        try {
            try {
                openWrite();
            } catch (Exception e) {
                displayLog("deleteAllStuff openWrite error " + e.toString());
            }
            database.delete(io.okverify.android.utilities.Constants.TABLE_NAME_STUFF, null, null);
            displayLog("deleteAllStuff() method executed");

        } catch (SQLException sqle) {
            displayLog("deleteAllStuff() error " + sqle.toString());

        } finally {
            close();
        }
    }

    public long insertStuff(String propname, String affiliation) {
        displayLog("insertStuff  method called " + propname + " value " + affiliation);
        long insertId = 0;
        try {
            try {
                openWrite();
            } catch (Exception e) {
                displayLog("insertStuff openWrite error " + e.toString());
            }

            ContentValues values = new ContentValues();
            values.put(io.okverify.android.utilities.Constants.COLUMN_PROPERTY, propname);
            values.put(io.okverify.android.utilities.Constants.COLUMN_VALUE, affiliation);

            insertId = database.insert(io.okverify.android.utilities.Constants.TABLE_NAME_STUFF, null, values);

            displayLog("insertStuff method executed " + insertId);
        } catch (SQLException sqle) {
            displayLog("insertStuff error " + sqle.toString());
        } finally {
            close();
        }
        return insertId;
    }

    public String getPropertyValue(String propertyname) {

        String carupgradedelivered = "";
        Cursor cursor;

        String[] columns = {io.okverify.android.utilities.Constants.COLUMN_VALUE};
        String selection = io.okverify.android.utilities.Constants.COLUMN_PROPERTY + " = ? ";
        String[] selectionArgs = {"" + propertyname};

        try {
            try {
                openRead();
            } catch (Exception e) {
                displayLog("getPropertyValue openRead error " + e.toString());
            }

            cursor = database.query(io.okverify.android.utilities.Constants.TABLE_NAME_STUFF, columns, selection, selectionArgs, null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                carupgradedelivered = cursor.getString(0);
                cursor.moveToNext();
            }

            cursor.close();

        } catch (SQLException sqle) {
            displayLog("getPropertyValue(String propertyname) error " + sqle.toString());
        } finally {
            close();
        }

        if ((carupgradedelivered.startsWith("07")) && (carupgradedelivered.length() == 10)) {
            carupgradedelivered = "+2547" + carupgradedelivered.substring(2);
        } else {
            carupgradedelivered = carupgradedelivered;
        }

        return carupgradedelivered;
    }

    private void displayLog(String log) {
        //Log.i(TAG, log);
    }
}
