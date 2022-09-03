package com.poc.filecoin.model;

import com.poc.filecoin.enums.SignatureType;
import com.poc.filecoin.util.UtilityService;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.bytes.Bytes32;
import tech.pegasys.teku.bls.BLS;
import tech.pegasys.teku.bls.BLSSecretKey;
import tech.pegasys.teku.bls.impl.blst.BlstLoader;

import java.util.Optional;

/**
 * Class for BLS Signature
 *
 * @author Vijay Pratap Singh
 */
public class BLSSignature extends Signature {

    public BLSSignature(byte[] transaction, byte[] privateKey) {


        var secretKey = Bytes32.wrap(privateKey);
        var blsSecretKey = BLSSecretKey.fromBytes(secretKey);
        var publicKey = blsSecretKey.toPublicKey();

        var digestedTransaction = UtilityService.getDigest(transaction);

        var bls12381 = BlstLoader.INSTANCE.get();

        BLS bls = new BLS();
        bls.setBlsImplementation(bls12381);

        var blsSignature = BLS.sign(
                blsSecretKey, Bytes.wrap(digestedTransaction));

        this.signatureBytes = blsSignature.toSSZBytes().toArray();
        ;
        this.signatureType = SignatureType.BLS;

    }

}
