package com.openclassrooms.payMyBuddy.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;


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

    @Column
    private Double amount;

    @Column
    private Double percentage;

}
