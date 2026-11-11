package io.salesorder.controller.external;

import io.lib.form.BaseFetchForm;
import io.lib.service.Message;
import io.lib.view.ApiResponse;
import io.lib.view.EntityApiResponse;
import io.lib.view.PagedEntityApiResponse;
import io.mpesapayment.exception.PaymentException;
import io.salesorder.entity.OrderStatus;
import io.salesorder.form.RegisterSalesOrderForm;
import io.salesorder.form.SaleOrderPaymentForm;
import io.salesorder.service.SalesOrderEditService;
import io.salesorder.service.SalesOrderReadService;
import io.salesorder.validation.RequiredSalesOrderStatus;
import io.salesorder.view.CustomerSalesOrderView;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

import static io.lib.service.SystemConfig.CUSTOMER_USER_BASE_URL;

@RestController
@Validated
@RequestMapping(CUSTOMER_USER_BASE_URL + "/sales-orders")
public class ExternalSalesOrderController {

    private SalesOrderEditService salesOrderEditService;
    private SalesOrderReadService salesOrderReadService;

    @PostMapping("register")
    public EntityApiResponse<CustomerSalesOrderView> registerSalesOrder(
            @RequestBody @Valid RegisterSalesOrderForm form,
            Authentication auth,
            Locale locale) {
        form.setSessionUserId(auth.getName());
        var salesOrder = salesOrderEditService.registerSalesOrder(form);
        return new EntityApiResponse<>(
                Message.get("sales.order.registration.success", locale),
                new CustomerSalesOrderView(salesOrder)
        );
    }

   @PostMapping("pay/{salesOrderId}")
    public ApiResponse initializePayment(
           @PathVariable String salesOrderId,
           @RequestBody
           @Valid
           SaleOrderPaymentForm form,
           Authentication auth,
           Locale locale) throws PaymentException {
        form.setSessionUserId(auth.getName());
        salesOrderEditService.initializePayment(salesOrderId, form);
        return new ApiResponse(Message.get("payment.initialization.success", locale));
    }

    @GetMapping("list")
    public PagedEntityApiResponse<CustomerSalesOrderView> listMyOrders(
            @RequestParam(name = "pageNum", required = false, defaultValue = "0") Integer pageNum,
            @RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize,
            Authentication auth
    ) {
        var form = new BaseFetchForm();
        form.setPageNum(pageNum);
        form.setPageSize(pageSize);

        var page = salesOrderReadService.listSalesOrdersForCustomer(auth.getName(), form);
        var views = page.stream()
                .map(CustomerSalesOrderView::new)
                .toList();

        return new PagedEntityApiResponse<>(page, views);
    }

    @PutMapping("cancel-order/{orderId}")
    public EntityApiResponse<CustomerSalesOrderView> cancelOrder(
            @PathVariable
            @RequiredSalesOrderStatus(
                    statuses = OrderStatus.UNPAID,
                    message = "error.sales.order.not.cancellable"
            )
            String orderId,
            Authentication auth,
            Locale locale) {
        var salesOrder = salesOrderEditService.cancelOrder(orderId, auth.getName());
        return new EntityApiResponse<>(
                Message.get("sales.order.cancellation.success", locale),
                new CustomerSalesOrderView(salesOrder)
        );
    }

    @Autowired
    private void setSalesOrderEditService(SalesOrderEditService service){
        this.salesOrderEditService = service;
    }

    @Autowired
    public void setSalesOrderReadService(SalesOrderReadService service){
        this.salesOrderReadService = service;
    }
}
