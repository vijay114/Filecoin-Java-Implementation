package com.poc.filecoin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class TransactionResponseDTO {
    int type;
    String signature;
}
