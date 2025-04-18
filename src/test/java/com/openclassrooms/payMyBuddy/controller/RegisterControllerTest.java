package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.model.UserModel;
import com.openclassrooms.payMyBuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {RegisterController.class})
public class RegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;


    @Test
    @WithMockUser
    public void testAccessToRegisterFormSuccess() throws Exception {

        mockMvc.perform(get("/register"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }
    
    
    @Test
    @WithMockUser
    public void testRegistrationSuccess() throws Exception {

        // Registration new user
        UserModel userModel = new UserModel();
        userModel.setEmail("michel6@paymybuddy.com");
        userModel.setUsername("Michel6");
        userModel.setPassword("1234");

        when(userService.getUserByEmail(userModel.getEmail())).thenReturn(null);
        doAnswer(invocation -> null).when(userService).saveUser(any(UserModel.class));

        mockMvc.perform(post("/register/save")
                .flashAttr("userModel", userModel)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register?success"))
                .andReturn();
    }

}
