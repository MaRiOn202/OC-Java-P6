package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.exception.UserNotFoundException;
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


/**
 *    Contrôleur permettant de gérer l'enregistrement des utilisateurs
 * */

@Controller
@AllArgsConstructor
public class RegisterController {

    private final UserService userService;

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);



    /**
     *  Méthode permettant d'afficher le formulaire d'inscription
     *
     *  @param model
     *  @return la vue "register"
     * */
    @GetMapping("/register")
    public String accessToRegistrationForm(Model model) {

        UserModel userModel = new UserModel();
        model.addAttribute("userModel", userModel);
        return "register";
    }


    /**
     *  Méthode permettant de traiter le formulaire d'inscription et de sauvegarder un nouvel utilisateur
     *
     *  @param userModel
     *  @param result
     *  @param model
     *  @return redirection vers une page d'enregistrement avec un message de succès
     *  ou réaffiche le formulaire en cas d'erreur
     *  @throws Exception
     * */
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





