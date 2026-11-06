package io.mpesapayment.controller;

import io.mpesapayment.form.MpesaC2BPaymentForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.mpesapayment.service.MpesaC2BPaymentRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@RestController
@RequestMapping("saf/c2b")
public class MpesaC2BController {
    private MpesaC2BPaymentRegistrationService paymentService;

    @PostMapping("register-payment")
    public ResponseEntity<Void> registerPayment(@RequestBody MpesaC2BPaymentForm form) {
        paymentService.registerPayment(form);
        return ResponseEntity.ok(null);
    }


    @Autowired
    public void setMpesaPaymentService(MpesaC2BPaymentRegistrationService paymentService){
        this.paymentService = paymentService;
    }



}
