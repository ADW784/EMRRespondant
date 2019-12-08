package edu.uwi.comp6107.emrrespondant.presenters;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import edu.uwi.comp6107.emrrespondant.managers.FirebaseManager;
import edu.uwi.comp6107.emrrespondant.model.Emergency;

public class EmergencyListPresenter {

    private static final String TAG = "MDB:EmergListPresenter";

    public interface View {
        void onUpdateEmergencyList(ArrayList<Emergency> emergencies);
        void errorRetrievingEmergencyList(String message);
    }

    private View view;

    public EmergencyListPresenter(View view) {this.view = view;}

    public void getActiveEmergencies() {
        FirebaseManager.getInstance().ACTIVE_EMERGENCIES_DATABASE_REFERENCE.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Emergency> emergencies = new ArrayList<>();
                for (DataSnapshot emergencySnapshot: dataSnapshot.getChildren()){
                    Emergency emergency = emergencySnapshot.getValue(Emergency.class);
                    if(emergency != null) {
                        emergencies.add(emergency);
                    } else {
                        Log.d(TAG, "getActiveEmergencies:onDataChange: emergency is null, check snapshot:" + emergencySnapshot);
                    }
                }

                view.onUpdateEmergencyList(emergencies);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "getActiveEmergencies:onCancelled: " + databaseError.getDetails());
                view.errorRetrievingEmergencyList(databaseError.getMessage());
            }
        });
    }

}
