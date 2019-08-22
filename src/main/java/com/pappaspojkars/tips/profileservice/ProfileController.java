package com.pappaspojkars.tips.profileservice;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.rmi.CORBA.Util;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
public class ProfileController {

    @Autowired
    ProfileRepo profileRepo;

    @GetMapping("/users")
    public Iterable<User> getAllUsers(){
     return  profileRepo.findAll();
    }




    @PutMapping("/user/{id}")
    public User updateUser(String token, @PathVariable Integer id,  @RequestBody User user){
        if(id != user.getId()){
            throw new RuntimeException("ID: Not authorized");
        }

       Optional <User> oUser = profileRepo.findById(id);
       if(!oUser.isPresent()){
           throw new RuntimeException("User does not exist");
       }



       User oldUser = oUser.get();
        System.out.println(oldUser.getToken());
        if(!token.equals(oldUser.getToken())){
            throw new RuntimeException("Token not valid");
        }



        Long checkTimeStamp = LocalDateTime.now().toEpochSecond(Utility.SERVER_OFFSET);
        if(oldUser.getTokenLastValidDate() < checkTimeStamp){
            throw new RuntimeException("Session timout");
        }

        

        if(user.getName() == null || !Utility.isFullname(user.getName())){
            throw new RuntimeException("Name must only contain letters, cant be left empty");
        }
        oldUser.setName(user.getName());


       profileRepo.save(oldUser);
       return oldUser;
    }

    @PostMapping("/user")
    public User addUser(@RequestBody User user){
        String name = user.getName();
        String email = user.getEmail();
        String password = user.getPassword();
        String phone = user.getPhone();
        String nickname = user.getNickname();

        //NULL CHECKS

        User newUser = new User(name,email,password,phone,nickname);
        user = profileRepo.save(newUser);
        return user;
    }
}
