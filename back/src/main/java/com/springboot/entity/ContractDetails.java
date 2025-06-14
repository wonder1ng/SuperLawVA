package com.springboot.entity;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "contract_details")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDate contractDate;
    private LocalDate leaseStartDate;
    private LocalDate leaseEndDate;
    private Integer depositAmount;
    private Integer monthlyRent;
    private Integer maintenanceFee;
    private Integer rentPaymentDate;
    private LocalDate moveInDate;
    private String paymentMethod;
}