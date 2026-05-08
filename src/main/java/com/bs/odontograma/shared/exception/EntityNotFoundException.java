package com.bs.odontograma.shared.exception;

import java.util.UUID;

/**
 * Exception thrown when a requested entity is not found.
 */
public class EntityNotFoundException extends DomainException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String entityName, UUID id) {
        super(String.format("%s with ID %s not found", entityName, id));
    }

    public EntityNotFoundException(String entityName, String field, String value) {
        super(String.format("%s with %s '%s' not found", entityName, field, value));
    }
}
