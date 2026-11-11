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
    private SalesOrderReadService salesOrderReadService;

    @Transactional
    void registerSalesOrderPayment(Payment payment) {

        var salesOrder = salesOrderReadService.findByEntityId(payment.getAccountReference());

        var salesOrderPayment = new SalesOrderPayment();
        salesOrder.setOrderStatus(OrderStatus.PAID);
        salesOrderPayment.setSalesOrder(salesOrder);
        salesOrderPayment.setAmount(payment.getAmount());
        salesOrderPayment.setTransactionId(payment.getTransactionId());

        save(salesOrderPayment);

        var logForm = new CreateActivityLogForm();
        logForm.setOwningEntityId(salesOrder.getEntityId());
        logForm.setAction("SalesOrder payment");
        String remarks = String.format("%s %s", salesOrder.getEntityId(), payment.getAmount());
        logForm.setRemarks(remarks);
        activityLogQueuingService.enqueueActivityLog(logForm);

    }

    @Autowired
    public void setSalesOrderReadService(SalesOrderReadService service) {
        this.salesOrderReadService = service;
    }

}