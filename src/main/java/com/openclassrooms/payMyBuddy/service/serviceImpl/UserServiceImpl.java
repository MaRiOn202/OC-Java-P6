package com.openclassrooms.payMyBuddy.service.serviceImpl;

import com.openclassrooms.payMyBuddy.entity.UserEntity;
import com.openclassrooms.payMyBuddy.exception.UserNotConnectedException;
import com.openclassrooms.payMyBuddy.exception.UserNotFoundException;
import com.openclassrooms.payMyBuddy.mapper.UserMapper;
import com.openclassrooms.payMyBuddy.model.UserConnexionModel;
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
import java.util.stream.Collectors;


@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    


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

    @Override
    public List<UserModel> findAllUsers() {

        List<UserEntity> listUsersEntity = userRepository.findAll();
        if (listUsersEntity.isEmpty()) {
              return Collections.emptyList();
          }
        return  listUsersEntity
                .stream()
                .map(userEntity -> userMapper.mapToUserModel(userEntity))
                .collect(Collectors.toList());
    }


    @Override
    public UserModel saveUser(UserModel userModel)  {

        // Validation des données d'entrée
        //Traiter les données
        // Construire les réponses à renvoyer à l'utilisateur
        log.info("Tentative de création d'un user avec l'email: {}", userModel.getEmail());

/*        if (userModel.getEmail() != null) {
            log.info("User existe déjà : {}", userModel.getEmail());
            throw new EmailAlreadyExistingException("Cet email est déjà utilisé.");
        }*/
        userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
        UserEntity userEntity = userMapper.mapToUserEntity(userModel);
        log.info("User à sauvegarder: {}", userEntity);

        userEntity = userRepository.save(userEntity);
        log.info("User a bien été sauvegardé victoire ! : {}", userEntity);

        UserModel savedUser = userMapper.mapToUserModel(userEntity);
        log.info("L'utilisateur a bien été ajouté.");
        return savedUser;

    }


    @Override
    public UserModel updateUser(UserModel userModel) {
        // 1  Validation des données d'entrée
        if (userModel == null || userModel.getEmail() == null) {
            log.error("Les données sont invalides.");
            throw new IllegalArgumentException("L'email est obligatoire.");
        }

        // 2  a) si l'user non trouvé b) ok
        UserEntity existingUser = userRepository.findByEmail(userModel.getEmail());
        if (existingUser == null) {
            log.error("Aucun utilisateur n'a été trouvé avec l'email : " + userModel.getEmail());
            throw new UserNotFoundException("Utilisateur non trouvé.");
        }

        // Traitement des données
        userMapper.updateUserEntityFromModel(userModel, existingUser);

        if (userModel.getPassword() != null && !userModel.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userModel.getPassword()));
        }
        // save
        UserEntity updatedUser = userRepository.save(existingUser);

        // Construire réponses à renvoyer à l'utilisateur 
        UserModel userReturn = userMapper.mapToUserModel(updatedUser);
            log.info("L'utilisateur a été mis à jour avec succès : " + userReturn.getEmail());
        return userReturn;
    }

    // Validation des données d'entrée
    //Traiter les données
    // Construire les réponses à renvoyer à l'utilisateur
    @Override
    public UserModel getConnectingUser() throws UserNotFoundException, UserNotConnectedException {
        // 1
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();  // contexte de sécurité pour récupérer le user connecté
        log.info("Utilisateur authentifié : {}", authentication);
        
        if(authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            log.error("L'utilisateur n'est pas connecté !");
            throw new UserNotConnectedException("Utilisateur non connecté.");
        }
        // 2
        String email = authentication.getName();
        UserEntity userEntity = userRepository.findByEmail(email);
        if(userEntity== null) {
          log.error("Aucun utilisateur n'a été trouvé avec l'email : " + email);
          throw new UserNotFoundException("Utilisateur non trouvé avec l'eamil : " + email);
        }
        // 3
        UserModel userModel = userMapper.mapToUserModel(userEntity);
        log.info("L'utilisateur a été récupéré avec succès : {} ", userModel.getEmail());
        return userModel;
    }


    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        log.info("La personne a bien été supprimée.");

    }

   // ajouter les connexions userconnexionModel
    @Override
    @Transactional
    public void addRelation(UserConnexionModel userConnexionModel) throws Exception {
        UserModel userModelConnected = getConnectingUser();
        UserEntity userEntityConnected = userRepository.findByEmail(userModelConnected.getEmail());

        if (userEntityConnected.getEmail().equals(userConnexionModel.getEmail())) {
            throw new Exception("Vous ne pouvez pas ajouter votre propre email comme relation");
        }

        UserEntity newRelation = userRepository.findByEmail(userConnexionModel.getEmail());
        if (newRelation == null) {
            log.error("Aucun utilisateur n'a été trouvé avec l'email : " + userConnexionModel.getEmail());
            throw new UserNotFoundException("Utilisateur non trouvé en base de données.");
        }

        // Initialiser liste si null
        if (userEntityConnected.getConnections() == null) {
           userEntityConnected.setConnections(new ArrayList<>());
        }

        if (!userEntityConnected.getConnections().contains(newRelation)) {
            userEntityConnected.getConnections().add(newRelation);
            log.info("Récupération de l'utilisateur ocnnecté : {}", userEntityConnected.getEmail());
            log.info("Ajout de la relation  : {}", newRelation.getEmail());
            log.info("Ajout de la relation entre {} et {}", userEntityConnected.getEmail(),
                    newRelation.getEmail());
            userRepository.save(userEntityConnected);       //persister la relation
        } else {
            log.error("La relation existe déjà");
            throw new Exception("La relation existe déjà");
        }

    }

}
