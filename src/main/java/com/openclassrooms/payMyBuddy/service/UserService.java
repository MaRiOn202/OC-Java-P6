package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.entity.UserEntity;
import com.openclassrooms.payMyBuddy.model.UserConnectionModel;
import com.openclassrooms.payMyBuddy.model.UserModel;
import jakarta.validation.Valid;

import java.util.List;


public interface UserService {



    UserModel getUserByEmail(String email) throws Exception;

    UserModel getUser(String userName) throws Exception;

    List<UserModel> findAllUsers() throws Exception;


    UserModel saveUser(UserModel userModel) throws Exception;

    UserModel updateUser(UserModel userModel) throws Exception;

    void deleteUser(final Long id) throws Exception;

    UserModel getConnectingUser() throws Exception;

    // ajouter les connexions userconnexionModel
    List<UserModel> addRelation(String emailFriend) throws Exception;

    UserConnectionModel getUserConnectionModel(Long id);

}
