package com.unibuc.user.controller;

import com.unibuc.user.model.User;
import com.unibuc.user.model.UserProfile;
import com.unibuc.user.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/user")
    public User addUser(){
        UserProfile userProfile = new UserProfile("Clau", "Clau", "1234567890");
        User user = new User("Clau", "12345", userProfile);
        return userService.save(user);
    }
}
