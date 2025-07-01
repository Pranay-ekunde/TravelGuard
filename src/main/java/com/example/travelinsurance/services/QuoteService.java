package com.example.travelinsurance.services;

import org.springframework.stereotype.Service;

import com.example.travelinsurance.model.CoverageType;
import com.example.travelinsurance.model.QuoteRequest;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

@Service
public class QuoteService {

    public BigDecimal calculatePremium(QuoteRequest quoteRequest) {
        BigDecimal premium = BigDecimal.ZERO;

        // Base premium factor for trip duration
        long tripDays = ChronoUnit.DAYS.between(quoteRequest.getStartDate(), quoteRequest.getEndDate());
        if (tripDays <= 0) tripDays = 1;
        premium = premium.add(BigDecimal.valueOf(tripDays * 2.5)); // $2.5 per day

        // Age factor
        if (quoteRequest.getTravelerAge() > 40) {
            premium = premium.add(BigDecimal.valueOf((quoteRequest.getTravelerAge() - 40) * 0.5)); // $0.5 for each year over 40
        }
        
        // Multi-trip factor
        if ("MULTI".equalsIgnoreCase(quoteRequest.getTripType())) {
            premium = premium.multiply(BigDecimal.valueOf(1.5)); // 50% more for multi-trip
        }

        // Add base prices of selected coverages
        for (CoverageType coverage : quoteRequest.getSelectedCoverages()) {
            premium = premium.add(coverage.getBasePrice());
        }

        return premium.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}