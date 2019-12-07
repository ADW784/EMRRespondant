package edu.uwi.comp6107.emrrespondant.model;

import android.location.Location;

public class CustomLocation extends Location {

    public CustomLocation() {
        super("fused");
    }

    public CustomLocation(String provider) {
        super(provider);
    }

    public CustomLocation(Location l) {
        super(l);
    }

}
