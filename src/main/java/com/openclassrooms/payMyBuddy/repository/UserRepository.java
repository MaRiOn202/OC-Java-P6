package com.openclassrooms.payMyBuddy.repository;

import com.openclassrooms.payMyBuddy.entity.UserEntity;
import com.openclassrooms.payMyBuddy.exception.UserNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {


    UserEntity findByEmail(String email);

    UserEntity findByUsername(String userName);

    Optional<UserEntity> findById(Long id);

    void deleteById(Long id);
}
