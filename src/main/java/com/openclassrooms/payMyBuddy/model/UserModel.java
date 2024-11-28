package com.openclassrooms.payMyBuddy.model;


import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserModel {

    @NotEmpty
    private String username;
    @NotEmpty(message = "L'email ne peut pas être vide")
    private String email;
    @NotEmpty(message = "Le mot de passe ne peut pas être vide")
    private String password;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserModel userModel = (UserModel) o;
        return Objects.equals(username, userModel.username) && Objects.equals(email, userModel.email) && Objects.equals(password, userModel.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email, password);
    }
}
