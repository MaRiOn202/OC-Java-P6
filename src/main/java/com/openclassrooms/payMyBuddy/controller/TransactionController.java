package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.exception.InvalidTransactionException;
import com.openclassrooms.payMyBuddy.model.TransactionModel;
import com.openclassrooms.payMyBuddy.model.UserConnectionModel;
import com.openclassrooms.payMyBuddy.model.UserModel;
import com.openclassrooms.payMyBuddy.service.TransactionService;
import com.openclassrooms.payMyBuddy.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Comparator;
import java.util.List;


@Controller
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @Autowired
    UserService userService;

    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);


    @GetMapping("/transfert")
    public String accessToTransactionForm(Model model) throws Exception {
        // Verif user connecté
        UserModel userModel = userService.getConnectingUser();
        log.info("accès vers /transfert utilisateur : {} ", userModel.getEmail());

        if (userModel.getId() == null) {
            throw new IllegalArgumentException("L'Id de l'utilisateur ne peut pas être nul");
        }
       // récupère les transactions
       List<TransactionModel> listUserTransactions = transactionService.getTransactionsByUser(userModel.getEmail());
       listUserTransactions.sort(Comparator.comparing(TransactionModel::getId).reversed()); // tri transaction ordre décroissant
       List<UserConnectionModel> listUserConnections = transactionService.getUserConnectionModel(userModel.getId());
       
       // Ajout != données au model
       model.addAttribute("userModel", userModel);
       model.addAttribute("transactions", listUserTransactions);
       model.addAttribute("transactionModel", new TransactionModel());
       model.addAttribute("relations", listUserConnections);
       return "transfert";
    }


    @PostMapping("/transfert/save")
    public String transaction(@Valid TransactionModel transactionModel, BindingResult result, Model model) throws Exception {

        if (result.hasErrors()) {
           model.addAttribute("transactionModel", transactionModel);
           model.addAttribute("error", "Erreur de validation");
           return "transfert";
        }
        log.info("TransactionController => Destinataire sélectionné: {}", transactionModel.getReceiver());

        try {
           transactionService.createTransaction(transactionModel);
           // récupréer transactions de l'utilisateur connecté mis à jour
            UserModel userModel = userService.getConnectingUser();
            // cf méthode ci dessus
            List<TransactionModel> listUserTransactions = transactionService.getTransactionsByUser(userModel.getEmail());
            listUserTransactions.sort(Comparator.comparing(TransactionModel::getId).reversed());

            // Ajout != données au model
            model.addAttribute("userModel", userModel);
            model.addAttribute("transactions", listUserTransactions);
            model.addAttribute("transactionModel", new TransactionModel());
            model.addAttribute("relations", transactionService.getUserConnectionModel(userModel.getId())); // formulaire de relation à jour

            model.addAttribute("success", "Le transfert a bien été effectué !");
            return "transfert";

        } catch (InvalidTransactionException e) {
            // Cas où le sold est insuffisant
            model.addAttribute("transactionModel", transactionModel);
            model.addAttribute("error", "Erreur lors du transfert : Le solde est insuffisant !");
            return "transfert";
        } catch (Exception e) {
            // Cas des autres erreurs
            model.addAttribute("transactionModel", transactionModel);
            model.addAttribute("error", "Erreur lors du transfert : " + e.getMessage());
            return "transfert";
        }
    }
}


