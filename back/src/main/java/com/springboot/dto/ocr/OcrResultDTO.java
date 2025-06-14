package com.springboot.dto.ocr;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

// OCR 결과 DTO
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OcrResultDTO {
    private Long documentId;
    private String ocrStatus;
    private Double ocrConfidence;
    private String completedAt;
    private Integer processingTimeMs;
    
    private OcrTextResult ocrResult;
    private ParsedContract parsedContract;
    private List<String> parsingErrors;
    
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OcrTextResult {
        private String rawText;
        private Double confidence;
    }
    
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ParsedContract {
        private String contractType;  // JEONSE, MONTHLY
        private PropertyInfo propertyInfo;
        private ContractDetails contractDetails;
        private ContractArticles contractArticles;
        private SpecialTerms specialTerms;
        private ContractSummary contractSummary;
    }
    
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PropertyInfo {
        @JsonProperty("property_address")
        private String propertyAddress;
        
        @JsonProperty("detailed_address")
        private String detailedAddress;
        
        @JsonProperty("area_sqm")
        private Double areaSqm;
        
        @JsonProperty("area_pyeong")
        private Double areaPyeong;
        
        private String floor;
        
        @JsonProperty("building_type")
        private String buildingType;
        
        @JsonProperty("building_structure")
        private String buildingStructure;
        
        @JsonProperty("land_classification")
        private String landClassification;
        
        @JsonProperty("ownership_type")
        private String ownershipType;
    }
    
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ContractDetails {
        @JsonProperty("contract_type")
        private String contractType;
        
        @JsonProperty("contract_date")
        private LocalDate contractDate;
        
        @JsonProperty("lease_start_date")
        private LocalDate leaseStartDate;
        
        @JsonProperty("lease_end_date")
        private LocalDate leaseEndDate;
        
        @JsonProperty("lease_period_months")
        private Integer leasePeriodMonths;
        
        @JsonProperty("deposit_amount")
        private Integer depositAmount;
        
        @JsonProperty("monthly_rent")
        private Integer monthlyRent;
        
        @JsonProperty("maintenance_fee")
        private Integer maintenanceFee;
        
        @JsonProperty("move_in_date")
        private LocalDate moveInDate;
        
        @JsonProperty("payment_date")
        private Integer paymentDate;  // 월세 납부일
        
        @JsonProperty("payment_method")
        private String paymentMethod;
        
        @JsonProperty("early_termination")
        private String earlyTermination;
    }
    
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ContractArticles {
        @JsonProperty("article_1")
        private String article1;
        
        @JsonProperty("article_2")
        private String article2;
        
        @JsonProperty("article_3")
        private String article3;
        
        @JsonProperty("article_4")
        private String article4;
        
        @JsonProperty("article_5")
        private String article5;
        
        @JsonProperty("article_6")
        private String article6;
        
        @JsonProperty("article_7")
        private String article7;
    }
    
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SpecialTerms {
        private List<SpecialTerm> defaultTerms;
        private List<SpecialTerm> additionalTerms;
        
        @Getter @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class SpecialTerm {
            private String termId;
            private String content;
        }
    }
    
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ContractSummary {
        @JsonProperty("contract_period")
        private String contractPeriod;
        
        private String deposit;
        
        @JsonProperty("maintenance_fee_display")
        private String maintenanceFeeDisplay;
        
        @JsonProperty("renewal_deadline")
        private String renewalDeadline;
        
        private String address;
        
        @JsonProperty("detailed_address_summary")
        private String detailedAddressSummary;
    }
}