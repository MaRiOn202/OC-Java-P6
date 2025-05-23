package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.exception.InvalidTransactionException;
import com.openclassrooms.payMyBuddy.model.TransactionModel;
import com.openclassrooms.payMyBuddy.model.UserConnectionModel;
import com.openclassrooms.payMyBuddy.model.UserModel;
import com.openclassrooms.payMyBuddy.service.TransactionService;
import com.openclassrooms.payMyBuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {TransactionController.class})
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private UserService userService;


    @Test
    @WithMockUser(username = "michel1@paymybuddy.com")
    public void testAccessToTransactionFormWhenUserIsConnecting() throws Exception {

        UserModel userModel = new UserModel();
        userModel.setId(1L);
        userModel.setEmail("michel1@paymybuddy.com");

        List<TransactionModel> transactions = List.of(new TransactionModel(), new TransactionModel());
        Page<TransactionModel> transactionModelPage = new PageImpl<>(transactions);

        UserConnectionModel userConnectionModel = new UserConnectionModel();

        when(userService.getConnectingUser()).thenReturn(userModel);
        when(transactionService.getTransactionsByUser(eq(userModel.getEmail()), any(Pageable.class))).thenReturn(transactionModelPage);
        when(userService.getUserConnectionModel(userModel.getId())).thenReturn(userConnectionModel);

        mockMvc.perform(get("/transfert"))
                .andExpect(status().isOk())
                .andExpect(view().name("transfert"))
                .andExpect(model().attributeExists("userModel"))
                .andExpect(model().attributeExists("transactions"))
                .andExpect(model().attributeExists("relations"));
    }

    @Test
    @WithMockUser(username = "michel1@paymybuddy.com")
    public void testTransactionSuccess() throws Exception {

        UserModel userModel = new UserModel();
        userModel.setId(1L);
        userModel.setEmail("michel1@paymybuddy.com");

        TransactionModel transactionModel = new TransactionModel();
        transactionModel.setReceiver("receiver@paymybuddy");
        transactionModel.setAmount(BigDecimal.valueOf(100.00));

        List<TransactionModel> transactions = List.of(new TransactionModel(), new TransactionModel());
        Page<TransactionModel> transactionModelPage = new PageImpl<>(transactions);

        UserConnectionModel userConnectionModel = new UserConnectionModel();

        when(userService.getConnectingUser()).thenReturn(userModel);
        when(transactionService.getTransactionsByUser(eq(userModel.getEmail()), any(Pageable.class))).thenReturn(transactionModelPage);
        when(userService.getUserConnectionModel(userModel.getId())).thenReturn(userConnectionModel);
        doAnswer(invocation -> null).when(transactionService).createTransaction(any(TransactionModel.class));  // méthode exécutée normlment but ss effet == void

        mockMvc.perform(post("/transfert/save")
                .param("receiver", transactionModel.getReceiver())
                .param("amount", String.valueOf(transactionModel.getAmount()))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfert?success=true"));
    }

    @Test
    @WithMockUser(username = "michel6@paymybuddy.com")
    public void testTransactionWithAnInvalidTransaction() throws Exception {

        UserModel userModel = new UserModel();
        userModel.setId(6L);
        userModel.setEmail("michel6@paymybuddy.com");

        TransactionModel transactionModel = new TransactionModel();
        transactionModel.setReceiver("receiver@paymybuddy");
        transactionModel.setAmount(BigDecimal.valueOf(1000.00)); // sold trop important

        when(userService.getConnectingUser()).thenReturn(userModel);
        when(transactionService.createTransaction(any(TransactionModel.class))).thenThrow(new InvalidTransactionException("Solde insuffisant"));

        mockMvc.perform(post("/transfert/save")
                .param("receiver", transactionModel.getReceiver())
                .param("amount", String.valueOf(transactionModel.getAmount()))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfert?error=true"));
    }


}
