package com.openclassrooms.payMyBuddy.service;


import com.openclassrooms.payMyBuddy.model.TransactionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface TransactionService {


    TransactionModel createTransaction(TransactionModel transactionModel) throws Exception;
    

    TransactionModel saveTransaction(TransactionModel transactionModel);

    //List<TransactionModel> getAllTransactions();


    Page<TransactionModel> getTransactionsByUser(String email, Pageable pageable);





}

