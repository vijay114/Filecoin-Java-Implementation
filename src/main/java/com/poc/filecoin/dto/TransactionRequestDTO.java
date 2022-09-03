package com.poc.filecoin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionRequestDTO {
    String privateKey;
    String fromAddress;
    String toAddress;
    String message;
    String value;
}
