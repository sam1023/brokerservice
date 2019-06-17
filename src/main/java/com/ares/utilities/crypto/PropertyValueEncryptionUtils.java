package com.ares.utilities.crypto;

public final class PropertyValueEncryptionUtils {

    private static final String ENCRYPTED_VALUE_PREFIX = "crypto:";

    public static boolean isEncryptedValue(final String value) {
        if (value == null) {
            return false;
        }
        final String trimmedValue = value.trim();
        return (trimmedValue.startsWith(ENCRYPTED_VALUE_PREFIX)
                && trimmedValue.length() > ENCRYPTED_VALUE_PREFIX.length());
    }

    private static String getInnerEncryptedValue(final String value) {
        return value.substring(ENCRYPTED_VALUE_PREFIX.length(), value.length());
    }

    public static String decrypt(final String encodedValue) {
        return TinyEncryption.decrypt(getInnerEncryptedValue(encodedValue.trim()));
    }

    public static String encrypt(final String decodedValue) {
        return ENCRYPTED_VALUE_PREFIX + TinyEncryption.encrypt(decodedValue);
    }

    private PropertyValueEncryptionUtils() {
        super();
    }

}