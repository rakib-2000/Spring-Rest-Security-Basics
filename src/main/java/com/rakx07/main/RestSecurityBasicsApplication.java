package com.rakx07.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class RestSecurityBasicsApplication {

    @Bean
    PasswordEncoder passwordEncoder(){
        // Use it while no password encryption is running.
        return NoOpPasswordEncoder.getInstance();
        // Use it while password is encrypted.
        //return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) {
        SpringApplication.run(RestSecurityBasicsApplication.class, args);
    }

}
