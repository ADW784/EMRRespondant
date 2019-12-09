package edu.uwi.comp6107.emrrespondant.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.LocationResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.uwi.comp6107.emrrespondant.R;
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
        String callerId = intent.getStringExtra("CALLER_ID");
        emergencyPresenter.getEmergencyDetailsForUserWithId(callerId);

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
        if(currentEmergency != null && currentEmergency.status.equals(EmergencyStatus.INPROGRESS)){
            Log.d(TAG, "onBackPressed: Override this to do nothing.");
        } else {
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
            if(emergency.responder.currentLocation != null && emergency.location != null) {
                Float distance = emergency.location.distanceTo(emergency.responder.currentLocation);
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

    @Override
    public void didRetrieveEmergencyDetails(Emergency emergency) {
        if(currentCaller == null) {
            userInfoPresenter.getUserWithUid(emergency.callerId);
        }
        currentEmergency = emergency;
        updateUI(emergency);
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

    }

    @Override
    public void didUpdateLocation(LocationResult locationResult) {

    }

    @Override
    public void onUserChanged(Responder responder) {
        emergencyPresenter.updateResponder(responder);
    }

    @Override
    public void didRetrieveSpecifiedCaller(Caller caller) {
        currentCaller = caller;
        updateCallerInfo(caller);
    }
}
