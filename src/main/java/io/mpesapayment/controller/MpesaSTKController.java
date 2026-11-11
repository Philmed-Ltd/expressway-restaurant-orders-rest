package io.mpesapayment.controller;

import io.mpesapayment.exception.PaymentException;
import io.mpesapayment.form.MpesaPaymentForm;
import io.mpesapayment.service.MpesaSTKPushService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("saf/c2b")
public class MpesaSTKController {
    private MpesaSTKPushService mpesaSTKPushService;

    @PostMapping("handle-stk-push-callback")
    public ResponseEntity<Void> handleSTKPushCallback(@RequestBody JSONObject callback) throws PaymentException {
        mpesaSTKPushService.handleSTKPushCallback(callback);
        return ResponseEntity.ok(null);
    }

    @Autowired
    public void setMpesaSTKPushService(MpesaSTKPushService mpesaSTKPushService) {
        this.mpesaSTKPushService = mpesaSTKPushService;
    }
}
