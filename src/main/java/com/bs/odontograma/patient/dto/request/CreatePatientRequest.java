package com.bs.odontograma.patient.dto.request;

import com.bs.odontograma.patient.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class CreatePatientRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100)
    private String lastName;

    @Size(max = 30)
    private String documentNumber;

    private LocalDate birthDate;

    private Gender gender;

    @Size(max = 30)
    private String phone;

    @Email
    @Size(max = 100)
    private String email;

    @Size(max = 200)
    private String address;

    @Size(max = 100)
    private String healthInsurance;

    @Size(max = 50)
    private String insuranceNumber;

    private String medicalNotes;

    private String allergies;
}
