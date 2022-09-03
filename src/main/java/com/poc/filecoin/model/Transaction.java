package com.poc.filecoin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

/**
 * Model for a transaction
 *
 * @author Vijay Pratap Singh
 */
@AllArgsConstructor
@Setter
@Getter
public class Transaction {

    private int version;
    private byte[] to;
    private byte[] from;
    private int nonce;
    private String value;
    private int gasLimit;
    private String gasFeeCap;
    private String gasPremium;
    private int method;
    private byte[] params;



}
