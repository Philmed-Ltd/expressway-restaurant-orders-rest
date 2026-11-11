package io.salesorder.view;

import io.salesorder.entity.PaymentStatus;
import io.salesorder.entity.SalesOrder;

import java.math.BigDecimal;

public class SalesOrderView extends CustomerSalesOrderView {

    public SalesOrderView(SalesOrder entity) {
        super(entity);
    }

    public String getCustomerName() { return entity.getCustomer().getName(); }

    public String getCustomerEmail() { return entity.getCustomer().getEmail(); }

    public String getCustomerPhoneNumber() { return entity.getCustomer().getPhoneNumber(); }

    public BigDecimal getAmountPaid(){return entity.getAmountPaid();}

    public PaymentStatus getPaymentStatus(){return entity.getOrderPaymentStatus();}

}
