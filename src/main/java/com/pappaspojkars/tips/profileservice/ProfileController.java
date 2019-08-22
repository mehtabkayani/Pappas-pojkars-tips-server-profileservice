package com.pappaspojkars.tips.profileservice;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfileController {

    @Autowired
    ProfileRepo profileRepo;


}
