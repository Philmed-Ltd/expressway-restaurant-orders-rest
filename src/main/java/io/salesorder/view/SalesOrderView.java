package io.salesorder.view;

import io.lib.view.BaseView;
import io.salesorder.entity.DeliveryAddress;
import io.salesorder.entity.SaleOrderItem;
import io.salesorder.entity.SalesOrder;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class SalesOrderView extends BaseView<SalesOrder> {

    private final List<SaleOrderItemView> saleOrderItems;

    public SalesOrderView(SalesOrder entity, List<SaleOrderItem> saleOrderItems) {
        super(entity);
        this.saleOrderItems = saleOrderItems.stream()
                .map(SaleOrderItemView::new)
                .collect(Collectors.toList());
    }

    public String getCustomerName() { return entity.getCustomer().getName(); }

    public String getCustomerPhoneNumber() { return entity.getCustomer().getPhoneNumber(); }

    public String getCustomerEmail() { return entity.getCustomer().getEmail(); }

    public DeliveryAddress getAddress() { return entity.getDeliveryAddress(); }

    public List<SaleOrderItemView> getSaleOrderItems() { return this.saleOrderItems; }

    public BigDecimal getTotal() {
        return saleOrderItems.stream()
                .map(SaleOrderItemView::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

