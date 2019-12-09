package edu.uwi.comp6107.emrrespondant.presenters;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import edu.uwi.comp6107.emrrespondant.managers.FirebaseManager;
import edu.uwi.comp6107.emrrespondant.model.Caller;
import edu.uwi.comp6107.emrrespondant.model.Responder;

public class LoginPresenter {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseManager firebaseManager = FirebaseManager.getInstance();

    public interface View {
        void onSuccessfulSignIn();
        void onSignInError(String email, String message);
    }

    private static final String TAG = "MDB:LoginPresenter";

    private View view;

    public LoginPresenter(View view) { this.view = view; }


    public void signInWith(final String email, String password) {

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser user = task.getResult().getUser();
                    Log.d(TAG, "signInWith:onComplete: Successfully signed in with user: " + user);
                    // TODO: Check for database user before proceeding!
                    setCurrentUserWithUID(user.getUid());
                    view.onSuccessfulSignIn();
                } else {
                    Log.w(TAG, "signInWith:createUserWithEmail:failure", task.getException());
                    view.onSignInError(email, task.getException().getLocalizedMessage());
                }
            }
        });
    }


    public void setCurrentUserWithUID(String uid) {

        firebaseManager.RESPONDERS_DATABASE_REFERENCE.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Responder responder = dataSnapshot.getValue(Responder.class);
                    if(responder != null) {
                        FirebaseManager.getInstance().setCurrentUser(responder);
                        Log.d(TAG, "setCurrentUserWithUID:onDataChange: current user set to: " + responder.toString());
                    } else {
                        Log.d(TAG, "setCurrentUserWithUID:onDataChange: could not get caller from snapshot: " + dataSnapshot.toString());
                    }
                } else {
                    Log.d(TAG, "setCurrentUserWithUID:onDataChange: snapshot does not exist!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "setCurrentUserWithUID:onCancelled: Unable to set current user! Error:" + databaseError.getMessage());
            }
        });
    }

}
