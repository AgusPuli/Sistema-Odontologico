package com.bs.odontograma.tenant.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Tenant entity representing a company using the system.
 * Does NOT extend BaseEntity because tenant doesn't have a tenantId.
 */
@Entity
@Table(name = "tenants")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Tenant {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "legal_name", length = 200)
    private String legalName;

    @Column(name = "tax_id", length = 20)
    private String taxId;

    @Column(length = 200)
    private String address;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "iva_condition", length = 100)
    @Setter
    private String ivaCondition;

    @Column(name = "iibb", length = 50)
    @Setter
    private String iibb;

    @Column(name = "activity_start_date")
    @Setter
    private java.time.LocalDate activityStartDate;

    @Column(name = "subscription_plan", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private SubscriptionPlan subscriptionPlan;

    @Column(name = "max_users", nullable = false)
    private Integer maxUsers;

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "plan_expiration")
    private LocalDateTime planExpiration;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    // Business methods
    public void deactivate() {
        if (Boolean.FALSE.equals(this.active)) {
            throw new IllegalStateException("Tenant is already deactivated");
        }
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public void updateInfo(String name, String legalName, String email) {
        this.name = name;
        this.legalName = legalName;
        this.email = email;
    }

    public void updateLogo(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public void removeLogo() {
        this.logoUrl = null;
    }

    public void updatePlan(SubscriptionPlan newPlan, LocalDateTime newExpiration) {
        this.subscriptionPlan = newPlan;
        this.maxUsers = newPlan.getMaxUsers();
        this.planExpiration = newExpiration;
    }

    public boolean canAddUser(int currentUserCount) {
        return currentUserCount < maxUsers;
    }

    public boolean isPlanExpired() {
        return planExpiration != null && planExpiration.isBefore(LocalDateTime.now());
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(this.active);
    }
}