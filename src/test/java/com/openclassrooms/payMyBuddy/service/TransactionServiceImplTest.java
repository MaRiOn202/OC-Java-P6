package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.entity.TransactionEntity;
import com.openclassrooms.payMyBuddy.entity.UserEntity;
import com.openclassrooms.payMyBuddy.exception.InvalidTransactionException;
import com.openclassrooms.payMyBuddy.exception.ReceiverNotFoundException;
import com.openclassrooms.payMyBuddy.exception.UserNotFoundException;
import com.openclassrooms.payMyBuddy.mapper.TransactionMapper;
import com.openclassrooms.payMyBuddy.model.TransactionModel;
import com.openclassrooms.payMyBuddy.repository.TransactionRepository;
import com.openclassrooms.payMyBuddy.repository.UserRepository;
import com.openclassrooms.payMyBuddy.service.serviceImpl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private TransactionServiceImpl transactionServiceImpl;

    private UserEntity sender;
    private UserEntity receiver;
    private TransactionEntity transactionEntity; 
    private TransactionModel transactionModel;



    @BeforeEach
    void setUp() {
         //MockitoAnnotations.openMocks(this);

        sender = new UserEntity();
        sender.setEmail("michel1@paymybuddy.com");
        sender.setSold(200.0);

        receiver = new UserEntity();
        receiver.setEmail("michel2@paymybuddy.com");
        receiver.setSold(60.0);

        transactionEntity = new TransactionEntity();
        transactionEntity.setSender(sender);
        transactionEntity.setReceiver(receiver);
        transactionEntity.setAmount(20.0);

        transactionModel = new TransactionModel();
        transactionModel.setAmount(20.0);
        transactionModel.setReceiver("michel2@paymybuddy.com");

        // Authentication
        Mockito.lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.lenient().when(authentication.getName()).thenReturn("michel1@paymybuddy.com");
        Mockito.lenient().when(authentication.isAuthenticated()).thenReturn(true);

        
    }


    @Test
    public void testCreateTransactionTestReturnNewTransaction() throws Exception {

         when(userRepository.findByEmail("michel1@paymybuddy.com")).thenReturn(sender);
         when(userRepository.findByEmail("michel2@paymybuddy.com")).thenReturn(receiver);
         when(transactionMapper.mapToTransactionEntity(transactionModel)).thenReturn(transactionEntity);
         when(transactionRepository.save(any(TransactionEntity.class))).thenReturn(transactionEntity);
         when(transactionMapper.mapToTransactionModel(any(TransactionEntity.class))).thenReturn(transactionModel);

         TransactionModel result = transactionServiceImpl.createTransaction(transactionModel);

         assertNotNull(result);
         assertEquals(transactionModel.getReceiver(), result.getReceiver());
         assertEquals(transactionModel.getAmount(), result.getAmount());
         verify(userRepository, times(1)).save(sender);
         verify(userRepository, times(1)).save(receiver);
         verify(transactionRepository, times(1)).save(any(TransactionEntity.class));

    }

    @Test
    public void testCreateTransactionWhenSenderNotFound() {

        when(userRepository.findByEmail("michel1@paymybuddy.com")).thenReturn(null);

         Exception e = assertThrows(UserNotFoundException.class, () -> {
             transactionServiceImpl.createTransaction(transactionModel);
         });

         assertEquals("L'expéditeur est introuvable", e.getMessage());

    }


    @Test
    public void testCreateTransactionWhenReceiverNotFound() {

        when(userRepository.findByEmail("michel1@paymybuddy.com")).thenReturn(sender);
        when(userRepository.findByEmail("michel2@paymybuddy.com")).thenReturn(null);

        Exception e = assertThrows(ReceiverNotFoundException.class, () -> {
            transactionServiceImpl.createTransaction(transactionModel);
        });

        assertEquals("Le destinataire est introuvable", e.getMessage());

    }


    @Test
    public void testCreateTransactionWhenSoldIsInsufficient() {
        // sold      // transac = 20.0
        sender.setSold(19.20);

        when(userRepository.findByEmail("michel1@paymybuddy.com")).thenReturn(sender);
        when(userRepository.findByEmail("michel2@paymybuddy.com")).thenReturn(receiver);

        Exception e = assertThrows(InvalidTransactionException.class, () -> {
            transactionServiceImpl.createTransaction(transactionModel);
        });

        assertEquals("Le solde de l'utilisateur est insuffisant", e.getMessage());

    }

    @Test
    public void testSaveTransactionSuccess() {
        
        when(transactionMapper.mapToTransactionEntity(transactionModel)).thenReturn(transactionEntity);
        when(transactionRepository.save(transactionEntity)).thenReturn(transactionEntity);
        when(transactionMapper.mapToTransactionModel(transactionEntity)).thenReturn(transactionModel);

        TransactionModel savedTransaction = transactionServiceImpl.saveTransaction(transactionModel);

        assertNotNull(savedTransaction);
        assertEquals(transactionModel.getAmount(), savedTransaction.getAmount());
        verify(transactionRepository, times(1)).save(transactionEntity);
        
    }

    @Test
    public void testGetTransactionByUser() {

        when(userRepository.findByEmail("michel1@paymybuddy.com")).thenReturn(sender);
        Page<TransactionEntity> transactionEntities = new PageImpl<>(Collections.singletonList(transactionEntity));
        when(transactionRepository.findBySenderOrReceiver(any(UserEntity.class), any(UserEntity.class), any(Pageable.class)))
                .thenReturn(transactionEntities);
        when(transactionMapper.mapToTransactionModel(any(TransactionEntity.class))).thenReturn(transactionModel);

        Page<TransactionModel> result = transactionServiceImpl.getTransactionsByUser("michel1@paymybuddy.com", Pageable.unpaged());

        assertFalse(result.isEmpty());
        assertEquals(1, result.getContent().size());
        verify(transactionRepository, times(1)).findBySenderOrReceiver(any(UserEntity.class),
                any(UserEntity.class), any(Pageable.class));
        
    }

    @Test
    public void testGetTransactionByUserWhenUserNotFound() {

        when(userRepository.findByEmail("null@paymybuddy.com")).thenReturn(null);

        Exception e = assertThrows(UserNotFoundException.class, () -> {
            transactionServiceImpl.getTransactionsByUser("null@paymybuddy.com", Pageable.unpaged());
        });

        assertEquals("Utilisateur non trouvé : null@paymybuddy.com", e.getMessage());
        
    }

}
