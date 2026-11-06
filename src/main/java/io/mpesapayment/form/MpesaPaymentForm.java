package io.mpesapayment.form;

import io.lib.form.SessionUserIdForm;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MpesaPaymentForm extends SessionUserIdForm {
    @NotBlank(message = "error.invalid.initiator.identifier")
    private String initiatorIdentifier;

    @NotBlank(message = "error.invalid.account.reference")
    private String accountReference;

    @NotBlank(message = "error.invalid.child.business.short.code")
    private String childBusinessShortCode;

    @NotBlank(message = "error.invalid.mpesa.phone.number")
    private String mpesaPhoneNumber;

    @DecimalMin(value = "1", message = "error.invalid.amount")
    private BigDecimal amount;
}
