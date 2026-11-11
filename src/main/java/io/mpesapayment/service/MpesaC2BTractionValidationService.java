package io.mpesapayment.service;

import io.lib.service.BaseJpaRepoEditService;
import io.mpesapayment.entity.*;
import io.mpesapayment.repository.MpesaC2BPaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
class MpesaC2BTractionValidationService
        extends BaseJpaRepoEditService<MpesaC2BPayment, MpesaC2BPaymentRepository>
        implements MpesaTransactionValidationListener {

    private List<PaymentListener> paymentListeners;

    @Override
    @Transactional
    public void afterCallback(MpesaTransactionValidation validation) {
        var spec = repository.notDeleted()
                .and(repository.transactionIdIs(validation.getTransactionId()));
        var payment = repository.findOne(spec).orElse(null);

        if (payment == null) {
            return;
        }

        payment.setValidationStatus(validation.getValidationStatus());
        save(payment);

        if (payment.getValidationStatus() == MpesaValidationStatus.CONFIRMED) {
            doOnSuccessfulPayment(payment);
        }
    }

    private void doOnSuccessfulPayment(MpesaC2BPayment c2BPayment){
        var payment = new Payment();
        payment.setAccountReference(c2BPayment.getTransactionReference());
        payment.setTransactionId(c2BPayment.getTransactionId());
        payment.setAmount(c2BPayment.getTransAmount());

        for(PaymentListener listener: paymentListeners){
            try {
                listener.onSuccessfulPayment(payment);
            }catch (Exception e){
                log.error("Error occurred while processing payment", e);
            }
        }
    }

    @Autowired
    public void setPaymentListeners(List<PaymentListener> paymentListeners) {
        this.paymentListeners = paymentListeners;
    }
}