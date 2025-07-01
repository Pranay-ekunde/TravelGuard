package com.example.travelinsurance.model;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;

@Entity
@Data
@Where(clause = "is_active = true")
public class CoverageType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    
    // NEW FIELD for soft delete
    private boolean isActive = true; // Default to true for new records
}
