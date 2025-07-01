package com.example.travelinsurance.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
public class QuoteRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String destinationCountry;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)

    private LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)

    private LocalDate endDate;
    private int travelerAge;
    private String tripType;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "quote_coverages",
        joinColumns = @JoinColumn(name = "quote_id"),
        inverseJoinColumns = @JoinColumn(name = "coverage_id")
    )
    private Set<CoverageType> selectedCoverages;
    
    @Transient 
    private String userName;
    
    @Transient
    private String userEmail;

    private BigDecimal totalPremium;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}