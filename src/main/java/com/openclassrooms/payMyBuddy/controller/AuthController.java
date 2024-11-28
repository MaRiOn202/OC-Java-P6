package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.model.UserModel;
import com.openclassrooms.payMyBuddy.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@AllArgsConstructor
public class AuthController {

    private UserService userService;

    @GetMapping("/home")
    public String home() {
        return "home";
    }


    //ajout méthode de gestion pour gérer les demandes d’enregistrement des utilisateurs
    @GetMapping("/registrer")
    public String accessToRegistrationForm(Model model) {
        UserModel userModel = new UserModel();
        model.addAttribute("user", userModel);
        return "registrer";
    }

    // ajout pour enregistrer le formulaire / inscription
    @PostMapping("/registrer/save")
    public String registration(@Valid UserModel userModel) {
        

        return null;
        //renvoyer page home
    }
}
