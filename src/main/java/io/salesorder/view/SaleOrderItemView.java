package io.salesorder.view;

import io.productcatalog.entity.ProductItem;
import io.salesorder.entity.SaleOrderItem;

public class SaleOrderItemView {

    private final String saleOrderItemId;
    private final ProductItem productItem;

    private final Double quantity;

    public SaleOrderItemView(SaleOrderItem saleOrderItem) {
        this.saleOrderItemId = saleOrderItem.getEntityId();
        this.productItem = saleOrderItem.getProductItem();
        this.quantity = saleOrderItem.getQuantity();
    }

    public String getSaleOrderItemId() { return saleOrderItemId; }

    public String getProductItemName() { return productItem.getName(); }

    public String getProductItemUrl() { return productItem.getImageUrl(); }

    public java.math.BigDecimal getLineTotal() {
        if (productItem.getPrice() == null || quantity == null) return java.math.BigDecimal.ZERO;
        return productItem.getPrice().multiply(java.math.BigDecimal.valueOf(quantity));
    }
}


