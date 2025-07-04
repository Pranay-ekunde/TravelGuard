package com.example.travelinsurance;
import com.example.travelinsurance.controller.QuoteController;
import com.example.travelinsurance.model.QuoteRequest;
import com.example.travelinsurance.model.User;
import com.example.travelinsurance.repository.CoverageTypeRepository;
import com.example.travelinsurance.repository.QuoteRequestRepository;
import com.example.travelinsurance.repository.UserRepository;
import com.example.travelinsurance.services.QuoteService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
@SpringBootTest
class test {

    @InjectMocks
    private QuoteController quoteController;

    @Mock
    private CoverageTypeRepository coverageTypeRepository;
    @Mock
    private QuoteRequestRepository quoteRequestRepository;
    @Mock
    private QuoteService quoteService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private Model model;
    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShowQuoteForm_ReturnsFormView() {
        when(coverageTypeRepository.findAll()).thenReturn(Collections.emptyList());

        String viewName = quoteController.showQuoteForm(model);

        assertEquals("quote/form", viewName);
        verify(model).addAttribute(eq("quoteRequest"), any(QuoteRequest.class));
        verify(model).addAttribute(eq("allCoverages"), eq(Collections.emptyList()));
    }

    @Test
    void testGetQuoteSummary_ReturnsSummaryView() {
        QuoteRequest quoteRequest = new QuoteRequest();
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("John Doe");

        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(quoteService.calculatePremium(any(QuoteRequest.class))).thenReturn(new BigDecimal("100.00"));

        String viewName = quoteController.getQuoteSummary(quoteRequest, model, authentication);

        assertEquals("quote/summary", viewName);
        verify(model).addAttribute(eq("quoteRequest"), eq(quoteRequest));
        assertEquals("test@example.com", quoteRequest.getUserEmail());
        assertEquals("John Doe", quoteRequest.getUserName());
        assertEquals(new BigDecimal("100.00"), quoteRequest.getTotalPremium());
    }

    @Test
    void testPurchaseQuote_ReturnsConfirmationView() {
        QuoteRequest quoteRequest = new QuoteRequest();
        User user = new User();
        user.setEmail("test@example.com");

        when(quoteService.calculatePremium(any(QuoteRequest.class))).thenReturn(new BigDecimal("200.00"));
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        QuoteRequest savedQuote = new QuoteRequest();
        savedQuote.setId(42L);
        when(quoteRequestRepository.save(any(QuoteRequest.class))).thenReturn(savedQuote);

        try (MockedStatic<SecurityContextHolder> mockedContext = mockStatic(SecurityContextHolder.class)) {
            Authentication mockAuth = mock(Authentication.class);
            when(mockAuth.getName()).thenReturn("test@example.com");

            var securityContext = mock(org.springframework.security.core.context.SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(mockAuth);

            mockedContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            String viewName = quoteController.purchaseQuote(quoteRequest, model);

            assertEquals("quote/confirmation", viewName);
            verify(model).addAttribute("quoteId", 42L);
            assertEquals(user, quoteRequest.getUser());
            assertEquals(new BigDecimal("200.00"), quoteRequest.getTotalPremium());
        }
    }
}
