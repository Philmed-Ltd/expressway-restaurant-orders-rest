package io.salesorder.service;

import io.lib.service.BaseJpaRepoEditService;
import io.mpesapayment.entity.Payment;
import io.mpesapayment.service.PaymentListener;
import io.salesorder.entity.SalesOrder;
import io.salesorder.repository.SalesOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SalesOrderPaymentListener
        extends BaseJpaRepoEditService<SalesOrder, SalesOrderRepository>
        implements PaymentListener {

    private SalesOrderPaymentEditService salesOrderPaymentEditService;

    @Override
    public void onSuccessfulPayment(Payment payment) {
        salesOrderPaymentEditService.registerSalesOrderPayment(payment);
    }

    @Autowired
    void setSalesOrderPaymentEditService(SalesOrderPaymentEditService service) {
        this.salesOrderPaymentEditService = service;
    }
}