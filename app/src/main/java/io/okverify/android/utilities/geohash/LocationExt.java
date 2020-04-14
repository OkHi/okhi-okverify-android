package io.okverify.android.utilities.geohash;

import android.location.Location;

/**
 * Created by ramogiochola on 9/22/17.
 */

public class LocationExt {

    private static final String PROVIDER = "geohash";

    static Location newLocation(double latitude, double longitude) {
        Location location = new Location(PROVIDER);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }
}
