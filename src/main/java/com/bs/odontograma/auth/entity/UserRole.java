package com.bs.odontograma.auth.entity;

/**
 * User roles in the system.
 * Role hierarchy (cascading):
 * - SUPERADMIN: Full system access + tenant creation
 * - ADMIN: Full access except tenant creation
 * - MANAGER: Same as ADMIN except user registration and tenant modification
 * - ADMINISTRATION: Read-only on all endpoints (except tenant and users)
 * - RECEPTIONIST: Full access to customers, vehicles, bookings, services, intake forms. Read-only on work orders and estimates
 * - PRODUCTION: Read-only on work orders (production view)
 * - SALES: Access to customer and vehicle information
 */
public enum UserRole {
    SUPERADMIN,
    ADMIN,
    MANAGER,
}
