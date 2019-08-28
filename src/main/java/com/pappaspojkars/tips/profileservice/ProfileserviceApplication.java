package com.pappaspojkars.tips.profileservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;

import static com.pappaspojkars.tips.profileservice.Utility.validateEmail;
import static com.pappaspojkars.tips.profileservice.Utility.validatePhone;

@SpringBootApplication
public class ProfileserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProfileserviceApplication.class, args);

        System.out.println(validatePhone("0735865682"));
        System.out.println(validatePhone("+735865682"));
        System.out.println(validatePhone("073-586-5682"));
        System.out.println(validatePhone("-+073+-7-23"));
        System.out.println(validatePhone("0046735865682"));

        Long now = LocalDateTime.now().plusMinutes(3L).toEpochSecond(Utility.SERVER_OFFSET);
        System.out.println(now);
    }

}
