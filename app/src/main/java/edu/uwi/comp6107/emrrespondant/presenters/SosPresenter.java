package edu.uwi.comp6107.emrrespondant.presenters;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.uwi.comp6107.emrrespondant.managers.FirebaseManager;
import edu.uwi.comp6107.emrrespondant.model.CustomLocation;
import edu.uwi.comp6107.emrrespondant.model.Emergency;
import edu.uwi.comp6107.emrrespondant.model.EmergencyStatus;

public class SosPresenter {

    public interface View {
        void didCreateEmergency(Emergency emergency);
        void errorCreatingEmergency(String message);
    }

    private static final String TAG = "MDB:SosPresenter";

    private View view;
    private FirebaseManager firebaseManager = FirebaseManager.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public SosPresenter(View view) { this.view = view; }

    public void createNewEmergency(Location location, String description) {

        Date date = new Date();

        String emergencyId = firebaseManager.ACTIVE_EMERGENCIES_DATABASE_REFERENCE.push().getKey();

        String currentUid = auth.getCurrentUser().getUid();

        CustomLocation customLocation = new CustomLocation(location);

        final Emergency emergency = new Emergency(emergencyId, date.getTime(), currentUid, null, description, EmergencyStatus.CREATED, customLocation);

        Map<String, Object> emergencyMapped = emergency.toMap();


        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(firebaseManager.ACTIVE_EMERGENCIES_REF + "/" + emergencyId, emergencyMapped);
        childUpdates.put(firebaseManager.USERS_REF + "/" + currentUid + "/" + firebaseManager.EMERGENCY_REF, emergencyMapped);

        firebaseManager.DATABASE_REFERENCE.updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "createNewEmergency:onSuccess: ");
                        view.didCreateEmergency(emergency);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "createNewEmergency:onFailure: " + e.getMessage());
                        view.errorCreatingEmergency(e.getLocalizedMessage());
                    }
                });

    }
}
