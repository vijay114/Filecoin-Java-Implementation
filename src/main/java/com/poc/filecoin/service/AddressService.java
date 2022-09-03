package com.poc.filecoin.service;

import com.poc.filecoin.dto.AddressDTO;
import com.poc.filecoin.enums.FilecoinNetwork;
import com.poc.filecoin.enums.FilecoinProtocol;
import com.poc.filecoin.exceptions.InvalidFilecoinAddressException;
import com.poc.filecoin.exceptions.InvalidFilecoinNetworkException;
import com.poc.filecoin.exceptions.InvalidFilecoinProtocolException;
import com.poc.filecoin.model.Address;
import com.poc.filecoin.model.BLSAddress;
import com.poc.filecoin.model.SECP256K1Address;
import com.google.common.io.BaseEncoding;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Service class for address
 *
 * @author Vijay Pratap Singh
 */
@Service
@Slf4j
public class AddressService {

    private static int PAYLOAD_HASH_LENGTH = 20;
    private static int BLS_PUBLIC_KEY_BYTES = 48;

    @Autowired
    NetworkService networkService;

    /**
     * Method to create an address
     *
     * @param filecoinProtocol
     * @return AddressDTO
     * @throws InvalidFilecoinProtocolException
     */
    @SneakyThrows
    public AddressDTO create(FilecoinProtocol filecoinProtocol) {
        // get filecoin network from network service
        FilecoinNetwork filecoinNetwork = networkService.getFilecoinNetwork();
        // define address object
        Address address;
        // based on network protocol create the required address
        if (filecoinProtocol == FilecoinProtocol.SECP256K1) {
            address = new SECP256K1Address(filecoinNetwork);
        } else if (filecoinProtocol == FilecoinProtocol.BLS) {
            address = new BLSAddress(filecoinNetwork);
        } else {
            throw new InvalidFilecoinProtocolException("Invalid filecoin protocol");
        }
        // post mapping the address with DTO, return the dto
        return new AddressDTO(Hex.encodeHexString(address.getPrivateKey())
                , Hex.encodeHexString(address.getPublicKey())
                , address.getFilecoinProtocol().toString()
                , address.getFilecoinNetwork().toString()
                , this.encode(address));
    }

    /**
     * Method to encode address based on the network type
     *
     * @param address
     * @return encodedAddress
     * @throws InvalidFilecoinNetworkException
     * @throws InvalidFilecoinProtocolException
     * @throws NoSuchAlgorithmException
     */
    @SneakyThrows
    public String encode(Address address) {
        // validating the filecoin network of the addresses
        if (address.getFilecoinNetwork().getValue() != FilecoinNetwork.DEV_NET.getValue()
                && address.getFilecoinNetwork().getValue() != FilecoinNetwork.MAIN_NET.getValue()) {
            throw new InvalidFilecoinNetworkException("Network for encoding address should be" +
                    " either MAIN_NET or DEV_NET");
        }

        // appending values of filecoin network and protocol, so that filecoin address appear in the required format
        StringBuilder encodedAddress = new StringBuilder()
                .append(address.getFilecoinNetwork().getValue())
                .append(address.getFilecoinProtocol().getValue());

        // switch case for encoding filecoin address based on the protocol
        switch (address.getFilecoinProtocol()) {
            case SECP256K1:
            case BLS:
                var checksum = checksum(address.getPayload(), address.getFilecoinProtocol());
                byte[] valueWithChecksum = new byte[address.getPayload().length + checksum.length];
                System.arraycopy(address.getPayload(), 0, valueWithChecksum, 0, address.getPayload().length);
                System.arraycopy(checksum, 0, valueWithChecksum, address.getPayload().length, checksum.length);
                encodedAddress.append(BaseEncoding.base32().omitPadding().lowerCase().encode(valueWithChecksum));
                break;
            default:
                throw new InvalidFilecoinProtocolException("Invalid filecoin address protocol: "
                        + address.getFilecoinProtocol().getValue());
        }

        // returning encoded address
        return encodedAddress.toString();
    }

    /**
     * Method to decode an address from the given string
     *
     * @param encodedAddress
     * @return
     * @throws InvalidFilecoinAddressException
     * @throws NoSuchAlgorithmException
     */
    @SneakyThrows
    public Address decode(String encodedAddress) {

        // Declaring an instance of address that will have the decoded address
        Address address;

        // validating the length of the encoded address
        if (encodedAddress.length() < 3 || encodedAddress.length() > 86) {
            throw new InvalidFilecoinAddressException("Invalid address length for filecoin address");
        }

        // validating the network present in encoded address
        if (encodedAddress.charAt(0) != FilecoinNetwork.DEV_NET.getValue().charAt(0)
                && encodedAddress.charAt(0) != FilecoinNetwork.MAIN_NET.getValue().charAt(0)) {

            throw new InvalidFilecoinAddressException("Filecoin address should either start with t or f");

        }

        // fetching values of filecoin network and protocol from the encoded address
        FilecoinNetwork filecoinNetwork = FilecoinNetwork.getEnum(Character.toString(encodedAddress.charAt(0)));
        FilecoinProtocol filecoinProtocol = FilecoinProtocol.getEnum(Integer.parseInt(String.valueOf(encodedAddress.charAt(1))));

        log.info("Filecoin network of address is: {}", filecoinNetwork.toString());
        log.info("Filecoin protocol of address is: {}", filecoinProtocol.toString());

        // Based on protocol initializing Address instance
        switch (filecoinProtocol) {
            case SECP256K1:
                address = new SECP256K1Address();
                break;
            case BLS:
                address = new BLSAddress();
                break;
            default:
                throw new InvalidFilecoinProtocolException("Given encoded address does not have valid filecoin protocol");
        }

        // setting filecoin network and protocol in the address
        address.setFilecoinNetwork(filecoinNetwork);
        address.setFilecoinProtocol(filecoinProtocol);

        // decoding raw payload which is base32 encoded
        var raw = BaseEncoding.base32().lowerCase().decode(encodedAddress.substring(2));

        // creating a new byte array to hold payload value from the raw payload
        byte[] payload = new byte[raw.length - 4];
        System.arraycopy(raw, 0, payload, 0, raw.length - 4);
        // setting the address value with the payload
        address.setPayload(payload);

        // creating a new byte array to hold payload value with protocol from rawWithProtocol
        byte[] payloadWithProtocol = new byte[raw.length - 3];
        payloadWithProtocol[0] = (byte)filecoinProtocol.getValue();
        System.arraycopy(raw, 0, payloadWithProtocol, 1, raw.length - 4);
        // setting the address value with the payload
        log.info("Bytes with protocol: {}", payloadWithProtocol);
        System.out.println();
        System.out.print("[");
        for(int i=0; i < payloadWithProtocol.length; i++) {
            System.out.print(Byte.toUnsignedInt(payloadWithProtocol[i]) + " ");
        }
        System.out.println("] ");
        address.setAddress(payloadWithProtocol);

        // validating payload length
        if (filecoinProtocol == FilecoinProtocol.SECP256K1) {
            if (payload.length != 20) {
                throw new InvalidFilecoinAddressException("Invalid bytes");
            }
        }

        // getting last 4 bytes to validate the checksum
        var encodedAddressChecksumBytes = new byte[4];
        System.arraycopy(raw, raw.length - 4, encodedAddressChecksumBytes, 0, 4);
        log.info(Hex.encodeHexString(payload));

        // validating the checksum
        if (!validateChecksum(payload, filecoinProtocol,
                encodedAddressChecksumBytes)) {
            throw new InvalidFilecoinAddressException("Invalid Checksum");
        }

        // returning the address instance
        return address;

    }

    /**
     * Method to get address using a text private key
     *
     * @param privateKey
     * @return Address
     */
    @SneakyThrows
    public Address getAddressFromPrivateKey(String privateKey, FilecoinProtocol  filecoinProtocol) {
        return getAddressFromPrivateKey(Hex.decodeHex(privateKey), filecoinProtocol);
    }

    /**
     * Method to get address using a byte array private key
     *
     * @param privateKeyBytes
     * @return Address
     */
    @SneakyThrows
    public Address getAddressFromPrivateKey(byte[] privateKeyBytes, FilecoinProtocol filecoinProtocol) {


        if (filecoinProtocol.equals(FilecoinProtocol.BLS)) {
            //  BLS Address
            return new BLSAddress(privateKeyBytes, this.networkService.getFilecoinNetwork());

        } else
        {
            // SECP256K1 Address
            return new SECP256K1Address(privateKeyBytes, this.networkService.getFilecoinNetwork());
        }
    }

    /**
     * Method to generate checksum bytes
     *
     * @param addressValue
     * @param filecoinProtocol
     * @return digestedBytes
     * @throws NoSuchAlgorithmException
     */
    public byte[] checksum(byte[] addressValue, FilecoinProtocol filecoinProtocol) {

        // merging protocol bytes with address value bytes
        byte[] bytesToDigest = new byte[addressValue.length + 1];
        bytesToDigest[0] = (byte) filecoinProtocol.getValue();
        System.arraycopy(addressValue, 0, bytesToDigest, 1, addressValue.length);

        // generating the digest of bytes of length 4
        byte[] digestedBytes = new byte[4];
        // initializing blake2bDigest object with length of 4 so that it generates only 4 bytes
        var blake2bDigest = new Blake2bDigest(null, 4, null, null);
        // updating the digest object with bytes to digest
        blake2bDigest.update(bytesToDigest, 0, bytesToDigest.length);
        // asking digest object to perform digestion and providing the digestedByte array
        // in which the digested byte array can be copied
        blake2bDigest.doFinal(digestedBytes, 0);

        log.info("Black2b 160 digest byte size is {} and byte value is {}", digestedBytes.length, digestedBytes);
        // returning the digested bytes
        return digestedBytes;
    }

    /**
     * Method to validate checksum
     *
     * @param addressValue
     * @param filecoinProtocol
     * @param expectedBytes
     * @return boolean
     * @throws NoSuchAlgorithmException
     */
    public boolean validateChecksum(byte[] addressValue, FilecoinProtocol filecoinProtocol,
                                    byte[] expectedBytes) {
        // getting the checksum with the provided parameters
        byte[] checksumBytes = checksum(addressValue, filecoinProtocol);
        // comparing the two arrays
        if (Arrays.equals(checksumBytes, expectedBytes)) {
            return true;
        }
        return false;
    }

}
