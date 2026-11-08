package io.mpesapayment.service;

import io.lib.exception.CommonRuntimeException;
import io.lib.exception.ExceptionType;
import io.lib.service.BaseJpaRepoEditService;
import io.mpesapayment.entity.MpesaSTKPayment;
import io.mpesapayment.entity.MpesaSTKPushPaymentStatus;
import io.mpesapayment.exception.PaymentException;
import io.mpesapayment.form.MpesaPaymentForm;
import io.mpesapayment.form.MpesaSTKPushInitForm;
import io.mpesapayment.repository.MpesaSTKPaymentRepository;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class MpesaSTKPushService extends BaseJpaRepoEditService<MpesaSTKPayment, MpesaSTKPaymentRepository> {

    private MpesaCredentialService mpesaCredentialService;
    private RestTemplate restTemplate;

    private String mpesaC2BConsumerKey;
    private String mpesaC2BConsumerSecret;
    private String mpesaC2BPassKey;
    private String mpesaStkPushRequestUrl;
    public String businessShortCode;
    public String stkPushCallbackUrl;

    public void stkInitialization(MpesaPaymentForm paymentForm)
            throws PaymentException {

        var timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        var phoneNumber = mpesaCredentialService.formatMpesaPaymentPhoneNumber(paymentForm.getMpesaPhoneNumber());

        var initForm = new MpesaSTKPushInitForm();
        initForm.setPrimaryBusinessShortCode(businessShortCode);
        initForm.setChildBusinessShortCode(paymentForm.getChildBusinessShortCode());
        initForm.setAccountReference(paymentForm.getAccountReference());
        initForm.setAmount(paymentForm.getAmount());
        initForm.setPayorPhoneNumber(phoneNumber);
        initForm.setPassword(generatePassword(timestamp));
        initForm.setTimestamp(timestamp);
        initForm.setCallBackUrl(stkPushCallbackUrl);

        var accessToken = mpesaCredentialService.getMpesaAccessToken(
                mpesaC2BConsumerKey,
                mpesaC2BConsumerSecret,
                MpesaCredentialType.M_PESA_C2B_PROMPT
        );
        log.info("STK Push access token generated.");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(accessToken);

        try {
            var httpRequest = new HttpEntity<>(initForm ,httpHeaders);
            var response = restTemplate.exchange(
                    mpesaStkPushRequestUrl,
                    HttpMethod.POST,
                    httpRequest,
                    JSONObject.class
            );
            var entityResponse = response.getBody();
            var transaction = new MpesaSTKPayment();
            transaction.setAccountReference(paymentForm.getAccountReference());
            transaction.setParentBusinessShortCode(businessShortCode);
            transaction.setChildBusinessShortCode(paymentForm.getChildBusinessShortCode());
            transaction.setAmount(paymentForm.getAmount());
            transaction.setPhoneNumber(phoneNumber);
            transaction.setCreatedByEntityId(paymentForm.getSessionUserId());

            save(transaction);

            if (entityResponse != null) {
                int resultCode = Integer.parseInt(entityResponse.getAsString("ResponseCode"));
                if(resultCode == 0){
                    transaction.setMpesaSTKPushPaymentStatus(MpesaSTKPushPaymentStatus.INITIALIZATION_SUCCESSFUL);

                }else{
                    transaction.setMpesaSTKPushPaymentStatus(MpesaSTKPushPaymentStatus.INITIALIZATION_FAILED);
                }
                transaction.setCheckoutRequestId(entityResponse.getAsString("CheckoutRequestID"));
                transaction.setInitialResponseDump(entityResponse.toJSONString());
                save(transaction);
            }
        }catch (Exception e){
            log.error("Error occurred while making stk push request", e);
            throw new PaymentException("STK push initialization failed");
        }

    }

    public void handleSTKPushCallback(JSONObject callback) throws PaymentException{
        LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) callback.get("Body");
        LinkedHashMap<String, Object> stkCallback = (LinkedHashMap<String, Object>) body.get("stkCallback");

        String checkoutRequestId = (String) stkCallback.get("CheckoutRequestID");
        var stkPush = getByCheckoutRequestId(checkoutRequestId);

        if (stkPush.getMpesaSTKPushPaymentStatus() == MpesaSTKPushPaymentStatus.PAYMENT_SUCCESSFUL) {
            return;
        }

        stkPush.setCallBackResponseDump(callback.toJSONString());
        Integer resultCode = (Integer) stkCallback.get("ResultCode");
        if(resultCode == 0){
            stkPush.setMpesaSTKPushPaymentStatus(MpesaSTKPushPaymentStatus.PAYMENT_SUCCESSFUL);
        }else{
            stkPush.setMpesaSTKPushPaymentStatus(MpesaSTKPushPaymentStatus.PAYMENT_FAILED);
        }
        save(stkPush);

        if (stkPush.getMpesaSTKPushPaymentStatus() == MpesaSTKPushPaymentStatus.PAYMENT_SUCCESSFUL) {
            LinkedHashMap<String, Object> metadata = (LinkedHashMap<String, Object>) stkCallback.get("CallbackMetadata");
            List<Object> items = (List<Object>) metadata.get("Item");

            for (Object parameterObj : items) {
                HashMap<String, Object> obj = (HashMap<String, Object>) parameterObj;
                String key = (String) obj.get("Name");

                if (key.equals("MpesaReceiptNumber")) {
                    stkPush.setTransactionId(obj.get("Value").toString());
                }

                if (key.equals("Amount")) {
                    stkPush.setAmount(new BigDecimal(obj.get("Value").toString()));
                }
            }
            stkPush = save(stkPush);
            doOnSuccessfulPayment(stkPush);
        }
    }

    private MpesaSTKPayment getByCheckoutRequestId (String checkoutRequestId){
        var spec = repository.notDeleted().and(repository.checkoutRequestIdIs(checkoutRequestId));
        var validationOpt = repository.findOne(spec);
        return validationOpt.orElseThrow(
                () -> new CommonRuntimeException(ExceptionType.NOT_FOUND, "error.entity.not.found")
        );
    }



    private String generatePassword(String timestamp) {
        return Base64.getEncoder().encodeToString((businessShortCode + mpesaC2BPassKey + timestamp).getBytes());
    }

    private void doOnSuccessfulPayment(MpesaSTKPayment stkPayment){

    }

    @Autowired
    void setMpesaCredentialService(MpesaCredentialService service) {
        this.mpesaCredentialService = service;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }



    @Value("${mpesa.c2b.shortcode}")
    public void setBusinessShortCode(String businessShortCode) {
        this.businessShortCode = businessShortCode;
    }

    @Value("${mpesa.stk.push.callback.url}")
    public void setStkPushCallbackUrl(String url) {
        this.stkPushCallbackUrl = url;
    }

    @Value("${mpesa.c2b.consumer.key}")
    public void setMpesaC2BConsumerKey(String consumerKey) {
        this.mpesaC2BConsumerKey = consumerKey;
    }

    @Value("${mpesa.c2b.consumer.secret}")
    public void setMpesaC2BConsumerSecret(String consumerSecret) {
        this.mpesaC2BConsumerSecret = consumerSecret;
    }

    @Value(("${mpesa.c2b.pass.key}"))
    public void setMpesaC2BPassKey(String passKey) {
        this.mpesaC2BPassKey = passKey;
    }

    @Value(("${mpesa.stk.push.request.url}"))
    public void setMpesaStkPushRequestUrl(String url) {
        this.mpesaStkPushRequestUrl = url;
    }
}
