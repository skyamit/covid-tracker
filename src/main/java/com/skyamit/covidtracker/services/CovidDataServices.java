package com.skyamit.covidtracker.services;

import com.skyamit.covidtracker.models.CountriesCount;
import com.skyamit.covidtracker.models.LocationStats;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Service
public class CovidDataServices {
    private List<LocationStats> homeList = new ArrayList<>();
    private List<CountriesCount> countriesCounts = new ArrayList<>();
    private Long totalCount;

    @PostConstruct
    @Scheduled(cron="* 1 * * * *")
    public void fetchData() throws IOException, InterruptedException {


        String link = "src\\main\\resources\\data.text";

        FileReader reader = new FileReader(link);
        ArrayList<String> list = new ArrayList<>();
        String current = "";

        int data = reader.read();
        int first = 0;
        while(data!=-1){
            char c = (char)data;
            if(c == '\r'){
                if(first != 0)
                    list.add(current);
                current = "";       
                first++; 
            }
            else
                current += c;
            data = reader.read();
        }
        reader.close();
        
        HashMap<String,Long> map = new HashMap<>();
        
        HashMap<String, LocationStats> stateLocation = new HashMap<>();

        // FIPS 0,Admin2 1,Province_State 2,Country_Region 3,Last_Update 4,Lat 5,Long_ 6,Confirmed 7,Deaths 8,Recovered 9, Active 10,Combined_Key,Incident_Rate,Case_Fatality_Ratio
        for(String temp : list){
            String[] arr = temp.split(",");
            System.out.println(arr[3]+" "+ arr[7] +" " +arr[8]+" " +arr[9]+" " +arr[10]);
            try{
                Long count = map.getOrDefault(arr[3], 0L) + Long.valueOf(arr[7]);
                map.put(arr[3],count);

                // arr[2], arr[3], arr[7], arr[8], arr[9], arr[10] String state, String country, String totalCases, String confirmed, String recovered, String active
                LocationStats locationStats = stateLocation.getOrDefault(arr[2], new LocationStats(arr[2], arr[3], "0", "0", "0", "0"));
                if(!arr[10].equals(""))
                    locationStats.setActive((Long.valueOf(arr[10]) + Long.valueOf(locationStats.getActive())+""));
                if(!arr[8].equals(""))
                    locationStats.setDeath((Long.valueOf(arr[8]) + Long.valueOf(locationStats.getDeath())+""));
                if(!arr[7].equals(""))
                    locationStats.setConfirmed((Long.valueOf(arr[7]) + Long.valueOf(locationStats.getConfirmed())+""));
                if(!arr[9].equals(""))
                    locationStats.setRecovered((Long.valueOf(arr[9]) + Long.valueOf(locationStats.getRecovered())+""));
                
                    stateLocation.put(arr[2],locationStats);

            }
            catch(Exception e){}
        }

        List<LocationStats> fullList = new ArrayList<>();
        for(Map.Entry<String, LocationStats> entry : stateLocation.entrySet()){
            fullList.add(entry.getValue());
        }

        this.homeList = fullList;

        List<CountriesCount> newCountries = new ArrayList<>();
        for(Map.Entry<String, Long> entry : map.entrySet()){
            newCountries.add(new CountriesCount(entry.getKey(),entry.getValue()+""));
        }
        this.countriesCounts = newCountries;

        Long newTotal = 0l;

        for(CountriesCount countriesCount : newCountries) {
            newTotal += Long.parseLong(countriesCount.getCovidCaseCount());
        }
        this.totalCount = newTotal;

    }

    public List<LocationStats> getList() {
        homeList.sort((A, B) -> Integer.parseInt(B.getConfirmed()) - Integer.parseInt(A.getConfirmed()));
        return homeList;
    }

    public void setList(List<LocationStats> homeList) {
        this.homeList = homeList;
    }

    public void setCountriesCounts(List<CountriesCount> countriesCounts) { this.countriesCounts = countriesCounts; }

    public List<CountriesCount> getCountriesCounts() {
        countriesCounts.sort((A, B) -> Integer.parseInt(B.getCovidCaseCount()) - Integer.parseInt(A.getCovidCaseCount()));
        return countriesCounts;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

}
