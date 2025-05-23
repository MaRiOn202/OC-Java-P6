package com.openclassrooms.payMyBuddy.model;


import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransactionModel {

    private Long id;
//    @NotBlank(message = "Le nom de l'émetteur est obligatoire")
    private String sender;
//    @NotBlank(message = "Le nom du destinataire est obligatoire")
    private String receiver;
//    @NotBlank(message = "La description est obligatoire")
    private String description;

    private String senderName;
    //    @NotBlank(message = "Le nom du destinataire est obligatoire")
    private String receiverName;

    @NotNull(message = "Le montant ne peut pas être nul")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    @Digits(integer = 10, fraction = 2, message = "Le montant doit avoir au maximum 2 décimales")
    private BigDecimal amount;
    @Digits(integer = 3, fraction = 2, message = "Le pourcentage doit avoir au maximum 2 décimales")
    private BigDecimal percentage;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionModel that)) return false;
        return Objects.equals(getSender(), that.getSender()) && Objects.equals(getReceiver(), that.getReceiver()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getAmount(), that.getAmount()) && Objects.equals(getPercentage(), that.getPercentage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSender(), getReceiver(), getDescription(), getAmount(), getPercentage());
    }
}
