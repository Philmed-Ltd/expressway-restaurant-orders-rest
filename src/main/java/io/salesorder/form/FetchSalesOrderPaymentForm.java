package io.salesorder.form;

import io.lib.form.BaseFetchForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FetchSalesOrderPaymentForm extends BaseFetchForm {
    private String salesOrderId;
}
