package com.poc.filecoin.exceptions;

public class InvalidFilecoinPayloadException extends Exception{

    public InvalidFilecoinPayloadException(String errorMessage) {
        super(errorMessage);
    }

}
