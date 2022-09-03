package com.poc.filecoin.enums;

import java.util.Arrays;

/**
 * Enum for Signature
 *
 * @author Vijay Pratap Singh
 */
public enum SignatureType {
    ECDSA(1),
    BLS(2);

    private int value;

    SignatureType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static SignatureType getEnum(int value) {
        return Arrays.stream(values()).filter(result -> result.value == value).findFirst().orElse(null);
    }
}

