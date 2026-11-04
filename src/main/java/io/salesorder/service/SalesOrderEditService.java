package io.salesorder.service;

import io.activitylog.form.CreateActivityLogForm;
import io.customer.service.CustomerReadService;
import io.lib.service.BaseJpaRepoEditService;
import io.salesorder.entity.SalesOrder;
import io.salesorder.form.RegisterSalesOrderForm;
import io.salesorder.repository.SalesOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SalesOrderEditService extends BaseJpaRepoEditService<SalesOrder, SalesOrderRepository> {

    private CustomerReadService customerReadService;

    private SaleOrderItemEditService saleOrderItemEditService;

    public SalesOrder registerSalesOrder(RegisterSalesOrderForm form){
        var customer = customerReadService.findByEntityId(form.getSessionUserId());

        var salesOrder = new SalesOrder();
        salesOrder.setCustomer(customer);
        salesOrder.setDeliveryAddress(form.getDeliveryAddress());
        salesOrder = save(salesOrder);

        saleOrderItemEditService.registerSaleOrderItems(salesOrder, form.getItems());

        var activityLogForm = new CreateActivityLogForm();
        activityLogForm.setOwningEntityId(salesOrder.getEntityId());
        activityLogForm.setAction("Sales order registration");
        activityLogForm.setSessionUserId(form.getSessionUserId());
        activityLogQueuingService.enqueueActivityLog(activityLogForm);

        return salesOrder;
    }

    @Autowired
    public void setCustomerReadService(CustomerReadService service){
        this.customerReadService = service;
    }

    @Autowired
    public void setSaleOrderItemEditService(SaleOrderItemEditService service){ this.saleOrderItemEditService = service; }

}
