package edu.uwi.comp6107.emrrespondant.model;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class Caller extends Person {

    public String firstName;
    public String lastName;
    public String allergies;
    public String medication;
    public String doctor;
    public Emergency emergency;

    public Caller() {};

    public Caller(String username, String email, String uid, String firstName, String lastName, String allergies, String medication, String doctor, Emergency emergency) {
        super(username, email, uid);
        this.firstName = firstName;
        this.lastName = lastName;
        this.allergies = allergies;
        this.medication = medication;
        this.doctor = doctor;
        this.emergency = emergency;
    }

    public void update(String username, String email, String firstName, String lastName, String allergies, String medication, String doctor){
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.allergies = allergies;
        this.medication = medication;
        this.doctor = doctor;
    }

    public void updateBasicDetails(String username, String email, String firstName, String lastName){
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void updateProfile(String firstName, String lastName, String allergies, String medication, String doctor){
        this.firstName = firstName;
        this.lastName = lastName;
        this.allergies = allergies;
        this.medication = medication;
        this.doctor = doctor;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("uid", uid);
        result.put("username", username);
        result.put("email", email);
        result.put("firstName", firstName);
        result.put("lastName", lastName);
        result.put("allergies", allergies);
        result.put("medication", medication);
        result.put("doctor", doctor);
        if(emergency != null) { result.put("emergency", emergency.toMap()); }

        return result;
    }

    @NonNull
    @Override
    public String toString() {

        return
                "Caller(" +
                        "uid: " + this.uid + ", " +
                        "username: " + this.username + ", " +
                        "email: " + this.email + ", " +
                        "firstName: " + this.firstName + ", " +
                        "lastName: " + this.lastName + ", " +
                        "allergies: " + this.allergies + ", " +
                        "medication: " + this.medication + ", " +
                        "doctor: " + this.doctor + ", " +
                        "emergency: " + this.emergency +
                        ")";

    }

}
