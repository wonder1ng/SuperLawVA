package com.springboot.entity;
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
@Table(name = "property_info")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String address;
    private String detailedAddress;
    private Double areaSqm;
    private Double areaPyeong;
    private String floor;
    private String buildingType;
    private String buildingStructure;
    private String landClassification;
    private String ownershipType;
}