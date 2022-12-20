package com.skyamit.covidtracker.models;

public class LocationStats {

    private String state;
    private String country;
    private String Death;
    private String confirmed;
    private String recovered;
    private String active;

    LocationStats(){}

    public LocationStats(String state, String country, String Death, String confirmed, String recovered,
            String active) {
        this.state = state;
        this.country = country;
        this.Death = Death;
        this.confirmed = confirmed;
        this.recovered = recovered;
        this.active = active;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getDeath() {
        return Death;
    }
    public void setDeath(String Death) {
        this.Death = Death;
    }
    public String getConfirmed() {
        return confirmed;
    }
    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }
    public String getRecovered() {
        return recovered;
    }
    public void setRecovered(String recovered) {
        this.recovered = recovered;
    }
    public String getActive() {
        return active;
    }
    public void setActive(String active) {
        this.active = active;
    }


}
