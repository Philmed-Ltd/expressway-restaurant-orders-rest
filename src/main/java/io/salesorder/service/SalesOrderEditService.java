package io.salesorder.service;

import io.activitylog.form.CreateActivityLogForm;
import io.customer.service.CustomerReadService;
import io.lib.service.BaseJpaRepoEditService;
import io.mpesapayment.exception.PaymentException;
import io.mpesapayment.form.MpesaPaymentForm;
import io.mpesapayment.service.MpesaSTKPushService;
import io.salesorder.entity.SalesOrder;
import io.salesorder.form.RegisterSalesOrderForm;
import io.salesorder.form.SaleOrderPaymentForm;
import io.salesorder.repository.SalesOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SalesOrderEditService extends BaseJpaRepoEditService<SalesOrder, SalesOrderRepository> {

    private CustomerReadService customerReadService;
    private MpesaSTKPushService mpesaSTKPushService;
    public String businessShortCode;


    private SaleOrderItemEditService saleOrderItemEditService;

    public SalesOrder registerSalesOrder(RegisterSalesOrderForm form){
        var customer = customerReadService.findByEntityId(form.getSessionUserId());

        var salesOrder = new SalesOrder();
        salesOrder.setCustomer(customer);
        salesOrder.setDeliveryAddress(form.getDeliveryAddress());
        salesOrder.setTotalAmountToBePaid(form.getTotalAmountToBePaid());
        salesOrder = save(salesOrder);

        saleOrderItemEditService.registerSaleOrderItems(salesOrder, form.getItems());

        var activityLogForm = new CreateActivityLogForm();
        activityLogForm.setOwningEntityId(salesOrder.getEntityId());
        activityLogForm.setAction("Sales order registration");
        activityLogForm.setSessionUserId(form.getSessionUserId());
        activityLogQueuingService.enqueueActivityLog(activityLogForm);

        return salesOrder;
    }

    public void initializePayment(String saleOrderId, SaleOrderPaymentForm form) throws PaymentException {
        var salesOrder = findByEntityId(saleOrderId);
        salesOrder = save(salesOrder, form.getSessionUserId());

        var mpesaPaymentForm = new MpesaPaymentForm();
        mpesaPaymentForm.setAccountReference(saleOrderId);
        mpesaPaymentForm.setChildBusinessShortCode(businessShortCode);
        mpesaPaymentForm.setMpesaPhoneNumber(form.getMpesaPhoneNumber());
        mpesaPaymentForm.setAmount(form.getAmount());
        mpesaPaymentForm.setSessionUserId(form.getSessionUserId());

        mpesaSTKPushService.stkInitialization(mpesaPaymentForm);

        var activityLogForm = new CreateActivityLogForm();
        activityLogForm.setOwningEntityId(salesOrder.getEntityId());
        activityLogForm.setAction("Initialize payment");
        activityLogForm.setSessionUserId(form.getSessionUserId());
        activityLogQueuingService.enqueueActivityLog(activityLogForm);

    }

    @Autowired
    public void setCustomerReadService(CustomerReadService service){
        this.customerReadService = service;
    }

    @Autowired
    public void setSaleOrderItemEditService(SaleOrderItemEditService service){ this.saleOrderItemEditService = service; }

    @Autowired
    public void setMpesaSTKPushService(MpesaSTKPushService mpesaSTKPushService) {this.mpesaSTKPushService = mpesaSTKPushService;}

    @Value("${mpesa.c2b.shortcode}")
    public void setBusinessShortCode(String businessShortCode) {this.businessShortCode = businessShortCode;}

}
