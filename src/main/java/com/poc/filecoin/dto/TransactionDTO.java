package com.poc.filecoin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class TransactionDTO {
    @JsonProperty("Version")
    int version;
    @JsonProperty("To")
    String to;
    @JsonProperty("From")
    String from;
    @JsonProperty("Nonce")
    int nonce;
    @JsonProperty("Value")
    String value;
    @JsonProperty("GasLimit")
    int gasLimit;
    @JsonProperty("GasFeeCap")
    String gasFeeCap;
    @JsonProperty("GasPremium")
    String gasPremium;
    @JsonProperty("Method")
    int method;
    @JsonProperty("Params")
    String params;
}
