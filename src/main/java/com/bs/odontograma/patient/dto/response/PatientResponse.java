package com.bs.odontograma.patient.dto.response;

import com.bs.odontograma.patient.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponse {
    private UUID id;
    private UUID tenantId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String documentNumber;
    private LocalDate birthDate;
    private Gender gender;
    private String phone;
    private String email;
    private String address;
    private String healthInsurance;
    private String insuranceNumber;
    private String medicalNotes;
    private String allergies;
    private Boolean active;
    private LocalDateTime createdAt;
}
