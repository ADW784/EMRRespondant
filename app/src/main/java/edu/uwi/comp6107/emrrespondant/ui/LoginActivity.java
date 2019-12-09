package edu.uwi.comp6107.emrrespondant.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import edu.uwi.comp6107.emrrespondant.R;
import edu.uwi.comp6107.emrrespondant.managers.FirebaseManager;
import edu.uwi.comp6107.emrrespondant.presenters.LoginPresenter;

public class LoginActivity extends AppCompatActivity implements LoginPresenter.View {

    private static final String TAG = "MDB:LoginActivity";

    private LoginPresenter loginPresenter;

    TextView register;
    EditText emailEditText;
    EditText passwordEditText;
    Button loginButton;
    Button guestButton;

    FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginPresenter = new LoginPresenter(this);
        firebaseManager = FirebaseManager.getInstance();

        register = findViewById(R.id.register_textView);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

        emailEditText = findViewById(R.id.editTextUserEmail);
        passwordEditText = findViewById(R.id.editTextUserPassword);

        loginButton = findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInputs()) {
                    loginPresenter.signInWith(String.valueOf(emailEditText.getText()), String.valueOf(passwordEditText.getText()));
                }
            }
        });

        setTitle("Login");

        if(firebaseManager.isLoggedIn()){
            loginPresenter.setCurrentUserWithUID(FirebaseAuth.getInstance().getUid());
            goToEmergencyListActivity();
        }

    }


    private boolean validateInputs() {

        boolean success = true;

        if(String.valueOf(emailEditText.getText()).trim().isEmpty()) {
            success = false;
            emailEditText.setError("Email cannot be empty!");
        }

        if(String.valueOf(passwordEditText.getText()).trim().isEmpty()) {
            success = false;
            passwordEditText.setError("Password cannot be empty!");
        }

        return success;
    }


    private void  goToEmergencyListActivity() {
        Intent intent = new Intent(LoginActivity.this, EmergencyListActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSuccessfulSignIn() {
        goToEmergencyListActivity();
    }

    @Override
    public void onSignInError(String email, String message) {

    }
}
