package io.mpesapayment.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MpesaTransactionValidationForm {
    @JsonProperty("Initiator")
    private String username;

    @JsonProperty("SecurityCredential")
    private String password;

    @JsonProperty("CommandID")
    private String commandID = "TransactionStatusQuery";

    @JsonProperty("TransactionID")
    private String transactionId;

    @JsonProperty("OriginatorConversationID")
    private String originatorConversationId;

    @JsonProperty("PartyA")
    private String shortcode;

    @JsonProperty("IdentifierType")
    private String identifierType = "4";

    @JsonProperty("ResultURL")
    private String callbackUrl;

    @JsonProperty("QueueTimeOutURL")
    private String queueTimeOut;

    @JsonProperty("Remarks")
    private String remarks = "Transaction status query";

    @JsonProperty("Occasion")
    private String occasion = "BnB payment verification";
}
