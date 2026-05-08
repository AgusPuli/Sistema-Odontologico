package com.bs.odontograma.tenant.dto;

import com.bs.odontograma.tenant.entity.SubscriptionPlan;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTenantRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @Size(max = 200, message = "Legal name cannot exceed 200 characters")
    private String legalName;

    @Size(max = 20, message = "Tax ID cannot exceed 20 characters")
    private String taxId;

    @Size(max = 200, message = "Address cannot exceed 200 characters")
    private String address;

    @Email(message = "Invalid email")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @Size(max = 20, message = "Phone cannot exceed 20 characters")
    private String phone;

    @Size(max = 100, message = "IVA condition cannot exceed 100 characters")
    private String ivaCondition;

    @Size(max = 50, message = "IIBB cannot exceed 50 characters")
    private String iibb;

    private LocalDate activityStartDate;

    @NotNull(message = "Subscription plan is required")
    private SubscriptionPlan subscriptionPlan;
}