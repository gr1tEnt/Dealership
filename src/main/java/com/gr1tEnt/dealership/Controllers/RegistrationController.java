package com.gr1tEnt.dealership.Controllers;

import com.gr1tEnt.dealership.models.RegisterDto;
import com.gr1tEnt.dealership.models.User;
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

    @GetMapping("/add")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "Register";
    }
}
