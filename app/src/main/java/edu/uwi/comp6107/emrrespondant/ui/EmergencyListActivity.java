package edu.uwi.comp6107.emrrespondant.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;

import java.util.ArrayList;

import edu.uwi.comp6107.emrrespondant.R;
import edu.uwi.comp6107.emrrespondant.managers.Constants;
import edu.uwi.comp6107.emrrespondant.model.Caller;
import edu.uwi.comp6107.emrrespondant.model.Emergency;
import edu.uwi.comp6107.emrrespondant.model.EmergencyStatus;
import edu.uwi.comp6107.emrrespondant.model.Responder;
import edu.uwi.comp6107.emrrespondant.presenters.EmergencyListPresenter;
import edu.uwi.comp6107.emrrespondant.presenters.LocationPresenter;
import edu.uwi.comp6107.emrrespondant.presenters.UserInfoPresenter;

public class EmergencyListActivity extends AppCompatActivity implements EmergencyListPresenter.View, UserInfoPresenter.View, LocationPresenter.LocationListener {

    private static final String TAG = "MDB2:EmergListActivity";

    private RecyclerView recyclerView;
    private EmergencyListAdaptor adaptor;
    private EmergencyListPresenter emergencyListPresenter;
    private UserInfoPresenter userInfoPresenter;
    private LocationPresenter locationPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_list);

        requestLocationPermission();

        locationPresenter = new LocationPresenter(this, this);
        locationPresenter.getLastLocation();
        locationPresenter.checkLocationSettingsAndStartUpdates(this);

        recyclerView = findViewById(R.id.emergency_list_recycler_view);
        adaptor = new EmergencyListAdaptor(this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.scrollToPosition(0);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adaptor);

        setTitle("Active Emergencies");

        emergencyListPresenter = new EmergencyListPresenter(this);
        emergencyListPresenter.getActiveEmergencies();

        userInfoPresenter = new UserInfoPresenter(this);
        userInfoPresenter.getCurrentUser();


    }

    @Override
    public void onUpdateEmergencyList(ArrayList<Emergency> emergencies) {
        adaptor.updateEmegencyList(emergencies);
    }

    @Override
    public void errorRetrievingEmergencyList(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserChanged(Responder responder) {

        adaptor.updateCurrentResponder(responder);
    }

    @Override
    public void didRetrieveSpecifiedCaller(Caller caller) {

    }

    @Override
    public void didGetLastLocation(Location location) {
        userInfoPresenter.updateLocationOfCurrentResponder(location);
        Log.d(TAG, "didGetLastLocation: ");
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
                ActivityCompat.requestPermissions(EmergencyListActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, Constants.PERMISSIONS_REQUEST_ACCESS_LOCATION);

            }

        } else {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // check if you need to show rational behind asking for permission
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {

                    Log.d(TAG, "requestPermission: show permission alert");
                    showPermissionAlert();

                } else {
                    ActivityCompat.requestPermissions(EmergencyListActivity.this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, Constants.PERMISSIONS_REQUEST_ACCESS_LOCATION);

                }
            }
        }

    }

    @Override
    public void didUpdateLocation(LocationResult locationResult) {
//        if(locationResult.getLastLocation() != null) {
//            userInfoPresenter.updateLocationOfCurrentResponder(locationResult.getLastLocation());
//        }
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
                ActivityCompat.requestPermissions(EmergencyListActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, Constants.PERMISSIONS_REQUEST_ACCESS_LOCATION);
            }
        });

        // create and show alert
        builder.create().show();
    }



}
