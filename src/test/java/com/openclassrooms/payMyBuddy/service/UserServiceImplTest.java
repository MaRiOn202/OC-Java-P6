package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.entity.UserEntity;
import com.openclassrooms.payMyBuddy.exception.UserNotFoundException;
import com.openclassrooms.payMyBuddy.mapper.UserMapper;
import com.openclassrooms.payMyBuddy.model.UserModel;
import com.openclassrooms.payMyBuddy.model.UserConnectionModel;
import com.openclassrooms.payMyBuddy.repository.UserRepository;
import com.openclassrooms.payMyBuddy.service.serviceImpl.UserServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Spy
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private static final Logger log = LoggerFactory.getLogger(UserServiceImplTest.class);

    private static Authentication authentication;
    private UserEntity userEntity;
    private UserModel userModelExpected;
    private String email;
    private String password;
    private UserConnectionModel userConnectionModel;


    @BeforeAll
    public static void beforeAll() {
        authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.lenient().when(authentication.getName()).thenReturn("michel1@paymybuddy.com");
        when(authentication.isAuthenticated()).thenReturn(true);
    }

    @BeforeEach
    public void setUp() {

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("Michel");
        userEntity.setPassword("1234");
        userEntity.setEmail("michel1@paymybuddy.com");

        userModelExpected = new UserModel();
        userModelExpected.setId(1L);
        userModelExpected.setUsername("Michel");
        userModelExpected.setPassword("1234");
        userModelExpected.setEmail("michel1@paymybuddy.com");
    }


    @Test
    public void testGetUserByEmailShouldReturnUserModel() throws Exception {
        email = "michel1@paymybuddy.com";

        when(userRepository.findByEmail(email)).thenReturn(userEntity);
        when(userMapper.mapToUserModel(any(UserEntity.class))).thenReturn(userModelExpected);

        UserModel userModelActual = userServiceImpl.getUserByEmail("michel1@paymybuddy.com");

        assertNotNull(userModelActual);
        assertEquals(userModelExpected.getEmail(), userModelActual.getEmail());
        assertEquals(userModelExpected.getUsername(), userModelActual.getUsername());
        assertEquals("michel1@paymybuddy.com", userModelActual.getEmail());
        verify(userRepository, times(1)).findByEmail(email);
        verify(userMapper, times(1)).mapToUserModel(userEntity);
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
        userEntityUpdated.setEmail("michel1@paymybuddy.com");

        UserModel userModelActual = new UserModel();
        userModelActual.setId(1L);
        userModelActual.setUsername("MichMich");
        userModelActual.setPassword("6789");
        userModelActual.setEmail("michel1@paymybuddy.com");

        UserModel userModelExpected = new UserModel();
        userModelExpected.setId(1L);
        userModelExpected.setUsername("MichMich");
        userModelExpected.setPassword("6789");
        userModelExpected.setEmail("michel1@paymybuddy.com");

        when(userRepository.findByEmail(userEntityUpdated.getEmail())).thenReturn(userEntity);
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



    @Test
    public void testAddRelation() throws Exception {

        String emailFriend = "michel4@paymybuddy.com";
      // UserEntity n°1 utilisateur connected
      UserEntity userConnected = new UserEntity();
      userConnected.setId(1L);
      userConnected.setUsername("Michel1");
      userConnected.setPassword("1234");
      userConnected.setEmail("michel1@paymybuddy.com");
      userConnected.setConnections(new ArrayList<>());

      // UserEntity n°2  new relation / connection
      UserEntity newRelation = new UserEntity();
      newRelation.setId(4L);
      newRelation.setUsername("Michel4");
      newRelation.setPassword("1234");
      newRelation.setEmail("michel4@paymybuddy.com");
      newRelation.setConnections(new ArrayList<>());

      when(userRepository.findByEmail(emailFriend)).thenReturn(newRelation);
      when(userRepository.findByEmail("michel1@paymybuddy.com")).thenReturn(userConnected);

      //when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
      when(userRepository.save(userConnected)).thenReturn(userConnected);

      Authentication authentication = new UsernamePasswordAuthenticationToken(
              "michel1@paymybuddy.com",
              null,
               List.of());
      SecurityContextHolder.getContext().setAuthentication(authentication);

      List<UserModel> resultList = userServiceImpl.addRelation("michel4@paymybuddy.com");

      assertNotNull(resultList);
      assertTrue(userConnected.getConnections().contains(newRelation),
              "La newRelation a bien été effectuée");
      assertTrue(newRelation.getConnections().contains(userConnected),
                "La relation est bidirectionnelle");
      verify(userRepository, times(2)).save(any(UserEntity.class));
      
   }

    @Test
    public void testAddRelationWhenUserUserNotFound() {

        when(userRepository.findByEmail("michel1@paymybuddy.com")).thenReturn(null);

        Exception e = assertThrows(UserNotFoundException.class, () -> {
            userServiceImpl.addRelation("michel4@paymybuddy.com");
        });

        assertEquals("Utilisateur non trouvé avec l'email : michel1@paymybuddy.com", e.getMessage());

    }


   @Test
   public void testGetUserConnectionModelWithFriends() {
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setEmail("michel1@paymybuddy.com");

        // Friends
       UserEntity friend1 = new UserEntity();
       friend1.setId(2L);
       friend1.setEmail("friend1@paymybuddy.com");

       UserEntity friend2 = new UserEntity();
       friend2.setId(3L);
       friend2.setEmail("friend2@paymybuddy.com");

       user.setConnections(List.of(friend1, friend2));
       when(userRepository.findById(userId)).thenReturn(Optional.of(user));

       UserModel friendModel1 = new UserModel();
       friendModel1.setEmail("friend1@paymybuddy.com");

       UserModel friendModel2 = new UserModel();
       friendModel2.setEmail("friend2@paymybuddy.com");

       when(userMapper.mapToUserModel(friend1)).thenReturn(friendModel1);
       when(userMapper.mapToUserModel(friend2)).thenReturn(friendModel2);

       UserConnectionModel result = userServiceImpl.getUserConnectionModel(userId);

       assertNotNull(result);
       assertEquals(2, result.getFriends().size());
       assertTrue(result.getFriends().contains(friendModel1));
       assertTrue(result.getFriends().contains(friendModel2));
       verify(userMapper, times(2)).mapToUserModel(any(UserEntity.class));
   }


}
