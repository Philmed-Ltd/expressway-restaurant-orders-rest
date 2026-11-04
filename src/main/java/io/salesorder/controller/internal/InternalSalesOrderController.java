package io.salesorder.controller.internal;

import io.lib.view.PagedEntityApiResponse;
import io.salesorder.entity.SaleOrderItem;
import io.salesorder.form.SalesOrderFetchForm;
import io.salesorder.service.SaleOrderItemReadService;
import io.salesorder.service.SalesOrderReadService;
import io.salesorder.view.SalesOrderView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.lib.service.SystemConfig.INTERNAL_USER_BASE_URL;

@RestController
@RequestMapping(INTERNAL_USER_BASE_URL + "/sales-orders")
public class InternalSalesOrderController {

    private SalesOrderReadService salesOrderReadService;
    private SaleOrderItemReadService saleOrderItemReadService;

    @PreAuthorize("hasAuthority('VIEW_SALES_ORDERS')")
    @GetMapping("list")
    public PagedEntityApiResponse<SalesOrderView> list(
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "customerEmail", required = false) String customerEmail,
            @RequestParam(name = "customerPhoneNumber", required = false) String customerPhoneNumber,
            @RequestParam(name = "pageNum", required = false, defaultValue = "0") Integer pageNum,
            @RequestParam(name = "pageSize", required = false, defaultValue = "100") Integer pageSize
    ) {
        var form = new SalesOrderFetchForm();
        form.setQuery(query);
        form.setCustomerEmail(customerEmail);
        form.setCustomerPhoneNumber(customerPhoneNumber);
        form.setPageNum(pageNum);
        form.setPageSize(pageSize);

        var page = salesOrderReadService.listSalesOrders(form);

        var orderIds = page.getContent().stream().map(order -> order.getEntityId()).toList();

        Map<String, List<SaleOrderItem>> itemsByOrderId = saleOrderItemReadService.getByOrderIds(orderIds).stream()
                        .collect(Collectors.groupingBy(SaleOrderItem::getSalesOrderEntityId));

        var views = page.getContent().stream().map(order -> new SalesOrderView(order, itemsByOrderId.getOrDefault(order.getEntityId(), List.of()))).toList();

        return new PagedEntityApiResponse<>(page, views);
    }

    @Autowired
    public void setSalesOrderReadService(SalesOrderReadService service) {
        this.salesOrderReadService = service;
    }

    @Autowired
    public void setSaleOrderItemReadService(SaleOrderItemReadService service) {
        this.saleOrderItemReadService = service;
    }
}
