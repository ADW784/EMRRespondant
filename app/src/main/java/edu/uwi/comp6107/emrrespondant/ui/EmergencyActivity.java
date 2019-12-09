package edu.uwi.comp6107.emrrespondant.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.uwi.comp6107.emrrespondant.R;
import edu.uwi.comp6107.emrrespondant.helpers.AlertHelper;
import edu.uwi.comp6107.emrrespondant.managers.Constants;
import edu.uwi.comp6107.emrrespondant.model.Caller;
import edu.uwi.comp6107.emrrespondant.model.Emergency;
import edu.uwi.comp6107.emrrespondant.model.EmergencyStatus;
import edu.uwi.comp6107.emrrespondant.model.Responder;
import edu.uwi.comp6107.emrrespondant.presenters.EmergencyPresenter;
import edu.uwi.comp6107.emrrespondant.presenters.LocationPresenter;
import edu.uwi.comp6107.emrrespondant.presenters.UserInfoPresenter;

public class EmergencyActivity extends AppCompatActivity implements EmergencyPresenter.View, LocationPresenter.LocationListener, UserInfoPresenter.View {

    EmergencyPresenter emergencyPresenter;
    Emergency currentEmergency;
    LocationPresenter locationPresenter;
    UserInfoPresenter userInfoPresenter;
    Caller currentCaller;
    Responder currentResponder;
    String retrievedEmergencyId;
    String retrievedCallerId;

    TextView timestampTextView;
    TextView statusTextView;
    TextView descriptionTextView;
    TextView distanceTextView;
    TextView allergiesTextView;
    TextView medicationTextView;
    TextView doctorTextView;
    Button getDirectionsButton;
    Button respondToEmergencyButton;

    private static final String TAG = "MDB:EmergencyActivity";

    View.OnClickListener getDirectionsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(currentEmergency != null && currentResponder != null) {
                if(currentEmergency.location != null && currentResponder.currentLocation != null) {

                    // Create a Uri from an intent string. Use the result to create an Intent.
                    Double latitude = currentEmergency.location.getLatitude();
                    Double longitude = currentEmergency.location.getLongitude();
                    Double startLatitude = currentResponder.currentLocation.getLatitude();
                    Double startLongitude = currentResponder.currentLocation.getLongitude();
                    String name = "Emergency Location";
                    String query = "saddr=" + startLatitude.toString() + "," + startLongitude.toString() + "&daddr=" + latitude + "," + longitude;
                    Uri gmmIntentUri = Uri.parse(Constants.GOOGLE_MAPS_DIRECTIONS_BASE_API + query);

                    Log.d(TAG, "getDirectionsListener:onClick: uri: " + gmmIntentUri);

                    // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                    // Make the Intent explicit by setting the Google Maps package
                    mapIntent.setPackage("com.google.android.apps.maps");

                    // Attempt to start an activity that can handle the Intent
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);

                    } else {
                        AlertHelper.showSimpleAlertDiag(EmergencyActivity.this, "Directions Error", "No application installed to handle directions. Install GoogleMaps from the play store to use the directions feature.");
                    }
                } else {
                    Toast.makeText(EmergencyActivity.this, "No location set for current responder. Please allow the app perssion to access location services to get directions!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    View.OnClickListener respondToEmergencyListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(currentEmergency.status.equals(EmergencyStatus.CREATED.toString())) {
                showRespondToEmergencyDialogue();
            } else {
                showResolveEmergencyDialogue();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        userInfoPresenter = new UserInfoPresenter(this);
        userInfoPresenter.getCurrentUser();

        emergencyPresenter = new EmergencyPresenter(this);
        Intent intent = getIntent();
        retrievedCallerId = intent.getStringExtra("CALLER_ID");
        retrievedEmergencyId = intent.getStringExtra("EMERGENCY_ID");
        emergencyPresenter.getEmergencyDetailsForUserWithId(retrievedCallerId);

        locationPresenter = new LocationPresenter(this, this);
        locationPresenter.checkLocationSettingsAndStartUpdates(this);

        timestampTextView = findViewById(R.id.timestamp_textView);
        statusTextView = findViewById(R.id.status_textView);
        descriptionTextView = findViewById(R.id.description_textView);
        distanceTextView = findViewById(R.id.distance_textView);
        allergiesTextView = findViewById(R.id.allergies_textView);
        medicationTextView = findViewById(R.id.medication_textView);
        doctorTextView = findViewById(R.id.doctor_textView);

        getDirectionsButton = findViewById(R.id.get_directions_button);
        getDirectionsButton.setOnClickListener(getDirectionsListener);

        respondToEmergencyButton = findViewById(R.id.respond_to_emergency_button);
        respondToEmergencyButton.setOnClickListener(respondToEmergencyListener);

        setTitle("Emergency");
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationPresenter.stopLocationUpdates();
    }

    @Override
    public void onBackPressed() {
        if(currentEmergency != null && currentEmergency.status.equals(EmergencyStatus.INPROGRESS.toString())){
            Log.d(TAG, "onBackPressed: Override this to do nothing.");
        } else {
            Log.d(TAG, "onBackPressed: Allowed.");
            super.onBackPressed();
        }
    }

    private void updateUI(Emergency emergency) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(emergency.timestamp);
        timestampTextView.setText(dateFormat.format(date));

        statusTextView.setText(emergency.status);

        if(emergency.description == null || emergency.description.isEmpty()) {
            descriptionTextView.setText("Caller did not submit any emergency details.");
        } else {
            descriptionTextView.setText(emergency.description);
        }


        String responderInfo = "";
        if(emergency.responder != null){
            if(currentResponder.currentLocation != null && emergency.location != null) {
                Float distance = emergency.location.distanceTo(currentResponder.currentLocation);
                Float distanceInKm = distance/1000;
                String stringDistance = String.format("%1.2f", distanceInKm);
                responderInfo += "The emergency is "
                        + stringDistance + "km away, at latitude: "
                        + emergency.location.getLatitude() + ", longitude: "
                        + emergency.location.getLongitude();
            }
        }
        distanceTextView.setText(responderInfo);

        if(emergency.status.equals(EmergencyStatus.CREATED.toString())){
            respondToEmergencyButton.setText("Respond to Emergency");
        } else {
            respondToEmergencyButton.setText("Resolve Emergency");
        }

    }

    private void updateCallerInfo(Caller caller){
        allergiesTextView.setText(caller.allergies);
        medicationTextView.setText(caller.medication);
        doctorTextView.setText(caller.doctor);
    }

    private void goBackToEmergencyListActivity() {

        Intent intent = new Intent(EmergencyActivity.this, EmergencyListActivity.class);
        startActivity(intent);

    }

    private void showRespondToEmergencyDialogue() {

        // create an alert dialogue builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // configure title and message for the alert
        builder.setTitle("Respond to Emergency");
        builder.setMessage("Are you sure you want to respond to this emergency?");

        // add acknowledgement button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setPositiveButton("Respond", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                emergencyPresenter.upDateEmergencyStatus(currentEmergency, EmergencyStatus.INPROGRESS);
                emergencyPresenter.updateResponder(currentEmergency, currentResponder);
            }
        });

        // create and show alert
        builder.create().show();
    }

    private void showResolveEmergencyDialogue() {

        // create an alert dialogue builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // configure title and message for the alert
        builder.setTitle("Resolve to Emergency");
        builder.setMessage("Are you sure you want to set this emergency as resolved?");

        // add acknowledgement button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setPositiveButton("Set as Resolved", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                emergencyPresenter.upDateEmergencyStatus(currentEmergency, EmergencyStatus.RESOLVED);
            }
        });

        // create and show alert
        builder.create().show();
    }

    private void showPermissionAlert() {
        // create an alert dialogue builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // configure title and message for the alert
        builder.setTitle("Location Access Permission");
        builder.setMessage("The app requires your permission to access your location in order to provide directions to emergencies.");

        // add cancel button
        builder.setNegativeButton("Do not Allow", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // add grant access button
        builder.setPositiveButton("Grant Access", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(EmergencyActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, Constants.PERMISSIONS_REQUEST_ACCESS_LOCATION);
            }
        });

        // create and show alert
        builder.create().show();
    }

    @Override
    public void didRetrieveEmergencyDetails(Emergency emergency) {

        if(emergency.id == null) {
            emergencyPresenter.deleteActiveEmergencyWithId(retrievedEmergencyId);
            userInfoPresenter.deleteEmergencyForUserWithUid(retrievedCallerId);
            goBackToEmergencyListActivity();
        } else {

            if(currentCaller == null) {
                userInfoPresenter.getUserWithUid(emergency.callerId);
            }

            currentEmergency = emergency;
            updateUI(emergency);
        }
    }

    @Override
    public void didArchiveEmergency(Emergency emergency) {

    }

    @Override
    public void didRemoveEmergencyFromActiveList(Emergency emergency) {

    }

    @Override
    public void didRemoveEmergencyFromUser(Emergency emergency) {

        goBackToEmergencyListActivity();
    }

    @Override
    public void errorRemovingEmergencyFromUser(String message) {

    }

    @Override
    public void errorRetrievingEmergencyDetails(String message) {

    }

    @Override
    public void errorArchivingEmergency(String message) {

    }

    @Override
    public void errorRemovingEmergencyFromActiveList(String message) {

    }

    @Override
    public void shouldDeleteCurrentEmergency() {
        Log.d(TAG, "shouldDeleteCurrentEmergency: retrievedEmergencyId" + retrievedEmergencyId);
        emergencyPresenter.deleteActiveEmergencyWithId(retrievedEmergencyId);
        goBackToEmergencyListActivity();
    }

    @Override
    public void didGetLastLocation(Location location) {
//        if(currentEmergency != null && currentEmergency.id != null) {
//            emergencyPresenter.updateEmergencyLocation(location);
//        }
        userInfoPresenter.updateLocationOfCurrentResponder(location);
    }

    @Override
    public void didFailToGetLastLocation(String message) {

    }

    @Override
    public void requestLocationPermission() {

        // check if permission has already been given
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // permission not granted

            // check if you need to show rational behind asking for permission
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                Log.d(TAG, "requestPermission: show permission alert");
                showPermissionAlert();

            } else {
                ActivityCompat.requestPermissions(EmergencyActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, Constants.PERMISSIONS_REQUEST_ACCESS_LOCATION);

            }

        } else {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // check if you need to show rational behind asking for permission
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {

                    Log.d(TAG, "requestPermission: show permission alert");
                    showPermissionAlert();

                } else {
                    ActivityCompat.requestPermissions(EmergencyActivity.this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, Constants.PERMISSIONS_REQUEST_ACCESS_LOCATION);

                }
            }
        }

    }

    @Override
    public void didUpdateLocation(LocationResult locationResult) {

    }

    @Override
    public void onUserChanged(Responder responder) {
        currentResponder = responder;
        if(currentEmergency != null && currentEmergency.responder == responder) {
            emergencyPresenter.updateResponder(currentEmergency, responder);
        }
    }

    @Override
    public void didRetrieveSpecifiedCaller(Caller caller) {
        currentCaller = caller;
        if(caller.emergency != null && (caller.emergency.id == null || caller.emergency.id.isEmpty())) {
            userInfoPresenter.deleteEmergencyForUserWithUid(caller.uid);
            emergencyPresenter.deleteActiveEmergencyWithId(retrievedCallerId);
            Log.d(TAG, "didRetrieveSpecifiedCaller: bad emergency in caller" );
            goBackToEmergencyListActivity();

        }
        updateCallerInfo(caller);
    }
}
