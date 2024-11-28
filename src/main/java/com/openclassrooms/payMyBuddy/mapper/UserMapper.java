package com.openclassrooms.payMyBuddy.mapper;

import com.openclassrooms.payMyBuddy.entity.UserEntity;
import com.openclassrooms.payMyBuddy.model.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@Mapper(componentModel="spring")
public interface UserMapper {

    UserEntity mapToUserEntity(UserModel userModel);

    UserModel mapToUserModel(UserEntity userEntity);

    // Méthode de mise à jour (cas particulier) 
    void updateUserEntityFromModel(UserModel userModel, @MappingTarget UserEntity userEntity);


    // Permet de cp les propriétés d'un obj UM ds un obj UE 
    void copy(UserModel userModel, @MappingTarget UserEntity userEntity);



}
