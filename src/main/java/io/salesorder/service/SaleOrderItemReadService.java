package io.salesorder.service;

import io.lib.service.BaseJpaRepoEditService;
import io.salesorder.entity.SaleOrderItem;
import io.salesorder.repository.SaleOrderItemRepository;
import org.springframework.stereotype.Service;


@Service
public class SaleOrderItemReadService extends BaseJpaRepoEditService<SaleOrderItem, SaleOrderItemRepository> {

}
