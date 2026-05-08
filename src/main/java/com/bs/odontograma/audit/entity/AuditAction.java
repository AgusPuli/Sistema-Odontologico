package com.bs.odontograma.audit.entity;

/**
 * Enum representing the type of action performed on an entity.
 */
public enum AuditAction {
    /**
     * Entity was created.
     */
    CREATE,

    /**
     * Entity was updated.
     */
    UPDATE,

    /**
     * Entity was deleted.
     */
    DELETE
}
