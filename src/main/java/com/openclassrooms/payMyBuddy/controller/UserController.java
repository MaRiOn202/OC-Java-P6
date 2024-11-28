package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.model.UserModel;
import com.openclassrooms.payMyBuddy.services.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@AllArgsConstructor
public class UserController {

    @Autowired
    UserService userService;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    
/*    @GetMapping
    public UserModel getUserByEmail(@RequestParam(name = "email") String email) throws Exception {

        return userService.getUserByEmail(email);
    }


    @GetMapping
    public ResponseEntity<UserModel> getUser(@RequestParam(name = "username") String username) throws Exception {

        return new ResponseEntity<>(userService.getUser(username), HttpStatus.OK);
    }*/


    @PostMapping("/registrer")
    public UserModel saveUser(@RequestBody UserModel userModel) throws Exception {
        log.info("Save new user" + userModel);
        return userService.saveUser(userModel);
    }

/*

    @PutMapping
    public UserModel updateUser(@RequestBody UserModel userModel) throws Exception {

        return userService.updateUser(userModel);
    }


    @DeleteMapping
    public void deleteUser(@RequestParam(name = "id") Long id) throws Exception {

        userService.deleteUser(id);
    }
*/


}
