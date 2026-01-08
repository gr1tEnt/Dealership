package com.gr1tEnt.dealership.Controllers;

import com.gr1tEnt.dealership.models.RegisterDto;
import com.gr1tEnt.dealership.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "Register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registerDto") RegisterDto registerDto, BindingResult result) {
        if (result.hasErrors() && !registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            return "Register";
        }

        try {
            userService.registerUser(registerDto);
        } catch (Exception e) {
            System.err.println("Error register user: " + e.getMessage());
            result.addError(new ObjectError("globalError", "Something went wrong: " + e.getMessage()));
            return "Register";
        }
        return "redirect:/cars";
    }
}
