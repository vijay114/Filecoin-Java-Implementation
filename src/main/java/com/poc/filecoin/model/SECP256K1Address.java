package com.poc.filecoin.model;

import com.poc.filecoin.enums.FilecoinNetwork;
import com.poc.filecoin.enums.FilecoinProtocol;
import com.poc.filecoin.exceptions.InvalidFilecoinException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.bytes.Bytes32;
import org.apache.tuweni.crypto.SECP256K1;
import org.bouncycastle.jcajce.provider.digest.Blake2b;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


import java.security.*;


/**
 * Class to create SECP256K1 Address
 *
 * @author Vijay Pratap Singh
 */
/*
    Protocol 1: libsecpk1 Elliptic Curve Public Keys
    Protocol 1 addresses represent secp256k1 public encryption keys.
    The payload field contains the Blake2b 160 hash of the uncompressed public key (65 bytes).

    String

    |------------|----------|--------------------------------|----------|
    |  network   | protocol |      payload                   | checksum |
    |------------|----------|--------------------------------|----------|
    | 'f' or 't' |    '1'   | blake2b-160(PubKey [65 bytes]) |  4 bytes |
                      base32[...........................................]
*/
@Slf4j
public class SECP256K1Address extends Address {

    public SECP256K1Address() {
    }

    @SneakyThrows
    public SECP256K1Address(byte[] privateKey, FilecoinNetwork filecoinNetwork) {


        Security.addProvider(new BouncyCastleProvider());
        var secretKey = SECP256K1.SecretKey.fromBytes(Bytes32.wrap(privateKey));
        var publicKey = SECP256K1.PublicKey.fromSecretKey(secretKey);

        createAddress(publicKey.bytesArray(), secretKey.bytesArray(), filecoinNetwork);
    }

    @SneakyThrows
    public SECP256K1Address(FilecoinNetwork filecoinNetwork) {

        Security.addProvider(new BouncyCastleProvider());
        var keyPair = SECP256K1.KeyPair.random();
        var privateKey = keyPair.secretKey().bytesArray();
        var publicKey = keyPair.publicKey().bytesArray();

        createAddress(publicKey, privateKey, filecoinNetwork);


    }

    /**
     * Method to create address
     *
     * @param publicKey
     * @param privateKey
     * @param filecoinNetwork
     */
    @SneakyThrows
    private void createAddress(byte[] publicKey, byte[] privateKey, FilecoinNetwork filecoinNetwork) {

        // getting uncompressed public key
        var secp256k1publicKey = SECP256K1.PublicKey.fromBytes(Bytes.wrap(publicKey));
        var x = secp256k1publicKey.asEcPoint().getXCoord().getEncoded();
        var y = secp256k1publicKey.asEcPoint().getYCoord().getEncoded();
        var uncompressedPublic = new byte[1 + x.length + y.length];
        uncompressedPublic[0] = 0x04;
        System.arraycopy(x, 0, uncompressedPublic, 1, x.length);
        System.arraycopy(y, 0, uncompressedPublic, x.length + 1, y.length);

        log.info("Uncompressed Public Key length: {}", uncompressedPublic.length);
        log.info("Uncompressed Public Key: {}", uncompressedPublic);


        // initialize blake2b160
        var blake2b = new Blake2b.Blake2b160();
        // digest encoded public key with protocol
        var blakeDigestedPublicKey = blake2b.digest(uncompressedPublic);

        if (blakeDigestedPublicKey.length != 20) {
            throw new InvalidFilecoinException("Invalid digested public key length " + blakeDigestedPublicKey.length);
        }

        var blakeDigestedPublicKeyWithProtocol = new byte[blakeDigestedPublicKey.length + 1];
        blakeDigestedPublicKeyWithProtocol[0] = (byte) FilecoinProtocol.SECP256K1.getValue();
        System.arraycopy(blakeDigestedPublicKey, 0, blakeDigestedPublicKeyWithProtocol, 1, blakeDigestedPublicKey.length);

        // set filecoin address network and protocol
        this.setFilecoinNetwork(filecoinNetwork);
        this.setFilecoinProtocol(FilecoinProtocol.SECP256K1);
        // set base64 encoded public key in address model
        this.setPublicKey(publicKey);
        // set base32 encoded private key in address model
        this.setPrivateKey(privateKey);
        // assign the value to the address model
        this.setPayload(blakeDigestedPublicKey);
        // set value with protocol
        this.setAddress(blakeDigestedPublicKeyWithProtocol);
    }
}
