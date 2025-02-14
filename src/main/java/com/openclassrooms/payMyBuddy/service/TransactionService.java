package com.openclassrooms.payMyBuddy.service;


import com.openclassrooms.payMyBuddy.model.TransactionModel;
import com.openclassrooms.payMyBuddy.model.UserConnectionModel;

import java.util.List;


public interface TransactionService {


    TransactionModel createTransaction(TransactionModel transactionModel) throws Exception;
    

    TransactionModel saveTransaction(TransactionModel transactionModel);

    //List<TransactionModel> getAllTransactions();


    List<TransactionModel> getTransactionsByUser(String email);


    List<UserConnectionModel> getUserConnectionModel(Long id);
}

