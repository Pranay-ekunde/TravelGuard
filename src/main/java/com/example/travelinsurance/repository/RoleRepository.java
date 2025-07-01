package com.example.travelinsurance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.travelinsurance.model.Role;

@org.springframework.stereotype.Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    com.example.travelinsurance.model.Role findByName(String name);
}