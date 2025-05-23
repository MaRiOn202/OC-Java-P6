package com.openclassrooms.payMyBuddy.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "password")
public class UserModel {


    private Long id;
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    private String username;
    @NotBlank(message = "L'email ne peut pas être vide")
    @Email(message = "L'email doit être valide")
    private String email;
    @JsonIgnore
    @Size(min=4, message = "Le mot de passe doit contenir au moins 4 caractères")
    private String password;
    @DecimalMin(value = "0.00", inclusive = true, message = "Le solde ne peut pas être négatif")
    @Digits(integer = 10, fraction = 2, message = "Le solde doit avoir au maximum 2 décimales")
    private BigDecimal sold;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserModel userModel = (UserModel) o;
        return Objects.equals(id, userModel.id) && Objects.equals(username, userModel.username) && Objects.equals(email, userModel.email) && Objects.equals(password, userModel.password) && Objects.equals(sold, userModel.sold);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, password, sold);
    }
}
