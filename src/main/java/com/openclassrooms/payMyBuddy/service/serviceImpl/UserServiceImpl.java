package com.openclassrooms.payMyBuddy.service.serviceImpl;

import com.openclassrooms.payMyBuddy.entity.UserEntity;
import com.openclassrooms.payMyBuddy.exception.UserNotConnectedException;
import com.openclassrooms.payMyBuddy.exception.UserNotFoundException;
import com.openclassrooms.payMyBuddy.mapper.UserMapper;
import com.openclassrooms.payMyBuddy.model.UserConnectionModel;
import com.openclassrooms.payMyBuddy.model.UserModel;
import com.openclassrooms.payMyBuddy.repository.UserRepository;
import com.openclassrooms.payMyBuddy.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 *    Service permettant de gérer les utilisateurs.
 * */

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);



    /**
     *  Méthode permettant de récupérer un utilisateur par son email
     *
     *  @param email
     *  @return UserModel
     *  @throws Exception
     * */
    @Override
    public UserModel getUserByEmail(String email) throws Exception {

        if (email == null) {
            log.info("L'email n'a pas été trouvé.");
            throw new Exception("L'email n'a pas été trouvé.");
        } else {
            UserEntity user = userRepository.findByEmail(email);
            UserModel userModel = userMapper.mapToUserModel(user);
            log.info("L'email a bien été trouvé.");
            return userModel;
        }
    }

  /// à supprimer
    @Override
    public UserModel getUser(String userName) throws Exception {
        if (userName == null) {
            log.info("La personne n'a pas été trouvée.");
            throw new Exception("La personne n'a pas été trouvée.");
        } else {
         UserEntity user = userRepository.findByUsername(userName);
         UserModel userModel = userMapper.mapToUserModel(user);
        log.info("La personne a bien été trouvée.");
        return userModel;
        }
    }


    /**
     *  Méthode permettant de sauvegarder un utilisateur en base de données
     *
     *  @param userModel
     *  @return UserModel
     * */
    @Override
    public UserModel saveUser(UserModel userModel)  {

        // Validation des données d'entrée
        //Traiter les données
        // Construire les réponses à renvoyer à l'utilisateur
        log.info("Tentative de création d'un user avec l'email: {}", userModel.getEmail());
        
        userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
        UserEntity userEntity = userMapper.mapToUserEntity(userModel);

        log.info("Avant sauvegarde: Email={}, Username={}", userEntity.getEmail(), userEntity.getUsername());
        userEntity = userRepository.save(userEntity);

        UserModel savedUser = userMapper.mapToUserModel(userEntity);
        log.info("L'utilisateur a bien été ajouté.");
        return savedUser;

    }


    /**
     *  Méthode permettant de mettre à jour les données d'un utilisateur
     *
     *  @param userModel
     *  @return UserModel
     *  @throws UserNotFoundException
     *  @throws IllegalArgumentException
     * */
    @Override
    public UserModel updateUser(UserModel userModel) {
        // 1  Validation des données d'entrée
        if (userModel == null || userModel.getEmail() == null) {
            log.error("Les données sont invalides.");
            throw new IllegalArgumentException("L'email est obligatoire.");
        }

        // 2  a) si l'user non trouvé b) si ok charger user
        UserEntity existingUser = userRepository.findByEmail(userModel.getEmail());
        if (existingUser == null) {
            log.error("Aucun utilisateur n'a été trouvé avec l'email : " + userModel.getEmail());
            throw new UserNotFoundException("Utilisateur non trouvé.");
        }

        if (existingUser.getId() == null) {
            throw new IllegalArgumentException("L'Id du user ne peut pas être null");
        }

        // Traitement des données
        // Mapper à la main les champs qui peuvent être modifiés
        existingUser.setUsername(userModel.getUsername());
        existingUser.setSold(userModel.getSold());

        if (userModel.getPassword() != null && !userModel.getPassword().isEmpty()) {
            if (!existingUser.getPassword().equals(userModel.getPassword())) {
                existingUser.setPassword(passwordEncoder.encode(userModel.getPassword()));
            }
        }

        UserEntity updatedUser = userRepository.save(existingUser);

        // Construire réponses à renvoyer à l'utilisateur 
        UserModel userReturn = userMapper.mapToUserModel(updatedUser);
        log.info("L'utilisateur a été mis à jour avec succès : " + userReturn.getEmail());
        return userReturn;
    }


    /**
     *  Méthode permettant de récupérer l'utilisateur connecté
     *
     *  @return UserModel
     *  @throws UserNotFoundException
     *  @throws UserNotConnectedException
     * */
    @Override                        
    public UserModel getConnectingUser() throws UserNotFoundException, UserNotConnectedException {
        // Validation des données d'entrée
        //Traiter les données
        // Construire les réponses à renvoyer à l'utilisateur
        // 1
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();  // contexte de sécurité pour récupérer le user connecté
        log.info("Utilisateur authentifié après connexion : {}", authentication);
        
        if(authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            log.error("L'utilisateur n'est pas connecté ! Authentication : {} ", authentication);
            throw new UserNotConnectedException("Utilisateur non connecté.");
        }

        // 2
        String email = authentication.getName();
        UserEntity userEntity = userRepository.findByEmail(email);
        if(userEntity== null) {
          log.error("Aucun utilisateur n'a été trouvé avec l'email : " + email);
          throw new UserNotFoundException("Utilisateur non trouvé avec l'email : " + email);
        }

        // 3
        UserModel userModel = userMapper.mapToUserModel(userEntity);
        log.info("L'email de l'utilisateur a été récupéré avec succès : {} ", userModel.getEmail());
        log.info("L'Id de l'utilisateur a été récupéré avec succès : {} ", userModel.getId());
        return userModel;
    }


    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        log.info("La personne a bien été supprimée.");

    }


    /**
     *  Méthode permettant d'ajouter une relation / connexion entre l'utilisateur connecté et un autre utilisateur
     *  en vue d'effectuer par la suite un transfert d'argent
     *
     *  @param emailFriend
     *  @return une liste de UserModel représentant les amis de l'utilisateur
     *  @throws UserNotFoundException
     *  @throws Exception
     * */
    @Transactional
    public List<UserModel> addRelation(String emailFriend) throws Exception {
        UserModel userModelConnected = getConnectingUser();

        UserEntity newRelation = userRepository.findByEmail(emailFriend);
        UserEntity userEntityConnected = userRepository.findByEmail(userModelConnected.getEmail());

        if (newRelation == null) {
            log.error("Aucun utilisateur n'a été trouvé avec l'email : " + emailFriend);
            throw new UserNotFoundException("Utilisateur non trouvé on trouvé avec l'email : " + emailFriend);
        }

        if (!userEntityConnected.getConnections().contains(newRelation)) {
            userEntityConnected.getConnections().add(newRelation);
            newRelation.getConnections().add(userEntityConnected);    // relation bidirectionnelle
            log.info("Ajout de la relation entre {} et {}", userEntityConnected.getEmail(),
                    newRelation.getEmail());

            userEntityConnected = userRepository.save(userEntityConnected);
            userRepository.save(newRelation);

        } else {
            log.error("La relation existe déjà");
            throw new Exception("La relation existe déjà");
        }
        return userEntityConnected.getConnections()         //persister la relation
                .stream()
                .map(userMapper::mapToUserModel)
                .collect(Collectors.toList());
    }


    /**
     *  Méthode permettant de récupérer les connexions d'un utilisateur
     *
     *  @param id
     *  @return UserConnectionModel
     *  @throws UserNotFoundException
     * */
    @Override
    public UserConnectionModel getUserConnectionModel(Long id) {

        // 1. user connecté par id
        Optional<UserEntity> optionalUserEntity = userRepository.findById(id);

        if (optionalUserEntity.isEmpty()) {
            throw new UserNotFoundException("Utilisateur non trouvé avec l'id : " + id);
        }
        UserEntity userEntity = optionalUserEntity.get();
        log.info("userEntity : {}", userEntity);
        // 2. user friends ctd les connections
        List<UserEntity> friends = userEntity.getConnections(); // car userEntity

        if (friends == null) {
            friends = new ArrayList<>();
        }

        if (friends.isEmpty()) {
            log.info("Aucune relation trouvée pour : {}", userEntity.getEmail());
        }
        
        // 3. List de UserConnectionModel
        UserConnectionModel userConnectionModel = new UserConnectionModel();
        userConnectionModel.setFriends(new ArrayList<>());
        for (UserEntity friendUserEntity : friends) {
            UserModel userModel = userMapper.mapToUserModel(friendUserEntity);
            userConnectionModel.getFriends().add(userModel);
        }
        log.info("Relations récupérées pour user {}: {}", userEntity.getEmail(), userConnectionModel.getFriends());
        return userConnectionModel;
    }

}
