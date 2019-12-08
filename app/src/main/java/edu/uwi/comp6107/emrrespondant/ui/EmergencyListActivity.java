package edu.uwi.comp6107.emrrespondant.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

import edu.uwi.comp6107.emrrespondant.R;
import edu.uwi.comp6107.emrrespondant.model.Emergency;
import edu.uwi.comp6107.emrrespondant.model.Responder;
import edu.uwi.comp6107.emrrespondant.presenters.EmergencyListPresenter;
import edu.uwi.comp6107.emrrespondant.presenters.UserInfoPresenter;

public class EmergencyListActivity extends AppCompatActivity implements EmergencyListPresenter.View, UserInfoPresenter.View {

    private static final String TAG = "MDB:EmergencyListActivity";

    private RecyclerView recyclerView;
    private EmergencyListAdaptor adaptor;
    private EmergencyListPresenter emergencyListPresenter;
    private UserInfoPresenter userInfoPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_list);

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

}
