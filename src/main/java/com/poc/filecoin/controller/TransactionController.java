package com.poc.filecoin.controller;

import com.poc.filecoin.dto.TransactionRequestDTO;
import com.poc.filecoin.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for transaction
 *
 * @author Vijay Pratap Singh
 */
@RestController()
@RequestMapping("transaction")
@Slf4j
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    /**
     * API method sign and send transaction\
     *
     * @param transactionRequest
     * @return ResponseEntity
     */
    @PostMapping(value = "send", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity signAndSendTransaction(@RequestBody TransactionRequestDTO transactionRequest) {
        try {
            var transaction = transactionService.signAndSendTransaction(transactionRequest.getPrivateKey(),
                    transactionRequest.getFromAddress(),
                    transactionRequest.getToAddress(),
                    transactionRequest.getMessage(),
                    transactionRequest.getValue());
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            log.error("Error while signing and sending transaction: {}", e.getStackTrace());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }


    }
}
