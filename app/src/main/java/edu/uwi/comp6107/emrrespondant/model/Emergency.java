package edu.uwi.comp6107.emrrespondant.model;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class Emergency {

    public String id;
    public Long timestamp;
    public String callerId;
    public Responder responder;
    public String description;
    public String status;
    public CustomLocation location;

    public Emergency() {}

    public Emergency(String id, Long timestamp, String callerId, Responder responder, String description, String status, CustomLocation location) {
        this.id = id;
        this.timestamp = timestamp;
        this.callerId = callerId;
        this.responder = responder;
        this.description = description;
        this.status = status;
        this.location = location;
    }

    public Emergency(String id, Long timestamp, String callerId, Responder responder, String description, EmergencyStatus status, CustomLocation location) {
        this.id = id;
        this.timestamp = timestamp;
        this.callerId = callerId;
        this.responder = responder;
        this.description = description;
        this.status = status.toString();
        this.location = location;
    }

    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<String, Object>();

        result.put("id", id);
        result.put("timestamp", timestamp);
        result.put("callerId", callerId);
        if(responder != null) { result.put("responder", responder.toMap()); }
        result.put("description", description);
        result.put("status", status);
        result.put("location", location);

        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString()
                + "\nEmergency(id: " + id
                + ", timestamp: " + timestamp
                + ", callerId: " + callerId
                + ", responderId: " + responder
                + ", description: " + description
                + ", status: " + status
                /*+ ", latitude" + latitude
                + ", longitude" + longitude
                +", altitude" + altitude */
                + ", location: " + location
                + ")";
    }

}
