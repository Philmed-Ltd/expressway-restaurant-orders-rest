package io.salesorder.view;

import io.lib.view.BaseView;
import io.salesorder.entity.SaleOrderItem;

import java.math.BigDecimal;

public class SaleOrderItemView extends BaseView<SaleOrderItem> {

    public SaleOrderItemView(SaleOrderItem entity) {
        super(entity);
    }

    public String getSaleOrderItemId() { return entity.getEntityId(); }

    public String getProductItemName() { return entity.getProductItem().getName(); }

    public Double getQuantity() { return entity.getQuantity(); }

    public String getProductItemUrl() { return entity.getProductItem().getImageUrl(); }

    public BigDecimal getLineTotal() {
        var productItem = entity.getProductItem();
        if (productItem.getPrice() == null || entity.getQuantity() == null) {
            return BigDecimal.ZERO;
        }
        return productItem.getPrice().multiply(BigDecimal.valueOf(entity.getQuantity()));
    }
}


