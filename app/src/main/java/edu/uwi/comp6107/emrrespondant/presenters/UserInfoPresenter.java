package edu.uwi.comp6107.emrrespondant.presenters;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import edu.uwi.comp6107.emrrespondant.managers.FirebaseManager;
import edu.uwi.comp6107.emrrespondant.model.Caller;
import edu.uwi.comp6107.emrrespondant.model.Responder;

public class UserInfoPresenter {

    private static final String TAG = "MDB:UserInfoPresenter";

    public interface View {
        void onUserChanged(Responder responder);
        void didRetrieveSpecifiedCaller(Caller caller);
    }

    View view;

    FirebaseManager firebaseManager = FirebaseManager.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public UserInfoPresenter(View view) { this.view = view; }

    public void getCurrentUser() {

        if(firebaseManager.isLoggedIn()) {

            firebaseManager.RESPONDERS_DATABASE_REFERENCE.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {

                        Responder responder = dataSnapshot.getValue(Responder.class);
                        if(responder != null) {
                            firebaseManager.setCurrentUser(responder);
                            view.onUserChanged(responder);
                            Log.d(TAG, "getCurrentUser:onDataChange: current user set to: " + responder.toString());
                        } else {
                            Log.d(TAG, "getCurrentUser:onDataChange: could not get caller from snapshot: " + dataSnapshot.toString());
                        }
                    } else {
                        Log.d(TAG, "getCurrentUser:onDataChange: snapshot does not exist!");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "getCurrentUser:onCancelled: Unable to get current user! Error:" + databaseError.getMessage());
                }
            });

        }

    }

    public void getUserWithUid(String uid){

        firebaseManager.USERS_DATABASE_REFERENCE.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Caller caller = dataSnapshot.getValue(Caller.class);
                    if(caller != null) {
                        Log.d(TAG, "getUserWithUid:onDataChange: " + caller);
                        view.didRetrieveSpecifiedCaller(caller);
                    } else {
                        Log.d(TAG, "getUserWithUid:onDataChange: caller is null, snapshot data: " + dataSnapshot.toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "getUserWithUid:onCancelled: " + databaseError.getDetails());
            }
        });

    }

    public void deleteEmergencyForUserWithUid(final String uid){

        firebaseManager.USERS_DATABASE_REFERENCE.child(uid).child(firebaseManager.EMERGENCY_REF).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "deleteEmergencyForUserWithUid:onSuccess: uid:" + uid);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "deleteEmergencyForUserWithUid:onFailure: " + e.getMessage());
                    }
                });


    }


    public void updateLocationOfCurrentResponder(Location location) {
        firebaseManager.RESPONDERS_DATABASE_REFERENCE.child(firebaseAuth.getCurrentUser().getUid()).child("currentLocation").setValue(location)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "updateLocationOfCurrentResponder:onSuccess: ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "updateLocationOfCurrentResponder:onFailure: " + e.getMessage());
                    }
                });
    }


    public void updateResponderProfile(final Responder responder) {
        DatabaseReference userRef = firebaseManager.RESPONDERS_DATABASE_REFERENCE.child(responder.uid);

        Map<String, Object> childUpdates = new HashMap<>();
        if(responder.firstName != null && !responder.firstName.isEmpty()) { childUpdates.put("firstName", responder.firstName); }
        if(responder.lastName != null && !responder.lastName.isEmpty()) { childUpdates.put("lastName", responder.lastName); }

        userRef.updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "updateResponderProfile:onSuccess: " + responder);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "updateResponderProfile:onFailure: " + e.getMessage());
                    }
                });
    }

}
