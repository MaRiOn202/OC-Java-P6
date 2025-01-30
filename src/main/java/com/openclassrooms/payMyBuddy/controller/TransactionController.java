package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.model.TransactionModel;
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

       List<TransactionModel> userTransactions = transactionService.getTransactionsByUser(userModel.getEmail());
       
       // Ajout != données au model
       model.addAttribute("userModel", userModel);
       model.addAttribute("transactions", userTransactions);
       model.addAttribute("transactionModel", new TransactionModel());
       return "transfert";
    }


    @PostMapping("/transfert/save")
    public String transaction(@Valid TransactionModel transactionModel, BindingResult bindingResult, Model model) {

        
        return null;
    }
}

