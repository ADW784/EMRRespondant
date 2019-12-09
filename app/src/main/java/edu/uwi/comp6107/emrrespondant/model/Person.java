package edu.uwi.comp6107.emrrespondant.model;

public class Person {

    public String username;
    public String email;
    public String uid;

    public Person() {
        // Default constructor required for calls to DataSnapshot.getValue(Person.class)
    }

    public Person(String username, String email, String uid) {
        this.username = username;
        this.email = email;
        this.uid = uid;
    }

}
