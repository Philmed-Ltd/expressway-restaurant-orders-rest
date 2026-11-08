package io.salesorder.service;

import io.lib.service.BaseJpaRepoReadService;
import io.salesorder.entity.SalesOrderPayment;
import io.salesorder.form.FetchSalesOrderPaymentForm;
import io.salesorder.repository.SalesOrderPaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class SalesOrderPaymentReadService
        extends BaseJpaRepoReadService<SalesOrderPayment, SalesOrderPaymentRepository> {

    public Page<SalesOrderPayment> listBySalesOrder(FetchSalesOrderPaymentForm form) {
        var spec = repository.notDeleted()
                .and(repository.salesOrderEntityIdIs(form.getSalesOrderId()));

        return repository.findAll(spec, repository.defaultPageable(form));
    }
}