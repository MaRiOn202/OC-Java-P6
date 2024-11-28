package com.openclassrooms.payMyBuddy.repository;

import com.openclassrooms.payMyBuddy.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {


    UserEntity findByEmail(String email);

    UserEntity findByUsername(String userName);


    void deleteById(Long id);
}
