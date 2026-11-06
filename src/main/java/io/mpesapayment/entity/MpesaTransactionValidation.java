package io.mpesapayment.entity;

import io.lib.entity.BaseJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class MpesaTransactionValidation extends BaseJpaEntity {

    private String transactionId;

    @Column(unique = true)
    private String originatorConversationId;

    @Column(unique = true)
    private String conversationId;

    private String shortcode;

    @Column(length = 1048)
    private String initialResponseDump;

    @Column(length = 1048)
    private String callbackDump;

    @Enumerated(EnumType.STRING)
    private MpesaValidationStatus validationStatus = MpesaValidationStatus.PENDING;
}
