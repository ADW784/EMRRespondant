package edu.uwi.comp6107.emrrespondant.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.LocationResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.uwi.comp6107.emrrespondant.R;
import edu.uwi.comp6107.emrrespondant.model.Emergency;
import edu.uwi.comp6107.emrrespondant.model.EmergencyStatus;
import edu.uwi.comp6107.emrrespondant.presenters.EmergencyPresenter;
import edu.uwi.comp6107.emrrespondant.presenters.LocationPresenter;

public class EmergencyActivity extends AppCompatActivity implements EmergencyPresenter.View, LocationPresenter.LocationListener {

    EmergencyPresenter emergencyPresenter;
    Emergency currentEmergency;
    LocationPresenter locationPresenter;

    TextView timestampTextView;
    TextView statusTextView;
    TextView descriptionTextView;
    TextView responderInfoTextView;
    Button cancelEmergencyButton;

    View.OnClickListener cancelEmergencyListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            emergencyPresenter.upDateEmergencyStatusForCurrentUser(EmergencyStatus.CANCELLED);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        emergencyPresenter = new EmergencyPresenter(this);
        emergencyPresenter.getEmergencyDetailsForCurrentUser();

        locationPresenter = new LocationPresenter(this, this);
        locationPresenter.checkLocationSettingsAndStartUpdates(this);

        timestampTextView = findViewById(R.id.timestamp_textView);
        statusTextView = findViewById(R.id.status_textView);
        descriptionTextView = findViewById(R.id.description_textView);
        responderInfoTextView = findViewById(R.id.responder_info_textView);

        cancelEmergencyButton = findViewById(R.id.cancel_emergency_button);
        cancelEmergencyButton.setOnClickListener(cancelEmergencyListener);

        setTitle("Emergency");
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationPresenter.stopLocationUpdates();
    }

    private void updateUI(Emergency emergency) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(emergency.timestamp);
        timestampTextView.setText(dateFormat.format(date));

        statusTextView.setText(emergency.status);
        descriptionTextView.setText(emergency.description);

        String responderInfo = "";


        if(emergency.responder != null) {

            if(emergency.responder.firstName != null){
                responderInfo = emergency.responder.firstName + " has responded to your emergency.";

                if(emergency.responder.currentLocation != null && emergency.location != null) {
                    Float distance = emergency.location.distanceTo(emergency.responder.currentLocation);
                    Float distanceInKm = distance/1000;
                    String stringDistance = String.format("%1.2f", distanceInKm);
                    responderInfo += " " + emergency.responder.firstName + " is " + stringDistance + "km away.";
                }
            }
        }

        responderInfoTextView.setText(responderInfo);

    }

    private void goBackToEmergencyListActivity() {

        Intent intent = new Intent(EmergencyActivity.this, EmergencyListActivity.class);
        startActivity(intent);

    }

    @Override
    public void didRetrieveEmergencyDetails(Emergency emergency) {
        updateUI(emergency);
        currentEmergency = emergency;
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
        if(currentEmergency != null && currentEmergency.id != null) {
            emergencyPresenter.updateEmergencyLocation(location);
        }
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
}
