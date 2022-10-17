package com.skyamit.covidtracker.services;

import com.skyamit.covidtracker.models.CountriesCount;
import com.skyamit.covidtracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

@Service
public class CovidDataServices {
    private String dataUrl ="https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_daily_reports/";
    private List<LocationStats> list = new ArrayList<>();
    private List<CountriesCount> countriesCounts = new ArrayList<>();
    private List<CountriesCount> todayCases = new ArrayList<>();
    private Long totalCount;
    @PostConstruct
    @Scheduled(cron="* 1 * * * *")
    public void fetchData() throws IOException, InterruptedException {

        String tempDate = getTodayDate();
        String lastDate = getPrevDate();

        String link = dataUrl + tempDate;

        RestTemplate restTemplate = new RestTemplate();

        StringReader stringReader = new StringReader(Objects.requireNonNull(restTemplate.getForObject(link, String.class)));
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(stringReader);

        List<LocationStats> newList = new ArrayList<>();
        HashMap<String,Long> map = new HashMap<>();

        for(CSVRecord record : records){
            String country = record.get("Country_Region");
            Long confirmed = Long.valueOf(record.get("Confirmed"));
            String state = record.get("Province_State");
            if(!state.equals("") && !confirmed.equals(0L)) {
                LocationStats locationStats = new LocationStats(state, country, confirmed + "");
                newList.add(locationStats);
            }
            map.put(country,map.getOrDefault(country,0l)+confirmed);
        }

        this.list = newList;

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


        // new cases
        String lastLink = dataUrl + lastDate;

        StringReader lastReader = new StringReader(Objects.requireNonNull(restTemplate.getForObject(lastLink, String.class)));
        Iterable<CSVRecord> lastRecords = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(lastReader);

        for(CSVRecord record : lastRecords){
            String country = record.get("Country_Region");
            Long confirmed = Long.valueOf(record.get("Confirmed"));
            map.put(country,map.getOrDefault(country, 0L)-confirmed);
        }

        List<CountriesCount> todayList = new ArrayList<>();
        for(Map.Entry<String, Long> entry : map.entrySet()){
            if(!entry.getValue().equals(0L))
                todayList.add(new CountriesCount(entry.getKey(),entry.getValue()+""));
        }

        this.todayCases = todayList;

    }

    public String getTodayDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,-2);
        int date = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH)+1;
        int year = calendar.get(Calendar.YEAR);

        String datee = "";
        if(date<10){
            datee = "0"+date;
        }
        else{
            datee = date+"";
        }
        String monthh = "";
        if(month<10){
            monthh = "0"+month;
        }
        else{
            monthh = month+"";
        }

        return new String(monthh+"-"+datee+"-"+year+".csv");
    }

    public String getPrevDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,-3);
        int date = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH)+1;
        int year = calendar.get(Calendar.YEAR);
        String datee = "";
        if(date<10){
            datee = "0"+date;
        }
        else{
            datee = date+"";
        }
        String monthh = "";
        if(month<10){
            monthh = "0"+month;
        }
        else{
            monthh = month+"";
        }

        return monthh+"-"+datee+"-"+year+".csv";
    }

    public List<LocationStats> getList() {
        list.sort((A, B) -> Integer.parseInt(B.getLatestTotalCases()) - Integer.parseInt(A.getLatestTotalCases()));
        return list;
    }

    public void setList(List<LocationStats> list) {
        this.list = list;
    }

    public void setCountriesCounts(List<CountriesCount> countriesCounts) { this.countriesCounts = countriesCounts; }

    public List<CountriesCount> getCountriesCounts() {
        countriesCounts.sort((A, B) -> (B.getCovidCaseCount().compareTo(A.getCovidCaseCount())));
        return countriesCounts;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public List<CountriesCount> getTodayCases() {
        todayCases.sort((A, B) -> (B.getCovidCaseCount().compareTo(A.getCovidCaseCount())));
        return todayCases;
    }

    public void setTodayCases(List<CountriesCount> todayCases) {
        this.todayCases = todayCases;
    }
}
