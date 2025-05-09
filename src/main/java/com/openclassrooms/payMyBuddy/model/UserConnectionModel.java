package com.openclassrooms.payMyBuddy.model;


import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserConnectionModel {
    
    //@NotBlank(message = "Le n")
    private List<UserModel> friends;        // amis déjà présents

    @Email(message = "L'email doit être valide")
    private String email;       // nouvel ami


}
