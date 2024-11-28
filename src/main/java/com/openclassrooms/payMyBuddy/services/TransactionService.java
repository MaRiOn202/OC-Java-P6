package com.openclassrooms.payMyBuddy.services;

import com.openclassrooms.payMyBuddy.entity.TransactionEntity;
import org.springframework.stereotype.Service;

@Service
public interface TransactionService {




    public TransactionEntity saveTransaction(TransactionEntity transaction);

    public void deleteTransaction(final Long id);
}
