package com.poc.filecoin.controller;

import com.poc.filecoin.dto.AddressCreateRequestDTO;
import com.poc.filecoin.dto.AddressDTO;
import com.poc.filecoin.dto.AddressRequestDTO;
import com.poc.filecoin.model.Address;
import com.poc.filecoin.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for address methods
 *
 * @author Vijay Pratap Singh
 */
@RestController()
@RequestMapping("address")
@Slf4j
public class AddressController {

    @Autowired
    AddressService addressService;

    /**
     * API method to create new address
     *
     * @param addressCreateRequestDTO
     * @return ResponseEntity
     */
    @PostMapping(value = "new", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createAddress(@RequestBody AddressCreateRequestDTO addressCreateRequestDTO) {
        try {
            AddressDTO addressDTO = addressService.create(addressCreateRequestDTO.getAddressProtocol());
            return ResponseEntity.ok(addressDTO);
        } catch (Exception e) {
            log.error("Error while creating address: {}", e.getStackTrace());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /**
     * API method to get address
     *
     * @param addressRequestDTO
     * @return ResponseEntity
     */
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAddress(@RequestBody AddressRequestDTO addressRequestDTO) {
        try {
            Address address = addressService.getAddressFromPrivateKey(addressRequestDTO.getPrivateKey(),
                    addressRequestDTO.getAddressProtocol());
            AddressDTO addressDTO = new AddressDTO(Hex.encodeHexString(address.getPrivateKey())
                    , Hex.encodeHexString(address.getPublicKey())
                    , address.getFilecoinProtocol().toString()
                    , address.getFilecoinNetwork().toString()
                    , addressService.encode(address));
            return ResponseEntity.ok(addressDTO);
        } catch (Exception e) {
            log.error("Error while getting address by private key: {}", e.getStackTrace());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }


}
