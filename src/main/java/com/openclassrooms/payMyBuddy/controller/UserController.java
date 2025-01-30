package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.exception.UserNotFoundException;
import com.openclassrooms.payMyBuddy.model.UserConnexionModel;
import com.openclassrooms.payMyBuddy.model.UserModel;
import com.openclassrooms.payMyBuddy.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@Controller
@AllArgsConstructor
public class UserController {

    @Autowired
    UserService userService;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);


   @GetMapping("home")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login(Model model, Principal principal) {

       // Redirection si déjà connecté
        if (principal != null) {
         return "redirect:/home";
        }
       UserModel userModel = new UserModel();       // model vide
       model.addAttribute("userModel", userModel);
       model.addAttribute("success", "Connexion réussie !");
        return "login";
    }


    @GetMapping("/logoutSuccess")
    public String logoutSuccess(Model model, Principal principal) {
        if (principal != null) {
            log.info("utilisateur dé connected : {}", principal.getName());
        } else {
            log.info("Déconnexion réussie, pas de session active en cours d'exécution");
        }

        model.addAttribute("message", "Vous avez été déconnecté avec succès");
        return "logout";
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
       // si aucun util n'est connecté
       if (principal == null) {
           log.info("redirection vers la page login car not utilisateur connecté");
           return "redirect:/login";
       }

       try {
           // vérifier l'utilisateur actuel
           log.info("chargemnt profile {}", principal.getName());
           UserModel userModel = userService.getConnectingUser();

           model.addAttribute("userModel", userModel);

           // champs affichés en mode lecture
           model.addAttribute("isOk", false);   // false  donc en mode modif
       } catch (Exception e)   {
           log.error("Erreur remontée lors du chargement page profile : {}", e.getMessage());
          model.addAttribute("error", "Une erreur s'est produite lors du chargement");
          return "profile";
       }
        log.info("accès vers profile effectué ok");
        return "profile";
    }

    @PostMapping("/profile/toChange")
    public String toChangeInformations(boolean isOk, Model model) throws Exception {
       UserModel userModel = userService.getConnectingUser();
       log.info("accès vers profile/toChangeInforamtions effectué");
       model.addAttribute("userModel", userModel);
       model.addAttribute("isOk", !isOk); // inversion de l'état donc modif à effectuer
       return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfil(@Valid UserModel userModel, BindingResult result, Model model) throws Exception {
       log.info("accès vers profile/update effectué");
       if (result.hasErrors()) {
          model.addAttribute("error", "error de valiudation");
           model.addAttribute("userModel", userModel);
           return "profile";
       }

       userService.updateUser(userModel);
       model.addAttribute("userModel", userModel);
       model.addAttribute("isOk", false);
       model.addAttribute("success", "Les modifications ont été enregistrées avec succès !");
        return "redirect:/profile?success";
    }

    @GetMapping("/relation")
    public String accessToRelationForm(Model model) {

       UserConnexionModel userConnexionModel = new UserConnexionModel();
       model.addAttribute("userConnexionModel", userConnexionModel);
       return "relation";

    }

    @PostMapping("/relation/save")
    public String addRelation(@Valid UserConnexionModel userConnexionModel, BindingResult result, Model model) throws Exception {

      if (result.hasErrors()) {
          model.addAttribute("userConnexionModel", userConnexionModel);
          return "relation";     //vue
      }

      try {
          userService.addRelation(userConnexionModel);
      } catch (UserNotFoundException e)  {
          result.rejectValue("email", "error.emailNotFound", "Utilisateur non trouvé en bdd"); //form de vue relation + message error
          model.addAttribute("userConnexionModel", userConnexionModel);
          return "relation";
        } catch (Exception e) {
            result.rejectValue("email", "error.email", e.getMessage()); //exception générée dans serviceImpl
            model.addAttribute("userConnexionModel", userConnexionModel);
            return "relation";
      }

      model.addAttribute("success", "La relation a été ajoutée avec succès !");
      return "redirect:/relation?success";
    }











}
