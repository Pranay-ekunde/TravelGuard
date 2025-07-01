package com.example.travelinsurance.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List; // <-- Make sure this import is correct

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
    // ... other fields ...
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true)
    private String email;
    private String password;

    // ...
    @ManyToMany(fetch = FetchType.EAGER) // <-- CORRECTED (cascade removed)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles;
    // ...

    @OneToMany(mappedBy = "user")
    private List<QuoteRequest> quoteRequests;
}