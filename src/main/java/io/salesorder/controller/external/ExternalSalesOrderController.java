package io.salesorder.controller.external;

import io.lib.form.BaseFetchForm;
import io.lib.service.Message;
import io.lib.view.EntityApiResponse;
import io.lib.view.PagedEntityApiResponse;
import io.salesorder.form.RegisterSalesOrderForm;
import io.salesorder.service.SalesOrderEditService;
import io.salesorder.service.SalesOrderReadService;
import io.salesorder.view.CustomerSalesOrderView;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

import static io.lib.service.SystemConfig.CUSTOMER_USER_BASE_URL;

@RestController
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

    @Autowired
    private void setSalesOrderEditService(SalesOrderEditService service){
        this.salesOrderEditService = service;
    }

    @Autowired
    public void setSalesOrderReadService(SalesOrderReadService service){
        this.salesOrderReadService = service;
    }
}
