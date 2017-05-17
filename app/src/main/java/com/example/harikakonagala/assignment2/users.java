package com.example.harikakonagala.assignment2;

/**
 * Created by Harika Konagala on 5/1/2017.
 */

public class users {
    private String email;
    private String full_name;
    private Double latitude;
    private Double longitude;
    private String last_active_time;

    users(String email, String full_name, Double latitude, Double longitude, String last_active_time) {
        this.email = email;
        this.full_name = full_name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.last_active_time = last_active_time;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getLast_active_time() {
        return last_active_time;
    }

    public void setLast_active_time(String last_active_time) {
        this.last_active_time = last_active_time;
    }
}
