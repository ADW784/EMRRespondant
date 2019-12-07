package edu.uwi.comp6107.emrrespondant.presenters;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import edu.uwi.comp6107.emrrespondant.managers.Constants;

public class LocationPresenter {

    public interface LocationListener {
        void didGetLastLocation(Location location);
        void didFailToGetLastLocation(String message);
        void requestLocationPermission();
        void didUpdateLocation(LocationResult locationResult);
    }

    private LocationListener locationListener;
    private Context context;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private static final String TAG = "MDB:LocationPresenter";

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                Log.d(TAG, "locationCallback:onLocationResult: result is null!");
                return;
            }
//            for (Location location : locationResult.getLocations()) {
//                // Update UI with location data
            locationListener.didUpdateLocation(locationResult);
            //Log.d(TAG, "locationCallback:onLocationResult: location result:" + locationResult);
//            }
            if(locationResult.getLastLocation() != null ){
                locationListener.didGetLastLocation(locationResult.getLastLocation());
                //Log.d(TAG, "locationCallback:onLocationResult: last location:" + locationResult.getLastLocation());
            }
        }
    };

    public LocationPresenter(LocationListener listener, Context context) {
        this.locationListener = listener;
        this.context = context;
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        createLocationRequest();
    }

    public void getLastLocation() {

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null) {
                    Log.d(TAG, "getLastLocation:onSuccess: Location: " + location);
                    locationListener.didGetLastLocation(location);
                } else {
                    Log.d(TAG, "getLastLocation:onSuccess: location is null!");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "getLastLocation:onFailure: Location failure: error: " + e.getLocalizedMessage());
                locationListener.didFailToGetLastLocation(e.getLocalizedMessage());
            }
        });

    }


    // create location request
    protected void createLocationRequest() {
        this.locationRequest = LocationRequest.create();
        this.locationRequest.setInterval(10000);
        this.locationRequest.setFastestInterval(5000);
        this.locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }




    // start location updates
    private void startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates: ");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    public void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }


    // get location settings
    public void checkLocationSettingsAndStartUpdates(final Activity activity) {

        // get location settings
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        // check for permissions
        SettingsClient client = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        // prompt user to change location settings
        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                //createLocationRequest();
                Log.d(TAG, "checkLocationSettingsAndStartUpdates:onSuccess: response" + locationSettingsResponse);
                startLocationUpdates();
            }
        });

        task.addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "checkLocationSettingsAndStartUpdates:onFailure: error:" + e.getMessage());

                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(activity, Constants.REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

}
