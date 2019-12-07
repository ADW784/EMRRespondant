package edu.uwi.comp6107.emrrespondant.presenters;

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
