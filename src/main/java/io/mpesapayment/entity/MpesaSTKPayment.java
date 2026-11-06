package io.mpesapayment.entity;

import io.lib.entity.BaseJpaEntity;
import io.lib.service.FormatUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Setter
@Getter
@Table(
        name = "mpesa_stk_payment",
        indexes = {@Index(name = "idx_initiator_identifier", columnList = "initiatorIdentifier")}
)
public class MpesaSTKPayment extends BaseJpaEntity {
    private String initiatorIdentifier;
    private String phoneNumber;
    private String transactionId;
    private String parentBusinessShortCode;
    private String childBusinessShortCode;
    private String accountReference;
    private String checkoutRequestId;

    @Column(precision = FormatUtil.BIG_DECIMAL_PRECISION, scale = FormatUtil.BIG_DECIMAL_SCALE)
    private BigDecimal amount;

    @Column(length = 1048)
    private String initialResponseDump;

    @Column(length = 1048)
    private String callBackResponseDump;

    @Enumerated(EnumType.STRING)
    private MpesaSTKPushPaymentStatus mpesaSTKPushPaymentStatus;
}
