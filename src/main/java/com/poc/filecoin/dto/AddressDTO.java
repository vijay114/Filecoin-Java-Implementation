package com.poc.filecoin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AddressDTO {

    String privateKey;
    String publicKey;
    String type;
    String network;
    String address;

}
