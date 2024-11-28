package com.openclassrooms.payMyBuddy.services.serviceImpl;

import com.openclassrooms.payMyBuddy.entity.UserEntity;
import com.openclassrooms.payMyBuddy.exception.UserNotFoundException;
import com.openclassrooms.payMyBuddy.mapper.UserMapper;
import com.openclassrooms.payMyBuddy.model.UserModel;
import com.openclassrooms.payMyBuddy.repository.UserRepository;
import com.openclassrooms.payMyBuddy.services.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;


@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    //private PasswordEncoder passwordEncoder;

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
    public UserModel saveUser(UserModel userModel)  {

        // Validation des données d'entrée
        //Traiter les données
        // Construire les réponses à renvoyer à l'utilisateur
        UserEntity userEntity = userMapper.mapToUserEntity(userModel);
        userEntity = userRepository.save(userEntity);
        UserModel savedUser = userMapper.mapToUserModel(userEntity);
        log.info("L'utilisateur a bien été ajouté.");
        return savedUser;

    }

    // Validation des données d'entrée
    //Traiter les données
    // Construire les réponses à renvoyer à l'utilisateur
    @Override
    public UserModel updateUser(UserModel userModel) throws Exception {
        // Étape 1 : Validation des données d'entrée
        if (userModel == null || userModel.getEmail() == null) {
            log.error("Validation échouée : les données d'entrée sont invalides.");
            throw new IllegalArgumentException("L'email de l'utilisateur est obligatoire pour la mise à jour.");
        }

        // Étape 2 : Traitement des données
        // Vérifier si l'utilisateur existe dans la base
        UserEntity existingUser = userRepository.findByEmail(userModel.getEmail());
        if (existingUser == null) {
            log.error("Aucun utilisateur trouvé avec l'email : " + userModel.getEmail());
            throw new UserNotFoundException("Utilisateur non trouvé pour la mise à jour.");
        }

        // Mapper les champs à mettre à jour
        userMapper.updateUserEntityFromModel(userModel, existingUser); 

        // Sauvegarder les changements
        UserEntity updatedUser = userRepository.save(existingUser);

        // Étape 3 : Construction de la réponse
        UserModel userResponse = userMapper.mapToUserModel(updatedUser);
        log.info("L'utilisateur a été mis à jour avec succès : " + userResponse.getEmail());
        return userResponse;
    }


    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        log.info("La personne a bien été supprimée.");

    }
}
