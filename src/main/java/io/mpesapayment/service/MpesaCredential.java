package io.mpesapayment.service;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MpesaCredential {
    private static final int expiryOffsetInSeconds = 2500;

    private MpesaCredentialType credentialType;
    private String accessToken;
    private LocalDateTime timeLastGenerated = LocalDateTime.now();

    public MpesaCredential(MpesaCredentialType credentialType, String accessToken) {
        this.credentialType = credentialType;
        this.accessToken = accessToken;
    }

    public boolean isExpired(){
        return  LocalDateTime.now().minusSeconds(expiryOffsetInSeconds).isAfter(timeLastGenerated);
    }
}
