package io.mpesapayment.service;

import io.mpesapayment.exception.PaymentException;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
class MpesaCredentialService {
    private String mpesaAccessTokenUrl;

    private final Map<MpesaCredentialType, MpesaCredential> credentialsMap= new HashMap<>();

    String getMpesaAccessToken(String consumerKey,
                               String consumerSecret,
                               MpesaCredentialType credentialType) throws PaymentException {

        if (consumerKey == null || consumerKey.isBlank()) {
            throw new PaymentException("M-Pesa consumer key is missing");
        }

        if (consumerSecret == null || consumerSecret.isBlank()) {
            throw new PaymentException("M-Pesa consumer secret is missing");
        }
        String accessToken;
        MpesaCredential credential = credentialsMap.get(credentialType);
        if(credential != null && !credential.isExpired()){
            accessToken = credential.getAccessToken();
        }else{
            accessToken = getMpesaAccessToken(consumerKey, consumerSecret);
            credentialsMap.put(credentialType, new MpesaCredential(credentialType, accessToken));
        }
        return accessToken;
    }

    private String getMpesaAccessToken(String consumerKey,
                                       String consumerSecret) throws PaymentException {
        String usernamePassword = consumerKey+":"+consumerSecret;
        String encodedPassword = new String(Base64.getEncoder().encode(usernamePassword.getBytes()));

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set(HttpHeaders.AUTHORIZATION, "Basic "+encodedPassword);

        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        String accessToken;
        JSONObject responseObj;
        try {
            ResponseEntity<JSONObject> response = restTemplate.exchange(
                    mpesaAccessTokenUrl,
                    HttpMethod.GET,
                    httpEntity,
                    JSONObject.class
            );
            responseObj = response.getBody();
        }catch (HttpClientErrorException e) {

            String responseBody = e.getResponseBodyAsString();
            log.error("M-Pesa OAuth Error: Status={}, Body={}", e.getStatusCode(), responseBody);
            throw new PaymentException("M-Pesa authentication failed: " + responseBody);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new PaymentException(e);
        }

        if(responseObj!= null) {
            accessToken = responseObj.getAsString("access_token");
        }else{
            throw new PaymentException(
                    new NullPointerException("responseObj.getAsString returned a null object")
            );
        }
        return accessToken;
    }

    @Value("${mpesa.access.token.url}")
    public void setMpesaAccessTokenUrl(String url) {
        this.mpesaAccessTokenUrl = url;
    }
}
