package io.salesorder.service;

import io.activitylog.form.CreateActivityLogForm;
import io.lib.service.BaseJpaRepoEditService;
import io.mpesapayment.entity.Payment;
import io.salesorder.entity.OrderStatus;
import io.salesorder.entity.SalesOrderPayment;
import io.salesorder.repository.SalesOrderPaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
class SalesOrderPaymentEditService
        extends BaseJpaRepoEditService<SalesOrderPayment, SalesOrderPaymentRepository> {
    private SalesOrderEditService salesOrderEditService;

    @Transactional
    void registerSalesOrderPayment(Payment payment) {

        var salesOrder = salesOrderEditService.findByEntityId(payment.getAccountReference());

        var salesOrderPayment = new SalesOrderPayment();
        salesOrderPayment.setSalesOrder(salesOrder);
        salesOrderPayment.setAmount(payment.getAmount());
        salesOrderPayment.setTransactionId(payment.getTransactionId());

        save(salesOrderPayment);
        salesOrder.setOrderStatus(OrderStatus.PAID);
        salesOrderEditService.save(salesOrder);

        var logForm = new CreateActivityLogForm();
        logForm.setOwningEntityId(salesOrder.getEntityId());
        logForm.setAction("SalesOrder payment");
        String remarks = String.format("%s %s", salesOrder.getEntityId(), payment.getAmount());
        logForm.setRemarks(remarks);
        activityLogQueuingService.enqueueActivityLog(logForm);

    }

    @Autowired
    public void setSalesOrderEditService(SalesOrderEditService service) {
        this.salesOrderEditService = service;
    }

}