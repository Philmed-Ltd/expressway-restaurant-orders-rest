package io.salesorder.view;

import io.lib.view.BaseView;
import io.salesorder.entity.SalesOrderPayment;

import java.math.BigDecimal;

public class SalesOrderPaymentView extends BaseView<SalesOrderPayment> {

    public SalesOrderPaymentView(SalesOrderPayment entity) {super(entity);}

    public BigDecimal getAmount() {return entity.getAmount();}

    public String getTransactionId() {return entity.getTransactionId();}

}