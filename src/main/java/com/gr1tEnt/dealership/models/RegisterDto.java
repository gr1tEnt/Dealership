package com.gr1tEnt.dealership.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterDto {

    @NotEmpty(message = "Username is required")
    @Size(min = 3, max = 20)
    private String username;

    @Email
    @NotEmpty(message = "Email is required")
    private String email;

    @Size(min = 8)
    @NotEmpty(message = "Password is required")
    private String password;

    @NotEmpty(message = "Password confirmation is required")
    private String confirmPassword;

    @NotEmpty(message = "First name is required")
    private String firstName;

    @NotEmpty(message = "Last name is required")
    private String lastName;

}
