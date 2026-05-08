package com.bs.odontograma.tenant.dto;

import com.bs.odontograma.tenant.entity.SubscriptionPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantResponse {
    private UUID id;
    private String name;
    private String legalName;
    private String taxId;
    private String address;
    private String email;
    private String phone;
    private String logoUrl;
    private String publicLogoUrl;
    private String ivaCondition;
    private String iibb;
    private LocalDate activityStartDate;
    private SubscriptionPlan subscriptionPlan;
    private Integer maxUsers;
    private Boolean active;
    private LocalDateTime planExpiration;
    private Boolean planExpired;
    private LocalDateTime createdAt;
}