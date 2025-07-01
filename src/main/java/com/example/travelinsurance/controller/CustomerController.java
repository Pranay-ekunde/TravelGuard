package com.example.travelinsurance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.travelinsurance.model.User;
import com.example.travelinsurance.services.PasswordUpdateDto;
import com.example.travelinsurance.services.UserService;
import com.example.travelinsurance.services.UserUpdateDto;

@Controller
@RequestMapping("/profile")
public class CustomerController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @GetMapping
    public String showProfile(Model model, Authentication authentication) {
        User user = userService.findUserByEmail(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("quotes", user.getQuoteRequests());
        model.addAttribute("userUpdateDto", new UserUpdateDto());
        model.addAttribute("passwordUpdateDto", new PasswordUpdateDto());
        return "customer/profile";
    }

    // ... inside CustomerController ...

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute("userUpdateDto") UserUpdateDto userUpdateDto,
            Authentication authentication, RedirectAttributes redirectAttributes) {
        String oldEmail = authentication.getName();

        User updatedUser = userService.updateUserDetails(oldEmail, userUpdateDto);
        UserDetails newUserDetails = userDetailsService.loadUserByUsername(updatedUser.getEmail());

        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                newUserDetails,
                null,
                newUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        redirectAttributes.addFlashAttribute("successMessage",
                "Profile updated successfully! Your session has been updated.");
        return "redirect:/profile";
    }

    @PostMapping("/update-password")
    public String updatePassword(@ModelAttribute("passwordUpdateDto") PasswordUpdateDto passwordUpdateDto,
            Authentication authentication, RedirectAttributes redirectAttributes) {
        boolean isPasswordChanged = userService.updatePassword(authentication.getName(), passwordUpdateDto);
        if (isPasswordChanged) {
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Incorrect old password. Please try again.");
        }
        return "redirect:/profile";
    }

}