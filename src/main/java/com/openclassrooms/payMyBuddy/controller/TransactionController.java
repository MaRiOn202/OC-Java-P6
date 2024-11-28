package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;


public class TransactionController {

    @Autowired
    TransactionService transactionService;


}
