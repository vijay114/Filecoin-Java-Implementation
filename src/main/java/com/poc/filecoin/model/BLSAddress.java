package com.poc.filecoin.model;

import com.poc.filecoin.enums.FilecoinNetwork;
import com.poc.filecoin.enums.FilecoinProtocol;

import com.poc.filecoin.exceptions.InvalidFilecoinException;
import lombok.SneakyThrows;
import org.apache.tuweni.bytes.Bytes32;
import tech.pegasys.teku.bls.BLSKeyPair;
import tech.pegasys.teku.bls.BLSSecretKey;

import java.security.SecureRandom;

/**
 * Class to create BLS Address
 *
 * @author Vijay Pratap Singh
 */
/*
    Protocol 3 addresses represent BLS public encryption keys. The payload field contains the BLS public key.
    
    |------------|----------|---------------------|----------|
    |  network   | protocol |      payload        | checksum |
    |------------|----------|---------------------|----------|
    | 'f' or 't' |    '3'   |  48 byte BLS PubKey |  4 bytes |
                      base32[................................]
*/
public class BLSAddress extends Address {

    public BLSAddress() {
    }

    @SneakyThrows
    public BLSAddress(byte[] privateKey, FilecoinNetwork filecoinNetwork) {

        var secretKey = Bytes32.wrap(privateKey);
        BLSSecretKey blsSecretKey = BLSSecretKey.fromBytes(secretKey);
        var publicKey = blsSecretKey.toPublicKey().toSSZBytes().toArray();
        createAddress(filecoinNetwork, blsSecretKey.toBytes(), publicKey);

    }

    @SneakyThrows
    public BLSAddress(FilecoinNetwork filecoinNetwork) {

        BLSKeyPair blsKeyPair = BLSKeyPair.random(new SecureRandom());
        var privateKey = blsKeyPair.getSecretKey().toBytes();
        var publicKey = blsKeyPair.getPublicKey().toSSZBytes().toArray();

        blsKeyPair.getSecretKey().toPublicKey().toString();
        createAddress(filecoinNetwork, privateKey, publicKey);

    }

    @SneakyThrows
    private void createAddress(FilecoinNetwork filecoinNetwork, Bytes32 privateKey, byte[] publicKey) {

        if(publicKey.length != 48) {
            throw new InvalidFilecoinException("Invalid public key length " + publicKey.length);
        }
        // set filecoin address network and protocol
        this.setFilecoinNetwork(filecoinNetwork);
        this.setFilecoinProtocol(FilecoinProtocol.BLS);
        // set base64 encoded private and public values in address model
        this.setPublicKey(publicKey);
        this.setPrivateKey(privateKey.toArray());
        // assign the value to the address model
        this.setPayload(publicKey);
        var publicKeyWithProtocol = new byte[publicKey.length + 1];
        publicKeyWithProtocol[0] = (byte) FilecoinProtocol.BLS.getValue();
        System.arraycopy(publicKey, 0, publicKeyWithProtocol, 1, publicKey.length);
        this.setAddress(publicKeyWithProtocol);

    }
}
