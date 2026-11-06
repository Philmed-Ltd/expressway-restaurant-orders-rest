package io.salesorder.view;

import io.salesorder.entity.SalesOrder;

public class SalesOrderView extends CustomerSalesOrderView {

    public SalesOrderView(SalesOrder entity) {
        super(entity);
    }

    public String getCustomerName() { return entity.getCustomer().getName(); }

    public String getCustomerEmail() { return entity.getCustomer().getEmail(); }

    public String getCustomerPhoneNumber() { return entity.getCustomer().getPhoneNumber(); }
}
