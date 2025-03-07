package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.entity.UserEntity;
import com.openclassrooms.payMyBuddy.mapper.UserMapper;
import com.openclassrooms.payMyBuddy.model.UserModel;
import com.openclassrooms.payMyBuddy.repository.UserRepository;
import com.openclassrooms.payMyBuddy.service.serviceImpl.UserServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

   @Mock
   private UserRepository userRepository;

   @Mock
   private UserMapper userMapper;

   @Mock
   private PasswordEncoder passwordEncoder;

   @InjectMocks
   private UserServiceImpl userServiceImpl;

   private static final Logger log = LoggerFactory.getLogger(UserServiceImplTest.class);

   private UserEntity userEntity;

   private UserModel userModelActual;
   private UserModel userModelExpected;
   private String email;
   private String password;


   @BeforeEach
   public void setUp() {

      MockitoAnnotations.openMocks(this);

      // Entity existante
       email = "michel1@paymybuddy.com";
       password = "1234";

       // authentication
/*       Authentication authentication = new UsernamePasswordAuthenticationToken(
               new org.springframework.security.core.userdetails.User(email, "password", List.of()),
               null,
               List.of()
       );
       SecurityContextHolder.getContext().setAuthentication(authentication);*/

       userEntity = new UserEntity();
       userEntity.setId(1L);
       userEntity.setUsername("Michel");
       userEntity.setPassword("1234");
       userEntity.setEmail(email);

       // model à maj
       userModelActual = new UserModel();
       userModelActual.setId(1L);
       userModelActual.setUsername("Michel");
       userModelActual.setEmail("michel1@paymybuddy.com");
       userModelActual.setPassword("1234");

       // model attendu après maj
       userModelExpected = new UserModel();
       userModelExpected.setId(2L);
       userModelExpected.setUsername("Francis");
       userModelExpected.setPassword("12345");
       userModelExpected.setEmail("francis@paymybuddy.com");
   }

   @AfterEach
   void endSecurityContext() {
      SecurityContextHolder.clearContext();
   }


    @Test
    public void testGetUserByEmailShouldReturnUserModel() throws Exception {
       
       when(userRepository.findByEmail(email)).thenReturn(userEntity);
       when(userMapper.mapToUserModel(userEntity)).thenReturn(userModelExpected);

       UserModel userModelActual = userServiceImpl.getUserByEmail(email);
       
       assertEquals(userModelExpected.getId(), userModelActual.getId());
       assertEquals(userModelExpected.getUsername(), userModelActual.getUsername());
       assertEquals(userModelExpected.getEmail(), userModelActual.getEmail());
       verify(userRepository, times(1)).findByEmail(email);
    }


    @Test
    public void testSaveUserSuccess() {

       UserEntity userEntityExpected = new UserEntity();
       userEntityExpected.setId(2L);
       userEntityExpected.setUsername("Francis");
       userEntityExpected.setEmail("francis@paymybuddy.com");
       userEntityExpected.setPassword("12345");

       when(passwordEncoder.encode(userModelExpected.getPassword())).thenReturn(password);
       when(userMapper.mapToUserEntity(userModelExpected)).thenReturn(userEntityExpected);
       when(userRepository.save(userEntityExpected)).thenReturn(userEntityExpected);
       when(userMapper.mapToUserModel(userEntityExpected)).thenReturn(userModelExpected);

       UserModel userModelSaved = userServiceImpl.saveUser(userModelExpected);

       assertEquals(userModelExpected.getPassword(), userModelSaved.getPassword());
       assertEquals(userModelExpected.getUsername(), userModelSaved.getUsername());
       assertEquals(userModelExpected.getEmail(), userModelSaved.getEmail());
       verify(userRepository, times(1)).save(userEntityExpected);

    }

    @Test
    public void testUpdateUserShouldReturnUserModelUpdated() {
        UserEntity userEntityUpdated = new UserEntity();
        userEntityUpdated = new UserEntity();
        userEntityUpdated.setId(1L);
        userEntityUpdated.setUsername("MichMich");
        userEntityUpdated.setPassword("passwordEncoded");
        userEntityUpdated.setEmail(email);

        UserModel userModelActual = new UserModel();
        userModelActual.setId(1L);
        userModelActual.setUsername("MichMich");
        userModelActual.setPassword("6789");
        userModelActual.setEmail(email);

        UserModel userModelExpected = new UserModel();
        userModelExpected.setId(1L);
        userModelExpected.setUsername("MichMich");
        userModelExpected.setPassword("6789");
        userModelExpected.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(userEntity);
        when(passwordEncoder.encode(userModelActual.getPassword())).thenReturn("passwordEncoded");
        when(userRepository.save(userEntity)).thenReturn(userEntityUpdated);
        when(userMapper.mapToUserModel(userEntityUpdated)).thenReturn(userModelExpected);

        UserModel userModelUpdated = userServiceImpl.updateUser(userModelActual);

        assertEquals(userModelExpected.getUsername(), userModelUpdated.getUsername());
        assertEquals(userModelExpected.getId(), userModelUpdated.getId());
        assertNotEquals(userModelActual.getPassword(), userEntityUpdated.getPassword());
        assertEquals("passwordEncoded", userEntityUpdated.getPassword());
        verify(userRepository, times(1)).save(userEntity);
        verify(passwordEncoder, times(1)).encode(userModelActual.getPassword());
    }


    @Test
    public void testGetConnectingUserShouldReturnUserConnected() {

        String email = "michel1@paymybuddy.com";
        UserEntity userConnected = new UserEntity();
        userConnected.setId(2L);
        userConnected.setEmail(email);
        userConnected.setUsername("Michel");

        UserModel userModelExpected = new UserModel();
        userModelExpected.setId(1L);
        userConnected.setEmail(email);
        userConnected.setUsername("Michel");

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "michel1@paymybuddy.com",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userRepository.findByEmail(email)).thenReturn(userConnected);
        when(userMapper.mapToUserModel(userConnected)).thenReturn(userModelExpected);

        UserModel userModelActual = userServiceImpl.getConnectingUser();

        assertEquals(userModelExpected.getEmail(), userModelActual.getEmail());
        assertEquals(userModelExpected.getId(), userModelActual.getId());
        assertEquals(userModelExpected.getUsername(), userModelActual.getUsername());
        verify(userRepository, times(1)).findByEmail(email);
    }
    

    @Test
    public void testDeleteUserSuccess() {

        Long id = 1L;
        userServiceImpl.deleteUser(id);

        verify(userRepository, times(1)).deleteById(id);
    }





 /*  @Test
    public void testAddRelation() throws Exception {

      // UserEntity n°1 utilisateur connecté
      UserEntity userConnected = new UserEntity();
      userConnected.setId(1L);
      userConnected.setUsername("Michel1");
      userConnected.setPassword("1234");
      userConnected.setEmail("michel1@paymybuddy.com");
      userConnected.setConnections(new ArrayList<>());

      // UserEntity n°2  nouvelle relation / connection
      UserEntity newRelation = new UserEntity();
      newRelation.setId(4L);
      newRelation.setUsername("Michel4");
      newRelation.setPassword("1234");
      newRelation.setEmail("michel4@paymybuddy.com");

      when(userRepository.findByEmail("michel1@paymybuddy.com")).thenReturn(userConnected);
      when(userRepository.findByEmail("michel4@paymybuddy.com")).thenReturn(newRelation);

      Authentication authentication = new UsernamePasswordAuthenticationToken(
              "michel1@paymybuddy.com",
      null,
              List.of());
      SecurityContextHolder.getContext().setAuthentication(authentication);

      UserConnexionModel userConnexionModel = new UserConnexionModel();
      userConnexionModel.setEmail("michel4@paymybuddy.com");
      userServiceImpl.addRelation(userConnexionModel);

      assertTrue(userConnected.getConnections().contains(newRelation),
              "La newRelation a bien été intégrée à Michel1");
      verify(userRepository, times(1)).save(newRelation);
      log.info("Test terminé avec succès ! ");
   }*/




}
