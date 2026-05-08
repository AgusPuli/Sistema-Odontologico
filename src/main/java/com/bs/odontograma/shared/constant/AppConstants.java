package com.bs.odontograma.shared.constant;

/**
 * Application-wide constants.
 */
public final class AppConstants {

    private AppConstants() {
        throw new IllegalStateException("Utility class");
    }

    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_SORT_FIELD = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "DESC";

    // String lengths
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_EMAIL_LENGTH = 100;
    public static final int MAX_PHONE_LENGTH = 20;
    public static final int MAX_ADDRESS_LENGTH = 200;
    public static final int MAX_DESCRIPTION_LENGTH = 500;
    public static final int MAX_NOTES_LENGTH = 2000;

    // File uploads
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/webp"};
    public static final String[] ALLOWED_DOCUMENT_TYPES = {"application/pdf"};

    // Business rules
    public static final int MIN_VEHICLE_YEAR = 1900;
    public static final int MIN_PASSWORD_LENGTH = 8;

    // Date formats
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static final String TIME_FORMAT = "HH:mm";

    // System
    public static final String SYSTEM_USER = "SYSTEM";
    public static final String ANONYMOUS_USER = "ANONYMOUS";
}