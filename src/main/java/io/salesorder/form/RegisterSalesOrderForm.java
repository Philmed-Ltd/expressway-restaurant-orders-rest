package io.salesorder.form;

import io.lib.form.SessionUserIdForm;
import io.salesorder.entity.DeliveryAddress;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RegisterSalesOrderForm extends SessionUserIdForm {

    @NotNull
    @Valid
    private DeliveryAddress deliveryAddress;

    @NotEmpty(message = "at.least.one.item.required")
    @Valid
    private List<SaleOrderItemForm> items;
}
