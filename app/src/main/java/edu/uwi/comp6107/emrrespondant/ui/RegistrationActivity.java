package edu.uwi.comp6107.emrrespondant.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import edu.uwi.comp6107.emrrespondant.R;
import edu.uwi.comp6107.emrrespondant.helpers.AlertHelper;
import edu.uwi.comp6107.emrrespondant.presenters.RegistrationPresenter;

public class RegistrationActivity extends AppCompatActivity implements RegistrationPresenter.View{

    Button buttonRegister;
    TextView textViewLoginLink;

    EditText firstName;
    EditText lastName;
    EditText email;
    EditText password;
    EditText confirmPassword;

    private static final String TAG = "MDB:RegistrationActivity";

    private RegistrationPresenter registrationPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        registrationPresenter = new RegistrationPresenter(this);

        textViewLoginLink = findViewById(R.id.textViewLoginLink);
        textViewLoginLink.setOnClickListener(goToLoginActivity);

        buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(register);

        firstName = findViewById(R.id.editTextFirstName);
        lastName = findViewById(R.id.editTextLastName);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        confirmPassword = findViewById(R.id.editTextConfirmPassword);

        setTitle("Registration");

    }

    private boolean validateInputs() {

        boolean success = true;

        if(String.valueOf(firstName.getText()).trim().isEmpty()) {
            success = false;
            firstName.setError("First name cannot be empty!");
        }

        if(String.valueOf(lastName.getText()).trim().isEmpty()) {
            success = false;
            lastName.setError("Last name cannot be empty!");
        }

        if(String.valueOf(email.getText()).trim().isEmpty()) {
            success = false;
            email.setError("Email cannot be empty!");
        }

        if(String.valueOf(password.getText()).trim().length()<6) {
            success = false;
            password.setError("Password must be at least 6 characters long");
        } else if(!(String.valueOf(password.getText()).equals(String.valueOf(confirmPassword.getText())))) {
            success = false;
            password.setError("passwords do not match!");
            confirmPassword.setError("passwords do not match!");
        }

        return success;
    }

    View.OnClickListener goToLoginActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(i);
        }
    };

    View.OnClickListener register = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(validateInputs()) {
                registrationPresenter.registerNewResponder(String.valueOf(email.getText()), String.valueOf(password.getText()), String.valueOf(firstName.getText()), String.valueOf(lastName.getText()));
            } else {
                Toast.makeText(RegistrationActivity.this, R.string.registration_error_message, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void successfullyRegisteredUser() {
        Intent intent = new Intent(RegistrationActivity.this, EmergencyListActivity.class);
        startActivity(intent);
    }

    @Override
    public void errorRegisteringUser(String email, String message) {
        AlertHelper.showSimpleAlertDiag(this, "Authentication Failed", "Error registering user.\n\n" + message);
    }

    @Override
    public void errorAddingUserToDatabase(String email, String message) {
        AlertHelper.showSimpleAlertDiag(this, "Error Adding User\n\n", message);
    }

}
