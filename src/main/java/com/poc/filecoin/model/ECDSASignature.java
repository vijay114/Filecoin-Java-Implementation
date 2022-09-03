package com.poc.filecoin.model;

import com.poc.filecoin.enums.SignatureType;
import com.poc.filecoin.util.UtilityService;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.tuweni.bytes.Bytes32;
import org.apache.tuweni.crypto.SECP256K1;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.web3j.crypto.Hash;

import java.security.*;

/**
 * Class for ECDSA Signature
 *
 * @author Vijay Pratap Singh
 */
@Getter
@Slf4j
public class ECDSASignature extends Signature {

    @SneakyThrows
    public ECDSASignature(byte[] transaction, byte[] privateKey) {

        Security.addProvider(new BouncyCastleProvider());
        // Get message digest
        var generatedCid = UtilityService.getDigest(transaction);
        // Generate blake2b hash of generated Cid
        var blake2b256HashCid = Hash.blake2b256(generatedCid);
        log.info("Bytes of final hash before signing:");
        System.out.print("{");
        for (int i = 0; i < blake2b256HashCid.length; i++) {
            System.out.print(Byte.toUnsignedInt(blake2b256HashCid[i]) + ", ");
        }
        System.out.println("}");
        var secretKey = SECP256K1.SecretKey.fromBytes(Bytes32.wrap(privateKey));
        var publicKey = SECP256K1.PublicKey.fromSecretKey(secretKey);
        var keyPair = SECP256K1.KeyPair.create(secretKey, publicKey);
        var secp256k1Signature = SECP256K1.signHashed(blake2b256HashCid, keyPair);
        var signature = secp256k1Signature.bytes().toArray();

        log.info("Decoded private key string: {}", Hex.encodeHexString(secretKey.bytesArray()));
        System.out.print("Private Key: {");
        for (int i = 0; i < privateKey.length; i++) {
            System.out.print(Byte.toUnsignedInt(privateKey[i]) + ", ");
        }
        System.out.println("}");

        System.out.print("ECDSA Signature: [");
        for (int i = 0; i < signature.length; i++) {
            System.out.print(Byte.toUnsignedInt(signature[i]) + " ");
        }
        System.out.println("]");

        this.signatureBytes = signature;
        this.signatureType = SignatureType.ECDSA;
    }

}
