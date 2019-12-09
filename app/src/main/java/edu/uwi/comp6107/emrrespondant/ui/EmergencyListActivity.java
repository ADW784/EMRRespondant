package edu.uwi.comp6107.emrrespondant.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;

import java.util.ArrayList;

import edu.uwi.comp6107.emrrespondant.R;
import edu.uwi.comp6107.emrrespondant.model.Caller;
import edu.uwi.comp6107.emrrespondant.model.Emergency;
import edu.uwi.comp6107.emrrespondant.model.Responder;
import edu.uwi.comp6107.emrrespondant.presenters.EmergencyListPresenter;
import edu.uwi.comp6107.emrrespondant.presenters.LocationPresenter;
import edu.uwi.comp6107.emrrespondant.presenters.UserInfoPresenter;

public class EmergencyListActivity extends AppCompatActivity implements EmergencyListPresenter.View, UserInfoPresenter.View, LocationPresenter.LocationListener {

    private static final String TAG = "MDB:EmergencyListActivity";

    private RecyclerView recyclerView;
    private EmergencyListAdaptor adaptor;
    private EmergencyListPresenter emergencyListPresenter;
    private UserInfoPresenter userInfoPresenter;
    private LocationPresenter locationPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_list);

        locationPresenter = new LocationPresenter(this, this);
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
    }

    @Override
    public void didFailToGetLastLocation(String message) {

    }

    @Override
    public void requestLocationPermission() {

    }

    @Override
    public void didUpdateLocation(LocationResult locationResult) {
//        if(locationResult.getLastLocation() != null) {
//            userInfoPresenter.updateLocationOfCurrentResponder(locationResult.getLastLocation());
//        }
    }
}
