package com.openclassrooms.payMyBuddy.service.serviceImpl;

import com.openclassrooms.payMyBuddy.entity.TransactionEntity;
import com.openclassrooms.payMyBuddy.entity.UserEntity;
import com.openclassrooms.payMyBuddy.exception.InvalidTransactionException;
import com.openclassrooms.payMyBuddy.exception.ReceiverNotFoundException;
import com.openclassrooms.payMyBuddy.exception.UserNotFoundException;
import com.openclassrooms.payMyBuddy.mapper.TransactionMapper;
import com.openclassrooms.payMyBuddy.mapper.UserMapper;
import com.openclassrooms.payMyBuddy.model.TransactionModel;
import com.openclassrooms.payMyBuddy.repository.TransactionRepository;
import com.openclassrooms.payMyBuddy.repository.UserRepository;
import com.openclassrooms.payMyBuddy.service.TransactionService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.math.BigDecimal.*;


/**
 *    Service permettant de gérer les transactions entre utilisateurs.
 * */

@AllArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {


    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);


    /**
     *  Méthode permettant de créer une transaction entre un utilisateur connecté et un destinataire
     *
     *  @param transactionModel
     *  @return TransactionModel
     *  @throws UserNotFoundException
     *  @throws ReceiverNotFoundException
     *  @throws InvalidTransactionException
     *  @throws Exception
     * */
    @Override
    @Transactional
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
        log.info("Expéditeur trouvé : {} avec sold : {}",  sender.getEmail(), sender.getSold());


        // 2 destinataire
        log.info("Recherche du destinaitaire email : " + transactionModel.getReceiver());


        UserEntity receiver = userRepository.findByEmail(transactionModel.getReceiver());
        if(receiver == null) {
            log.error("TransServImpl : Le destinataire est introuvable avec email {}", transactionModel.getReceiver());
            throw new ReceiverNotFoundException("Le destinataire est introuvable");
        }
        log.info("Le destinataire a été trouvé : {} ", receiver.getEmail());


        // Vérification côté back de la relation entre un sender et un receiver
        if (sender.getConnections() == null || !sender.getConnections().contains(receiver)) {
            log.error("TransServImpl : Le destinataire {} est n'est pas une relation de {}", receiver.getEmail(), sender.getEmail());
            throw new InvalidTransactionException("Le transfert d'argent n'est possible que si vous êtes en relation avec le destinataire.");
        }
        log.info("Le destinataire fait bien partie de la liste d'amis");
        
        // montant +
        BigDecimal amount = transactionModel.getAmount();     // BigDecimal
        log.info("Le montant de la transac : {} ", amount);

        if (amount == null || amount.compareTo(ZERO) <= 0) {
            log.error("TransServImpl : Le montant de la transaction doit être positif");
            throw new InvalidTransactionException("Le montant de la transaction doit être positif");
        }

        // commission 5%
        BigDecimal commissionOfFivePercent = new BigDecimal("0.05");
        BigDecimal commission = amount.multiply(commissionOfFivePercent).setScale(2, RoundingMode.HALF_UP);
        transactionModel.setPercentage(commission);

        // montant total avec com
        BigDecimal totalMontant = amount.add(commission);

        // verif sold expé
        BigDecimal senderSold = sender.getSold();
        log.info("Sold actuel du user connecté / expédi. ({}) : {} ", sender.getEmail(), sender.getSold());

        if (senderSold.compareTo(totalMontant) < 0 ) {
            log.error("TransServImpl : Le solde est insuffisant pour couvrir le montant + la commission");
            throw new InvalidTransactionException("Le solde est insuffisant pour couvrir le montant + la commission");
        }

        sender.setSold(senderSold.subtract(totalMontant));      // solde - totalMontant
        log.info("Sold du sender : {} - totalMontant : {} (total commission : {} )", sender.getSold(), totalMontant, commission);
        receiver.setSold(receiver.getSold().add(amount)); // amount ss commission

        TransactionEntity transactionEntity = transactionMapper.mapToTransactionEntity(transactionModel);
        transactionEntity.setSender(sender);
        transactionEntity.setReceiver(receiver);
        transactionEntity.setPercentage(commission);

        userRepository.save(sender);
        userRepository.save(receiver);
        transactionRepository.save(transactionEntity);

        TransactionModel transactionResult = transactionMapper.mapToTransactionModel(transactionEntity);
        return transactionResult;   // retour model + commission
    }


    /**
     *  Méthode permettant de sauvegarder une transaction en base de données
     *
     *  @param transactionModel
     *  @return TransactionModel
     * */
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


    /**
     *  Méthode permettant de récupérer toutes les transactions d'un utilisateur (destinataire ou expéditeur)
     *
     *  @param emailUser
     *  @param pageable
     *  @return Page<TransactionModel>
     *  @throws UserNotFoundException
     * */
    @Override
    public Page<TransactionModel> getTransactionsByUser(String emailUser, Pageable pageable) {

        UserEntity userConnected = userRepository.findByEmail(emailUser);
        if (userConnected == null) {
            log.error("Aucun utilisateur n'a été trouvé avec l'email : " + emailUser);
            throw new UserNotFoundException("Utilisateur non trouvé : " + emailUser);
        }
        // transactions du user connected
        Page<TransactionEntity> transactionEntityPage = transactionRepository.findBySenderOrReceiver(userConnected, userConnected, pageable);

        return transactionEntityPage.map(transactionMapper::mapToTransactionModel);
    }



}

