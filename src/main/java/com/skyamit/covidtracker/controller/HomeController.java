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


}
