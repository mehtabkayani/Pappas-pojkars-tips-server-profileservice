package com.pappaspojkars.tips.profileservice;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@RestController
public class ProfileController {

    @Autowired
    ProfileRepo profileRepo;

    @GetMapping("/users")
    public Iterable<UserViewable> getAllUsers(){
       Iterable<User> users = profileRepo.findAll();


       Iterable <UserViewable> userViewable = StreamSupport.stream(users.spliterator(), false)
               .map(user -> new UserViewable(user))
               .collect(Collectors.toList());

       return userViewable;
    }




    @PutMapping("/user/{id}")
    public UserViewable updateUser(String token, @PathVariable Integer id,  @RequestBody User user){
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

        //Name check

        if(user.getName() == null || !Utility.isFullname(user.getName())){
            throw new RuntimeException("Name must only contain letters, cant be left empty");
        }
        oldUser.setName(user.getName());

        //Nickname check

        if(user.getNickname() == null || user.getNickname().isEmpty() || user.getNickname().equals(oldUser.getNickname()) ){

        }else {
            if (profileRepo.existsByNickname(user.getNickname())) {
                throw new RuntimeException("Nickname Already Exists");
            } else {
                oldUser.setNickname(user.getNickname());
            }
        }

        //Email check


        if(profileRepo.existsByEmail(user.getEmail()) && !oldUser.getEmail().equals(user.getEmail())){
            throw new RuntimeException("Email is already taken. Choose another one");
        }


        if(user.getEmail() == null || !Utility.validateEmail(user.getEmail())){
            throw new RuntimeException("Email can´t be left empty and should be in this format 'name@email.com'");
        }
        oldUser.setEmail(user.getEmail());

        //Phone check

        if(user.getPhone() == null || !Utility.validatePhone(user.getPhone())){
            throw new RuntimeException("Phone number must only contain numbers and be in the range of 10-13");
        }

        oldUser.setPhone(user.getPhone());



       profileRepo.save(oldUser);
       UserViewable userViewable = new UserViewable(oldUser);
       return userViewable;
    }

    @PostMapping("/user")
    public UserViewable addUser(@RequestBody User user){
        String name = user.getName();
        String email = user.getEmail();
        String password = user.getPassword();
        String phone = user.getPhone();
        String nickname = user.getNickname();

        //NULL CHECKS

        User newUser = new User(name,email,password,phone,nickname);
        user = profileRepo.save(newUser);

        UserViewable userViewable = new UserViewable(user);
        System.out.println(userViewable);
        return userViewable;
    }
}
