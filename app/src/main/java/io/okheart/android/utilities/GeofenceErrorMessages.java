package io.okheart.android.utilities;

import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.GeofenceStatusCodes;


public class GeofenceErrorMessages {
    /**
     * Prevents instantiation.
     */
    public GeofenceErrorMessages() {
    }

    /**
     * Returns the error string for a geofencing exception.
     */
    public static String getErrorString(Context context, Exception e) {
        if (e instanceof ApiException) {
            return getErrorString(context, ((ApiException) e).getStatusCode());
        } else {
            return context.getResources().getString(io.okheart.android.R.string.unknown_geofence_error);
        }
    }

    /**
     * Returns the error string for a geofencing error code.
     */
    public static String getErrorString(Context context, int errorCode) {
        Resources mResources = context.getResources();
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return mResources.getString(io.okheart.android.R.string.geofence_not_available);
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return mResources.getString(io.okheart.android.R.string.geofence_too_many_geofences);
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return mResources.getString(io.okheart.android.R.string.geofence_too_many_pending_intents);
            default:
                return mResources.getString(io.okheart.android.R.string.unknown_geofence_error);
        }
    }
}
