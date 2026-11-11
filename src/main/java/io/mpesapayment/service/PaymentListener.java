package io.mpesapayment.service;

import io.mpesapayment.entity.Payment;

public interface PaymentListener {
    void onSuccessfulPayment(Payment payment);
}
