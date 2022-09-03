package com.poc.filecoin.enums;

import java.util.Arrays;

/**
 * Enum for filecoin protocol
 *
 * @author Vijay Pratap Singh
 */
public enum FilecoinProtocol {
    ID(0),
    SECP256K1(1),
    ACTOR(2),
    BLS(3);

    private int value;

    FilecoinProtocol(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static FilecoinProtocol getEnum(int value) {
        return Arrays.stream(values()).filter(result -> result.value == value).findFirst().orElse(null);
    }
}
