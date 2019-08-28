package com.pappaspojkars.tips.profileservice;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;
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

    //Delete check

    @DeleteMapping("/user/{id}")
    public User deleteUserById(String token, @PathVariable Integer id,@RequestBody User user){

        if(id != user.getId ()){
            throw new RuntimeException("ID not authorized");
        }
        Optional<User> tempUser = profileRepo.findById(id);

        User oldUser = tempUser.get();

        if(!token.equals(oldUser.getToken())){
            throw new RuntimeException("Token is not valid");
        }

        Long checkTimeStamp = LocalDateTime.now().toEpochSecond(Utility.SERVER_OFFSET);
        if(oldUser.getTokenLastValidDate() < checkTimeStamp){
            throw new RuntimeException("Session timout");
        }
        if(oldUser.getPayStatus() != 99){
            throw new RuntimeException("You are active in the game and can´t delete your account");
        }
         profileRepo.delete(oldUser);

        return oldUser;

    }

    @PutMapping("user/{id}/password")
    public boolean changePassword(String token, String oldPassword, @PathVariable Integer id,@RequestBody User user){
        if(id != user.getId()) {
            throw  new RuntimeException("ID not authorized");
        }
         Optional<User> tempUser = profileRepo.findById(id);

        User oldUser = tempUser.get();



        Long checkTimeStamp = LocalDateTime.now().toEpochSecond(Utility.SERVER_OFFSET);
        if(oldUser.getTokenLastValidDate() < checkTimeStamp){
            throw new RuntimeException("Session timout");
        }
        if(!Utility.MD5Encode(oldPassword).equals(oldUser.getPassword())){
            throw  new RuntimeException("Invalid old password");
        }
        if(!token.equals(oldUser.getToken())){
            throw  new RuntimeException("Token is invalid");
        }

        if(user.getPassword().equals("")|| user.getPassword() == null){
            throw new RuntimeException("Password can´t be left empty");
        }


        oldUser.setPassword(Utility.MD5Encode(user.getPassword()));
        profileRepo.save(oldUser);

        return true;
    }


}
