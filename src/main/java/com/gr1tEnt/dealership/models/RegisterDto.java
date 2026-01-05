package com.gr1tEnt.dealership.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RegisterDto {

    @NotEmpty(message = "Username is required")
    @Size(min = 3, max = 20)
    String username;

    @Email
    @NotEmpty(message = "Email is required")
    String email;

    @Size(min = 8)
    @NotEmpty(message = "Password is required")
    String password;

    @NotEmpty(message = "Password confirmation is required")
    String confirmPassword;

    @NotEmpty(message = "First name is required")
    String firstName;

    @NotEmpty(message = "Last name is required")
    String lastName;

}
