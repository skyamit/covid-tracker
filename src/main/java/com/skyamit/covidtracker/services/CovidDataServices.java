package com.skyamit.covidtracker.services;

import com.skyamit.covidtracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class CovidDataServices {
    private String dataUrl ="https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_daily_reports/";
    private String tempDate = "09-10-2022.csv";
    private List<LocationStats> list = new ArrayList<>();

    @PostConstruct
    @Scheduled(cron="* * 1 * * *")
    public void fetchData() throws IOException, InterruptedException {
        String link = dataUrl + tempDate;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(link)).build();

        HttpResponse<String> httpResponse = client.send(request,HttpResponse.BodyHandlers.ofString());

        StringReader stringReader = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(stringReader);

        List<LocationStats> newList = new ArrayList<>();

        for(CSVRecord record : records){
            LocationStats locationStats = new LocationStats();
            locationStats.setCountry(record.get("Country_Region"));
            locationStats.setState(record.get("Province_State"));
            locationStats.setLatestTotalCases(record.get("Confirmed"));
            newList.add(locationStats);
        }

        this.list = newList;
    }


    public List<LocationStats> getList() {
        return list;
    }

    public void setList(List<LocationStats> list) {
        this.list = list;
    }
}
