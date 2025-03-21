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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
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
    public String accessToTransactionForm(Model model,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "5") int nberOfOccurrences,
                                          Principal principal,
                                          @ModelAttribute("error") String error)
            throws Exception {

        if (principal == null) {
            log.info("transfertController : redirection vers la page login car not utilisateur connecté");
            return "redirect:/login";
        }

        // Verif user connecté
        UserModel userModel = userService.getConnectingUser();
        log.info("accès vers /transfert utilisateur : {} ", userModel.getEmail());

        if (userModel.getId() == null) {
            throw new IllegalArgumentException("L'Id de l'utilisateur ne peut pas être nul");
        }

       Pageable pageable = PageRequest.of(page, nberOfOccurrences, Sort.by("localDateTime").descending());
       Page<TransactionModel> transactionsPage = transactionService.getTransactionsByUser(userModel.getEmail(), pageable);
       UserConnectionModel listUserConnections = userService.getUserConnectionModel(userModel.getId());
       
       // Ajout != données au model
       model.addAttribute("userModel", userModel);
       model.addAttribute("transactions", transactionsPage.getContent());
        model.addAttribute("currentPage", transactionsPage.getNumber());
        model.addAttribute("totalPages", transactionsPage.getTotalPages());
       model.addAttribute("transactionModel", new TransactionModel());
       model.addAttribute("relations", listUserConnections);

        if (!error.isEmpty()) {
           model.addAttribute("error", error);
        }
       return "transfert";
    }

    


    @PostMapping("/transfert/save")
    public String transaction(@Valid TransactionModel transactionModel,
                              BindingResult result,
                              Model model) throws Exception {

        if (result.hasErrors()) {
           model.addAttribute("transactionModel", transactionModel);
           return "redirect:/transfert?error=true";
        }
        log.info("TransactionController => Destinataire sélectionné: {}", transactionModel.getReceiver());

        try {
           transactionService.createTransaction(transactionModel);
           // récupérer transactions de l'utilisateur connecté mis à jour
            UserModel userModel = userService.getConnectingUser();

            Pageable pageable = PageRequest.of(0, 5, Sort.by("localDateTime").descending());
            Page<TransactionModel> transactionsPage = transactionService.getTransactionsByUser(userModel.getEmail(), pageable);

            // Ajout != données au model
            model.addAttribute("userModel", userModel);
            model.addAttribute("transactions", transactionsPage.getContent());
            model.addAttribute("currentPage", transactionsPage.getNumber());
            model.addAttribute("totalPages", transactionsPage.getTotalPages());
            model.addAttribute("transactionModel", new TransactionModel());
            model.addAttribute("relations", userService.getUserConnectionModel(userModel.getId())); // formulaire de relation à jour

            model.addAttribute("success", "Le transfert a bien été effectué !");
            return "redirect:/transfert?success=true";

        } catch (InvalidTransactionException e) {
            // Cas où le sold est insuffisant
            model.addAttribute("transactionModel", transactionModel);
            return "redirect:/transfert?error=true";
        } catch (Exception e) {
            // Cas des autres erreurs
            model.addAttribute("transactionModel", transactionModel);
            return "redirect:/transfert?error=true";
        }
    }
}


