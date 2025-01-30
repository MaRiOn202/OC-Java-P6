package com.openclassrooms.payMyBuddy.repository;

import com.openclassrooms.payMyBuddy.entity.TransactionEntity;
import com.openclassrooms.payMyBuddy.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

     // cf. transfert.html
     List<TransactionEntity> findBySenderAndReceiver(UserEntity emailSender, UserEntity emailReceiver);

     //List<TransactionEntity> findBySender(TransactionEntity sender);

  

}
