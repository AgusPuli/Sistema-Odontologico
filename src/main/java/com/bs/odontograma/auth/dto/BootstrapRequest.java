package com.bs.odontograma.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Bootstrap payload: creates the first tenant + SUPERADMIN user in one shot.
 * Only succeeds when the database has zero tenants — otherwise returns 409.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BootstrapRequest {

    @NotBlank(message = "Clinic name is required")
    @Size(max = 100)
    private String clinicName;

    @NotBlank(message = "Admin email is required")
    @Email
    private String adminEmail;

    @NotBlank(message = "Admin password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String adminPassword;

    @NotBlank(message = "Admin first name is required")
    @Size(max = 100)
    private String adminFirstName;

    @Size(max = 100)
    private String adminLastName;
}
