package io.salesorder.view;

import io.lib.view.BaseView;
import io.salesorder.entity.DeliveryAddress;
import io.salesorder.entity.OrderStatus;
import io.salesorder.entity.SalesOrder;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerSalesOrderView extends BaseView<SalesOrder> {

    private final List<SaleOrderItemView> saleOrderItems;

    public CustomerSalesOrderView(SalesOrder entity) {
        super(entity);
        this.saleOrderItems = entity.getSalesOrderItems().stream()
                .map(SaleOrderItemView::new)
                .collect(Collectors.toList());
    }

    public String getOrderId() { return entity.getEntityId(); }

    public DeliveryAddress getAddress() { return entity.getDeliveryAddress(); }

    public List<SaleOrderItemView> getSaleOrderItems() { return this.saleOrderItems; }

    public OrderStatus getOrderStatus(){ return entity.getOrderStatus(); }

    public BigDecimal getTotal() {
        return saleOrderItems.stream()
                .map(SaleOrderItemView::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

