package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.model.UserModel;
import com.openclassrooms.payMyBuddy.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
public class RegisterController {


    private final UserService userService;

    //private final PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);


    //ajout méthode de gestion pour gérer les demandes d’enregistrement des utilisateurs
    @GetMapping("/register")
    public String accessToRegistrationForm(Model model) {

        UserModel userModel = new UserModel();
        model.addAttribute("userModel", userModel);
        return "register";
    }



    // ajout pour enregistrer le formulaire / inscription
    @PostMapping("/register/save")
    public String registration(@Valid UserModel userModel, BindingResult result, Model model) throws Exception {
         UserModel existingUser = userService.getUserByEmail(userModel.getEmail());

         if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            result.rejectValue("email", "error", "Le compte utilisateur existe déjà.");
         }

         if (result.hasErrors()) {
             model.addAttribute("userModel", userModel);
             return "/register";
         }

         userService.saveUser(userModel);
         model.addAttribute("success", "L'utilisateur a été créé avec succès !");
        return "redirect:/register?success";

    }                                              

    


}





