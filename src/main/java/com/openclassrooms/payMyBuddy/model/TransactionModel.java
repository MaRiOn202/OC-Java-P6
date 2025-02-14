package com.openclassrooms.payMyBuddy.model;

import com.openclassrooms.payMyBuddy.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

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

    @NotNull(message = "Le montant ne peut pas être nul")
    @Min(value=1, message = "Le montant doit être supérieur à 0")
    private Double amount;
    private Double percentage;

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
