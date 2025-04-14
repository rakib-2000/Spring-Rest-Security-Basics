package com.rakx07.main.controller;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RestApiController {

    // Public
    @GetMapping("/home")
    public String homeApi(){
        return "Hello from home API";
    }

    // Public
    @GetMapping("/user-dashboard")
    public String userDashboardApi(){
        return "Hello from User Dashboard API";
    }

    // Public
    @GetMapping("/admin-dashboard")
    public String adminDashboardApi(){
        return "Hello from Admin Dashboard API";
    }
}
