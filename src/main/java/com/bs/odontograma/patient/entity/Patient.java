package com.bs.odontograma.patient.entity;

import com.bs.odontograma.patient.enums.Gender;
import com.bs.odontograma.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Patient entity. Represents a dental clinic patient.
 *
 * Business rules:
 * - documentNumber is unique per tenant (a clinic does not register the same DNI twice)
 * - Soft delete via {@code active} flag, never hard delete
 * - When created the patient does not yet have an odontogram; it is created on first visit
 */
@Entity
@Table(
        name = "patients",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "document_number"}),
        indexes = {
                @Index(name = "idx_patients_tenant", columnList = "tenant_id"),
                @Index(name = "idx_patients_document", columnList = "document_number"),
                @Index(name = "idx_patients_last_name", columnList = "last_name")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Patient extends BaseEntity {

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "document_number", length = 30)
    private String documentNumber;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(length = 30)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(length = 200)
    private String address;

    @Column(name = "health_insurance", length = 100)
    private String healthInsurance;

    @Column(name = "insurance_number", length = 50)
    private String insuranceNumber;

    @Column(name = "medical_notes", columnDefinition = "TEXT")
    private String medicalNotes;

    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    public void deactivate() {
        if (Boolean.FALSE.equals(this.active)) {
            throw new IllegalStateException("Patient is already inactive");
        }
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }
}
