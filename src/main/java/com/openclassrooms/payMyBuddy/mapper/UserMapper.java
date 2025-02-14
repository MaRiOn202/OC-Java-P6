package com.openclassrooms.payMyBuddy.mapper;

import com.openclassrooms.payMyBuddy.entity.UserEntity;
import com.openclassrooms.payMyBuddy.model.UserConnectionModel;
import com.openclassrooms.payMyBuddy.model.UserModel;
import org.mapstruct.*;


@Mapper(componentModel="spring")
public interface UserMapper {

        // ignore le mappage en 1
    UserEntity mapToUserEntity(UserModel userModel);

    
    @Mapping(target = "id", source = "id")
    UserModel mapToUserModel(UserEntity userEntity);

    UserEntity mapUCMToUserEntity(UserConnectionModel userConnectionModel);
    UserConnectionModel mapToUserConnexionModel(UserEntity userEntity);


    // Méthode de mise à jour (cas particulier)
    //void updateUserEntityFromModel(UserModel userModel, @MappingTarget UserEntity userEntity);


    // Permet de cp les propriétés d'un obj UM ds un obj UE
    @Mapping(target = "password", ignore = true)     // ne mappe pas le password sécu
    void copy(UserModel userModel, @MappingTarget UserEntity userEntity);



}
