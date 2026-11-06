package io.mpesapayment.form;

import com.fasterxml.jackson.annotation.JsonProperty;


import io.micrometer.common.util.StringUtils;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class MpesaC2BPaymentForm {
    @JsonProperty("TransactionType")
    private String transactionType;

    @JsonProperty("TransID")
    private String transactionId;

    @JsonProperty("TransTime")
    private String transactionTime;

    @JsonProperty("TransAmount")
    private BigDecimal amount;

    @JsonProperty("BusinessShortCode")
    private String businessShortCode;

    @JsonProperty("BillRefNumber")
    private String transactionReference;

    @JsonProperty("OrgAccountBalance")
    private BigDecimal accountBalance;

    @JsonProperty("MSISDN")
    private String phoneNumber;

    @JsonProperty("FirstName")
    private String firstName;

    @JsonProperty("LastName")
    private String lastName;


    public String getPayorName(){
        String payorName = "";

        if(StringUtils.isNotBlank(getFirstName())){
            payorName = getFirstName();
        }

        if(StringUtils.isNotBlank(getLastName())){
            payorName = payorName + " " + getLastName();
        }

        return payorName;
    }

}
