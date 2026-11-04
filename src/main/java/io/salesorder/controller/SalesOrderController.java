package io.salesorder.controller;

import io.lib.service.Message;
import io.lib.view.EntityApiResponse;
import io.salesorder.entity.SaleOrderItem;
import io.salesorder.form.RegisterSalesOrderForm;
import io.salesorder.service.SaleOrderItemReadService;
import io.salesorder.service.SalesOrderEditService;
import io.salesorder.view.SalesOrderView;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

import static io.lib.service.SystemConfig.CUSTOMER_USER_BASE_URL;

@RestController
@RequestMapping(CUSTOMER_USER_BASE_URL + "/sales-orders")
public class SalesOrderController {

    private SalesOrderEditService salesOrderEditService;
    private SaleOrderItemReadService saleOrderItemReadService;

    @PreAuthorize("hasAuthority('REGISTER_SALES_ORDER')")
    @PostMapping("register")
    public EntityApiResponse<SalesOrderView> registerSalesOrder(
            @RequestBody @Valid RegisterSalesOrderForm form,
            Authentication auth,
            Locale locale) {
        form.setSessionUserId(auth.getName());
        var salesOrder = salesOrderEditService.registerSalesOrder(form);
        List<SaleOrderItem> items = saleOrderItemReadService.getByOrderId(salesOrder.getEntityId());
        return new EntityApiResponse<>(
                Message.get("sales.order.registration.success", locale),
                new SalesOrderView(salesOrder, items)
        );
    }

    @Autowired
    private void setSalesOrderEditService(SalesOrderEditService service){
        this.salesOrderEditService = service;
    }

    @Autowired
    private void setSaleOrderItemReadService(SaleOrderItemReadService service){ this.saleOrderItemReadService = service; }
}
