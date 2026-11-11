package io.mpesapayment.service;

import io.mpesapayment.entity.MpesaTransactionValidation;

interface MpesaTransactionValidationListener {
    void afterCallback(MpesaTransactionValidation validation);
}
