package com.superlawva.domain.ocr.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.superlawva.domain.ocr.entity.RentalContract;

public interface RentalContractRepository extends JpaRepository<RentalContract, Long> {
    Optional<RentalContract> findByOcrJobId(Long ocrJobId);
}