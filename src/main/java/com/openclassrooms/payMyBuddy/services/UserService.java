package com.openclassrooms.payMyBuddy.services;

import com.openclassrooms.payMyBuddy.model.UserModel;
import org.springframework.stereotype.Service;


public interface UserService {



    UserModel getUserByEmail(String email) throws Exception;

    UserModel getUser(String userName) throws Exception;

    //List<UserModel> findAllUsers() throws Exception;

    UserModel saveUser(UserModel userModel) throws Exception;

    UserModel updateUser(UserModel userModel) throws Exception;

    void deleteUser(final Long id) throws Exception;

    

}
