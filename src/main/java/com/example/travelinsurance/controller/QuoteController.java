package com.example.travelinsurance.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.travelinsurance.model.QuoteRequest;
import com.example.travelinsurance.model.User;
import com.example.travelinsurance.repository.CoverageTypeRepository;
import com.example.travelinsurance.repository.QuoteRequestRepository;
import com.example.travelinsurance.repository.UserRepository;
import com.example.travelinsurance.services.QuoteService;

import java.math.BigDecimal;

@Controller
@RequestMapping("/quote")
public class QuoteController {

    @Autowired
    private CoverageTypeRepository coverageTypeRepository;
    @Autowired
    private QuoteRequestRepository quoteRequestRepository;
    @Autowired
    private QuoteService quoteService;
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/new")
    public String showQuoteForm(Model model) {
        model.addAttribute("quoteRequest", new QuoteRequest());
        model.addAttribute("allCoverages", coverageTypeRepository.findAll());
        return "quote/form";
    }

    @PostMapping("/summary")
    public String getQuoteSummary(@ModelAttribute QuoteRequest quoteRequest, Model model, Authentication authentication) {

        User currentUser = userRepository.findByEmail(authentication.getName());
        
        quoteRequest.setUserEmail(currentUser.getEmail());
        quoteRequest.setUserName(currentUser.getName());
        
        BigDecimal premium = quoteService.calculatePremium(quoteRequest);
        quoteRequest.setTotalPremium(premium);
        
        model.addAttribute("quoteRequest", quoteRequest);
        return "quote/summary";
    }

    @PostMapping("/purchase")
    public String purchaseQuote(@ModelAttribute QuoteRequest quoteRequest, Model model) {
        // Recalculate on server side to be safe
        BigDecimal premium = quoteService.calculatePremium(quoteRequest);
        quoteRequest.setTotalPremium(premium);

        // Associate with logged-in user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName());
        quoteRequest.setUser(user);

        QuoteRequest savedQuote = quoteRequestRepository.save(quoteRequest);
        
        model.addAttribute("quoteId", savedQuote.getId());
        return "quote/confirmation";
    }
}