package com.openclassrooms.payMyBuddy.service.serviceImpl;

import com.openclassrooms.payMyBuddy.entity.TransactionEntity;
import com.openclassrooms.payMyBuddy.entity.UserEntity;
import com.openclassrooms.payMyBuddy.exception.InvalidTransactionException;
import com.openclassrooms.payMyBuddy.exception.ReceiverNotFoundException;
import com.openclassrooms.payMyBuddy.exception.UserNotFoundException;
import com.openclassrooms.payMyBuddy.mapper.TransactionMapper;
import com.openclassrooms.payMyBuddy.model.TransactionModel;
import com.openclassrooms.payMyBuddy.model.UserConnexionModel;
import com.openclassrooms.payMyBuddy.repository.TransactionRepository;
import com.openclassrooms.payMyBuddy.repository.UserRepository;
import com.openclassrooms.payMyBuddy.service.TransactionService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {


    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private TransactionMapper transactionMapper;
    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Override
    public TransactionModel createTransaction(TransactionModel transactionModel) throws Exception {

        // 1 utilisateur connecté
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated()) {
            log.error("L'utilisateur n'est pas connecté !");
            throw new Exception("Utilisateur non connecté.");
        }
        //
        String email = authentication.getName();
        log.info(" TransactionServiceImpl : Utilisateur authentifié : " + email);

        UserEntity sender = userRepository.findByEmail(email);
        if(sender == null) {
            log.error("TransServImpl : L'expéditeur est introuvable");
           throw new UserNotFoundException("L'expéditeur est introuvable");
        }

        // 2 destinataire
        UserEntity receiver = userRepository.findByEmail(transactionModel.getReceiver());
        if(receiver == null) {
            log.error("TransServImpl : Le destinataire est introuvable");
            throw new ReceiverNotFoundException("Le destinataire est introuvable");
        }

        // montant +
        if (transactionModel.getAmount() <= 0) {
            log.error("TransServImpl : Le montant de la transaction doit être positif");
            throw new InvalidTransactionException("Le montant de la transaction doit être positif");
        }

        // verif sold expé
        if (sender.getSold() < transactionModel.getAmount()) {
            log.error("TransServImpl : Le solde de l'utilisateur est insuffisant");
            throw new InvalidTransactionException("Le solde de l'utilisateur est insuffisant");
        }

        // commission 5%
        double commission = 5.0;
        transactionModel.setPercentage(commission);

        // montant total avec com
        double totalMontant = transactionModel.getAmount() * (1 + commission / 100);

        sender.setSold(sender.getSold() - totalMontant);      // solde - totalMontant
        receiver.setSold(receiver.getSold() + transactionModel.getAmount()); // amount ss commission
        TransactionEntity transactionEntity = transactionMapper.mapToTransactionEntity(transactionModel);
        transactionEntity.setSender(sender);
        transactionEntity.setReceiver(receiver);
        transactionEntity.setPercentage(totalMontant);

        userRepository.save(sender);
        userRepository.save(receiver);
        transactionRepository.save(transactionEntity);

        TransactionModel transactionResult = transactionMapper.mapToTransactionModel(transactionEntity);
        return transactionResult;   // retour model + commission
    }


    // S'envoyer soi-même une transaction  UTILITE ????
    @Override
    public TransactionModel createAutoTransaction(TransactionModel transactionModel) {
        // A FAIRE !!!!     Ok + pas de frais de 5%
        return null;
    }


    @Override
    public TransactionModel saveTransaction(TransactionModel transactionModel) {

        TransactionEntity transactionEntity = transactionMapper.mapToTransactionEntity(transactionModel);
        log.info("Transaction à sauvegarder: {}", transactionEntity);

        transactionEntity = transactionRepository.save(transactionEntity);
        log.info("Transaction a bien été sauvegardée victoire ! : {}", transactionEntity);

        TransactionModel savedTransaction = transactionMapper.mapToTransactionModel(transactionEntity);
        log.info("La transaction a bien été ajoutée.");
        return savedTransaction;
    }

    @Override
    public List<TransactionModel> getTransactionsByUser(String emailUser) {

        UserEntity userEntity = userRepository.findByEmail(emailUser);

        List<TransactionEntity> transactions = transactionRepository.findBySenderAndReceiver(userEntity, userEntity);
        return transactions.stream()
                .map(transactionMapper::mapToTransactionModel)  // appel mapper pour chaque ligne
                .collect(Collectors.toList());
    }

    @Override
    public List<UserConnexionModel> getUserConnectionModel() {
        return null;
    }


}

