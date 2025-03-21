package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@WebMvcTest(controllers = {UserController.class})
public class UserControllerTest {
    // test d'intégration
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;


    @Test
    public void testLoginUser() throws Exception {

     mockMvc.perform(get("/login"))
             .andDo(MockMvcResultHandlers.print())
             .andExpect(MockMvcResultMatchers.status().isOk())
             .andReturn();


    }


    
}
