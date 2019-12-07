package edu.uwi.comp6107.emrrespondant.model;

public enum EmergencyStatus {

    CREATED("Awaiting response."), INPROGRESS("Response in progress."), RESOLVED("Resolved"), CANCELLED("Cancelled");

    private String status;

    EmergencyStatus(String status) { this.status = status; }

    public String toString() { return this.status; }

}
