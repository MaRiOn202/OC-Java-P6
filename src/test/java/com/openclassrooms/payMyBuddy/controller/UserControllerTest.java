package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.model.UserConnectionModel;
import com.openclassrooms.payMyBuddy.model.UserModel;
import com.openclassrooms.payMyBuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = {UserController.class})
public class UserControllerTest {
    // test d'intégration
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;


    @Test
    public void testHome() throws Exception {

        mockMvc.perform(get("/home")
                .with(user("testUser").roles("USER")))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testLoginUser() throws Exception {

     mockMvc.perform(get("/login"))
             .andDo(MockMvcResultHandlers.print())
             .andExpect(status().isOk())
             .andReturn();
    }

    @Test
    public void testLogoutSuccessFirstCaseUserConnected() throws Exception {

        mockMvc.perform(get("/logoutSuccess")
                .with(user("testUser1").roles("USER")))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testLogoutSuccessSecondCaseUserNoConnected() throws Exception {
        // 401 Unauthorized car non authentifié
        mockMvc.perform(get("/logoutSuccess")
                .with(anonymous()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(401));
    }

    @Test
    public void testProfile() throws Exception {
        // User doit être authentifié
        UserDetails userDetails = User.builder()
                .username("Michel4")
                .password("$2a$10$ekHyPA/UB8tJ2IS4Vp5Pquxzwi9x0Aila0ynInvTTCuVWJ550nsFK")
                .roles("USER")
                .build();

        // Security context
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Simuler un user
        UserModel userModel = new UserModel(4L, "Michel4", "michel4@paymybuddy.com",
                "$2a$10$ekHyPA/UB8tJ2IS4Vp5Pquxzwi9x0Aila0ynInvTTCuVWJ550nsFK", 100.00);
        when(userService.getConnectingUser()).thenReturn(userModel);
        
        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("userModel"))
                .andExpect(model().attribute("userModel", userModel));
    }

    @Test
    @WithMockUser(username = "Michel4", roles = "USER")
    public void testAccessToRelationFormFirstCaseUserConnected() throws Exception {

        mockMvc.perform(get("/relation"))
                .andExpect(status().isOk())
                .andExpect(view().name("relation"))
                .andExpect(model().attributeExists("userConnectionModel"));
    }

    @Test
    public void testAccessToRelationFormSecondCaseUserNoConnected() throws Exception {

        mockMvc.perform(get("/relation")
                .with(anonymous()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(401));
    }

    @Test
    @WithMockUser(username = "Michel3", roles = "USER")
    public void testAddRelation() throws Exception {

        UserConnectionModel userConnectionModel = new UserConnectionModel();
        userConnectionModel.setEmail("michel3@paymybuddy.com");

        // Friend
        List<UserModel> list = new ArrayList<>();
        list.add(new UserModel(5L, "MichelNum5", "michel5@paymybuddy.com", "1234", 200.00));

        when(userService.addRelation(userConnectionModel.getEmail())).thenReturn(list);

        mockMvc.perform(post("/relation/save")
                .flashAttr("userConnectionModel", userConnectionModel)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/relation?success"))
                .andReturn();
    }


/*    @Test
    @WithMockUser(username = "Michel4", roles = "USER")
    public void testProfileToUpdate() throws Exception {
        // User avt modif
        UserModel userModel = new UserModel(4L, "Michel4", "michel4@paymybuddy.com",
                "$2a$10$ekHyPA/UB8tJ2IS4Vp5Pquxzwi9x0Aila0ynInvTTCuVWJ550nsFK", 100.00);
        // Modifs
        UserModel userModelUpdated = new UserModel(4L, "MichelToutCourt", "michel4@paymybuddy.com",
                "$2a$10$ekHyPA/UB8tJ2IS4Vp5Pquxzwi9x0Aila0ynInvTTCuVWJ550nsFK", 500.00);

        when(userService.getConnectingUser()).thenReturn(userModel);

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("userModel"))
                .andExpect(model().attribute("userModel", userModel));

        // update method
        when(userService.updateUser(any(UserModel.class))).thenReturn(userModelUpdated);

        mockMvc.perform(post("/profile/update")
                        .param("sold", String.valueOf(500.00))
                        .param("username", "MichelToutCourt")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?success"))  // Redirection vers pprofile success
                .andExpect(model().attribute("success", "Les modifications ont été enregistrées avec succès !"));

    }*/




}
