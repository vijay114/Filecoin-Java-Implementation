package com.poc.filecoin.exceptions;

public class InvalidFilecoinPrivateKeyException extends Exception{

    public InvalidFilecoinPrivateKeyException(String errorMessage) {
        super(errorMessage);
    }

}
