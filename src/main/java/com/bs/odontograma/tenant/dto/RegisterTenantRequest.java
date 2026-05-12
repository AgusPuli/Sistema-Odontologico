package com.bs.odontograma.tenant.dto;

import com.bs.odontograma.tenant.entity.SubscriptionPlan;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request body for the open /api/tenants/register endpoint.
 * Creates a new Tenant + its first ADMIN user in one shot and returns JWT tokens.
 */
@Data
public class RegisterTenantRequest {

    @NotBlank(message = "Clinic name is required")
    private String clinicName;

    @NotBlank(message = "Admin first name is required")
    private String adminFirstName;

    @NotBlank(message = "Admin last name is required")
    private String adminLastName;

    @NotBlank(message = "Admin email is required")
    @Email(message = "Admin email must be valid")
    private String adminEmail;

    @NotBlank(message = "Admin password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String adminPassword;

    @NotNull(message = "Subscription plan is required")
    private SubscriptionPlan plan;

    /** Optional — clinic's tax identifier (CUIT, RFC, etc.) */
    private String taxId;

    /** Optional — clinic phone number */
    private String phone;
}
