package com.poc.filecoin.service;

import com.poc.filecoin.dto.TransactionDTO;
import com.poc.filecoin.enums.FilecoinNetwork;
import com.poc.filecoin.exceptions.InvalidFilecoinNetworkException;

import com.poc.filecoin.model.Signature;
import com.poc.filecoin.model.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.googlecode.jsonrpc4j.JsonRpcClient;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Service class for methods that invoke methods on Filecoin blockchain
 *
 * @author Vijay Pratap Singh
 */
@Service
@Slf4j
public class NetworkService {

    @Value("${filecoin.http.client-address}")
    String clientHttpAddress;

    @Value("${filecoin.bearer.auth.token}")
    String bearerAuthToken;


    /**
     * Method to get filecoin network
     *
     * @return FilecoinNetwork
     */
    @SneakyThrows
    public FilecoinNetwork getFilecoinNetwork() {

        String network = this.getJsonRpcHttpClient().invoke("Filecoin.StateNetworkName",
                new ArrayList<>(),
                String.class);
        if (network.equals("calibrationnet")) {
            return FilecoinNetwork.DEV_NET;
        } else if (network.equals("mainnet")) {
            return FilecoinNetwork.MAIN_NET;
        } else {
            throw new InvalidFilecoinNetworkException("Invalid Filecoin network");
        }
    }

    /**
     * Method to get nonce for a filecoin address
     *
     * @param address
     * @return int
     */
    @SneakyThrows
    public int getNonceForAddress(String address) {

        int nonce = this.getJsonRpcHttpClient().invoke("Filecoin.MpoolGetNonce",
                new ArrayList<>() {{
                    add(address);
                }},
                Integer.class);
        return nonce;

    }

    /**
     * Method to get chain head of the filecoin network
     *
     * @return String
     */
    @SneakyThrows
    public String getChainHead() {

        var result = this.getJsonRpcHttpClient().invoke("Filecoin.ChainHead",
                new ArrayList<>(),
                JsonNode.class);

        return result.get("Cids").get(0).get("/").asText();

    }

    /**
     * Method to send a signed transaction to the blockchain and retrieve the response in JSON
     *
     * @param transaction
     * @param from
     * @param to
     * @param signature
     * @param cid
     * @return JsonNode
     */
    @SneakyThrows
    public JsonNode sendTransaction(Transaction transaction, String from, String to, Signature signature, String cid) {

        TransactionDTO transactionDTO = new TransactionDTO(transaction.getVersion(),
                to,
                from,
                transaction.getNonce(),
                transaction.getValue(),
                transaction.getGasLimit(),
                transaction.getGasFeeCap(),
                transaction.getGasPremium(),
                transaction.getMethod(),
                new String(transaction.getParams()));

        log.info("Transaction getValue is {}", transaction.getValue());
        log.info("Transaction getGasFeeCap is {}", transaction.getGasFeeCap());
        log.info("Transaction getGasPremium is {}", transaction.getGasPremium());
        log.info("Transaction getParams is {}", new String(transaction.getParams()));


        var result = this.getJsonRpcHttpClient().invoke("Filecoin.MpoolPush",
                new ArrayList<>() {{
                    add(
                            new HashMap() {{
                                put("Message", new ObjectMapper().valueToTree(transactionDTO));
                                put("Signature", new HashMap<>() {{
                                    put("Type", signature.getSignatureType().getValue());
                                    put("Data", Base64.encodeBase64String(signature.getSignatureBytes()));
                                }});
                                put("CID", new HashMap() {{
                                    put("/", cid);
                                }});
                            }});
                }},
                JsonNode.class);

        return result;

    }

    /**
     * Method to get json rpc http client
     *
     * @return JsonRpcHttpClient
     */
    @SneakyThrows
    private JsonRpcHttpClient getJsonRpcHttpClient() {
        JsonRpcHttpClient jsonRpcHttpClient = new JsonRpcHttpClient(new URL(clientHttpAddress));
        jsonRpcHttpClient.setHeaders(new HashMap<>() {{
            put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        }});
        jsonRpcHttpClient.setAdditionalJsonContent(new HashMap<>() {{
            put("id", 1);
        }});

        // request and response listeners, you may add your custom logic
        JsonRpcClient.RequestListener listener = new JsonRpcClient.RequestListener() {
            @Override
            public void onBeforeRequestSent(JsonRpcClient jsonRpcClient, ObjectNode objectNode) {
                log.info("Request to filecoin network is: {}", objectNode.toPrettyString());
            }

            @Override
            public void onBeforeResponseProcessed(JsonRpcClient jsonRpcClient, ObjectNode objectNode) {

            }
        };

        jsonRpcHttpClient.setRequestListener(listener);

        return jsonRpcHttpClient;
    }

}
