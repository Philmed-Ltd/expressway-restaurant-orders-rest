package io.mpesapayment.controller;

import io.mpesapayment.exception.PaymentException;
import io.mpesapayment.form.MpesaC2BPaymentForm;
import io.mpesapayment.service.MpesaTransactionValidationService;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.mpesapayment.service.MpesaC2BPaymentRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@RestController
@RequestMapping("saf/c2b")
public class MpesaC2BController {
    private MpesaC2BPaymentRegistrationService paymentService;
    private MpesaTransactionValidationService transactionValidationService;

    @PostMapping("register-payment")
    public ResponseEntity<Void> registerPayment(@RequestBody MpesaC2BPaymentForm form) {
        paymentService.registerPayment(form);
        return ResponseEntity.ok(null);
    }

    @PostMapping("transaction-status/result")
    public ResponseEntity<Void>transactionStatusResult(
            @RequestBody JSONObject callback) throws PaymentException {
        transactionValidationService.transactionStatusCallback(callback);
        return ResponseEntity.ok(null);
    }

    @PostMapping("transaction-status/timeout")
    public ResponseEntity<Void>transactionStatusTimeout(
            @RequestBody JSONObject result) throws PaymentException {
        transactionValidationService.transactionStatusTimeout(result);
        return ResponseEntity.ok(null);
    }


    @Autowired
    public void setMpesaPaymentService(MpesaC2BPaymentRegistrationService paymentService){
        this.paymentService = paymentService;
    }

    @Autowired
    public void setTransactionValidationService(MpesaTransactionValidationService transactionValidationService) {
        this.transactionValidationService = transactionValidationService;
    }

}
