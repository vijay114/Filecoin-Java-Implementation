package com.poc.filecoin.service;

import com.poc.filecoin.enums.FilecoinProtocol;
import com.poc.filecoin.model.BLSSignature;
import com.poc.filecoin.model.ECDSASignature;
import com.poc.filecoin.model.Signature;
import com.poc.filecoin.model.Transaction;
import com.poc.filecoin.util.UtilityService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

/**
 * Service class for the signatures
 *
 * @author Vijay Pratap Singh
 */
@Service
@Slf4j
public class SignatureService {

    /**
     * Method to sign a transaction for filecoin network
     *
     * @param privateKey
     * @param transaction
     * @param filecoinProtocol
     * @return Signature
     */
    @SneakyThrows
    public Signature signTransaction(String privateKey, Transaction transaction, FilecoinProtocol filecoinProtocol) {

        Signature signature;

        ByteArrayOutputStream byteArrayOutputStream = UtilityService.marshalCBOR(transaction);
        log.info("Byte array:");
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        System.out.print("[");
        for (int i = 0; i < byteArrayOutputStream.size(); i++) {
            System.out.print(Byte.toUnsignedInt(byteArray[i]) + " ");
        }
        System.out.println("]");


        var privateKeyBytes = privateKey.getBytes();
        log.info("Private Key Byte Length: {}", privateKeyBytes.length);

        if (filecoinProtocol.equals(FilecoinProtocol.SECP256K1)) {
            //  SECP256K1 Address
            signature = new ECDSASignature(byteArrayOutputStream.toByteArray(), Hex.decodeHex(privateKey));
        } else {
            //  BLS Address
            signature = new BLSSignature(byteArrayOutputStream.toByteArray(), Hex.decodeHex(privateKey));
        }
        log.info("Transaction signature data  {}", Base64.encodeBase64String(signature.getSignatureBytes()));
        return signature;
    }

}
