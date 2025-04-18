package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.exception.UserNotFoundException;
import com.openclassrooms.payMyBuddy.model.UserConnectionModel;
import com.openclassrooms.payMyBuddy.model.UserLoginModel;
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
import java.util.List;


/**
 *    Contrôleur permettant de gérer les utilisateurs
 * */
@Controller
@AllArgsConstructor
public class UserController {

    @Autowired
    UserService userService;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);


    /**
     *  Méthode permettant d'afficher la page d'accueil
     *
     *  @return la vue "home"
     * */
    @GetMapping("home")
    public String home() {
        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        log.info("User après connexion : {}", authentication.getName());
        return "home";
    }

    /**
     *  Méthode permettant d'afficher le formulaire de connexion
     *
     *  @param model
     *  @param principal
     *  @param userLoginModel
     *  @param result
     *  @return la vue "login" ou redirection vers la page home si l'utilisateur est connecté
     *  @throws Exception
     * */
    @GetMapping("/login")
    public String loginUser(Model model, Principal principal, @ModelAttribute("userLoginModel")
    UserLoginModel userLoginModel, BindingResult result) {
        if (principal != null) {
            return "redirect:/home";
        } else {
            log.info("Controller : User non authentifié");
        }

        if (userLoginModel == null) {
            userLoginModel = new UserLoginModel();    // model vierge créé
        } else {
            log.info("Controller : User null");
        }

        model.addAttribute("userLoginModel", userLoginModel);

        if (result.hasErrors()) {
            log.error("Erreurs de validation dans le formulaire de connexion");
            return "login";
        }

        model.addAttribute("success", "Connexion réussie !");
        return "login";
    }


    /**
     *  Méthode permettant de gérer l'affichage après une déconnexion
     *
     *  @param model
     *  @param principal
     *  @return la vue "logout"
     * */
    @GetMapping("/logoutSuccess")
    public String logoutSuccess(Model model, Principal principal) {
        if (principal != null) {
            log.info("utilisateur déconnecté : {}", principal.getName());
        } else {
            log.info("Déconnexion réussie, pas de session active en cours d'exécution");
        }

        model.addAttribute("message", "Vous avez été déconnecté avec succès");
        return "logout";
    }


    /**
     *  Méthode permettant d'afficher le profil de l'utilisateur connecté
     *
     *  @param model
     *  @param principal
     *  @return la vue "profile" ou redirection vers la page login si l'utilisateur n'est pas connecté
     *  @throws Exception
     * */
    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        if (principal == null) {
            log.info("redirection vers la page login car pas d'utilisateur connecté");
            return "redirect:/login";
        }
        try {
            log.info("Chargement du profil pour {}", principal.getName());
            UserModel userModel = userService.getConnectingUser();
            model.addAttribute("userModel", userModel);
            model.addAttribute("editMode", false); // Mode lecture
        } catch (Exception e) {
            log.error("Erreur lors du chargement de la page profil : {}", e.getMessage());
            model.addAttribute("error", "Une erreur s'est produite lors du chargement");
            return "profile";
        }
        log.info("Accès au profil effectué avec succès");
        return "profile";
    }


    /**
     *  Méthode permettant d'activer le mode édition sur la page de profil
     *
     *  @param editMode
     *  @param model
     *  @return la vue "profile" en mode édition
     *  @throws Exception
     * */
    @PostMapping("/profile/toChange")
    public String toChangeInformations(@RequestParam(required = false) Boolean editMode, Model model) throws Exception {
        UserModel userModel = userService.getConnectingUser();
        log.info("Accès à /profile/toChange");
        model.addAttribute("userModel", userModel);
        model.addAttribute("editMode", editMode != null && !editMode); // Inversion du mode (passage à édition)
        return "profile";
    }


    /**
     *  Méthode permettant de mettre à jour le profil de l'utilisateur après soumission du formulaire
     *
     *  @param userModel
     *  @param result
     *  @param model
     *  @return redirection vers le profil ou retour au formulaire en cas d'erreur
     *  @throws Exception
     * */
    @PostMapping("/profile/update")
    public String updateProfil(@Valid UserModel userModel, BindingResult result, Model model) throws Exception {
        log.info("Accès à /profile/update");
        if (result.hasErrors()) {
            model.addAttribute("userModel", userModel);
            model.addAttribute("editMode", true);
            return "profile";
        }

        log.info("UserModel modifié : {}", userModel);
        userService.updateUser(userModel);

        model.addAttribute("userModel", userModel);
        model.addAttribute("editMode", false);
        model.addAttribute("success", "Les modifications ont été enregistrées avec succès !");
        return "redirect:/profile?success";
    }


    /**
     *  Méthode permettant d'afficher le formulaire pour ajouter une relation
     *
     *  @param model
     *  @param principal
     *  @return la vue "relation" ou redirection vers login
     * */
    @GetMapping("/relation")
    public String accessToRelationForm(Model model, Principal principal) {
        if (principal == null) {
            log.info("redirection vers la page login car pas d'utilisateur connecté");
            return "redirect:/login";
        }

        UserConnectionModel userConnectionModel = new UserConnectionModel();
        model.addAttribute("userConnectionModel", userConnectionModel);
        return "relation";
    }


    /**
     *  Méthode permettant de traiter la demande d'ajout d'une relation utilisateur
     *
     *  @param userConnectionModel
     *  @param result
     *  @param model
     *  @return redirection vers vers la page "relation" en cas de succès ou retour au formulaire en cas d'erreur
     *  @throws Exception
     * */
    @PostMapping("/relation/save")
    public String addRelation(@Valid UserConnectionModel userConnectionModel, BindingResult result, Model model) throws Exception {
        if (result.hasErrors()) {
            model.addAttribute("userConnectionModel", userConnectionModel);
            model.addAttribute("error", "Erreur de validation");
            return "relation";
        }

        try {
            List<UserModel> listUserEntity = userService.addRelation(userConnectionModel.getEmail());
            userConnectionModel.setFriends(listUserEntity);
        } catch (UserNotFoundException e) {
            result.rejectValue("email", "error.emailNotFound", "Utilisateur non trouvé en BDD");
            model.addAttribute("userConnectionModel", userConnectionModel);
            return "relation";
        } catch (Exception e) {
            result.rejectValue("email", "error.email", e.getMessage());
            model.addAttribute("userConnectionModel", userConnectionModel);
            return "relation";
        }

        model.addAttribute("success", "La relation a été ajoutée avec succès !");
        return "redirect:/relation?success";
    }

}
