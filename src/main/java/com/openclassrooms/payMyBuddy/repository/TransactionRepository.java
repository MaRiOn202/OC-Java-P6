package com.openclassrooms.payMyBuddy.repository;

import com.openclassrooms.payMyBuddy.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

     //List<TransactionEntity> findByUsername(String username);

     

    
}
