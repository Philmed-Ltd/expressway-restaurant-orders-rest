package io.mpesapayment.service;

import io.lib.exception.CommonRuntimeException;
import io.lib.exception.ExceptionType;
import io.lib.service.BaseJpaRepoEditService;
import io.mpesapayment.entity.MpesaTransactionValidation;
import io.mpesapayment.entity.MpesaValidationStatus;
import io.mpesapayment.exception.PaymentException;
import io.mpesapayment.form.MpesaTransactionValidationForm;
import io.mpesapayment.repository.MpesaTransactionValidationRepository;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class MpesaTransactionValidationService
        extends BaseJpaRepoEditService<MpesaTransactionValidation, MpesaTransactionValidationRepository> {
    private RestTemplate restTemplate;
    private MpesaCredentialService mpesaCredentialService;
    private String mpesaC2BConsumerKey;
    private String mpesaC2BConsumerSecret;
    private String mpesaC2BApiUsername;
    private String mpesaC2BApiPassword;
    private String mpesaC2BShortCode;

    private String mpesaTransactionValidationUrl;
    private String mpesaTransactionValidationCallbackUrl;

    private String mpesaTransactionValidationTimeoutUrl;

    public void queryTransactionStatus(String transactionId) throws PaymentException {
        if (transactionId == null || transactionId.isBlank()) {
            throw new CommonRuntimeException(ExceptionType.BAD_REQUEST, "error.invalid.transaction.id");
        }
        log.info("Starting M-Pesa transaction validation for transaction: {}", transactionId);

        var accessToken = mpesaCredentialService.getMpesaAccessToken(
                mpesaC2BConsumerKey,
                mpesaC2BConsumerSecret,
                MpesaCredentialType.M_PESA_C2B_VALIDATION
        );


        log.info("M-Pesa access token obtained successfully");

        String originatorConvId = UUID.randomUUID().toString();

        var form = new MpesaTransactionValidationForm();

        form.setUsername(mpesaC2BApiUsername);
        form.setPassword(mpesaC2BApiPassword);
        form.setOriginatorConversationId(originatorConvId);
        form.setTransactionId(transactionId);
        form.setShortcode(mpesaC2BShortCode);
        form.setCallbackUrl(mpesaTransactionValidationCallbackUrl);
        form.setQueueTimeOut(mpesaTransactionValidationTimeoutUrl);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(accessToken);

        HttpEntity<MpesaTransactionValidationForm> httpRequest = new HttpEntity<>(form, httpHeaders);

        try {
            var validation = new MpesaTransactionValidation();
            validation.setShortcode(form.getShortcode());
            validation.setTransactionId(form.getTransactionId());
            validation.setOriginatorConversationId(form.getOriginatorConversationId());
            log.info("in conv id {}",validation.getOriginatorConversationId());

            validation = save(validation);

            log.info( "Sending Mpesa transaction validation");
            ResponseEntity<JSONObject> response = restTemplate.postForEntity(
                    mpesaTransactionValidationUrl,
                    httpRequest,
                    JSONObject.class
            );

            var responseBody = response.getBody();
            log.info("Mpesa transaction validation response received: {}", responseBody);

            if (responseBody != null) {
                validation.setInitialResponseDump(responseBody.toJSONString());
                validation.setValidationStatus(MpesaValidationStatus.INITIALIZED);

                String conversationId = responseBody.getAsString("ConversationID");
                if (StringUtils.isNotBlank(conversationId)) {
                    validation.setConversationId(conversationId);
                }

                String returnedOriginatorId = responseBody.getAsString("OriginatorConversationID");
                if (StringUtils.isNotBlank(returnedOriginatorId)) {
                    validation.setOriginatorConversationId(returnedOriginatorId);
                }
                save(validation);
            }else{
                log.error("Mpesa transaction status query returned empty response");
            }
        }catch (Exception e){
            log.error("Error initializing transaction validation", e);
        }
    }

    public void transactionStatusCallback (JSONObject callback) throws PaymentException {
        if (callback == null){
            log.error("Mpesa transaction status callback returned empty response");
            return;
        }

        var resultObject = callback.get("Result");
        JSONObject result = new JSONObject();
        result.putAll((Map<String ,Object>) resultObject);

        var originatorConversationId = result.get("OriginatorConversationID").toString();
        var validation = getByOriginatorConversationId(originatorConversationId);
        validation.setCallbackDump(new ObjectMapper().writeValueAsString(callback));
        save(validation);

        var resultCode = result.getAsString("ResultCode");
        if(StringUtils.equalsIgnoreCase(resultCode, "0")){
            var resultParametersObject = result.get("ResultParameters");
            var resultParameters = new JSONObject();
            resultParameters.putAll((Map<String, Object>) resultParametersObject);
            var resultPar = resultParameters.get("ResultParameter");

            String transactionStatus = null;

            for (Object paramObj : (Iterable<?>) resultPar){
                Map <String , Object > parameter = (Map<String, Object>) paramObj;
                var key = parameter.get("Key").toString();
                var value = parameter.get("Value");

                if(StringUtils.equalsIgnoreCase(key, "TransactionStatus")){
                    transactionStatus = value.toString();
                    break;
                }
            }

            if(StringUtils.equalsIgnoreCase(transactionStatus, "Completed")){
                validation.setValidationStatus(MpesaValidationStatus.CONFIRMED);
            }else{
                validation.setValidationStatus(MpesaValidationStatus.FAILED);
            }
        }else{
            validation.setValidationStatus(MpesaValidationStatus.FAILED);
        }

        save(validation);
    }

    public void transactionStatusTimeout (JSONObject callback) throws PaymentException {
        if (callback == null){
            log.error("M-PESA transaction val timeout returned empty response");
            return;
        }

        log.info("Processing transaction timeout callback: {}", callback);

        var resultObject = callback.get("Result");
        var result = new JSONObject();
        result.putAll((Map<String ,Object>) resultObject);

        var originatorConversationId = result.get("OriginatorConversationID").toString();
        var validation =  getByOriginatorConversationId(originatorConversationId);
        validation.setCallbackDump(new ObjectMapper().writeValueAsString(callback));
        validation.setValidationStatus(MpesaValidationStatus.PENDING);
        save(validation);

    }

    private MpesaTransactionValidation getByOriginatorConversationId(String originatorConversationID){
        var spec = repository.notDeleted().and(repository.originatorConversationIdIs(originatorConversationID));
        var validationOpt = repository.findOne(spec);
        return validationOpt.orElseThrow(
                () -> new CommonRuntimeException(ExceptionType.NOT_FOUND, "error.entity.not.found")
        );
    }



    @Autowired
    public void setRestTemplate (RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    @Autowired
    void setMpesaCredentialService(MpesaCredentialService service) {
        this.mpesaCredentialService = service;
    }



    @Value("${mpesa.c2b.consumer.key}")
    public void setMpesaC2BConsumerKey(String mpesaC2BConsumerKey) {
        this.mpesaC2BConsumerKey = mpesaC2BConsumerKey;
    }

    @Value("${mpesa.c2b.consumer.secret}")
    public void setMpesaC2BConsumerSecret(String mpesaC2BConsumerSecret) {
        this.mpesaC2BConsumerSecret = mpesaC2BConsumerSecret;
    }

    @Value("${mpesa.c2b.api.username}")
    public void setMpesaC2BApiUsername(String mpesaC2BApiUsername) {
        this.mpesaC2BApiUsername = mpesaC2BApiUsername;
    }

    @Value("${mpesa.c2b.api.password}")
    public void setMpesaC2BApiPassword(String mpesaC2BApiPassword) {
        this.mpesaC2BApiPassword = mpesaC2BApiPassword;
    }

    @Value("${mpesa.transaction.validation.url}")
    public void setMpesaTransactionValidationUrl(String url) {
        this.mpesaTransactionValidationUrl = url;
    }

    @Value("${mpesa.c2b.transaction.validation.callback.url}")
    public void setMpesaTransactionValidationCallbackUrl(String url) {
        this.mpesaTransactionValidationCallbackUrl = url;
    }

    @Value("${mpesa.c2b.transaction.validation.timeout.url}")
    public void setMpesaTransactionValidationTimeoutUrl(String url) {
        this.mpesaTransactionValidationTimeoutUrl = url;
    }

    @Value("${mpesa.c2b.shortcode}")
    public void setMpesaC2BShortCode(String mpesaC2BShortCode) {
        this.mpesaC2BShortCode = mpesaC2BShortCode;
    }
}
