package com.superlawva.domain.ocr.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rental_contracts")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalContract {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "ocr_job_id")
    private OcrJob ocrJob;
    
    // 계약 타입
    @Enumerated(EnumType.STRING)
    private ContractType contractType;
    
    // 부동산 정보 (OneToOne 관계)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "property_info_id")
    private PropertyInfo propertyInfo;
    
    // 계약 내용 (OneToOne 관계)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contract_details_id")
    private ContractDetails contractDetails;
    
    // 특약사항 (JSON으로 저장)
    @Column(columnDefinition = "TEXT")
    private String specialTermsJson;
    
    // 당사자 정보 (JSON으로 저장)
    @Column(columnDefinition = "TEXT")
    private String partyInfoJson;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    public enum ContractType {
        JEONSE, MONTHLY
    }
}





