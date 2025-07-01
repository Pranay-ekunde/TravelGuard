package com.example.travelinsurance.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.travelinsurance.model.Role;
import com.example.travelinsurance.model.User;
import com.example.travelinsurance.repository.RoleRepository;
import com.example.travelinsurance.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User save(UserRegistrationDto registrationDto) {

        System.out.println("Registering user with name: " + registrationDto.getName());

        User user = new User();
        user.setName(registrationDto.getName());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            userRole = new Role("ROLE_USER");
            roleRepository.save(userRole);
        }
        user.setRoles(Collections.singletonList(userRole));
        return userRepository.save(user);
    }
    
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User updateUserDetails(String currentEmail, UserUpdateDto userUpdateDto) {
        User user = userRepository.findByEmail(currentEmail);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        if(userUpdateDto.getName() != null && !userUpdateDto.getName().isEmpty()){
            user.setName(userUpdateDto.getName());

        }else if (userUpdateDto.getEmail() != null && !userUpdateDto.getEmail().isEmpty()) {
            user.setEmail(userUpdateDto.getEmail());
        }else{
            throw new UsernameNotFoundException("No username or email found to update");
        }
        
        return userRepository.save(user);
    }

    public boolean updatePassword(String email, PasswordUpdateDto passwordUpdateDto) {
        User user = userRepository.findByEmail(email);
        
        // Check if the old password matches
        if (!passwordEncoder.matches(passwordUpdateDto.getOldPassword(), user.getPassword())) {
            return false; // Old password does not match
        }

        user.setPassword(passwordEncoder.encode(passwordUpdateDto.getNewPassword()));
        userRepository.save(user);
        return true;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}
