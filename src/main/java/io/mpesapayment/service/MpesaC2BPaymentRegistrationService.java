package io.mpesapayment.service;

import io.lib.exception.CommonRuntimeException;
import io.lib.exception.ExceptionType;
import io.lib.service.BaseJpaRepoEditService;
import io.mpesapayment.entity.MpesaC2BPayment;
import io.mpesapayment.form.MpesaC2BPaymentForm;
import io.mpesapayment.repository.MpesaC2BPaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
@Slf4j
public class MpesaC2BPaymentRegistrationService extends BaseJpaRepoEditService<MpesaC2BPayment, MpesaC2BPaymentRepository> {

    public void registerPayment(MpesaC2BPaymentForm form){

        var spec = repository
                .notDeleted()
                .and(repository.transactionIdIs(form.getTransactionId()));
        boolean exists = repository.exists(spec);

        if(exists){
            throw new CommonRuntimeException(
                    ExceptionType.BAD_REQUEST,
                    "error.entity.already.exists"
            );
        }

        var transaction = new MpesaC2BPayment();
        transaction.setTransactionId(form.getTransactionId());
        transaction.setTransactionType(form.getTransactionType());
        transaction.setTransAmount(form.getAmount());
        transaction.setBusinessShortCode(form.getBusinessShortCode());
        transaction.setTransactionReference(form.getTransactionReference());
        transaction.setAccountBalance(form.getAccountBalance());
        transaction.setPhoneNumber(form.getPhoneNumber());
        transaction.setPayerName(form.getPayorName());

        try{
            var mpesaDateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            var transactionTime = LocalDateTime.parse(form.getTransactionTime(), mpesaDateTimeFormatter);
            transaction.setTransactionTime(transactionTime);
        }catch (Exception e){
            log.error("Error logging Mpesa C2B transaction time {}", form.getTransactionTime());
        }

        save(transaction);

    }


}
