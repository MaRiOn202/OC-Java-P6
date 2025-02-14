package com.openclassrooms.payMyBuddy.service.serviceImpl;

import com.openclassrooms.payMyBuddy.entity.TransactionEntity;
import com.openclassrooms.payMyBuddy.entity.UserEntity;
import com.openclassrooms.payMyBuddy.exception.InvalidTransactionException;
import com.openclassrooms.payMyBuddy.exception.ReceiverNotFoundException;
import com.openclassrooms.payMyBuddy.exception.UserNotFoundException;
import com.openclassrooms.payMyBuddy.mapper.TransactionMapper;
import com.openclassrooms.payMyBuddy.mapper.UserMapper;
import com.openclassrooms.payMyBuddy.model.TransactionModel;
import com.openclassrooms.payMyBuddy.model.UserConnectionModel;
import com.openclassrooms.payMyBuddy.model.UserModel;
import com.openclassrooms.payMyBuddy.repository.TransactionRepository;
import com.openclassrooms.payMyBuddy.repository.UserRepository;
import com.openclassrooms.payMyBuddy.service.TransactionService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {


    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private UserMapper userMapper;

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
        log.info("Expéditeur trouvé : {} avec sold : {}",  sender.getEmail(), sender.getSold());

        if(sender == null) {
            log.error("TransServImpl : L'expéditeur est introuvable");
           throw new UserNotFoundException("L'expéditeur est introuvable");
        }

        // 2 destinataire
        log.info("Recherche du destinaitaire email : " + transactionModel.getReceiver());


        UserEntity receiver = userRepository.findByEmail(transactionModel.getReceiver());
        if(receiver == null) {
            log.error("TransServImpl : Le destinataire est introuvable avec email {}", transactionModel.getReceiver());
            throw new ReceiverNotFoundException("Le destinataire est introuvable");
        }
        log.info("Le destinataire a été trouvé : {} ", receiver.getEmail());

        
        // montant +
        log.info("Le montant de la transac : {} ", transactionModel.getAmount());
        if (transactionModel.getAmount() <= 0) {
            log.error("TransServImpl : Le montant de la transaction doit être positif");
            throw new InvalidTransactionException("Le montant de la transaction doit être positif");
        }

        // verif sold expé
        log.info("Sold actuel du user connecté / expédi. ({}) : {} ", sender.getEmail(), sender.getSold());
        if (sender.getSold() < transactionModel.getAmount()) {
            log.error("TransServImpl : Le solde de l'utilisateur est insuffisant");
            throw new InvalidTransactionException("Le solde de l'utilisateur est insuffisant");
        }

        // commission 5%
        double commission = (5.0 / 100) * transactionModel.getAmount()  ;
        transactionModel.setPercentage(commission);

        // montant total avec com
        double totalMontant = transactionModel.getAmount() + commission;

        sender.setSold(sender.getSold() - totalMontant);      // solde - totalMontant
        receiver.setSold(receiver.getSold() + transactionModel.getAmount()); // amount ss commission
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
    public List<UserConnectionModel> getUserConnectionModel(Long id) {

        // 1. user connecté par id
        Optional<UserEntity> optionalUserEntity = userRepository.findById(id);

        if (optionalUserEntity.isEmpty()) {
            throw new UserNotFoundException("Utilisateur non toruvé avec l'id : " + id);
        }

        UserEntity userEntity = optionalUserEntity.get();

        // 2. user friends ctd les connections
        List<UserEntity> friends = userEntity.getConnections(); // car userEntity
        if (friends.isEmpty()) {
           log.info("Aucune relation trouvée pour : {}", userEntity.getEmail());
        }
        

        // 3. List de UserConnectionModel
        List<UserConnectionModel> listUserConnectionModel = new ArrayList<>();

        for (UserEntity friendUserEntity : friends) {
            UserModel userModel = userMapper.mapToUserModel(friendUserEntity);
            listUserConnectionModel.add(
                    new UserConnectionModel(
                       List.of(userModel), friendUserEntity.getEmail()        // champs dans UCModel
                    )
            );
        }
        log.info("Relations récupérées pour user {}: {}", userEntity.getEmail(), friends);
        return listUserConnectionModel;
    }


}

