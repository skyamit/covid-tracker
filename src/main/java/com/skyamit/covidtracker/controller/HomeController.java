package com.skyamit.covidtracker.controller;

import com.skyamit.covidtracker.services.CovidDataServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    CovidDataServices covidDataServices;

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("locationStats",covidDataServices.getList());
        return "home";
    }

    @GetMapping("/countries")
    public String countries(Model model){
        model.addAttribute("countriesName", covidDataServices.getCountriesCounts());
        return "countries";
    }

    @GetMapping("/totalCases")
    public String totalCount(Model model){
        model.addAttribute("totalCount",covidDataServices.getTotalCount());
        return "totalCases";
    }

    @GetMapping("/contactMe")
    public String contactMe(Model model){
        return "contactMe";
    }

    @GetMapping("/today")
    public String today(Model mode){
        mode.addAttribute("newCount",covidDataServices.getTodayCases());
        return "today";
    }
}
