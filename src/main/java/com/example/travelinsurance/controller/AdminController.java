package com.example.travelinsurance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.travelinsurance.model.CoverageType;
import com.example.travelinsurance.repository.CoverageTypeRepository;
import com.example.travelinsurance.services.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CoverageTypeRepository coverageTypeRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String userDashboard(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "admin/user-dashboard";
    }

    @GetMapping("/coverages")
    public String listCoverages(Model model) {
        model.addAttribute("coverages", coverageTypeRepository.findAllForAdmin());
        return "admin/coverages";
    }

    @GetMapping("/coverages/add")
    public String showAddForm(Model model) {
        model.addAttribute("coverage", new CoverageType());
        return "admin/coverage-form";
    }

    @PostMapping("/coverages/save")
    public String saveCoverage(@ModelAttribute CoverageType coverage) {
        coverageTypeRepository.save(coverage);
        return "redirect:/admin/coverages";
    }

    @GetMapping("/coverages/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        CoverageType coverage = coverageTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid coverage Id:" + id));
        model.addAttribute("coverage", coverage);
        return "admin/coverage-form";
    }

    @GetMapping("/coverages/toggle/{id}")
public String toggleCoverageStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    CoverageType coverage = coverageTypeRepository.findByIdForAdmin(id);

    if (coverage != null) {
        coverage.setActive(!coverage.isActive()); // Toggle the status
        coverageTypeRepository.save(coverage);
        String status = coverage.isActive() ? "restored" : "deactivated";
        redirectAttributes.addFlashAttribute("successMessage", "Coverage successfully " + status + ".");
    } else {
        redirectAttributes.addFlashAttribute("errorMessage", "Coverage not found.");
    }
    
    return "redirect:/admin/coverages";
}
}