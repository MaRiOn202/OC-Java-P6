package com.openclassrooms.payMyBuddy.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0", insertable = false)
    private Double sold = 0.0;

    @ManyToMany
    @JoinTable(
            name = "users_connections",
            joinColumns = @JoinColumn(name = "user_entity_id"),
            inverseJoinColumns = @JoinColumn(name = "connections_id")
    )
    @JsonManagedReference
    private List<UserEntity> connections;

    @OneToMany(fetch = FetchType.EAGER)
    @JsonBackReference
    private List<TransactionEntity> transactions;

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", sold=" + sold +
                ", connections=" + connections +
                ", transactions=" + transactions +
                '}';
    }
}
