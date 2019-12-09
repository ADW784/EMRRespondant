package edu.uwi.comp6107.emrrespondant.managers;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.uwi.comp6107.emrrespondant.model.Caller;
import edu.uwi.comp6107.emrrespondant.model.Responder;

public class FirebaseManager {

        private static final String TAG = "MDB:FirebaseManager";

        private static FirebaseManager instance;
        private FirebaseAuth auth;
        private Responder currentUser = null;

        public final DatabaseReference DATABASE_REFERENCE = FirebaseDatabase.getInstance().getReference();

        public final String USERS_REF = "users";
        public final DatabaseReference USERS_DATABASE_REFERENCE = DATABASE_REFERENCE.child(USERS_REF);

        public final String EMERGENCY_REF = "emergency";
        public final String ACTIVE_EMERGENCIES_REF = "active_emergencies";
        public final DatabaseReference ACTIVE_EMERGENCIES_DATABASE_REFERENCE = DATABASE_REFERENCE.child(ACTIVE_EMERGENCIES_REF);
        public final String EMERGENCY_HISTORY_REF = "emergency_history";
        public final DatabaseReference EMERGENCY_HISTORY_DATABASE_REFERENCE = DATABASE_REFERENCE.child(EMERGENCY_HISTORY_REF);

        public final String RESPONDERS_REF = "responders";
        public final DatabaseReference RESPONDERS_DATABASE_REFERENCE = DATABASE_REFERENCE.child(RESPONDERS_REF);


        private FirebaseManager(){
            auth = FirebaseAuth.getInstance();
        };

        public static FirebaseManager getInstance() {
            if(instance == null) {
                instance = new FirebaseManager();
            }
            return instance;
        }

        public boolean isLoggedIn() {
            return auth.getCurrentUser() != null;
        }

        public Responder getCurrentUser() {
            return currentUser;
        }

        public void setCurrentUser(Responder currentUser) {
            this.currentUser = currentUser;
        }


        public DatabaseReference getRefForCurrentUser() {
            return RESPONDERS_DATABASE_REFERENCE.child(auth.getCurrentUser().getUid());
        }

}
