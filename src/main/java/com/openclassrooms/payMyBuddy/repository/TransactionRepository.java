package com.openclassrooms.payMyBuddy.repository;

import com.openclassrooms.payMyBuddy.entity.TransactionEntity;
import com.openclassrooms.payMyBuddy.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

     // cf. transfert.html
     List<TransactionEntity> findBySenderAndReceiver(UserEntity emailSender, UserEntity emailReceiver);

     //List<TransactionEntity> findBySender(TransactionEntity sender);

      Page<TransactionEntity> findBySenderOrReceiver(UserEntity sender, UserEntity receiver, Pageable pageable);

}
