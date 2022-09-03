package com.poc.filecoin.util;


import com.poc.filecoin.model.Transaction;
import io.ipfs.cid.Cid;
import io.ipfs.multibase.Multibase;
import io.ipfs.multihash.Multihash;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.web3j.crypto.Hash;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Base64;

/**
 * Utility Service for various common methods
 *
 * @author Vijay Pratap Singh
 */
@Service
@Slf4j
public class UtilityService {

    private static byte MAJ_UNSIGNED_INT = 0;
    private static byte MAJ_BYTE_STRING = 2;
    private static byte MAJ_NEGATIVE_INT = 1;
    private static long BYTE_ARRAY_MAX_LENGTH = 2 << 20;

    /**
     * Method to get digest for a given message's byte array
     *
     * @param message
     * @return byte[]
     */
    public static byte[] getDigest(byte[] message) {

        // getting 256 byte blake 2b hash of given message
        var blake2b256HashMessage = Hash.blake2b256(message);
        // generating a cid with the above generated blake 2b hash
        Cid generatedCid = Cid.buildCidV1(Cid.Codec.DagCbor, Multihash.Type.blake2b_256, blake2b256HashMessage);
        // getting cid string by encoding generated cid with base32 for logging purpose only
        String cidString = Multibase.encode(Multibase.Base.Base32, generatedCid.toBytes());
        log.info("Encoded Base32 CID is: {}", cidString);
        // returning generated cid byte array
        return generatedCid.toBytes();

    }

    /**
     * Method to generate marchal CBOR of a transaction
     *
     * @param transaction
     * @return ByteArrayOutputStream
     */
    @SneakyThrows
    public static ByteArrayOutputStream marshalCBOR(Transaction transaction) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(138);

        // t.Version
        writeMajorTypeHeaderBuf(byteArrayOutputStream, MAJ_UNSIGNED_INT, transaction.getVersion());

        // t.To
        marshalCBORAddress(byteArrayOutputStream, transaction.getTo());

        // t.From
        marshalCBORAddress(byteArrayOutputStream, transaction.getFrom());

        // t.Nonce
        writeMajorTypeHeaderBuf(byteArrayOutputStream, MAJ_UNSIGNED_INT, transaction.getNonce());

        // t.Value
        marshalCBORBigInt(byteArrayOutputStream, new BigInteger(transaction.getValue()));

        // t.GasLimit
        if (transaction.getGasLimit() >= 0) {
            writeMajorTypeHeaderBuf(byteArrayOutputStream, MAJ_UNSIGNED_INT, transaction.getGasLimit());
        } else {
            writeMajorTypeHeaderBuf(byteArrayOutputStream, MAJ_NEGATIVE_INT, -(transaction.getGasLimit() - 1));
        }

        // t.GasFeeCap
        marshalCBORBigInt(byteArrayOutputStream, new BigInteger(transaction.getGasFeeCap()));


        // t.GasPremium (big.Int) (struct)
        marshalCBORBigInt(byteArrayOutputStream, new BigInteger(transaction.getGasPremium()));


        // t.Method (abi.MethodNum) (uint64)
        writeMajorTypeHeaderBuf(byteArrayOutputStream, MAJ_UNSIGNED_INT, transaction.getMethod());

        // t.Params ([]uint8) (slice)
        if (transaction.getParams().length > BYTE_ARRAY_MAX_LENGTH) {
            throw new Exception("Byte array in field transaction params was too long");
        }

        var finalParams = Base64.getDecoder().decode(transaction.getParams());

        writeMajorTypeHeaderBuf(byteArrayOutputStream, MAJ_BYTE_STRING, finalParams.length);
        byteArrayOutputStream.writeBytes(finalParams);
        byteArrayOutputStream.flush();
        return byteArrayOutputStream;
    }

    /**
     * Method to generate marshal CBOR of a big integer
     *
     * @param w
     * @param value
     */
    @SneakyThrows
    public static void marshalCBORBigInt(ByteArrayOutputStream w, BigInteger value) {
        byte[] bigIntegerByteArray = removeLeadingZeroFromByteArray(value.toByteArray());
        byte[] bigIntegerByteArrayModified = new byte[bigIntegerByteArray.length + 1];
        if (value.compareTo(BigInteger.ZERO) == 1) {
            bigIntegerByteArrayModified[0] = 0;
            System.arraycopy(bigIntegerByteArray, 0, bigIntegerByteArrayModified, 1, bigIntegerByteArray.length);
        } else if (value.compareTo(BigInteger.ZERO) == -1) {
            bigIntegerByteArrayModified[0] = 1;
            System.arraycopy(bigIntegerByteArray, 0, bigIntegerByteArrayModified, 1, bigIntegerByteArray.length);
        } else {
            bigIntegerByteArrayModified = new byte[]{};
        }

        writeMajorTypeHeaderBuf(w, MAJ_BYTE_STRING, bigIntegerByteArrayModified.length);
        w.write(bigIntegerByteArrayModified);
    }

    /**
     * Method to generate marshal CBOR of an address
     *
     * @param w
     * @param addressWithProtocol
     */
    @SneakyThrows
    public static void marshalCBORAddress(ByteArrayOutputStream w, byte[] addressWithProtocol) {
        writeMajorTypeHeaderBuf(w, MAJ_BYTE_STRING, addressWithProtocol.length);
        w.write(addressWithProtocol);
    }

    /**
     * Method to write unsigned major integers to bytes
     *
     * @param w
     * @param t
     * @param l
     */
    @SneakyThrows
    public static void writeMajorTypeHeaderBuf(ByteArrayOutputStream w, byte t, long l) {
        if (l < 24) {
            w.write((int) ((t << 5) | l));
        } else if (l < (256)) {
            w.write((t << 5) | 24);
            w.write((int) l);
        } else if (l < 65536) {
            w.write((t << 5) | 25);
            w.write(BigEndian.putUint16(l));
        } else if (l < 4294967296L) {
            w.write((t << 5) | 26);
            w.write(BigEndian.putUint32(l));

        } else {
            w.write((t << 5) | 27);
            w.write(BigEndian.putUint64(l));
        }
    }

    /**
     * Method to remobe leading 0 from a given byte array
     *
     * @param bytes
     * @return byte[]
     */
    private static byte[] removeLeadingZeroFromByteArray(byte[] bytes) {
        if (bytes.length <= 1) return bytes;

        int counter = 0;
        while (bytes[counter] == 0) {
            counter++;
        }

        return Arrays.copyOfRange(bytes, counter, bytes.length);
    }
}
