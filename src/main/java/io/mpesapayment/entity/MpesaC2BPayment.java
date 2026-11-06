package io.mpesapayment.entity;

import io.lib.entity.BaseJpaEntity;
import io.lib.service.FormatUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "mpesa_c2b_payment")
public class MpesaC2BPayment extends BaseJpaEntity {

    @Column(unique = true, nullable = false)
    private String transactionId;

    private String transactionType;
    private LocalDateTime transactionTime;

    @Column(precision = FormatUtil.BIG_DECIMAL_PRECISION, scale = FormatUtil.BIG_DECIMAL_SCALE)
    private BigDecimal transAmount;

    private String businessShortCode;
    private String transactionReference;

    @Column(precision = FormatUtil.BIG_DECIMAL_PRECISION, scale = FormatUtil.BIG_DECIMAL_SCALE)
    private BigDecimal accountBalance;

    private String phoneNumber;
    private String payerName;

    @Enumerated(EnumType.STRING)
    private MpesaValidationStatus validationStatus = MpesaValidationStatus.PENDING;
}