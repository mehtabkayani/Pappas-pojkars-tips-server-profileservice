package com.pappaspojkars.tips.profileservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;

@SpringBootApplication
public class ProfileserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProfileserviceApplication.class, args);

        Long now = LocalDateTime.now().plusMinutes(3L).toEpochSecond(Utility.SERVER_OFFSET);
        System.out.println(now);
    }

}
