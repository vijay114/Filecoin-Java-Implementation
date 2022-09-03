package com.poc.filecoin.dto;

import com.poc.filecoin.enums.FilecoinProtocol;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddressCreateRequestDTO {
    FilecoinProtocol addressProtocol;
}
