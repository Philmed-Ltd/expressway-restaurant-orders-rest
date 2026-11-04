package io.salesorder.service;

import io.lib.service.BaseJpaRepoEditService;
import io.productcatalog.service.ProductItemReadService;
import io.salesorder.entity.SaleOrderItem;
import io.salesorder.entity.SalesOrder;
import io.salesorder.form.SaleOrderItemForm;
import io.salesorder.repository.SaleOrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SaleOrderItemEditService  extends BaseJpaRepoEditService<SaleOrderItem, SaleOrderItemRepository> {

    private ProductItemReadService productItemReadService;

    public void registerSaleOrderItems(SalesOrder salesOrder, List<SaleOrderItemForm> items){

        List<SaleOrderItem> saleOrderItems = new ArrayList<>();
        for(var item: items){
            var productItem = productItemReadService.findByEntityId(item.getProductItemEntityId());
            var saleOrderItem = new SaleOrderItem();
            saleOrderItem.setSalesOrder(salesOrder);
            saleOrderItem.setProductItem(productItem);
            saleOrderItem.setProductItemEntityId(item.getProductItemEntityId());
            saleOrderItem.setQuantity(item.getQuantity());
            saleOrderItem.setProductPrice(productItem.getPrice());
            saleOrderItems.add(saleOrderItem);
        }
        save(saleOrderItems, salesOrder.getUpdatedByEntityId());
    }

    @Autowired
    public void setProductItemReadService(ProductItemReadService service){
        this.productItemReadService = service;
    }

}
