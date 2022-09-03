package com.poc.filecoin.exceptions;

public class InvalidFilecoinAddressException extends Exception{

    public InvalidFilecoinAddressException(String errorMessage) {
        super(errorMessage);
    }

}
