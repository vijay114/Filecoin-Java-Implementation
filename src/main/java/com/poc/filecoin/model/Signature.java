package com.poc.filecoin.model;

import com.poc.filecoin.enums.SignatureType;
import lombok.Getter;

/**
 * Abstract class for signature model
 *
 * @author Vijay Pratap Singh
 */
@Getter
public abstract class Signature {

    byte[] signatureBytes;
    SignatureType signatureType;

}
