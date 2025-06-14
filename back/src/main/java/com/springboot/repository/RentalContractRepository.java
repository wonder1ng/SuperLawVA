package com.springboot.repository;

import com.springboot.entity.RentalContract;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RentalContractRepository extends JpaRepository<RentalContract, Long> {
    Optional<RentalContract> findByOcrJobId(Long ocrJobId);
}