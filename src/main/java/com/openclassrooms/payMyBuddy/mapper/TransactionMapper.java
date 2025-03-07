package com.openclassrooms.payMyBuddy.mapper;


import com.openclassrooms.payMyBuddy.entity.TransactionEntity;
import com.openclassrooms.payMyBuddy.entity.UserEntity;
import com.openclassrooms.payMyBuddy.model.TransactionModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel="spring")
public interface TransactionMapper {

    @Mapping(ignore = true, target= "sender")         // a voir
    @Mapping(ignore = true, target= "receiver")
    TransactionEntity mapToTransactionEntity(TransactionModel transactionModel);

    @Mapping(source = "sender.email", target= "sender")                //    entree et sotie     //attribut
    @Mapping(source = "receiver.email", target= "receiver")
    @Mapping(source = "sender.username", target= "senderName")                //    entree et sotie     //attribut
    @Mapping(source = "receiver.username", target= "receiverName")
    TransactionModel mapToTransactionModel(TransactionEntity transactionEntity);

    void copy(TransactionModel transactionModel, @MappingTarget TransactionEntity transactionEntity);


    // Convertir String sender & receiver en un obj UserEntity 
    default UserEntity map(String email) {
        if (email == null) {
          return null;
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        return userEntity;
    }

    // Convertir un userE en un String
    default String map(UserEntity userEntity) {
        if (userEntity == null) {
           return null;
        }
        return userEntity.getEmail();
    }

}
