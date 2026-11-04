package io.salesorder.service;

import io.lib.service.BaseJpaRepoEditService;
import io.salesorder.entity.SaleOrderItem;
import io.salesorder.repository.SaleOrderItemRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class SaleOrderItemReadService extends BaseJpaRepoEditService<SaleOrderItem, SaleOrderItemRepository> {

    public List<SaleOrderItem> getByOrderId(String orderId) {
        return repository.findAll(repository.byOrderId(orderId));
    }

    public List<SaleOrderItem> getByOrderIds(Collection<String> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return List.of();
        }
        return repository.findAll(repository.byOrderIds(orderIds));
    }
}
