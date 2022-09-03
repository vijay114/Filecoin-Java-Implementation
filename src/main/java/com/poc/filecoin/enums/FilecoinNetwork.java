package com.poc.filecoin.enums;

/**
 * Enum of filecoin network
 *
 * @author Vijay Pratap Singh
 */
public enum FilecoinNetwork {
    MAIN_NET("f"),
    DEV_NET("t");

    private String value;

    FilecoinNetwork(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static FilecoinNetwork getEnum(String value) {
        if(value.equals("f")) return MAIN_NET;
        else if(value.equals("t")) return  DEV_NET;
        else return DEV_NET;
    }

}
