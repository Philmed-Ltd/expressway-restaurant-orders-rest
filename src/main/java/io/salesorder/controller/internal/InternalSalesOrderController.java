package io.salesorder.controller.internal;

import io.lib.form.BaseFetchForm;
import io.lib.view.PagedEntityApiResponse;
import io.salesorder.form.FetchSalesOrderPaymentForm;
import io.salesorder.service.SalesOrderPaymentReadService;
import io.salesorder.service.SalesOrderReadService;
import io.salesorder.view.SalesOrderPaymentView;
import io.salesorder.view.SalesOrderView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static io.lib.service.SystemConfig.INTERNAL_USER_BASE_URL;

@RestController
@RequestMapping(INTERNAL_USER_BASE_URL + "/sales-orders")
public class InternalSalesOrderController {

    private SalesOrderReadService salesOrderReadService;
    private SalesOrderPaymentReadService salesOrderPaymentReadService;

    @PreAuthorize("hasAuthority('VIEW_SALES_ORDERS')")
    @GetMapping("list")
    public PagedEntityApiResponse<SalesOrderView> list(
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "pageNum", required = false, defaultValue = "0") Integer pageNum,
            @RequestParam(name = "pageSize", required = false, defaultValue = "100") Integer pageSize
    ) {
        var form = new BaseFetchForm();
        form.setQuery(query);
        form.setPageNum(pageNum);
        form.setPageSize(pageSize);

        var page = salesOrderReadService.listSalesOrders(form);

        var views = page.stream()
                .map(SalesOrderView::new)
                .toList();

        return new PagedEntityApiResponse<>(page, views);
    }
    @PreAuthorize("hasAuthority('VIEW_SALES_ORDERS')")
    @GetMapping("list/payments/{salesOrderId}")
    public PagedEntityApiResponse<SalesOrderPaymentView> listPayments(
            @PathVariable String salesOrderId,
            @RequestParam(name="pageNum", required = false, defaultValue = "0") Integer pageNum,
            @RequestParam(name="pageSize", required = false, defaultValue = "25") Integer pageSize
    ){
        var form = new FetchSalesOrderPaymentForm();
        form.setSalesOrderId(salesOrderId);
        form.setPageNum(pageNum);
        form.setPageSize(pageSize);

        var page = salesOrderPaymentReadService.listBySalesOrder(form);
        var views = page.getContent().stream().map(SalesOrderPaymentView::new).toList();
        return new PagedEntityApiResponse<>(page, views);
    }


    @Autowired
    public void setSalesOrderReadService(SalesOrderReadService service) {
        this.salesOrderReadService = service;
    }

    @Autowired

    public void setSalesOrderPaymentReadService(SalesOrderPaymentReadService salesOrderPaymentReadService) {
        this.salesOrderPaymentReadService = salesOrderPaymentReadService;
    }
}
