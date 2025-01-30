package com.openclassrooms.payMyBuddy.service;


import com.openclassrooms.payMyBuddy.model.TransactionModel;
import com.openclassrooms.payMyBuddy.model.UserConnexionModel;

import java.util.List;


public interface TransactionService {


    TransactionModel createTransaction(TransactionModel transactionModel) throws Exception;

    TransactionModel createAutoTransaction(TransactionModel transactionModel);

    TransactionModel saveTransaction(TransactionModel transactionModel);

    //List<TransactionModel> getAllTransactions();


    List<TransactionModel> getTransactionsByUser(String email);

    List<UserConnexionModel> getUserConnectionModel();
}

