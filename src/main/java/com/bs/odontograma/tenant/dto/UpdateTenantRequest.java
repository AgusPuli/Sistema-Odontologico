package com.bs.odontograma.tenant.dto;

import jakarta.validation.constraints.Email;
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
public class UpdateTenantRequest {

    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @Size(max = 200, message = "Legal name cannot exceed 200 characters")
    private String legalName;

    @Email(message = "Invalid email")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @Size(max = 200, message = "Address cannot exceed 200 characters")
    private String address;

    @Size(max = 20, message = "Phone cannot exceed 20 characters")
    private String phone;

    @Size(max = 100, message = "IVA condition cannot exceed 100 characters")
    private String ivaCondition;

    @Size(max = 50, message = "IIBB cannot exceed 50 characters")
    private String iibb;

    private LocalDate activityStartDate;
}