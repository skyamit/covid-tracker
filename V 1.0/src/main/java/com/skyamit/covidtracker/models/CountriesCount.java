package com.skyamit.covidtracker.models;

public class CountriesCount {
    private String countryName;
    private String covidCaseCount;

    public CountriesCount(String name, String count){
        this.countryName = name;
        this.covidCaseCount = count;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCovidCaseCount() {
        return covidCaseCount;
    }

    public void setCovidCaseCount(String covidCaseCount) {
        this.covidCaseCount = covidCaseCount;
    }
}
