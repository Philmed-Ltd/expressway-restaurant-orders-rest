package io.mpesapayment.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Payment {
   private String accountReference;
    private String transactionId;
    private BigDecimal amount;
}
