package com.bs.odontograma.auth.entity;

import com.bs.odontograma.shared.entity.BaseEntity;
import com.bs.odontograma.shared.enums.DentalSpecialty;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a system User.
 */
@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(columnNames = {"email", "tenant_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(length = 100)
    private String lastName;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false)
    private Boolean active;

    /**
     * Dental specialty of this user (only meaningful for practitioners). Nullable for
     * non-clinical roles such as receptionists or admins.
     */
    @Column(length = 40)
    @Enumerated(EnumType.STRING)
    @Setter
    private DentalSpecialty specialty;

    /**
     * Professional license number (matrícula profesional). Optional, free-form text so it
     * works across countries.
     */
    @Column(name = "license_number", length = 50)
    @Setter
    private String licenseNumber;

    // Business methods

    public void deactivate() {
        if (Boolean.FALSE.equals(this.active)) {
            throw new IllegalStateException("User is already inactive");
        }
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public void updatePassword(String newPasswordHash) {
        if (newPasswordHash == null || newPasswordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be blank");
        }
        this.passwordHash = newPasswordHash;
    }

    public void updateFirstName(String firstName) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be blank");
        }
        this.firstName = firstName.trim();
    }

    public void updateLastName(String lastName) {
        this.lastName = lastName != null ? lastName.trim() : null;
    }

    public void updateRole(UserRole role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        this.role = role;
    }

    public String getFullName() {
        if (this.lastName != null && !this.lastName.isBlank()) {
            return this.firstName + " " + this.lastName;
        }
        return this.firstName;
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(this.active);
    }
}
