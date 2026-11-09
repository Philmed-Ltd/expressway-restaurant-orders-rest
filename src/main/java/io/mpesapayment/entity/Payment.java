package io.mpesapayment.entity;

import io.salesorder.validation.UniqueTransactionId;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Payment {
   private String accountReference;
   @UniqueTransactionId
   private String transactionId;
   private BigDecimal amount;
}
