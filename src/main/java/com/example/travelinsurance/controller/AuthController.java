package com.example.travelinsurance.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.travelinsurance.model.User;
import com.example.travelinsurance.services.UserRegistrationDto;
import com.example.travelinsurance.services.UserService;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUserAccount(@ModelAttribute("user") UserRegistrationDto registrationDto) {
        System.out.println("DTO received with name: " + registrationDto.getName());
        User existing = userService.findUserByEmail(registrationDto.getEmail());
        if (existing != null) {
            return "redirect:/register?error";
        }
        userService.save(registrationDto);
        return "redirect:/auth/login";
    }
}