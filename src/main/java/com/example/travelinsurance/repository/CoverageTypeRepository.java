package com.example.travelinsurance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.travelinsurance.model.CoverageType;

@org.springframework.stereotype.Repository
public interface CoverageTypeRepository extends JpaRepository<CoverageType, Long> {

    @Query("select c from CoverageType c")
    List<CoverageType> findAllForAdmin();

    @Query("select c from CoverageType c where c.id = :id")
    CoverageType findByIdForAdmin(Long id);
}
