package edu.uwi.comp6107.emrrespondant.presenters;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import edu.uwi.comp6107.emrrespondant.managers.FirebaseManager;
import edu.uwi.comp6107.emrrespondant.model.Caller;
import edu.uwi.comp6107.emrrespondant.model.Responder;

public class RegistrationPresenter {

    public interface View {
        void successfullyRegisteredUser();
        void errorRegisteringUser(String email, String message);
        void errorAddingUserToDatabase(String email, String message);
    }

    private static final String TAG = "MDB:Reg..Presenter";

    private FirebaseManager firebaseManager = FirebaseManager.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private View view;

    public RegistrationPresenter(View view) { this.view = view; }

    public void registerNewResponder(final String email, String password, final String firstName, final String lastName) {


        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String uid = task.getResult().getUser().getUid();
                    Log.d(TAG, "registerNewResponder:createUserWithEmail:onComplete: Successfully registered new user with email: " + email);

                    Responder responder = new Responder(email, email, uid, firstName, lastName, null);
                    addNewResponderToDatabase(responder);
                } else {
                    Log.w(TAG, "registerNewResponder:createUserWithEmail:failure", task.getException());
                    view.errorRegisteringUser(email, task.getException().getLocalizedMessage());
                }
            }
        });

    }

    public void addNewResponderToDatabase(final Responder responder) {

        final FirebaseManager firebaseManager = FirebaseManager.getInstance();
        firebaseManager.RESPONDERS_DATABASE_REFERENCE.child(responder.uid).setValue(responder)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Successfully added responder to database: " + responder.toString());
                firebaseManager.setCurrentUser(responder);
                view.successfullyRegisteredUser();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to add responder to database: " + responder.toString() + " error: " + e.getLocalizedMessage());
                view.errorAddingUserToDatabase(responder.email, e.getLocalizedMessage());
            }
        });
    }

}
