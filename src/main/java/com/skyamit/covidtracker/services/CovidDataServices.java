package com.skyamit.covidtracker.services;

import com.skyamit.covidtracker.models.CountriesCount;
import com.skyamit.covidtracker.models.LocationStats;
import com.sun.source.tree.CaseLabelTree;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.emitter.ScalarAnalysis;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
        String tempDate = monthh+"-"+datee+"-"+year+".csv";

        calendar.add(Calendar.DATE,-1);
        date = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH)+1;

        if(date<10){
            datee = "0"+date;
        }
        else{
            datee = date+"";
        }
        if(month<10){
            monthh = "0"+month;
        }
        else{
            monthh = month+"";
        }

        // first is month, day, year
        String lastDate = monthh+"-"+datee+"-"+year+".csv";

        String link = dataUrl + tempDate;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(link)).build();

        HttpResponse<String> httpResponse = client.send(request,HttpResponse.BodyHandlers.ofString());

        StringReader stringReader = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(stringReader);

        List<LocationStats> newList = new ArrayList<>();
        HashMap<String,Long> map = new HashMap<>();

        for(CSVRecord record : records){
            String country = record.get("Country_Region");
            Long confirmed = Long.valueOf(record.get("Confirmed"));
            String state = record.get("Province_State");
            LocationStats locationStats = new LocationStats();
            locationStats.setCountry(country);
            locationStats.setState(state);
            locationStats.setLatestTotalCases(confirmed+"");
            newList.add(locationStats);
            map.put(country,map.getOrDefault(country,0l)+confirmed);
        }

        Collections.sort(newList,(A,B)->(Integer.valueOf(B.getLatestTotalCases()) - Integer.valueOf(A.getLatestTotalCases())));
        this.list = newList;

        List<CountriesCount> newCountries = new ArrayList<>();
        for(Map.Entry<String, Long> entry : map.entrySet()){
            newCountries.add(new CountriesCount(entry.getKey(),entry.getValue()+""));
        }

        Collections.sort(newCountries,(A,B)->((int)(Long.valueOf(B.getCovidCaseCount())-Long.valueOf(A.getCovidCaseCount()))));
        this.countriesCounts = newCountries;

        Long newTotal = 0l;

        for(CountriesCount countriesCount : newCountries) {
            newTotal += Long.valueOf(countriesCount.getCovidCaseCount());
        }
        this.totalCount = newTotal;


        // new cases
        String lastLink = dataUrl + lastDate;

        HttpRequest lastRequest = HttpRequest.newBuilder().uri(URI.create(lastLink)).build();

        HttpResponse<String> lastHttpResponse = client.send(lastRequest,HttpResponse.BodyHandlers.ofString());

        StringReader lastReader = new StringReader(lastHttpResponse.body());
        Iterable<CSVRecord> lastRecords = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(lastReader);

        for(CSVRecord record : lastRecords){
            String country = record.get("Country_Region");
            Long confirmed = Long.valueOf(record.get("Confirmed"));
            map.put(country,map.getOrDefault(country,0l)-confirmed);
        }

        List<CountriesCount> todayList = new ArrayList<>();
        for(Map.Entry<String, Long> entry : map.entrySet()){
            todayList.add(new CountriesCount(entry.getKey(),entry.getValue()+""));
        }
        Collections.sort(todayList,(A,B)->((int)(Long.valueOf(B.getCovidCaseCount())-Long.valueOf(A.getCovidCaseCount()))));
        this.todayCases = todayList;

    }


    public List<LocationStats> getList() {
        return list;
    }

    public void setList(List<LocationStats> list) {
        this.list = list;
    }

    public void setCountriesCounts(List<CountriesCount> countriesCounts) { this.countriesCounts = countriesCounts; }

    public List<CountriesCount> getCountriesCounts() { return countriesCounts; }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public List<CountriesCount> getTodayCases() {
        return todayCases;
    }

    public void setTodayCases(List<CountriesCount> todayCases) {
        this.todayCases = todayCases;
    }
}
