package com.bs.odontograma.tenant.entity;

import lombok.Getter;

/**
 * Available subscription plans.
 */
@Getter
public enum SubscriptionPlan {
    BASIC(5),
    PROFESSIONAL(10),
    ENTERPRISE(20),
    SUPER(999);

    private final int maxUsers;

    SubscriptionPlan(int maxUsers) {
        this.maxUsers = maxUsers;
    }
}