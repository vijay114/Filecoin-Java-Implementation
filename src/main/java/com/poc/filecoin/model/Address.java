package com.poc.filecoin.model;


import com.poc.filecoin.enums.FilecoinNetwork;
import com.poc.filecoin.enums.FilecoinProtocol;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


/**
 * Model class for Filecoin address
 *
 * @author Vijay Pratap Singh
 */
@Getter
@Setter
@Slf4j
public abstract class Address {

    private byte[] payload;
    private byte[] address;
    private byte[] privateKey;
    private byte[] publicKey;
    private FilecoinProtocol filecoinProtocol;
    private FilecoinNetwork filecoinNetwork;

}
