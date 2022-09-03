package com.poc.filecoin.service;

import com.poc.filecoin.model.Address;
import com.poc.filecoin.model.Signature;
import com.poc.filecoin.model.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * Service class for filecoin transaction
 *
 * @author Vijay Pratap Singh
 */
@Service
@Slf4j
public class TransactionService {

    @Value("${filecoin.gas.limit}")
    private int GAS_LIMIT;

    @Value("${filecoin.gas.fee.cap}")
    private String GAS_FEE_CAP;

    @Value("${filecoin.gas.premium}")
    private String GAS_PREMIUM;

    @Value("${filecoin.sign.message.version}")
    private int MESSAGE_SIGN_VERSION;

    @Value("${filecoin.sign.method}")
    private int SIGN_METHOD;

    private SignatureService signatureService;

    private AddressService addressService;

    private NetworkService networkService;

    @Autowired
    public void setNetworkService(NetworkService networkService) {
        this.networkService = networkService;
    }

    @Autowired
    public void setSignatureService(SignatureService signatureService) {
        this.signatureService = signatureService;
    }

    @Autowired
    public void setAddressService(AddressService addressService) {
        this.addressService = addressService;
    }

    /**
     * Method to sign and send a transaction on filecoin network
     *
     * @param privateKey
     * @param from
     * @param to
     * @param message
     * @param value
     * @return JsonNode
     */
    public JsonNode signAndSendTransaction(String privateKey, String from, String to, String message, String value) {

        log.info("Method signAndSendTransaction invoked from address {} to {}", from, to);

        // Decoding from address and toAddress
        Address fromAddress = addressService.decode(from);
        Address toAddress = addressService.decode(to);
        // getting nonce for the address
        int nonce = networkService.getNonceForAddress(from);
        log.info("Nonce for the address {} is {}", from, nonce);

        byte[] params = Base64.encodeBase64(message.getBytes());
        log.info("Encoded message: {}", params);

        // initializing transaction
        Transaction transaction = new Transaction(MESSAGE_SIGN_VERSION,
                toAddress.getAddress(),
                fromAddress.getAddress(),
                nonce,
                value,
                GAS_LIMIT,
                GAS_FEE_CAP,
                GAS_PREMIUM,
                SIGN_METHOD,
                params);

        // sign transaction
        Signature signature = signatureService.signTransaction(privateKey, transaction, fromAddress.getFilecoinProtocol());
        log.info("Transaction signed successfully");

        // get cid for sending the transaction
        String cid = networkService.getChainHead();
        log.info("Obtained CID is : {}", cid);


        // send the transaction
        JsonNode transactionResponse = networkService.sendTransaction(transaction, from, to, signature, cid);
        log.info("Transaction sent successfully");

        return transactionResponse;

    }


}
