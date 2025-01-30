package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.model.UserConnexionModel;
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
    void addRelation(@Valid UserConnexionModel userModel) throws Exception;

}
