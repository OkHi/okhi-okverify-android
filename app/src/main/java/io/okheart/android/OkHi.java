package io.okheart.android;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

public final class OkHi extends ContentProvider {

    private static final String TAG = "OkHi";
    private static String firstname,lastname, phonenumber, color, name, logo;
    private static Context mContext;
    private static OkHiCallback callback;

    public static void initialize(String applicationKey){
        try {
            displayLog("okhi initialized");
            writeToFile(applicationKey);
        } catch (Exception io){

        } finally {

        }

    }

    public static void customize(String color, String name, String logo){
        try {
            displayLog("okhi customized");
            JSONObject jsonObject = new JSONObject();
            if(color != null){
                if(color.length() > 0){
                    jsonObject.put("color", color);
                }
            }
            if(name != null){
                if(name.length() > 0){
                    jsonObject.put("name", name);
                }
            }

            if(logo != null){
                if(logo.length() > 0){
                    jsonObject.put("logo", logo);
                }
            }
            String customString = jsonObject.toString();
            displayLog("logo "+jsonObject.get("logo"));
            String testString = "{\"color\":\""+color+"\", \"name\": \""+name+"\",\"logo\": \""+logo+"\"}";

            displayLog("custom string "+customString);
            displayLog(testString);
            writeToFileCustomize(testString);
        } catch (Exception io){

        } finally {

        }

    }


    public static void displayClient(OkHiCallback okHiCallback, JSONObject jsonObject) {

        displayLog("display client "+jsonObject.toString());

        callback = okHiCallback;
        firstname = jsonObject.optString("firstName");
        lastname = jsonObject.optString("lastName");
        phonenumber = jsonObject.optString("phone");


        try {
            Intent intent = new Intent( mContext, OkHeartActivity.class);
            intent.putExtra("firstname", firstname);
            intent.putExtra("lastname", lastname);
            intent.putExtra("phone", phonenumber);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } catch (Exception e) {
            displayLog("error calling receiveActivity activity " + e.toString());
        }
    }

    public static void checkInternet(){
        try{
            HeartBeatCallBack heartBeatCallBack = new HeartBeatCallBack() {
                @Override
                public void querycomplete(Boolean response) {
                    if(response){

                    }
                    else{
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("message", "fatal_exit");
                            JSONObject jsonObject1 = new JSONObject();
                            jsonObject1.put("Error","Network error");
                            jsonObject.put("payload",jsonObject1);
                            displayLog(jsonObject.toString());
                            callback.querycomplete(jsonObject);
                        }
                        catch (JSONException jse){
                            displayLog("json error "+jse.toString());
                        }
                    }
                }
            };
            HeartBeatTask heartBeatTask = new HeartBeatTask(heartBeatCallBack);
            heartBeatTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        catch (Exception e){
            displayLog("check internet error "+e.toString());
        }
    }

    private static void displayLog(String log){
        Log.i(TAG,log);
    }


    public OkHi() {
    }

    @Override
    public boolean onCreate() {
        // get the context (Application context)
        mContext = getContext();
        //initialize whatever you need
        return true;

    }

    private static void writeToFile(String customString ){
        try {
            File path = mContext.getFilesDir();
            File file = new File(path, "okheart.txt");
            if (!file.exists()) {
                FileOutputStream stream = new FileOutputStream(file);
                try {

                    stream.write(customString.getBytes());
                } catch (Exception e) {
                    displayLog("filestream error " + e.toString());
                } finally {
                    stream.close();
                }
            }
            else{
                file.delete();
                FileOutputStream stream = new FileOutputStream(file);
                try {

                    stream.write(customString.getBytes());
                } catch (Exception e) {
                    displayLog("filestream error " + e.toString());
                } finally {
                    stream.close();
                }
            }

        }
        catch (Exception e){
            displayLog("write to file error "+e.toString());

        }

    }


    private static void writeToFileCustomize(String apiKey ){
        try {
            File path = mContext.getFilesDir();
            File file = new File(path, "custom.txt");
            if (!file.exists()) {
                FileOutputStream stream = new FileOutputStream(file);
                try {

                    stream.write(apiKey.getBytes());
                } catch (Exception e) {
                    displayLog("filestream error " + e.toString());
                } finally {
                    stream.close();
                }
            }
            else{
                file.delete();
                FileOutputStream stream = new FileOutputStream(file);
                try {

                    stream.write(apiKey.getBytes());
                } catch (Exception e) {
                    displayLog("filestream error " + e.toString());
                } finally {
                    stream.close();
                }
            }

        }
        catch (Exception e){
            displayLog("write to file error "+e.toString());

        }

    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    public static OkHiCallback getCallback() {
        return callback;
    }

    public static void setCallback(OkHiCallback callback) {
        OkHi.callback = callback;
    }
}
