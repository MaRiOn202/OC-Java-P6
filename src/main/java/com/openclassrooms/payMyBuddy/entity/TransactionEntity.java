package com.openclassrooms.payMyBuddy.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


//@Data
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table (name = "Transactions")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JsonManagedReference
    private UserEntity sender;

    @ManyToOne
    @JsonManagedReference
    private UserEntity receiver;

    @Column
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(precision = 5, scale = 2)
    private BigDecimal percentage;

    @Column(nullable = false, updatable = false)
    private LocalDateTime localDateTime = LocalDateTime.now();

    // persistence de Entity en bdd pas à l'instanciation
    @PrePersist
    protected void onCreate() {
        this.localDateTime = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "TransactionEntity{" +
                "id=" + id +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", percentage=" + percentage +
                ", localDateTime=" + localDateTime +
                '}';
    }
}
