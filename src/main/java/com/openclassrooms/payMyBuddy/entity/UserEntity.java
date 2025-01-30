package com.openclassrooms.payMyBuddy.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table (name = "Users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private Double sold;

    @OneToMany
    @JsonBackReference
    private List<UserEntity> connections;

    @OneToMany
    @JsonBackReference
    private List<TransactionEntity> transactions;


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", connections=" + connections +
                ", transactions=" + transactions +
                '}';
    }

}
